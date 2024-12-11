package com.bitsunisage.campusconnect.project.dataAccessObject;

import com.bitsunisage.campusconnect.project.entities.FileData;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FileDAO extends JpaRepository<FileData,Long> {
    List<FileData> findAllByOwnersName(String name);

//    List<FileData> findAllBy
}
