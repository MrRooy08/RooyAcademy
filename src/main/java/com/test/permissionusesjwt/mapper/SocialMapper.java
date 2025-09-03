package com.test.permissionusesjwt.mapper;

import com.test.permissionusesjwt.dto.request.SocialRequest;
import com.test.permissionusesjwt.dto.response.SocialResponse;
import com.test.permissionusesjwt.entity.Social;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface SocialMapper {

    Social toSocial (SocialRequest request);
    SocialResponse toSocialResponse (Social Social);
}
