package com.bitsunisage.campusconnect.project.DataTransferObject;


import org.springframework.web.multipart.MultipartFile;

public class FileUploadDTO {
    private MultipartFile file;
    private Long courseId;
    private Long semesterId;
    private Long subjectId;
    private Long departmentId;
    private String ownerDepartmentName;


    public MultipartFile getFile() {
        return file;
    }

    public void setFile(MultipartFile file) {
        this.file = file;
    }

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    public Long getSemesterId() {
        return semesterId;
    }

    public void setSemesterId(Long semesterId) {
        this.semesterId = semesterId;
    }

    public Long getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(Long subjectId) {
        this.subjectId = subjectId;
    }

    public String getOwnerDepartmentName() {
        return ownerDepartmentName;
    }

    public void setOwnerDepartmentName(String ownerDepartmentName) {
        this.ownerDepartmentName = ownerDepartmentName;
    }

    public Long getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(Long departmentId) {
        this.departmentId = departmentId;
    }
}
