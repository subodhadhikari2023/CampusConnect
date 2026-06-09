package com.bitsunisage.campusconnect.project.DataTransferObject;

import org.springframework.web.multipart.MultipartFile;

/**
 * Data Transfer Object that carries file upload form data from the teacher upload views
 * to {@link com.bitsunisage.campusconnect.project.service.StorageService#uploadToFileSystem}.
 *
 * <p>All IDs ({@code courseId}, {@code semesterId}, {@code subjectId}, {@code departmentId})
 * determine the filesystem path where the file is stored:
 * {@code {upload-dir}/{departmentId}/{fileRole}/{courseId}/{semesterId}/{subjectId}/}.</p>
 */
public class FileUploadDTO {

    private String fileRole;
    private MultipartFile file;
    private Long courseId;
    private Long semesterId;
    private Long subjectId;
    private Long departmentId;
    private String ownerDepartmentName;

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

    /** @return the uploaded multipart file */
    public MultipartFile getFile() {
        return file;
    }

    /** @param file the uploaded multipart file */
    public void setFile(MultipartFile file) {
        this.file = file;
    }

    /** @return ID of the course this file belongs to */
    public Long getCourseId() {
        return courseId;
    }

    /** @param courseId course ID */
    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    /** @return ID of the semester this file belongs to */
    public Long getSemesterId() {
        return semesterId;
    }

    /** @param semesterId semester ID */
    public void setSemesterId(Long semesterId) {
        this.semesterId = semesterId;
    }

    /** @return ID of the subject this file belongs to */
    public Long getSubjectId() {
        return subjectId;
    }

    /** @param subjectId subject ID */
    public void setSubjectId(Long subjectId) {
        this.subjectId = subjectId;
    }

    /** @return ID of the uploader's department */
    public Long getDepartmentId() {
        return departmentId;
    }

    /** @param departmentId department ID */
    public void setDepartmentId(Long departmentId) {
        this.departmentId = departmentId;
    }

    /** @return display name of the uploader's department */
    public String getOwnerDepartmentName() {
        return ownerDepartmentName;
    }

    /** @param ownerDepartmentName department display name */
    public void setOwnerDepartmentName(String ownerDepartmentName) {
        this.ownerDepartmentName = ownerDepartmentName;
    }

    @Override
    public String toString() {
        return "FileUploadDTO{" +
                "fileRole='" + fileRole + '\'' +
                ", file=" + file +
                ", courseId=" + courseId +
                ", semesterId=" + semesterId +
                ", subjectId=" + subjectId +
                ", departmentId=" + departmentId +
                ", ownerDepartmentName='" + ownerDepartmentName + '\'' +
                '}';
    }
}
