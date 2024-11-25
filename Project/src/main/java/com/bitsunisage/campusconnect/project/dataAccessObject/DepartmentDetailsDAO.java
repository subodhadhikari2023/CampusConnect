package com.bitsunisage.campusconnect.project.dataAccessObject;

import com.bitsunisage.campusconnect.project.entities.DepartmentDetails;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DepartmentDetailsDAO extends JpaRepository<DepartmentDetails, Integer> {
    DepartmentDetails getDepartmentIdByUserName(String userName);


}
