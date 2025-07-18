package com.jyx.hr.resume_manager.repository;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document("image_metadata")
public record FileMetaData(
        String originalName,
        String storedName,
        String mimeType,
        String ownerId,
        long size,
        Instant createdAt,
        @Id ObjectId id

) {
}
