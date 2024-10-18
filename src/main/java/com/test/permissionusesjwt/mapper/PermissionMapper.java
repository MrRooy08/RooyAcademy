package com.test.permissionusesjwt.mapper;

import com.test.permissionusesjwt.dto.request.PermissionRequest;
import com.test.permissionusesjwt.dto.request.UserCreationRequest;
import com.test.permissionusesjwt.dto.request.UserUpdateRequest;
import com.test.permissionusesjwt.dto.response.PermissionResponse;
import com.test.permissionusesjwt.dto.response.UserResponse;
import com.test.permissionusesjwt.entity.Permission;
import com.test.permissionusesjwt.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface PermissionMapper {
    Permission toPermission (PermissionRequest request);
    //@Mapping dùng để map 2 properties nếu không trùng kiểu dữ liệu
    //@Mapping (source = "firstName", target = "lastName"): 2 giá trị này sẽ bằng nhau
    PermissionResponse toPermissionResponse (Permission permission);
}
