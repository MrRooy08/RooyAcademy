package com.test.permissionusesjwt.mapper;

import com.test.permissionusesjwt.dto.request.PermissionRequest;
import com.test.permissionusesjwt.dto.request.RoleRequest;
import com.test.permissionusesjwt.dto.response.PermissionResponse;
import com.test.permissionusesjwt.dto.response.RoleResponse;
import com.test.permissionusesjwt.entity.Permission;
import com.test.permissionusesjwt.entity.Role;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RoleMapper {
    @Mapping(target = "permissions", ignore = true)
    Role toRole (RoleRequest request);
    //@Mapping dùng để map 2 properties nếu không trùng kiểu dữ liệu
    //@Mapping (source = "firstName", target = "lastName"): 2 giá trị này sẽ bằng nhau
    RoleResponse toRoleResponse (Role role);
}
