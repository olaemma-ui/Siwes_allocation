package com.siwes.allocation.Repository;

import com.siwes.allocation.Model.Student;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentRepo extends JpaRepository<Student, String> {
    @Query(value = "DELETE Student u WHERE u.department.id =:department AND u.session.id =:session")
    void deleteAllByDept(String department, String session);

    @Query(value = "SELECT u FROM Student u WHERE u.department.id =:deptId AND u.session.id =:sessionId")
    Page<Student> getStudentsByDeptAndSession(String deptId, String sessionId, Pageable pageable);

    @Query(value = "SELECT u FROM Student u WHERE u.department.id =:deptId AND u.session.id =:sessionId AND u.status =:status")
    Page<Student> getStudentByStatus(String deptId, String sessionId, String status, Pageable pageable);

    @Query(value = "SELECT u FROM Student u WHERE u.matricNo =:matricNo AND u.session.id =:session")
    Optional<Student> findByMatric(String matricNo, String session);

    @Transactional
    @Modifying
    @Query(value = "UPDATE Student u SET u.status=:status WHERE u.siwesCenter.id =:centerId")
    int updateStatus(String centerId, String status);
}
