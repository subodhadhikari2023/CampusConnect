package com.bitsunisage.campusconnect.repository;

import com.bitsunisage.campusconnect.entities.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for {@link Department} (the {@code department} table).
 */
@Repository
public interface DepartmentDAO extends JpaRepository<Department, String> {

    /**
     * Looks up a department by its numeric primary key.
     *
     * @param id numeric department ID
     * @return the matching {@link Department}, or {@code null} if not found
     */
    Department findById(Integer id);

    /**
     * Looks up a department by its unique name.
     *
     * @param name the department name
     * @return the matching {@link Department}, or {@code null} if not found
     */
    Department findByName(String name);

    /**
     * Returns all departments whose IDs are in the given list.
     *
     * @param ids list of department IDs to fetch
     * @return list of matching {@link Department} entities; may be empty
     */
    List<Department> findByIdIn(List<Long> ids);
}
