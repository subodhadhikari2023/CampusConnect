package com.bitsunisage.campusconnect.repository;

import com.bitsunisage.campusconnect.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for {@link User} (the {@code members} table).
 * Inherits standard CRUD operations from {@link JpaRepository}.
 */
@Repository
public interface UserDAO extends JpaRepository<User, String> {

    /**
     * Looks up a user by their login username.
     *
     * @param userId login username ({@code members.user_id})
     * @return the matching {@link User}, or {@code null} if none found
     */
    User findByUserId(String userId);

    /**
     * Returns all users whose login usernames are in the given list.
     * Used to resolve department-member usernames into full {@link User} records.
     *
     * @param userIds list of login usernames
     * @return list of matching {@link User} records; may be empty
     */
    List<User> findByUserIdIn(List<String> userIds);
}
