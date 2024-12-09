package com.bitsunisage.campusconnect.project.dataAccessObject;

import com.bitsunisage.campusconnect.project.entities.FileData;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FileDAO extends JpaRepository<FileData,Long> {
}
