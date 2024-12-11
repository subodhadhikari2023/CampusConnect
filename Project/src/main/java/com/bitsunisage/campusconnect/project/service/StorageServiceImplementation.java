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

import java.io.File;
import java.nio.file.Path;
import java.util.List;
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

        String filePath = uploadDir + "/" + fileUploadDTO.getFileRole() + "/" + fileUploadDTO.getCourseId() + "/" + fileUploadDTO.getSemesterId() + "/" + fileUploadDTO.getSubjectId();
        try {
//            System.out.println(filePath);
            String fileExtension = Objects.requireNonNull(file.getOriginalFilename()).substring(file.getOriginalFilename().lastIndexOf("."));
            new java.io.File(uploadDir + "/" + fileUploadDTO.getFileRole() + "/" + fileUploadDTO.getCourseId() + "/" + fileUploadDTO.getSemesterId() + "/" + fileUploadDTO.getSubjectId()).mkdirs();
            file.transferTo(Path.of(new File(uploadDir + "/" + fileUploadDTO.getFileRole() + "/" + fileUploadDTO.getCourseId() + "/" + fileUploadDTO.getSemesterId() + "/" + fileUploadDTO.getSubjectId()) + "/" + file.getOriginalFilename()));

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
            fileData.setFileRole(fileUploadDTO.getFileRole());
            FileData savedFileData = fileDAO.save(fileData);
            System.out.println(fileUploadDTO);
            System.out.println("Saved FileData: " + savedFileData);


        } catch (Exception e) {
            System.out.println("Some exception occurred!!!");

        }

    }

    @Override
    public String getCurrentOwnersName() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            return authentication.getName();
        }
        return null;
    }

    @Override
    public void deleteResource(Long id) {
        FileData fileData = fileDAO.findById(id).orElseThrow(() -> new RuntimeException("Not found"));
        File file = new File(fileData.getFilePath() + "/" + fileData.getFileName());
        System.out.println(fileData.getFilePath());
        if (file.exists()) {
            System.out.println("FIle present");
            file.delete();

        }
        fileDAO.deleteById(id);
    }


    @Override
    public List<FileData> findResourcesUploaded(String name) {
        return fileDAO.findAllByOwnersName(name);

    }


}
