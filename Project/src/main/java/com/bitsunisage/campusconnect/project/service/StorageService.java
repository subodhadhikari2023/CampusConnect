package com.bitsunisage.campusconnect.project.service;

import com.bitsunisage.campusconnect.project.DataTransferObject.FileUploadDTO;
import com.bitsunisage.campusconnect.project.entities.Department;
import com.bitsunisage.campusconnect.project.entities.FileData;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;

public interface StorageService {
    void uploadToFileSystem(FileUploadDTO fileUploadDTO) throws IOException;

    List<FileData> findResourcesUploaded(String name);

    String getCurrentOwnersName();

    void deleteResource(Long id);

    List<FileData> findAll();

    List<Department> findAllDepartment(List<Long> departmentIds);

    List<FileData> findFilesByFilters(Long departmentId, Long courseId, Long semesterId, Long subjectId,String fileRole);

    InputStream compressFileWithGzip(String filePath) throws IOException;
    InputStream compressFileWithZip(String filePath) throws IOException;


    Optional<FileData> getFileById(Long fileId);
}
