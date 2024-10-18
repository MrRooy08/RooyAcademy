package com.test.permissionusesjwt.service;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.test.permissionusesjwt.dto.request.AuthenticationRequest;
import com.test.permissionusesjwt.dto.request.IntrospectRequest;
import com.test.permissionusesjwt.dto.request.LogOutRequest;
import com.test.permissionusesjwt.dto.request.RefreshRequest;
import com.test.permissionusesjwt.dto.response.AuthenticationResponse;
import com.test.permissionusesjwt.dto.response.IntrospectResponse;
import com.test.permissionusesjwt.entity.InvalidatedToken;
import com.test.permissionusesjwt.entity.User;
import com.test.permissionusesjwt.exception.AppException;
import com.test.permissionusesjwt.exception.ErrorCode;
import com.test.permissionusesjwt.repository.InvalidatedTokenRepository;
import com.test.permissionusesjwt.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.StringJoiner;
import java.util.UUID;


@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationService {

    @NonFinal //đánh dấu k tạo constructer
    @Value("${jwt.signer.key}")
    protected String SIGNER_KEY;

    @NonFinal //đánh dấu k tạo constructer
    @Value("${jwt.valid-duration}")
    protected long VALID_DURATION;

    @NonFinal //đánh dấu k tạo constructer
    @Value("${jwt.refreshable-duration}")
    protected long REFRESHABLE_DURATION;

    UserRepository userRepository;
    InvalidatedTokenRepository tokenRepository;


    public AuthenticationResponse authenticate (AuthenticationRequest request)
    {
        var user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
        boolean authenticated =  passwordEncoder.matches(request.getPassword(), user.getPassword());
        if(!authenticated)
            throw new AppException (ErrorCode.UNAUTHENTICATED);
        var token = generateToken(user);

        return AuthenticationResponse.builder()
                .token(token)
                .authenticated(true)
                .build();
    }

    public IntrospectResponse introspect (IntrospectRequest request)
            throws JOSEException, ParseException {
        var token = request.getToken();
        log.info("Token received for introspection: {}", token);
        boolean isRefresh = true;
        try {
            verifyToken(token,false);
        }
        catch (AppException e) {
            isRefresh = false;
        }

        return  IntrospectResponse.builder()
                .valid(isRefresh)
                .build();
    }

    private String generateToken (User user)
    {
        JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);

        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .subject(user.getUsername())//đại diện cho user đăng nhập
                .issuer("minhrom.com") // được kế thừa từ ai
                .issueTime(new Date()) // thời điểm hiện tại
                .expirationTime(new Date(
                        // thoi diem hien tai tang them 1 thoi gian
                  //      Instant.now().plus(1, ChronoUnit.HOURS).toEpochMilli()
                        Instant.now().plus(VALID_DURATION, ChronoUnit.SECONDS).toEpochMilli()
                ))
                .jwtID(UUID.randomUUID().toString())
                .claim("scope",buildScope(user))
                .build();
        Payload payload = new Payload(jwtClaimsSet.toJSONObject());

        JWSObject jwsObject = new JWSObject (header, payload);

        // ky token
        try {
            jwsObject.sign(new MACSigner(SIGNER_KEY.getBytes()));
            return jwsObject.serialize();
        } catch (JOSEException e) {
            log.error("Cannot create token",e);
            throw new RuntimeException(e);
        }

    }

    //khoi tao scope cho user (quyen user)
    private String buildScope (User user )
    {
        StringJoiner stringJoiner = new StringJoiner(" ");
        if (!CollectionUtils.isEmpty(user.getRoles()))
        {
            user.getRoles().forEach(role -> {
                stringJoiner.add("ROLE_" + role.getName());
                if (!CollectionUtils.isEmpty(role.getPermissions()))
                {
                    role.getPermissions().forEach(
                            permission -> stringJoiner.add(permission.getName())
                    );
                }
            });

        }
        return stringJoiner.toString();
    }

    public void logOut (LogOutRequest request)
            throws ParseException, JOSEException {
        var signToken = verifyToken(request.getToken(),false);

        String jwt = signToken.getJWTClaimsSet().getJWTID();
        Date expiryDate = signToken.getJWTClaimsSet().getExpirationTime();

        InvalidatedToken invalidatedToken = InvalidatedToken.builder()
                .id(jwt)
                .expiryTime(expiryDate)
                .build();

        tokenRepository.save(invalidatedToken);
    }

    private SignedJWT verifyToken (String  token, boolean isRefresh)
            throws JOSEException, ParseException {
        log.info("Token before verification: {}", token); // Log token trước khi xác minh

        JWSVerifier verifier = new MACVerifier(SIGNER_KEY.getBytes());

        SignedJWT signedJWT = SignedJWT.parse(token);

        //kiểm tra token có hết hạn chưa
        Date expiration = (isRefresh)
                ? new Date(signedJWT
                .getJWTClaimsSet()
                .getIssueTime()
                .toInstant()
                .plus(REFRESHABLE_DURATION, ChronoUnit.SECONDS)
                .toEpochMilli())
               : signedJWT.getJWTClaimsSet().getExpirationTime();

        var verified = signedJWT.verify(verifier);

        if(!(verified && expiration.after(new Date())))
            throw new AppException (ErrorCode.UNAUTHENTICATED);

        if(tokenRepository.existsById(signedJWT.getJWTClaimsSet().getJWTID()))
            throw new AppException(ErrorCode.UNAUTHENTICATED);

        log.info("Token after verification: {}", signedJWT.serialize());
        return signedJWT;
        
    }

    //refresh token
    public AuthenticationResponse refreshToken (RefreshRequest request)
            throws ParseException, JOSEException {
        var singedJwt = verifyToken(request.getToken(),true);

        var jit = singedJwt.getJWTClaimsSet().getJWTID();
        var expiryTime = singedJwt.getJWTClaimsSet().getExpirationTime();

        InvalidatedToken invalidatedToken = InvalidatedToken.builder()
                .id(jit)
                .expiryTime(expiryTime)
                .build();

        tokenRepository.save(invalidatedToken);

        var userName = singedJwt.getJWTClaimsSet().getSubject();
        var user = userRepository.findByUsername(userName).orElseThrow(
                () -> new AppException(ErrorCode.USER_NOT_EXISTED)
        );

        var token = generateToken(user);

        return AuthenticationResponse.builder()
                .token(token)
                .authenticated(true)
                .build();

    }
}
