package com.bitsunisage.campusconnect.project.dataAccessObject;

import com.bitsunisage.campusconnect.project.entities.DepartmentDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository for {@link DepartmentDetails} (the {@code department_details} table),
 * which maps each member to a department and records their role label (HOD, TEACHER, STUDENT, ADMIN).
 */
@Repository
public interface DepartmentDetailsDAO extends JpaRepository<DepartmentDetails, Integer> {

    /**
     * Returns the department-details record for a given user.
     *
     * @param userName login username
     * @return the matching {@link DepartmentDetails}, or {@code null} if not found
     */
    DepartmentDetails getDepartmentIdByUserName(String userName);
}
