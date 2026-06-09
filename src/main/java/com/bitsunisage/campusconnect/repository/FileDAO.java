package com.bitsunisage.campusconnect.repository;

import com.bitsunisage.campusconnect.entities.FileData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for {@link FileData} (the {@code file_data} table).
 * Stores uploaded file metadata; actual file bytes live on the local filesystem
 * under the path configured by {@code file.upload-dir}.
 */
@Repository
public interface FileDAO extends JpaRepository<FileData, Long> {

    /**
     * Returns all files uploaded by the given user.
     *
     * @param name login username of the uploader
     * @return list of the user's uploaded files; empty list if none
     */
    List<FileData> findAllByOwnersName(String name);

    /**
     * Returns all files whose IDs are in the given list.
     *
     * @param ids list of file IDs to fetch
     * @return list of matching {@link FileData} records; may be empty
     */
    List<FileData> findAllByIdIn(List<Long> ids);

    /**
     * Finds files that match all five filter criteria simultaneously.
     * All parameters are required; passing {@code null} for any will return no results.
     *
     * @param departmentId uploader's department ID
     * @param courseId     course the file belongs to
     * @param semesterId   semester the file belongs to
     * @param subjectId    subject the file belongs to
     * @param fileRole     resource type label (e.g. {@code "PPTS"}, {@code "Notes"})
     * @return list of matching {@link FileData} records; may be empty
     */
    @Query("SELECT f FROM FileData f WHERE "
            + "(f.fileDepartmentId = :departmentId) AND "
            + "( f.courseId = :courseId) AND "
            + "( f.semesterId = :semesterId) AND "
            + "(f.subjectId = :subjectId) AND "
            + "f.fileRole=:fileRole")
    List<FileData> findFilesByFilters(@Param("departmentId") Long departmentId,
                                      @Param("courseId") Long courseId,
                                      @Param("semesterId") Long semesterId,
                                      @Param("subjectId") Long subjectId,
                                      @Param("fileRole") String fileRole);

    /**
     * Looks up a file by its primary key.
     *
     * @param id file primary key
     * @return an {@link Optional} containing the file, or empty if not found
     */
    Optional<FileData> findById(Long id);
}
