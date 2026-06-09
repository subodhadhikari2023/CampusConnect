package com.bitsunisage.campusconnect.project.dataAccessObject;

import com.bitsunisage.campusconnect.project.entities.Roles;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for {@link Roles} (the {@code roles} table).
 * Each row maps a single {@code user_id} to a Spring Security role string.
 */
@Repository
public interface RoleDAO extends JpaRepository<Roles, String> {

    /**
     * Looks up the role record for a given user.
     *
     * @param userId login username
     * @return the {@link Roles} record, or {@code null} if the user has no role entry
     */
    Roles findByUserId(String userId);

    /**
     * Returns all users that hold the specified role.
     *
     * @param role Spring Security role string, e.g. {@code "ROLE_STUDENT"}
     * @return list of matching {@link Roles} records; empty list if none
     */
    List<Roles> readRolesByRole(String role);
}
