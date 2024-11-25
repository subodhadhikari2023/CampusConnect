package com.bitsunisage.campusconnect.project.service;

import com.bitsunisage.campusconnect.project.dataAccessObject.FileDAO;
import com.bitsunisage.campusconnect.project.entities.Files.FileData;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


@Service
public class StorageServiceImplementation implements StorageService {
    private final FileDAO fileDAO;

    @Value("${file.upload-dir}")
    private String uploadDir;


    @Autowired
    StorageServiceImplementation(FileDAO fileDAO) {
        this.fileDAO = fileDAO;
    }

    @Transactional
    @Override
    public void uploadImageToFileSystem(MultipartFile file) {
        String filePath = uploadDir + file.getOriginalFilename();
        try {
//            System.out.println(filePath);
            file.transferTo(new java.io.File(filePath));


            FileData fileData = new FileData();
            fileData.setFileName(file.getOriginalFilename());
            fileData.setFilePath(filePath);
            fileData.setFileType(file.getContentType());
            fileData.setFileSize(file.getSize());
            FileData savedFileData = fileDAO.save(fileData);
//            System.out.println("Saved FileData: " + savedFileData.getId());


        } catch (Exception e) {
            e.printStackTrace();

        }
    }

}
