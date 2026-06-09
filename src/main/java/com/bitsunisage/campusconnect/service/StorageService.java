package com.bitsunisage.campusconnect.service;

import com.bitsunisage.campusconnect.dto.FileUploadDTO;
import com.bitsunisage.campusconnect.entities.Department;
import com.bitsunisage.campusconnect.entities.FileData;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;

/**
 * Service interface for file upload, retrieval, download, and deletion.
 * Files are stored on the local filesystem; this service manages both the
 * filesystem writes and the corresponding {@link FileData} metadata rows.
 * Implemented by {@link StorageServiceImplementation}.
 */
public interface StorageService {

    /**
     * Saves a file to the local filesystem and persists its metadata to the database.
     * The destination path is derived from the DTO's IDs:
     * {@code {upload-dir}/{departmentId}/{fileRole}/{courseId}/{semesterId}/{subjectId}/}.
     *
     * @param fileUploadDTO form data including the file and its classification IDs
     * @throws IOException if the file cannot be written to disk
     */
    void uploadToFileSystem(FileUploadDTO fileUploadDTO) throws IOException;

    /**
     * Returns all files uploaded by a specific user.
     *
     * @param name login username of the uploader
     * @return list of that user's uploaded files; empty list if none
     */
    List<FileData> findResourcesUploaded(String name);

    /**
     * Returns the login username of the currently authenticated user.
     * Reads from {@link org.springframework.security.core.context.SecurityContextHolder}.
     *
     * @return username of the authenticated user, or {@code null} if unauthenticated
     */
    String getCurrentOwnersName();

    /**
     * Deletes both the physical file on disk and its metadata record.
     * Throws if no record exists for the given ID.
     *
     * @param id the {@link FileData} primary key
     * @throws jakarta.persistence.EntityNotFoundException if no file with that ID exists
     */
    void deleteResource(Long id);

    /**
     * Returns all file metadata records in the database.
     *
     * @return list of all {@link FileData} records; empty list if none
     */
    List<FileData> findAll();

    /**
     * Returns departments matching the given list of IDs.
     *
     * @param departmentIds list of department IDs to fetch
     * @return list of matching {@link Department} entities; may be empty
     */
    List<Department> findAllDepartment(List<Long> departmentIds);

    /**
     * Returns files matching all five filter criteria simultaneously.
     *
     * @param departmentId uploader's department ID
     * @param courseId     course ID
     * @param semesterId   semester ID
     * @param subjectId    subject ID
     * @param fileRole     resource type label (e.g. {@code "PPTS"}, {@code "Notes"})
     * @return list of matching {@link FileData} records; may be empty
     */
    List<FileData> findFilesByFilters(Long departmentId, Long courseId, Long semesterId,
                                      Long subjectId, String fileRole);

    /**
     * Reads a file from disk and returns a GZIP-compressed stream of its contents.
     * The entire compressed output is buffered in memory before being returned.
     *
     * @param filePath absolute path to the source file on disk
     * @return an {@link InputStream} of the gzip-compressed file bytes
     * @throws IOException if the file cannot be read or compressed
     */
    InputStream compressFileWithGzip(String filePath) throws IOException;

    /**
     * Reads a file from disk and returns a ZIP-compressed stream of its contents.
     * The entire compressed output is buffered in memory before being returned.
     *
     * @param filePath absolute path to the source file on disk
     * @return an {@link InputStream} of the zip-compressed file bytes
     * @throws IOException if the file cannot be read or compressed
     */
    InputStream compressFileWithZip(String filePath) throws IOException;

    /**
     * Looks up a file metadata record by its primary key.
     *
     * @param fileId the {@link FileData} primary key
     * @return an {@link Optional} containing the record, or empty if not found
     */
    Optional<FileData> getFileById(Long fileId);
}
