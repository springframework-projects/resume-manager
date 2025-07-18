package com.jyx.hr.resume_manager.service;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Set;

@ConfigurationProperties(prefix = "app.file-storage")
@Component
public record FileStorageProperties(
        String basePath,
        Set<String> allowedMimeTypes
) {
    // static initialize the allowedMimeTypes since it is a finite list
    public FileStorageProperties() {
        this(
                "./files",
                Set.of(
                        "application/pdf",
                        "application/vnd.oasis.opendocument.text",
                        "application/vnd.openxmlformats-officedocument.wordprocessingml.document"

                )
        );
    }
}
