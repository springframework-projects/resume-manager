package com.jyx.hr.resume_manager.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface FileMetageDataRepository extends MongoRepository<FileMetaData, ObjectId> {
}
