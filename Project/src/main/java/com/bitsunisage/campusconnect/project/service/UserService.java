package com.bitsunisage.campusconnect.project.service;

import com.bitsunisage.campusconnect.project.entities.Roles;
import com.bitsunisage.campusconnect.project.entities.User;

import java.util.List;

public interface UserService {
    List<User> findAllUsers();
    List<Roles> findAllRoles();

    User findById(int userId);

    User save(User user);

    void deleteUser(int userId);
}
