package com.bitsunisage.campusconnect.service;

import com.bitsunisage.campusconnect.dto.FileUploadDTO;
import com.bitsunisage.campusconnect.repository.DepartmentDAO;
import com.bitsunisage.campusconnect.repository.FileDAO;
import com.bitsunisage.campusconnect.entities.Department;
import com.bitsunisage.campusconnect.entities.FileData;
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

/**
 * {@link StorageService} implementation that stores files on the local filesystem.
 *
 * <p>The base upload directory is injected from {@code file.upload-dir} in
 * {@code application.properties}. Within that directory, files are organised as:
 * {@code {departmentId}/{fileRole}/{courseId}/{semesterId}/{subjectId}/filename}.</p>
 */
@Service
public class StorageServiceImplementation implements StorageService {

    private final FileDAO fileDAO;
    private final DepartmentDAO departmentDAO;

    @Value("${file.upload-dir}")
    private String uploadDir;

    /**
     * @param fileDAO       repository for file metadata records
     * @param departmentDAO repository for department lookups
     */
    @Autowired
    StorageServiceImplementation(FileDAO fileDAO, DepartmentDAO departmentDAO) {
        this.fileDAO = fileDAO;
        this.departmentDAO = departmentDAO;
    }

    /**
     * Writes the uploaded file to disk and saves its metadata row.
     * Intermediate directories are created if they do not already exist.
     * Exceptions during file I/O are caught and logged; the metadata row is
     * only saved if the file transfer succeeds.
     *
     * @param fileUploadDTO form data with the file and its classification IDs
     */
    @Transactional
    @Override
    public void uploadToFileSystem(FileUploadDTO fileUploadDTO) {
        MultipartFile file = fileUploadDTO.getFile();

        String filePath = uploadDir + "/" + fileUploadDTO.getDepartmentId() + "/"
                + fileUploadDTO.getFileRole() + "/" + fileUploadDTO.getCourseId() + "/"
                + fileUploadDTO.getSemesterId() + "/" + fileUploadDTO.getSubjectId();
        try {
            String fileExtension = Objects.requireNonNull(file.getOriginalFilename())
                    .substring(file.getOriginalFilename().lastIndexOf("."));
            new java.io.File(filePath).mkdirs();
            file.transferTo(Path.of(filePath + "/" + file.getOriginalFilename()));

            FileData fileData = new FileData();
            fileData.setFileName(file.getOriginalFilename());
            fileData.setFilePath(filePath);
            fileData.setFileType(fileExtension);
            fileData.setFileSize(file.getSize());
            fileData.setOwnersName(getCurrentOwnersName());
            fileData.setFileDepartmentId(fileUploadDTO.getDepartmentId());
            fileData.setCourseId(fileUploadDTO.getCourseId());
            fileData.setSemesterId(fileUploadDTO.getSemesterId());
            fileData.setSubjectId(fileUploadDTO.getSubjectId());
            fileData.setFileRole(fileUploadDTO.getFileRole());
            fileDAO.save(fileData);
        } catch (Exception e) {
            System.out.println("Some exception occurred!!!");
        }
    }

    /**
     * Returns the login username of the currently authenticated user by reading
     * from {@link SecurityContextHolder}.
     *
     * @return username string, or {@code null} if no authenticated principal is present
     */
    @Override
    public String getCurrentOwnersName() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            return authentication.getName();
        }
        return null;
    }

    /**
     * Deletes the physical file from disk and removes the metadata row from the database.
     *
     * @param id the {@link FileData} primary key
     * @throws EntityNotFoundException if no record exists for the given ID
     */
    @Override
    public void deleteResource(Long id) {
        FileData fileData = fileDAO.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("File not found with ID: " + id));
        File file = new File(fileData.getFilePath() + "/" + fileData.getFileName());
        if (file.exists()) {
            file.delete();
        }
        fileDAO.deleteById(id);
    }

    /** {@inheritDoc} */
    @Override
    public List<FileData> findAll() {
        return fileDAO.findAll();
    }

    /** {@inheritDoc} */
    @Override
    public List<Department> findAllDepartment(List<Long> departmentIds) {
        return departmentDAO.findByIdIn(departmentIds);
    }

    /** {@inheritDoc} */
    @Override
    public List<FileData> findFilesByFilters(Long departmentId, Long courseId,
                                             Long semesterId, Long subjectId,
                                             String fileRole) {
        return fileDAO.findFilesByFilters(departmentId, courseId, semesterId, subjectId, fileRole);
    }

    /** {@inheritDoc} */
    @Override
    public Optional<FileData> getFileById(Long fileId) {
        return fileDAO.findById(fileId);
    }

    /**
     * Reads the file at {@code filePath} and returns a GZIP-compressed stream.
     * The full compressed output is buffered in memory before being returned.
     *
     * @param filePath absolute path to the source file
     * @return {@link InputStream} of gzip-compressed bytes
     * @throws IOException if the file cannot be read or the compressor cannot be initialised
     */
    @Override
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

    /**
     * Reads the file at {@code filePath} and returns a ZIP-compressed stream.
     * The full compressed output is buffered in memory before being returned.
     *
     * @param filePath absolute path to the source file
     * @return {@link InputStream} of zip-compressed bytes
     * @throws IOException if the file cannot be read or the compressor cannot be initialised
     */
    @Override
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

    /** {@inheritDoc} */
    @Override
    public List<FileData> findResourcesUploaded(String name) {
        return fileDAO.findAllByOwnersName(name);
    }
}
