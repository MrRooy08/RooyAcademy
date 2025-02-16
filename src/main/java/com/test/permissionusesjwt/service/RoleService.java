package com.test.permissionusesjwt.service;

import com.test.permissionusesjwt.dto.request.RoleRequest;
import com.test.permissionusesjwt.dto.request.UserCreationRequest;
import com.test.permissionusesjwt.dto.request.UserUpdateRequest;
import com.test.permissionusesjwt.dto.response.RoleResponse;
import com.test.permissionusesjwt.dto.response.UserResponse;
import com.test.permissionusesjwt.entity.Permission;
import com.test.permissionusesjwt.entity.User;
import com.test.permissionusesjwt.exception.AppException;
import com.test.permissionusesjwt.exception.ErrorCode;
import com.test.permissionusesjwt.mapper.RoleMapper;
import com.test.permissionusesjwt.mapper.UserMapper;
import com.test.permissionusesjwt.repository.PermissionRepository;
import com.test.permissionusesjwt.repository.RoleRepository;
import com.test.permissionusesjwt.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults (level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class RoleService {
    RoleRepository roleRepository;
    PermissionRepository permissionRepository;
    RoleMapper roleMapper;

    public RoleResponse create (RoleRequest request)
    {
            var role = roleMapper.toRole(request);

            HashSet<Permission> permissions = new HashSet<>();
            for (String permission : request.getPermissions()) {
                permissionRepository.findByName(permission)
                        .ifPresent(permissions::add);
            }


            role.setPermissions(permissions);

            role = roleRepository.save(role);
            return roleMapper.toRoleResponse(role);
    }

    public List<RoleResponse> getAll ()
    {
        var roles = roleRepository.findAll();
        return roles.stream().map(roleMapper::toRoleResponse).toList();
    }

    public void delete (String roleName)
    {
        roleRepository.deleteById(roleName);
    }
}
