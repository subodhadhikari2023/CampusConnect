package com.bitsunisage.campusconnect.project.dataAccessObject;

import com.bitsunisage.campusconnect.project.entities.Department;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface DepartmentDAO extends JpaRepository<Department, String> {
    //    List<Department> findAllDepartment();
//    List<Department> findAll();
    Department findById(Integer id);

    Department findByName(String name);

    List<Department> findByIdIn(List<Long> ids);
}
