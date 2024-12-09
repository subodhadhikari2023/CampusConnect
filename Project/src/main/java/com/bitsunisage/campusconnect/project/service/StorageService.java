package com.bitsunisage.campusconnect.project.service;

import com.bitsunisage.campusconnect.project.DataTransferObject.FileUploadDTO;

import java.io.IOException;

public interface StorageService {
    public void uploadToFileSystem(FileUploadDTO fileUploadDTO) throws IOException;
}
