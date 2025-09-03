package com.test.permissionusesjwt.service;

import com.test.permissionusesjwt.dto.response.TierPriceResponse;
import com.test.permissionusesjwt.entity.TierPrice;
import com.test.permissionusesjwt.mapper.TierPriceMapper;
import com.test.permissionusesjwt.repository.TierPriceRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class TierPriceService {
    TierPriceRepository tierPriceRepository;
    TierPriceMapper tierPriceMapper;

    public List<TierPriceResponse> getAllPrice() {
        return tierPriceRepository.findAll().stream()
                .sorted(Comparator.comparing(TierPrice::getPrice))
                .map(tierPriceMapper::toTierPriceResponse)
                .collect(Collectors.toList());
    }
}
