package com.bitsunisage.campusconnect.project.entities.Files;

import jakarta.persistence.*;

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

    @Column(name = "uploader_department_id")
    private int ownersDepartmentID;


    @Column(name = "uploader_name")
    private String ownersName;

    @Column(name = "course_id")
    private int courseId;

    @Column(name = "semester_id")
    private int semesterId;

    @Column(name = "subject_id")
    private int subjectId;

    public int getCourseId() {
        return courseId;
    }

    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }

    public int getSemesterId() {
        return semesterId;
    }

    public void setSemesterId(int semesterId) {
        this.semesterId = semesterId;
    }

    public int getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(int subjectId) {
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

    public int getOwnersDepartmentID() {
        return ownersDepartmentID;
    }

    public void setOwnersDepartmentID(int ownersDepartmentID) {
        this.ownersDepartmentID = ownersDepartmentID;
    }

    public String getOwnersName() {
        return ownersName;
    }

    public void setOwnersName(String ownersName) {
        this.ownersName = ownersName;
    }

    @Override
    public String toString() {
        return "FileData{" +
                "id=" + id +
                ", fileName='" + fileName + '\'' +
                ", filePath='" + filePath + '\'' +
                ", fileType='" + fileType + '\'' +
                ", fileSize=" + fileSize +
                ", ownersDepartmentID=" + ownersDepartmentID +
                ", ownersName='" + ownersName + '\'' +
                ", courseId=" + courseId +
                ", semesterId=" + semesterId +
                ", subjectId=" + subjectId +
                '}';
    }
}
