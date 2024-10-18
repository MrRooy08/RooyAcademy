package com.test.permissionusesjwt.mapper;

import com.test.permissionusesjwt.dto.request.LevelRequest;

import com.test.permissionusesjwt.dto.request.UserUpdateRequest;
import com.test.permissionusesjwt.dto.response.LevelResponse;
import com.test.permissionusesjwt.entity.Level;
import com.test.permissionusesjwt.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface LevelMapper {
    Level toLevel (LevelRequest levelRequest);

    LevelResponse toLevelResponse (Level level);

    void updateLevel(@MappingTarget Level level, LevelRequest levelRequest);
}
