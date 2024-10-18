package com.test.permissionusesjwt.service;

import com.test.permissionusesjwt.constant.DefinedRole;
import com.test.permissionusesjwt.dto.request.UserCreationRequest;
import com.test.permissionusesjwt.dto.request.UserUpdateRequest;
import com.test.permissionusesjwt.dto.response.UserResponse;
import com.test.permissionusesjwt.entity.User;
import com.test.permissionusesjwt.entity.Role;
import com.test.permissionusesjwt.exception.AppException;
import com.test.permissionusesjwt.exception.ErrorCode;
import com.test.permissionusesjwt.mapper.UserMapper;
import com.test.permissionusesjwt.repository.RoleRepository;
import com.test.permissionusesjwt.repository.UserRepository;
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
import java.util.HashSet;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults (level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class UserService {
    UserRepository userRepository;
    UserMapper userMapper;
    PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;

    public UserResponse createRequest(UserCreationRequest request)
    {

        User user = userMapper.toUser(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        HashSet<Role> roles = new HashSet<>();
        roleRepository.findById(DefinedRole.USER_ROLE).ifPresent(roles::add);
        user.setRoles(roles);

        try{
            user = userRepository.save(user);
        }
        catch (DataIntegrityViolationException e) {
            throw new AppException(ErrorCode.USER_EXISTED);
        }

        return userMapper.toUserResponse(user);
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

    public void deleteUser (String id)
    {
        userRepository.deleteById(id);
    }

    //thong tin se duoc luu trong securitycontextholder nếu dang nhap thanh cong
    public UserResponse getMyInfo()
    {
        var context = SecurityContextHolder.getContext();
        String name = context.getAuthentication().getName();

        User user =  userRepository.findByUsername(name).orElseThrow(
                () -> new AppException(ErrorCode.USER_NOT_EXISTED));

        return userMapper.toUserResponse(user);
    }
}
