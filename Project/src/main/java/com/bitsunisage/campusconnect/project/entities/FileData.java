package com.bitsunisage.campusconnect.project.entities;

import jakarta.persistence.*;

import java.sql.Timestamp;

/**
 * JPA entity for the {@code file_data} table.
 * Stores metadata about an uploaded resource; the actual file bytes are written
 * to the local filesystem under {@code file.upload-dir} and accessed via {@code filePath}.
 */
@Entity
@Table(name = "file_data")
public class FileData {

    @Id
    @Column(name = "file_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "file_name")
    private String fileName;

    @Column(name = "file_path")
    private String filePath;

    @Column(name = "file_type")
    private String fileType;

    @Column(name = "file_size")
    private long fileSize;

    @Column(name = "file_role")
    private String fileRole;

    @Column(name = "uploaded_at", insertable = false, updatable = false)
    private Timestamp uploadDate;

    @Column(name = "uploader_department_id")
    private Long fileDepartmentId;

    @Column(name = "uploader_name")
    private String ownersName;

    @Column(name = "course_id")
    private Long courseId;

    @Column(name = "semester_id")
    private Long semesterId;

    @Column(name = "subject_id")
    private Long subjectId;

    /** @return numeric file record ID (primary key) */
    public Long getId() {
        return id;
    }

    /** @param id numeric file record ID */
    public void setId(Long id) {
        this.id = id;
    }

    /** @return original filename as stored on disk */
    public String getFileName() {
        return fileName;
    }

    /** @param fileName original filename */
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    /**
     * Returns the directory path where the file is stored on disk.
     * The full file location is {@code filePath + "/" + fileName}.
     *
     * @return absolute directory path on the server filesystem
     */
    public String getFilePath() {
        return filePath;
    }

    /** @param filePath absolute directory path */
    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    /** @return file extension (e.g. {@code ".pdf"}, {@code ".pptx"}) */
    public String getFileType() {
        return fileType;
    }

    /** @param fileType file extension */
    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    /** @return file size in bytes */
    public long getFileSize() {
        return fileSize;
    }

    /** @param fileSize file size in bytes */
    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    /**
     * Returns the resource category label used to organise files in the filesystem hierarchy.
     * Examples: {@code "PPTS"}, {@code "Notes"}, {@code "Programs"}, {@code "Videos"}.
     *
     * @return resource type label
     */
    public String getFileRole() {
        return fileRole;
    }

    /** @param fileRole resource type label */
    public void setFileRole(String fileRole) {
        this.fileRole = fileRole;
    }

    /** @return timestamp when the row was inserted; managed by the database default */
    public Timestamp getUploadDate() {
        return uploadDate;
    }

    /** @param uploadDate upload timestamp */
    public void setUploadDate(Timestamp uploadDate) {
        this.uploadDate = uploadDate;
    }

    /** @return department ID of the user who uploaded the file */
    public Long getFileDepartmentId() {
        return fileDepartmentId;
    }

    /** @param ownersDepartmentID department ID of the uploader */
    public void setFileDepartmentId(Long ownersDepartmentID) {
        this.fileDepartmentId = ownersDepartmentID;
    }

    /** @return login username of the user who uploaded the file */
    public String getOwnersName() {
        return ownersName;
    }

    /** @param ownersName login username of the uploader */
    public void setOwnersName(String ownersName) {
        this.ownersName = ownersName;
    }

    /** @return ID of the course this file is categorised under */
    public Long getCourseId() {
        return courseId;
    }

    /** @param courseId course ID */
    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    /** @return ID of the semester this file is categorised under */
    public Long getSemesterId() {
        return semesterId;
    }

    /** @param semesterId semester ID */
    public void setSemesterId(Long semesterId) {
        this.semesterId = semesterId;
    }

    /** @return ID of the subject this file is categorised under */
    public Long getSubjectId() {
        return subjectId;
    }

    /** @param subjectId subject ID */
    public void setSubjectId(Long subjectId) {
        this.subjectId = subjectId;
    }

    @Override
    public String toString() {
        return "FileData{" +
                "id=" + id +
                ", fileName='" + fileName + '\'' +
                ", filePath='" + filePath + '\'' +
                ", fileType='" + fileType + '\'' +
                ", fileSize=" + fileSize +
                ", ownersDepartmentID=" + fileDepartmentId +
                ", ownersName='" + ownersName + '\'' +
                ", courseId=" + courseId +
                ", semesterId=" + semesterId +
                ", subjectId=" + subjectId +
                '}';
    }
}
