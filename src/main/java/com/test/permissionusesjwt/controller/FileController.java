package com.test.permissionusesjwt.controller;

import com.test.permissionusesjwt.entity.Media;
import com.test.permissionusesjwt.service.FileStorageService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/upload")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
@Slf4j
public class FileController {

    private final FileStorageService fileStorageService;

    @PostMapping("/course-image")
    public ResponseEntity<?> uploadCourseImage(
            @RequestParam("courseId") String courseId,
            @RequestPart("file") MultipartFile file) {
        String imageUrl = fileStorageService.saveImageCourse(file, "courses/" + courseId);
        return ResponseEntity.ok(Map.of("imageUrl", imageUrl));
    }

    @GetMapping("/lesson/{lessonId}/{fileName:.+}")
    public ResponseEntity<Resource> downloadLessonResource(
            @PathVariable String lessonId,
            @PathVariable String fileName,
            HttpServletRequest request) {
        try {
            Path filePath = Paths.get("uploads")
                    .resolve("lessons")
                    .resolve(lessonId)
                    .resolve(fileName)
                    .normalize();

            Resource resource = new UrlResource(filePath.toUri());
            if (!resource.exists()) {
                return ResponseEntity.notFound().build();
            }

            String contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
            if (contentType == null) {
                contentType = "application/octet-stream";
            }

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                    .body(resource);
        } catch (IOException ex) {
            log.error("Download file failed", ex);
            return ResponseEntity.internalServerError().body(null);
        }
    }

//    @PostMapping("/lesson/{lessonId}")
//    public ResponseEntity<List<MediaResponse>> deletedFileFromLession(
//            @PathVariable String lessonId,
//            @RequestParam("files") MultipartFile[] files) {
//        List<MediaResponse> result = mediaService.uploadLessonFiles(lessonId, files);
//        return ResponseEntity.ok(result);
//    }
}
