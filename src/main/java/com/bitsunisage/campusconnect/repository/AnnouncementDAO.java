package com.bitsunisage.campusconnect.repository;

import com.bitsunisage.campusconnect.entities.Announcement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for {@link Announcement} (the {@code announcements} table).
 * Announcements are scoped to a single department and authored by an HOD.
 */
@Repository
public interface AnnouncementDAO extends JpaRepository<Announcement, Long> {

    /**
     * Returns all announcements for the given department, newest first.
     *
     * @param deptId the department primary key
     * @return list of announcements; empty if none posted
     */
    List<Announcement> findByDeptIdOrderByCreatedAtDesc(Long deptId);
}
