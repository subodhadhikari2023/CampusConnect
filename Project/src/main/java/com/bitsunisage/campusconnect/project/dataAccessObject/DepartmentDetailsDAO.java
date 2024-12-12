package com.bitsunisage.campusconnect.project.dataAccessObject;

import com.bitsunisage.campusconnect.project.entities.DepartmentDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DepartmentDetailsDAO extends JpaRepository<DepartmentDetails, Integer> {
    DepartmentDetails getDepartmentIdByUserName(String userName);


}
