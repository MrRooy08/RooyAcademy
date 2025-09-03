package com.test.permissionusesjwt.service;

import com.test.permissionusesjwt.authUtils.AuthUtils;
import com.test.permissionusesjwt.dto.request.LessonRequest;
import com.test.permissionusesjwt.dto.request.LessonUpdateRequest;
import com.test.permissionusesjwt.dto.response.LessonResponse;
import com.test.permissionusesjwt.entity.Lesson;
import com.test.permissionusesjwt.entity.Media;
import com.test.permissionusesjwt.mapper.LessonMapper;
import com.test.permissionusesjwt.repository.CourseRepository;
import com.test.permissionusesjwt.repository.LessonRepository;
import com.test.permissionusesjwt.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class LessonService {
    LessonMapper lessonMapper;
    FileStorageService fileStorageService;
    private final LessonRepository lessonRepository;
    private final UserRepository userRepository;
    private final AuthUtils authUtils;
    private final CourseRepository courseRepository;

    @Transactional
    public LessonResponse createLesson (LessonRequest request, MultipartFile[] files, MultipartFile videoFile){
        Lesson lesson = lessonMapper.toLesson(request);
        String indexOfLesson = String.valueOf(lesson.getIndex());
        lesson = lessonRepository.save(lesson);
        String lessonId = lesson.getId();

        if (videoFile != null){
            String fileName = fileStorageService.saveVideoLesson(videoFile, "lessons/videos/" + lessonId);
            lesson.setVideo_url(fileName);
            lesson = lessonRepository.save(lesson);
        }

        if(files != null){
            List<Media> mediaList = fileStorageService.saveLessonResources(files, "lessons/resource/" + lessonId);
            for (Media media : mediaList) {
                media.setLesson(lesson);
            }
            lesson.setMediaList(mediaList);
            lesson = lessonRepository.save(lesson);
        }
        return lessonMapper.toLessonResponse(lesson);
    }

    @Transactional
    public LessonResponse updateLesson(LessonUpdateRequest request, MultipartFile[] files, MultipartFile videoFile) {
        Lesson lesson = lessonRepository.findById(request.getId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy bài giảng"));

        // ✅ Cập nhật thông tin bài giảng cơ bản
        lesson.setName(request.getName());
        lesson.setDescription(request.getDescription());
        lesson.setContent(request.getContent());
        lesson.setIndex(Integer.parseInt(request.getIndex()));
        lesson.setIsPreviewable(request.getIsPreviewable());

        // ✅ 1. Xóa những media không nằm trong mediaKept
        Set<String> mediaKept = request.getMediaKept() != null ? new HashSet<>(request.getMediaKept()) : new HashSet<>();
        lesson.getMediaList().removeIf(media -> !mediaKept.contains(media.getId()));

        // ✅ 2. Thêm media mới (nếu có)
        if (files != null && files.length > 0) {
            List<Media> newMediaList = fileStorageService.saveLessonResources(files, "lessons/resource/" + lesson.getId());
            for (Media media : newMediaList) {
                media.setLesson(lesson); // gắn lesson
                lesson.getMediaList().add(media); // thêm vào danh sách
            }
        }

        // ✅ 3. Cập nhật video (nếu có)
        if (videoFile != null && !videoFile.isEmpty()) {
            String videoUrl = fileStorageService.saveVideoLesson(videoFile, "lessons/videos/" + lesson.getId());
            lesson.setVideo_url(videoUrl);
        }

        // ✅ Lưu lại entity
        lessonRepository.save(lesson);
        return lessonMapper.toLessonResponse(lesson);
    }

    public List<Lesson> getLessonBySection(String sectionId) {
//        String username = authUtils.getCurrentUsername();
//        Instructor instructor = userRepository.findInstructorByUsername(username).orElseThrow(
//                () -> new AppException(ErrorCode.USER_NOT_EXISTED)
//        );

//        Course course = courseRepository.findCourseBySectionsId(sectionId);

        List<Lesson> lessons = lessonRepository.getLessonBySectionId(sectionId);
        return lessons;
    }

//    @Transactional
//    public ResponseEntity<LessonResponse> createLesson(String lessonId, MultipartFile[] files) {
//        // Lưu trữ các tệp tin và lấy lại mediaList
//        List<Media> mediaList = fileStorageService.saveLessonResources(files, "lessons/" + lessonId);
//
//        // Giả sử bạn có một đối tượng lesson (có thể được truy vấn từ database)
//        Lesson lesson = lessonRepository.getLessonById(lessonId);
//
//        // Chuyển đổi đối tượng Lesson thành LessonResponse thông qua mapper
//        LessonResponse lessonResponse = lessonMapper.toLessonResponse(lesson);
//
//        // Trả về ResponseEntity với LessonResponse và mã trạng thái HTTP 200 OK
//        return ResponseEntity.ok(lessonResponse);
//    }

}
