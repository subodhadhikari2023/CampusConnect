package com.bitsunisage.campusconnect.project.service;

import com.bitsunisage.campusconnect.project.DataTransferObject.FileUploadDTO;
import com.bitsunisage.campusconnect.project.dataAccessObject.FileDAO;
import com.bitsunisage.campusconnect.project.entities.FileData;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Objects;


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
    public void uploadToFileSystem(FileUploadDTO fileUploadDTO) {
        MultipartFile file = fileUploadDTO.getFile();

        String filePath = uploadDir + file.getOriginalFilename();
        try {
//            System.out.println(filePath);
            String fileExtension = Objects.requireNonNull(file.getOriginalFilename()).substring(file.getOriginalFilename().lastIndexOf("."));
            switch (fileExtension) {
                case ".pptx":
                    new java.io.File(uploadDir + "/presentations/").mkdirs();
                    file.transferTo(new java.io.File(uploadDir + "/presentations/" + file.getOriginalFilename()));
                    break;
                case ".ppt":
                    new java.io.File(uploadDir + "/presentations/ppt/").mkdirs();
                    file.transferTo(new java.io.File(uploadDir + "/presentations/" + file.getOriginalFilename()));
                    break;
                case ".pdf":
                    new java.io.File(uploadDir + "/pdf/").mkdirs();
                    file.transferTo(new java.io.File(uploadDir + "/pdf/" + file.getOriginalFilename()));
                    break;
                default:

                    System.out.println("Unsupported file type: " + fileExtension);
            }


            FileData fileData = new FileData();
            fileData.setFileName(file.getOriginalFilename());
            fileData.setFilePath(filePath);
            fileData.setFileType(fileExtension);
            fileData.setFileSize(file.getSize());
            fileData.setOwnersName(getCurrentOwnersName());
            fileData.setOwnersDepartmentID(fileUploadDTO.getDepartmentId());
            fileData.setCourseId(fileUploadDTO.getCourseId());
            fileData.setSemesterId(fileUploadDTO.getSemesterId());
            fileData.setSubjectId(fileUploadDTO.getSubjectId());
            FileData savedFileData = fileDAO.save(fileData);
            System.out.println("Saved FileData: " + savedFileData);


        } catch (Exception e) {
            System.out.println("Some exception occurred!!!");

        }

    }

    public String getCurrentOwnersName() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            return authentication.getName();
        }
        return null;
    }


}
