package com.jyx.hr.resume_manager.web;

import com.jyx.hr.resume_manager.repository.FileMetaData;
import com.jyx.hr.resume_manager.service.FileService;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.print.attribute.standard.Media;
import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/files")
public class FileController {

    private final FileService fileService;

    public FileController(FileService fileService) {
        this.fileService = fileService;
    }


    @PostMapping("/upload")
    public ResponseEntity<?> uploadFile(
            @RequestParam("file") MultipartFile file
    ) {

        // provide proper user management
        String ownerId = "123";

        try {
            FileMetaData metaData = fileService.uploadFile(file, ownerId);
            return ResponseEntity.ok(Map.ofEntries(Map.entry("imageId", metaData.id().toHexString()), Map.entry("originalName", metaData.originalName()), Map.entry("siwe", metaData.size())));

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @GetMapping("/{imageId}")
    public ResponseEntity<Resource> downloadFile(
            @PathVariable String imageId
    ) {

        try {
            FileMetaData metaData = fileService.getFileMetaData(imageId);
            Resource resource = fileService.getFileResource(imageId);
            return ResponseEntity
                    .ok()
                    .header(
                            HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"" + metaData.originalName() + "\"")
                    .contentType(MediaType.parseMediaType(metaData.mimeType()))
                    .contentLength(metaData.size())
                    .body(resource);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}
