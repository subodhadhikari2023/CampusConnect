package com.bitsunisage.campusconnect.project.entities;

import jakarta.persistence.*;

import java.sql.Timestamp;

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

    public String getFileRole() {
        return fileRole;
    }

    public void setFileRole(String fileRole) {
        this.fileRole = fileRole;
    }

    @Column(name = "file_role")
    private String fileRole;

    public Timestamp getUploadDate() {
        return uploadDate;
    }

    public void setUploadDate(Timestamp uploadDate) {
        this.uploadDate = uploadDate;
    }

    @Column(name = "uploaded_at" ,insertable = false,updatable = false)
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

    public void setSubjectId(Long subjectId) {
        this.subjectId = subjectId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public Long getFileDepartmentId() {
        return fileDepartmentId;
    }

    public void setFileDepartmentId(Long ownersDepartmentID) {
        this.fileDepartmentId = ownersDepartmentID;
    }

    public String getOwnersName() {
        return ownersName;
    }

    public void setOwnersName(String ownersName) {
        this.ownersName = ownersName;
    }


}
