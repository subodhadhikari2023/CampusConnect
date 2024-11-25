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

    @Column(name = "uploader_department")
    private int ownersDepartmentID;


    @Column(name = "uploader_name")
    private String ownersName;

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
                '}';
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
}
