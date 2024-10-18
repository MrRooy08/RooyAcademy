package com.test.permissionusesjwt.service;

import com.test.permissionusesjwt.dto.request.PermissionRequest;
import com.test.permissionusesjwt.dto.response.PermissionResponse;
import com.test.permissionusesjwt.entity.Permission;
import com.test.permissionusesjwt.mapper.PermissionMapper;
import com.test.permissionusesjwt.repository.PermissionRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class PermissionService {

    PermissionRepository permissionRepository;
    PermissionMapper permissionMapper;

    public PermissionResponse createPermission(PermissionRequest request)
    {
        Permission permission = permissionMapper.toPermission(request);
        permission = permissionRepository.save(permission);
        return permissionMapper.toPermissionResponse(permission);
    }

    public List<PermissionResponse> getAll ()
    {
        var permissions = permissionRepository.findAll();
        return permissions.stream()
                .map(permissionMapper::toPermissionResponse).toList();
    }

    public void delete (String name)
    {
        permissionRepository.deleteById(name);
    }
}
