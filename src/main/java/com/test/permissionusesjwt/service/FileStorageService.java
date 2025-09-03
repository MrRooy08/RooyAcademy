package com.test.permissionusesjwt.service;

import com.test.permissionusesjwt.entity.Course;
import com.test.permissionusesjwt.entity.Lesson;
import com.test.permissionusesjwt.entity.Media;
import com.test.permissionusesjwt.enums.ActiveStatus;
import com.test.permissionusesjwt.enums.MediaType;
import com.test.permissionusesjwt.exception.AppException;
import com.test.permissionusesjwt.exception.ErrorCode;
import com.test.permissionusesjwt.repository.CourseRepository;
import com.test.permissionusesjwt.repository.LessonRepository;
import com.test.permissionusesjwt.repository.MediaRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class FileStorageService {
    private final CourseRepository courseRepository;
    private final MediaRepository mediaRepository;
    private final String UPLOAD_DIR = "uploads/";
    private final LessonRepository lessonRepository;

    @Transactional
    public String saveVideoCourse(MultipartFile file, String subFolder) {
        try {
            String extension = StringUtils.getFilenameExtension(file.getOriginalFilename());

            // ✅ Chỉ cho phép định dạng video
            List<String> allowed = List.of("mp4", "webm", "mov", "mkv");
            if (extension == null || !allowed.contains(extension.toLowerCase())) {
                throw new IllegalArgumentException("Định dạng video không hỗ trợ");
            }

            // ✅ Trích courseId từ đường dẫn folder (course/{id})
            String[] courseIdPath = subFolder.split("/");
            if (courseIdPath.length < 2) {
                throw new IllegalArgumentException("Sai cấu trúc thư mục, cần format: course/{id}");
            }
            String courseId = courseIdPath[2];

            String originalName = file.getOriginalFilename();
            String baseName = originalName.substring(0, originalName.lastIndexOf('.'));
            baseName = baseName.replaceAll("\\s+", "_")
                    .replaceAll("[^a-zA-Z0-9_\\-]", "");
            String fileName = baseName + "." + extension;
            Path folderPath = Paths.get(UPLOAD_DIR + subFolder);
            Files.createDirectories(folderPath);

            // ✅ Xoá video cũ (nếu có)
            Files.list(folderPath).forEach(path -> {
                try {
                    Files.deleteIfExists(path);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

            // ✅ Lưu video mới
            Path filePath = folderPath.resolve(fileName);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            // ✅ Cập nhật DB
            Course course = courseRepository.findById(courseId)
                    .orElseThrow(() -> new AppException(ErrorCode.COURSE_NOT_EXISTED));
            course.setVideoUrl(fileName);
            courseRepository.save(course);

            return fileName; // client sẽ ghép base URL
        } catch (IOException e) {
            throw new RuntimeException("Upload video thất bại", e);
        }
    }

    @Transactional
    public String saveVideoLesson(MultipartFile file, String subFolder) {
        try {
            String extension = StringUtils.getFilenameExtension(file.getOriginalFilename());

            // ✅ Chỉ cho phép định dạng video
            List<String> allowed = List.of("mp4", "webm", "mov", "mkv");
            if (extension == null || !allowed.contains(extension.toLowerCase())) {
                throw new IllegalArgumentException("Định dạng video không hỗ trợ");
            }

            // ✅ Trích courseId từ đường dẫn folder (course/{id})
            String[] courseIdPath = subFolder.split("/");
            if (courseIdPath.length < 2) {
                throw new IllegalArgumentException("Sai cấu trúc thư mục, cần format: course/{id}");
            }
            String lessonId = courseIdPath[2];

            String originalName = file.getOriginalFilename();
            String baseName = originalName.substring(0, originalName.lastIndexOf('.'));
            baseName = baseName.replaceAll("\\s+", "_")
                    .replaceAll("[^a-zA-Z0-9_\\-]", "");
            String fileName = baseName + "." + extension;
            Path folderPath = Paths.get(UPLOAD_DIR + subFolder);
            Files.createDirectories(folderPath);

            // ✅ Xoá video cũ (nếu có)
            Files.list(folderPath).forEach(path -> {
                try {
                    Files.deleteIfExists(path);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

            // ✅ Lưu video mới
            Path filePath = folderPath.resolve(fileName);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            return fileName;
        } catch (IOException e) {
            throw new RuntimeException("Upload video thất bại", e);
        }
    }





    @Transactional
    public String saveImageCourse(MultipartFile file, String subFolder) {
        try {
            String extension = StringUtils.getFilenameExtension(file.getOriginalFilename());

            List<String> allowed = List.of("jpg", "jpeg", "png", "webp", "mp4");
            if (!allowed.contains(extension.toLowerCase())) {
                throw new IllegalArgumentException("Định dạng không hỗ trợ");
            }

            String[] courseIdPath = subFolder.split("/");

            String fileName = courseIdPath[1] + "." + extension;

            Path folderPath = Paths.get(UPLOAD_DIR + subFolder);
            Files.createDirectories(folderPath);

            Files.list(folderPath).forEach(path -> {
                try {
                    Files.deleteIfExists(path);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

            Path filePath = folderPath.resolve(fileName);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            Course course = courseRepository.findById(courseIdPath[1])
                    .orElseThrow(() -> new AppException(ErrorCode.COURSE_NOT_EXISTED));

            course.setImageUrl(fileName);
            courseRepository.save(course);

            // Trả về URL truy cập từ browser
            return fileName;

        } catch (IOException e) {
            throw new RuntimeException("Upload failed", e);
        }
    }




    @Transactional
    public List<Media> saveLessonResources(MultipartFile[] files, String subFolder) {
        try {
            String[] pathParts = subFolder.split("/");
            String lessonId = pathParts[2];

            List<Media> mediaList = new ArrayList<>();

            Path folderPath = Paths.get(UPLOAD_DIR + subFolder);
            Files.createDirectories(folderPath);

            for (MultipartFile file : files) {
                String extension = StringUtils.getFilenameExtension(file.getOriginalFilename());

                List<String> allowed = List.of("pdf", "doc", "docx", "ppt", "pptx", "xls", "xlsx",
                        "png", "jpg", "jpeg", "webp", "zip", "rar", "mp4", "txt");

                if (extension == null || !allowed.contains(extension.toLowerCase())) {
                    throw new IllegalArgumentException("Định dạng không hỗ trợ: " + extension);
                }

                String fileName = file.getOriginalFilename();

                Path filePath = folderPath.resolve(fileName);
                Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
                Media media = Media.builder()
                        .name(fileName)
                        .size(file.getSize())
                        .mediaType(MediaType.AVAILABLE)
                        .activeStatus(ActiveStatus.Active)
                        .build();
                mediaList.add(media);
            }

            return mediaList;

        } catch (IOException e) {
            throw new RuntimeException("Lưu tài nguyên thất bại", e);
        }
    }

//    @Transactional
//    public List<Media> saveLessonResources(MultipartFile[] files, String subFolder) {
//        try {
//            // VD: subFolder = "lessons/abc123"
//            Path folderPath = Paths.get(UPLOAD_DIR + subFolder);
//            Files.createDirectories(folderPath);
//
//            List<Media> mediaList = new ArrayList<>();
//
//            for (MultipartFile file : files) {
//                String extension = StringUtils.getFilenameExtension(file.getOriginalFilename());
//
//                List<String> allowed = List.of(
//                        "pdf", "doc", "docx", "ppt", "pptx", "xls", "xlsx",
//                        "png", "jpg", "jpeg", "webp", "zip", "rar", "mp4", "txt"
//                );
//
//                if (extension == null || !allowed.contains(extension.toLowerCase())) {
//                    throw new IllegalArgumentException("Định dạng không hỗ trợ: " + extension);
//                }
//
//                // Đặt tên mới để tránh trùng
//                String fileName = UUID.randomUUID() + "." + extension;
//
//                // Đường dẫn lưu
//                Path filePath = folderPath.resolve(fileName);
//                Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
//
//                // Tạo đối tượng Media
//                Media media = Media.builder()
//                        .name(file.getOriginalFilename()) // tên gốc
//                        .url("/files/" + subFolder + "/" + fileName) // đường dẫn để truy cập từ FE
//                        .size(file.getSize())
//                        .mediaType(MediaType.AVAILABLE)
//                        .activeStatus(ActiveStatus.Active)
//                        .build();
//
//                mediaList.add(media);
//            }
//
//            // ✅ Lưu vào DB
//            return mediaRepository.saveAll(mediaList);
//
//        } catch (IOException e) {
//            throw new RuntimeException("Lưu tài nguyên thất bại", e);
//        }
//    }

}

