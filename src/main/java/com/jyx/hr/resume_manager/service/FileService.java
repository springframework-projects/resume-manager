package com.jyx.hr.resume_manager.service;

import com.jyx.hr.resume_manager.repository.FileMetaData;
import com.jyx.hr.resume_manager.repository.FileMetageDataRepository;
import org.bson.types.ObjectId;
import org.springframework.core.io.Resource;
import org.springframework.data.mongodb.core.aggregation.ArrayOperators;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;
import java.util.Objects;

@Service
public class FileService {


    private final FileStorageProperties properties;
    private final FileMetageDataRepository repository;
    private final FileStorageService service;

    public FileService(FileStorageProperties properties, FileMetageDataRepository repository, FileStorageService service) {
        this.properties = properties;
        this.repository = repository;
        this.service = service;
    }

    public FileMetaData uploadFile(MultipartFile file, String ownerId) throws IOException {
        validateFile(file);

        String storagePath;
        try (InputStream inputStream = file.getInputStream()) {
            storagePath = service.storeFile(inputStream, file.getOriginalFilename());
        }

        FileMetaData metaData = new FileMetaData(
                file.getOriginalFilename(),
                storagePath,
                file.getContentType(),
                ownerId,
                file.getSize(),
                Instant.now(),
                ObjectId.get()
        );

        return repository.save(metaData);
    }

    public Resource getFileResource(String fileId) throws IOException {
        FileMetaData metaData = getFileMetaData(fileId);
        return service.getFileResource(metaData.storedName());
    }

    public FileMetaData getFileMetaData(String fileId) throws IOException {
        ObjectId objectId = new ObjectId(fileId);
        return repository.findById(objectId).orElseThrow(
                () -> new FileNotFoundException("File not found.")
        );
    }

    private void validateFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new IllegalStateException("File is empty.");
        }

        String mimeType = file.getContentType();
        if (Objects.requireNonNull(mimeType).isEmpty() || !properties.allowedMimeTypes().contains(mimeType)) {
            throw new IllegalArgumentException("Invalid mime type");
        }


    }

}
