package com.test.permissionusesjwt.service;

import com.test.permissionusesjwt.authUtils.AuthUtils;
import com.test.permissionusesjwt.constant.DefinedRole;
import com.test.permissionusesjwt.dto.request.EnrolledCourseDto;

import com.test.permissionusesjwt.dto.request.UserCreationRequest;
import com.test.permissionusesjwt.dto.request.UserGoogleCreate;
import com.test.permissionusesjwt.dto.request.UserUpdateRequest;
import com.test.permissionusesjwt.dto.response.UserResponse;
import com.test.permissionusesjwt.entity.Enrollment;
import com.test.permissionusesjwt.entity.Instructor;
import com.test.permissionusesjwt.entity.Profile;
import com.test.permissionusesjwt.entity.User;
import com.test.permissionusesjwt.entity.Role;
import com.test.permissionusesjwt.exception.AppException;
import com.test.permissionusesjwt.exception.ErrorCode;
import com.test.permissionusesjwt.mapper.UserMapper;
import com.test.permissionusesjwt.repository.*;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.test.permissionusesjwt.dto.response.CartResponse;
import com.test.permissionusesjwt.service.CartService;

@Service
@RequiredArgsConstructor
@FieldDefaults (level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class UserService {
    UserRepository userRepository;
    AuthUtils authUtils;
    UserMapper userMapper;
    CartRepository cartRepository;
    CartService cartService;
    PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final ProfileRepository profileRepository;
    private final OtpVerificationRepository otpRepository;
    private final InstructorRepository instructorRepository;
    EnrollmentRepository enrollmentRepository;

    public UserResponse createRequest(UserCreationRequest request)
    {
        boolean check= verifyOtp(request.getUsername(), request.getOtp());
        if (check){
            User user = userMapper.toUser(request);
            user.setPassword(passwordEncoder.encode(request.getPassword()));

            HashSet<Role> roles = new HashSet<>();
            roleRepository.findByName(DefinedRole.USER_ROLE)
                    .ifPresent(roles::add);
            user.setRoles(roles);
            user.setIsActive(true);
            try{
                user = userRepository.save(user);
                String username = user.getUsername();
                String firstName = "";
                String lastName = username;
                if (username.contains(" ")) {
                    int idx = username.lastIndexOf(" ");
                    firstName = username.substring(0, idx);
                    lastName = username.substring(idx+1);
                }
                Profile profile = Profile.builder()
                        .user(user)
                        .firstName(firstName)
                        .lastName(lastName)
                        .headline("")
                        .bio("")
                        .dob(LocalDate.now())
                        .build();
                profileRepository.save(profile);
            }
            catch (DataIntegrityViolationException e) {
                throw new AppException(ErrorCode.USER_EXISTED);
            }

            return userMapper.toUserResponse(user);
        }
        else {
            throw new AppException(ErrorCode.USER_NOT_EXISTED);
        }
    }

    public boolean verifyOtp(String email, String otp) {
        return otpRepository.findTopByEmailOrderByExpirationTimeDesc(email)
                .filter(o -> !o.isVerified()
                        && o.getExpirationTime().isAfter(LocalDateTime.now())
                        && o.getOtp().equals(otp))
                .map(o -> {
                    o.setVerified(true);
                    otpRepository.save(o);
                    return true;
                })
                .orElse(false);
    }


    public User createGoogleRequest(UserGoogleCreate request)
    {
        User user = new User();
        HashSet<Role> roles = new HashSet<>();
        roleRepository.findByName(DefinedRole.USER_ROLE)
                .ifPresent(roles::add);
        user.setUsername(request.getEmail());
        user.setRoles(roles);
        user.setIsActive(true);
        try{
            user = userRepository.save(user);
            String username = request.getTen();
            String firstName = "";
            String lastName = username;
            if (username.contains(" ")) {
                int idx = username.lastIndexOf(" ");
                firstName = username.substring(0, idx);
                lastName = username.substring(idx+1);
            }
            Profile profile = Profile.builder()
                    .user(user)
                    .firstName(firstName)
                    .lastName(lastName)
                    .headline("")
                    .bio("")
                    .dob(LocalDate.now())
                    .build();
            profileRepository.save(profile);
        }
        catch (DataIntegrityViolationException e) {
            throw new AppException(ErrorCode.USER_EXISTED);
        }
        return user;
    }

    public UserResponse updateUser (String id, UserUpdateRequest request)
        {
            User user = userRepository.findById(id).orElseThrow(()
                    -> new AppException(ErrorCode.USER_NOT_EXISTED));


            userMapper.updateUser(user,request);
            user.setPassword(passwordEncoder.encode(request.getPassword()));

            var roles = roleRepository.findAllById(request.getRoles());
            user.setRoles(new HashSet<>(roles));

            return userMapper.toUserResponse(userRepository.save(user));
        }


    @PreAuthorize("hasRole('ADMIN')") //kiểm tra trước khi method được thực hiện
    public List<UserResponse> getAllUsers() {
        log.info("Get all users");
        return userRepository.findAll().stream()
                .map(userMapper::toUserResponse)
                .toList();
    }

    //"returnObject.username == authentication.name" user đang đăng nhập phải truùng với user đang lấy thì mới duyệt
    // chỉ được lấy thông tin của chính mình
    @PostAuthorize("returnObject.username == authentication.name") //kiểm tra sau khi method thực hiện sau
    public UserResponse getUserById (String id)
    {
        log.info("Get user by id {}", id);
        return userMapper.toUserResponse(userRepository.findById(id).orElseThrow(()
        -> new AppException(ErrorCode.USER_NOT_EXISTED)));
    }

    public boolean checkEmail (String username)
    {
        boolean exists = userRepository.findByUsername(username).isPresent();
        if (exists) {
            throw new AppException(ErrorCode.USER_EXISTED);
        }
        return true;
    }

    public void deleteUser (String id)
    {
        userRepository.deleteById(id);
    }

    //thong tin se duoc luu trong securitycontextholder nếu dang nhap thanh cong
    public UserResponse getMyInfo() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        // Map basic info first
        UserResponse response = userMapper.toUserResponse(user);

        // Fetch cart if exists
        cartRepository.findByUser(user).ifPresent(cart -> {
            CartResponse cartResp = cartService.toCartResponse(cart);
            response.setCart(cartResp);
        });

        // Add enrolled courses to student info
        if (user.getProfile() != null) {
            List<Enrollment> enrollments = enrollmentRepository.findByProfile(user.getProfile());
            List<EnrolledCourseDto> enrolledCourses = enrollments.stream()
                    .map(enrollment -> EnrolledCourseDto.builder()
                            .enrollmentId(enrollment.getId())
                            .courseId(enrollment.getCourse().getId())
                            .build())
                    .collect(Collectors.toList());
            response.setEnrolledCourses(enrolledCourses);
        }

        return response;
    }

    /**
     * Cấp quyền giảng viên cho người dùng
     * Thêm bản ghi vào bảng ho_so_giang_vien và thêm role INSTRUCTOR vào vai_tro_nguoi_dung
     */// Chỉ admin mới có thể cấp quyền giảng viên
    @Transactional
    public void grantInstructorRole() {
        String username = authUtils.getCurrentUsername();
        // Kiểm tra user tồn tại
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        if (instructorRepository.findByUser(user).isPresent()) {
            throw new AppException(ErrorCode.INSTRUCTOR_ALREADY_EXISTS);
        }

        // Tạo hồ sơ giảng viên
        Instructor instructor = Instructor.builder()
                .user(user)
                .firstName(username)
                .lastName(username)
                .build();

        instructor = instructorRepository.save(instructor);

        // Thêm role INSTRUCTOR cho user
        Role instructorRole = roleRepository.findByName(DefinedRole.INSTRUCTOR_ROLE)
                .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_FOUND));

        user.getRoles().add(instructorRole);
        userRepository.save(user);
    }


}
