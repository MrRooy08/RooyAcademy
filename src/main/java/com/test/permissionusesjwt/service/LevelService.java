package com.test.permissionusesjwt.service;


import com.test.permissionusesjwt.dto.request.LevelRequest;
import com.test.permissionusesjwt.dto.response.LevelResponse;
import com.test.permissionusesjwt.dto.response.UserResponse;
import com.test.permissionusesjwt.entity.Level;
import com.test.permissionusesjwt.exception.AppException;
import com.test.permissionusesjwt.exception.ErrorCode;
import com.test.permissionusesjwt.mapper.LevelMapper;
import com.test.permissionusesjwt.repository.LevelRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class LevelService {
    LevelMapper levelMapper;
    LevelRepository levelRepository;

    public LevelResponse createLevel(LevelRequest levelRequest) {
        Level level = levelMapper.toLevel(levelRequest);

        try {
            level = levelRepository.save(level);
        } catch (DataIntegrityViolationException e) {
            throw new AppException(ErrorCode.LEVEL_EXISTED);
        }

        return levelMapper.toLevelResponse(level);
    }

    public void deleteByName(String name) {
        Level level = levelRepository.findLevelByName(name).orElseThrow(
                ()-> new AppException(ErrorCode.LEVEL_NOT_EXISTED)
        );
        levelRepository.delete(level);
    }


    public LevelResponse updateLevel (String name, LevelRequest levelRequest) {
            Level level = levelRepository.findLevelByName(name).orElseThrow(
                    () -> new AppException(ErrorCode.LEVEL_NOT_EXISTED)
            );

            levelMapper.updateLevel(level, levelRequest);
            return levelMapper.toLevelResponse(levelRepository.save(level));
    }

    public List<LevelResponse> getAllLevels() {
        return levelRepository.findAll().stream()
                .map(levelMapper::toLevelResponse)
                .toList();
    }
}
