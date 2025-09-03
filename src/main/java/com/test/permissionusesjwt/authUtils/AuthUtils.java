package com.test.permissionusesjwt.authUtils;

import com.test.permissionusesjwt.exception.AppException;
import com.test.permissionusesjwt.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component ("courseSecurity")
@RequiredArgsConstructor
public class AuthUtils {

    public String getCurrentUsername () {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof Jwt jwt)) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }
        return jwt.getSubject();
    }

    public List<String> getRolesFromToken() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof Jwt jwt)) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }

        List<String> roles = jwt.getClaimAsStringList("scope");
        if (roles == null || roles.isEmpty()) {
            // Một số provider đặt scope là chuỗi cách nhau bởi khoảng trắng
            String scopeStr = jwt.getClaimAsString("scope");
            if (scopeStr != null) {
                roles = List.of(scopeStr.split(" ")); // tách theo khoảng trắng
            }
        } else if (roles.size() == 1 && roles.get(0).contains(" ")) {
            // Trường hợp list có 1 phần tử nhưng chứa nhiều role cách nhau bởi space
            roles = List.of(roles.get(0).split(" "));
        }
        return roles != null ? roles : Collections.emptyList();
    }


}
