package com.bitsunisage.campusconnect.project.dataAccessObject;

import com.bitsunisage.campusconnect.project.entities.Roles;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface RoleDAO extends JpaRepository<Roles, String> {
    //    While using the JpaRepository no need to define queries

    Roles findByUserId(String userId);

    List<Roles> readRolesByRole(String role);
//    void deleteUserById(String userId);
}
