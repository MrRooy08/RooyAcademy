package com.test.permissionusesjwt.service;

import com.test.permissionusesjwt.dto.request.ProfileRequest;
import com.test.permissionusesjwt.dto.request.ProfileSocialRequest;
import com.test.permissionusesjwt.dto.response.ProfileResponse;
import com.test.permissionusesjwt.entity.*;
import com.test.permissionusesjwt.exception.AppException;
import com.test.permissionusesjwt.exception.ErrorCode;
import com.test.permissionusesjwt.mapper.ProfileMapper;
import com.test.permissionusesjwt.repository.ProfileRepository;
import com.test.permissionusesjwt.repository.ProfileSocialRepository;
import com.test.permissionusesjwt.repository.SocialRepository;
import com.test.permissionusesjwt.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class ProfileService {
    ProfileMapper profileMapper;
    ProfileRepository profileRepository;
    UserRepository userRepository;
    private final SocialRepository socialRepository;
    private final ProfileSocialRepository profileSocialRepository;

    public ProfileResponse createProfile(ProfileRequest profileRequest) {
        Profile profile = profileMapper.toProfile(profileRequest);
        User user = userRepository.findById(profileRequest.getUser_id()).orElseThrow(
                () -> new AppException(ErrorCode.USER_NOT_EXISTED)
        );
        profile.setUser(user);
        try {
            profileRepository.save(profile);
        } catch (DataIntegrityViolationException e) {
            throw new AppException(ErrorCode.USER_EXISTED);
        }
        return profileMapper.toProfileResponse(profile);
    }

    public ProfileResponse save(ProfileRequest profileRequest) {

        Profile profile = profileRepository.findByUserId(profileRequest.getUser_id()).orElseThrow(
                () -> new AppException(ErrorCode.USER_NOT_EXISTED)
        );
        profile.setBio(profileRequest.getBio());
        profile.setDob(profileRequest.getDob());
        profile.setHeadline(profileRequest.getHeadline());
        profile.setFirstName(profileRequest.getFirstName());
        profile.setLastName(profileRequest.getLastName());
        profileRepository.save(profile);

        //kiem tra null
        if (profileRequest.getSocial() != null) {
            List<ProfileSocial> listToSave = new ArrayList<>();
            for (ProfileSocialRequest p : profileRequest.getSocial())
            {
                ProfileSocial profileSocial = new ProfileSocial();
                Social social = socialRepository.findByUrl(p.getUrl()).orElseThrow(
                        () -> new AppException(ErrorCode.USER_NOT_EXISTED)
                );

                ProfileSocialID profileSocialID = new ProfileSocialID();
                profileSocialID.setProfile_id(profile.getId());
                profileSocialID.setSocial_id(social.getId());

                profileSocial.setProfileSocialID(profileSocialID);
                profileSocial.setSocial(social);
                profileSocial.setName(p.getUsername());
                profileSocial.setProfile(profile);
                listToSave.add(profileSocial);
            }

            Set<ProfileSocial> temp = new HashSet<>(listToSave);
            profile.setProfile(temp);
            profileSocialRepository.saveAll(listToSave);

            // xoá dữ liệu mxh nào là null để toi uu db
            for (ProfileSocial p : listToSave)
            {
                if ( p.getName() == null || p.getName().isEmpty() )
                {
                    profileSocialRepository.delete(p);
                }
            }
        }
        return profileMapper.toProfileResponse(profile);
    }

}
