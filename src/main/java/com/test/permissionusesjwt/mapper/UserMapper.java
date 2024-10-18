package com.test.permissionusesjwt.mapper;

import com.test.permissionusesjwt.dto.request.UserCreationRequest;
import com.test.permissionusesjwt.dto.request.UserUpdateRequest;
import com.test.permissionusesjwt.dto.response.UserResponse;
import com.test.permissionusesjwt.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User toUser (UserCreationRequest request);

    //@Mapping dùng để map 2 properties nếu không trùng kiểu dữ liệu
    //@Mapping (source = "firstName", target = "lastName"): 2 giá trị này sẽ bằng nhau

    UserResponse toUserResponse (User user);
    //Trong mapstruct sẽ tạo 1 đối tượng mới khi map
    // nên dùng MappingTarget thì mapstruct sẽ thay đôổi trực tiếp object đó và k taạo mới
    @Mapping(target = "roles", ignore = true)
    // do dang su dung List<String> roles va List<Role> roles nen lờ đi để chạy
    void updateUser(@MappingTarget User user, UserUpdateRequest request);
}
