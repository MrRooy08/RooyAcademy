package com.test.permissionusesjwt.mapper;

import com.test.permissionusesjwt.dto.response.TierPriceResponse;
import com.test.permissionusesjwt.entity.TierPrice;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TierPriceMapper {

    TierPriceResponse toTierPriceResponse (TierPrice tierPrice);
}
