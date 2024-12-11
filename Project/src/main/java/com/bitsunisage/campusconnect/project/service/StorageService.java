package com.bitsunisage.campusconnect.project.service;

import com.bitsunisage.campusconnect.project.DataTransferObject.FileUploadDTO;
import com.bitsunisage.campusconnect.project.entities.FileData;
import com.bitsunisage.campusconnect.project.entities.User;

import java.io.IOException;
import java.util.List;

public interface StorageService {
    public void uploadToFileSystem(FileUploadDTO fileUploadDTO) throws IOException;
    List<FileData> findResourcesUploaded(String name);
    public String getCurrentOwnersName();
    public void deleteResource(Long id);
}
