package com.test.permissionusesjwt.service;

import com.test.permissionusesjwt.dto.request.SocialRequest;
import com.test.permissionusesjwt.dto.response.SocialResponse;
import com.test.permissionusesjwt.entity.Social;
import com.test.permissionusesjwt.exception.AppException;
import com.test.permissionusesjwt.exception.ErrorCode;
import com.test.permissionusesjwt.mapper.SocialMapper;
import com.test.permissionusesjwt.repository.SocialRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class SocialService {
    SocialRepository socialRepository;
    SocialMapper socialMapper;

    public SocialResponse createSocial(SocialRequest socialRequest) {
        Social social = socialMapper.toSocial(socialRequest);
        social.setUrl(socialRequest.getUrl().toLowerCase());

         try {
             social = socialRepository.save(social);

         } catch (DataIntegrityViolationException e) {
             throw new AppException(ErrorCode.COURSE_EXISTED);
         }

         return socialMapper.toSocialResponse(social);
    }
}
