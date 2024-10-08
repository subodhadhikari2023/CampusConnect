package com.bitsunisage.campusconnect.project.dataAccessObject;

import com.bitsunisage.campusconnect.project.entities.Roles;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RoleDAO extends JpaRepository<Roles, String> {
    //    While using the JpaRepository no need to define queries

    Roles findByUserId(String userId);

    List<Roles> readRolesByRole(String role);
//    void deleteUserById(String userId);
}
