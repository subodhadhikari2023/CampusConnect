package com.bitsunisage.campusconnect.project.dataAccessObject;

import com.bitsunisage.campusconnect.project.entities.FileData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FileDAO extends JpaRepository<FileData, Long> {
    List<FileData> findAllByOwnersName(String name);

    List<FileData> findAllByIdIn(List<Long> ids);
//    List<FileData> findAllBy

    @Query("SELECT f FROM FileData f WHERE "
            + "(f.fileDepartmentId = :departmentId) AND "
            + "( f.courseId = :courseId) AND "
            + "( f.semesterId = :semesterId) AND "
            + "(f.subjectId = :subjectId) AND "
            + "f.fileRole=:fileRole"  )
    List<FileData> findFilesByFilters(@Param("departmentId") Long departmentId,
                                      @Param("courseId") Long courseId,
                                      @Param("semesterId") Long semesterId,
                                      @Param("subjectId") Long subjectId,
                                      @Param("fileRole") String fileRole);
}
