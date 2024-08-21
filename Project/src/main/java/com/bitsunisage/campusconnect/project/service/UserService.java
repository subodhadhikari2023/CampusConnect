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
    User findUserByUserId(String userId);

    //    Allows to save the user
    Roles findRoleByUserId(String userId);

    User save(User user);

    Roles save(Roles roles);

    //    Delete the user
    void deleteUser(User user);
    void deleteRole(Roles roles);

    //    Returns an integer value that stores the total number of users present irrespective of roles
    Integer totalUsers();

    // Fetches the total number of users based on the role passed
    Integer totalUsers(String role);
// Fetches the list of users based on the role passed

    List<Roles> findByRole(String role);


}
