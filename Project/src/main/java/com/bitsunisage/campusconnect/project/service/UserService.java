package com.bitsunisage.campusconnect.project.service;

import com.bitsunisage.campusconnect.project.entities.Roles;
import com.bitsunisage.campusconnect.project.entities.User;

import java.util.List;

public interface UserService {
//    Returns the list of all the users
    List<User> findAllUsers();
//    Returns the list of all the roles
    List<Roles> findAllRoles();
//    Allows to find the user by id
    User findById(int userId);
//    Allows to save the user
    User save(User user);
//    Delete the user
    void deleteUser(int userId);
//    Returns an integer value that stores the total number of users present irrespective of roles
    Integer totalUsers();
//
    Integer totalStudents(String role);

    Integer totalTeachers();


}
