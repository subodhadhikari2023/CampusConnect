package com.bitsunisage.campusconnect.project.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface StorageService {
    public void uploadImageToFileSystem(MultipartFile file) throws IOException;
}
