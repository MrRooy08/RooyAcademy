package com.test.permissionusesjwt.mapper;

import com.test.permissionusesjwt.dto.request.UserCreationRequest;
import com.test.permissionusesjwt.dto.request.UserUpdateRequest;
import com.test.permissionusesjwt.dto.response.UserResponse;
import com.test.permissionusesjwt.exception.AppException;
import com.test.permissionusesjwt.exception.ErrorCode;
import com.test.permissionusesjwt.repository.RoleRepository;
import com.test.permissionusesjwt.entity.Role;
import com.test.permissionusesjwt.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(source = "roles", target = "roles")
    User toUser (UserCreationRequest request);

    default Set<Role> map(Set<String> roleNames) {
        if (roleNames == null) {
            return null;
        }
        return roleNames.stream()
                .map(Role::new) // Chuyển String thành Role
                .collect(Collectors.toSet());
    }


    //@Mapping dùng để map 2 properties nếu không trùng kiểu dữ liệu
    //@Mapping (source = "firstName", target = "lastName"): 2 giá trị này sẽ bằng nhau


    UserResponse toUserResponse (User user);
    //Trong mapstruct sẽ tạo 1 đối tượng mới khi map
    // nên dùng MappingTarget thì mapstruct sẽ thay đôổi trực tiếp object đó và k taạo mới
    @Mapping(target = "roles", ignore = true)
    // do dang su dung List<String> roles va List<Role> roles nen lờ đi để chạy
    void updateUser(@MappingTarget User user, UserUpdateRequest request);
}
