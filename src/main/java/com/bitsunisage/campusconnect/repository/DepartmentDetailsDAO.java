package com.bitsunisage.campusconnect.repository;

import com.bitsunisage.campusconnect.entities.DepartmentDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

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

    /**
     * Counts members in a department that hold the given role label.
     *
     * @param departmentId department primary key
     * @param role         role label as stored in this table (e.g. {@code "TEACHER"}, {@code "STUDENT"})
     * @return count of matching rows
     */
    int countByDepartmentIdAndRole(Integer departmentId, String role);

    /**
     * Deletes the department-details record for a given user.
     * Called before removing a user to satisfy FK constraints.
     *
     * @param userName login username
     */
    void deleteByUserName(String userName);

    /**
     * Counts all members assigned to the given department regardless of role.
     *
     * @param departmentId department primary key
     * @return count of members in that department
     */
    int countByDepartmentId(int departmentId);

    /**
     * Returns all membership records in a department for the given role label.
     *
     * @param departmentId department primary key
     * @param role         role label as stored in this table (e.g. {@code "TEACHER"}, {@code "STUDENT"})
     * @return list of matching {@link DepartmentDetails}; empty if none
     */
    List<DepartmentDetails> findByDepartmentIdAndRole(Integer departmentId, String role);
}
