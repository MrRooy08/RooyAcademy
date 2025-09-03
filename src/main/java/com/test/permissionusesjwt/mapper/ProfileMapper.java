package com.test.permissionusesjwt.mapper;

import com.test.permissionusesjwt.dto.request.ProfileRequest;

import com.test.permissionusesjwt.dto.response.ProfileResponse;
import com.test.permissionusesjwt.dto.response.ProfileSocialResponse;

import com.test.permissionusesjwt.entity.Profile;
import com.test.permissionusesjwt.entity.ProfileSocial;

import com.test.permissionusesjwt.entity.User;

import org.mapstruct.*;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface ProfileMapper {
    @Mapping(source = "user_id", target = "user",qualifiedByName = "mapStringToUser")
    Profile toProfile ( ProfileRequest request);


    @Named("mapStringToUser")
    default User mapStringToUser(String user_id) {
        if (user_id == null || user_id.isBlank()) {
            return null;
        }
        User user = new User();
        user.setId(user_id);
        return user;
    }

    @Mapping(source = "user", target = "user_id", qualifiedByName = "mapUserToString")
//    @Mapping(source = "profile", target = "social", qualifiedByName = "mapPStoPSR")
    ProfileResponse toProfileResponse (Profile profile);

    @Named("mapUserToString")
    default String mapUserToString(User user) {
        if (user.getId() == null || user.getId().isBlank()) {
            return null;
        }
        return user.getId();
    }

//    @Named("mapPStoPSR")
//    default Set<ProfileSocialResponse> mapPStoPSR(Set<ProfileSocial> profileSocial) {
//        if (profileSocial == null || profileSocial.isEmpty()) {
//            return null;
//        }
//
//        return profileSocial.stream()
//                .map(temp -> new ProfileSocialResponse(
//                        temp.getSocial().getUrl()
//                ))
//                .collect(Collectors.toSet());
//    }

}
