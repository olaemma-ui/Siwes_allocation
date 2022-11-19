package com.siwes.allocation.Repository;

import com.siwes.allocation.Model.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DepartmentRepo extends JpaRepository<Department, String> {

    @Query(value = "SELECT u FROM Department u WHERE u.name =:name AND u.id <>:id")
    Optional<Department> findByName(String name, String id);
}
