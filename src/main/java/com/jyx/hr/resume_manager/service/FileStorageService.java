package com.jyx.hr.resume_manager.service;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.util.UUID;

@Service
public class FileStorageService {

    private final FileStorageProperties properties;
    private final Path rootPath;

    public FileStorageService(FileStorageProperties properties) {
        this.properties = properties;
        this.rootPath = Paths.get(properties.basePath());
    }

    public String storeFile(InputStream inputStream, String originalName) throws IOException {
        LocalDate today = LocalDate.now();

        Path datedDirectory = rootPath.resolve(
                today.getYear() + File.separator +
                        String.format("%02d", today.getMonthValue()) + File.separator +
                        String.format("%02d", today.getDayOfMonth())
        );

        Files.createDirectories(datedDirectory);


        Path filePath = datedDirectory.resolve(buildStoredName(originalName));

        try (OutputStream outputStream = Files.newOutputStream(filePath, StandardOpenOption.CREATE_NEW)) {
            StreamUtils.copy(inputStream, outputStream);
        }

        return rootPath.relativize(filePath).toString();

    }

    public Resource getFileResource(String storedPath) throws IOException {

        Path filePath = rootPath.resolve(storedPath).normalize().toAbsolutePath();
        Path normalizedRootPath = rootPath.normalize().toAbsolutePath();

        if (!filePath.startsWith(normalizedRootPath)) {
            throw new SecurityException("Access Denied");
        }

        if (!Files.exists(filePath)) {
            throw new FileNotFoundException("File not found");
        }

        return new UrlResource(filePath.toUri());
    }

    private String buildStoredName(String originalName) {
        String extension = getFileExtension(originalName);
        return UUID.randomUUID() + (extension.isEmpty() ? "" : "." + extension);
    }

    /**
     * @param fileName
     * @return
     */
    private String getFileExtension(String fileName) {
        int lastDotIndex = fileName.lastIndexOf(".");
        return lastDotIndex == 1 ? "" : fileName.substring(lastDotIndex + 1);
    }


}
