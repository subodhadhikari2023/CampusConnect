package com.bitsunisage.campusconnect.project.dataAccessObject;

import com.bitsunisage.campusconnect.project.entities.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DepartmentDAO extends JpaRepository<Department, String> {
    //    List<Department> findAllDepartment();
//    List<Department> findAll();
    Department findById(Integer id);

    Department findByName(String name);

    List<Department> findByIdIn(List<Long> ids);
}
