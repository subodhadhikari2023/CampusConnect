package com.bitsunisage.campusconnect.project.service;

import com.bitsunisage.campusconnect.project.DataTransferObject.FileUploadDTO;
import com.bitsunisage.campusconnect.project.dataAccessObject.DepartmentDAO;
import com.bitsunisage.campusconnect.project.dataAccessObject.FileDAO;
import com.bitsunisage.campusconnect.project.entities.Department;
import com.bitsunisage.campusconnect.project.entities.FileData;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;


@Service
public class StorageServiceImplementation implements StorageService {
    private final FileDAO fileDAO;
    private final DepartmentDAO departmentDAO;


    @Value("${file.upload-dir}")
    private String uploadDir;


    @Autowired
    StorageServiceImplementation(FileDAO fileDAO, DepartmentDAO departmentDAO) {
        this.fileDAO = fileDAO;
        this.departmentDAO = departmentDAO;
    }

    @Transactional
    @Override
    public void uploadToFileSystem(FileUploadDTO fileUploadDTO) {
        MultipartFile file = fileUploadDTO.getFile();

        String filePath = uploadDir + "/"+fileUploadDTO.getDepartmentId() + "/" + fileUploadDTO.getFileRole() + "/" + fileUploadDTO.getCourseId() + "/" + fileUploadDTO.getSemesterId() + "/" + fileUploadDTO.getSubjectId();
        try {
//            System.out.println(filePath);
            String fileExtension = Objects.requireNonNull(file.getOriginalFilename()).substring(file.getOriginalFilename().lastIndexOf("."));
            new java.io.File(uploadDir +"/"+ fileUploadDTO.getDepartmentId() + "/" + fileUploadDTO.getFileRole() + "/" + fileUploadDTO.getCourseId() + "/" + fileUploadDTO.getSemesterId() + "/" + fileUploadDTO.getSubjectId()).mkdirs();
            file.transferTo(Path.of(new File(uploadDir +"/"+ fileUploadDTO.getDepartmentId() + "/" + fileUploadDTO.getFileRole() + "/" + fileUploadDTO.getCourseId() + "/" + fileUploadDTO.getSemesterId() + "/" + fileUploadDTO.getSubjectId()) + "/" + file.getOriginalFilename()));

            FileData fileData = new FileData();
            fileData.setFileName(file.getOriginalFilename());
            fileData.setFilePath(filePath);
            fileData.setFileType(fileExtension);
            fileData.setFileSize(file.getSize());
            fileData.setOwnersName(getCurrentOwnersName());
            fileData.setFileDepartmentId(fileUploadDTO.getDepartmentId());
            fileData.setCourseId(fileUploadDTO.getCourseId());
            fileData.setFileDepartmentId(fileUploadDTO.getDepartmentId());
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
        FileData fileData = fileDAO.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("File not found with ID: " + id));
        File file = new File(fileData.getFilePath() + "/" + fileData.getFileName());
        System.out.println(fileData.getFilePath());
        if (file.exists()) {
            System.out.println("FIle present");
            file.delete();

        }
        fileDAO.deleteById(id);
    }

    @Override
    public List<FileData> findAll() {
        return fileDAO.findAll();
    }

    @Override
    public List<Department> findAllDepartment(List<Long> departmentIds) {
        return departmentDAO.findByIdIn(departmentIds);
    }

    @Override
    public List<FileData> findFilesByFilters(Long departmentId, Long courseId, Long semesterId, Long subjectId, String fileRole) {
//        System.out.println(fileDAO.findFilesByFilters(departmentId, courseId, semesterId, subjectId));
//        System.out.println(departmentId);
        return fileDAO.findFilesByFilters(departmentId, courseId, semesterId, subjectId, fileRole);

    }


    @Override
    public Optional<FileData> getFileById(Long fileId) {
        return fileDAO.findById(fileId);

//        return null;
    }
    public InputStream compressFileWithGzip(String filePath) throws IOException {
        FileInputStream fileInputStream = new FileInputStream(filePath);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        GZIPOutputStream gzipOutputStream = new GZIPOutputStream(byteArrayOutputStream);

        byte[] buffer = new byte[1024];
        int length;
        while ((length = fileInputStream.read(buffer)) != -1) {
            gzipOutputStream.write(buffer, 0, length);
        }

        gzipOutputStream.finish();
        fileInputStream.close();
        return new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
    }

    public InputStream compressFileWithZip(String filePath) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ZipOutputStream zipOutputStream = new ZipOutputStream(byteArrayOutputStream);

        File file = new File(filePath);
        FileInputStream fileInputStream = new FileInputStream(file);

        ZipEntry zipEntry = new ZipEntry(file.getName());
        zipOutputStream.putNextEntry(zipEntry);

        byte[] buffer = new byte[1024];
        int length;
        while ((length = fileInputStream.read(buffer)) != -1) {
            zipOutputStream.write(buffer, 0, length);
        }

        zipOutputStream.closeEntry();
        zipOutputStream.close();
        fileInputStream.close();

        return new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
    }


    @Override
    public List<FileData> findResourcesUploaded(String name) {
        return fileDAO.findAllByOwnersName(name);

    }


}
