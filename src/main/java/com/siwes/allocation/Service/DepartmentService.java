package com.siwes.allocation.Service;

import com.siwes.allocation.Message.Response;
import com.siwes.allocation.Model.Department;
import com.siwes.allocation.Model.Session;
import com.siwes.allocation.Model.Student;
import com.siwes.allocation.Repository.DepartmentRepo;
import com.siwes.allocation.Repository.SessionRepo;
import com.siwes.allocation.Repository.StudentRepo;
import com.siwes.allocation.Utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.swing.text.html.Option;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class DepartmentService {

    private Boolean success;
    private String code;
    private String message;
    private Object data;
    private Object error;

    private final Utils utils;
    private final  DepartmentRepo departmentRepo;
    private final SessionRepo sessionRepo;
    private final StudentRepo studentRepo;

    @Autowired
    DepartmentService(Utils utils, DepartmentRepo departmentRepo, SessionRepo sessionRepo, StudentRepo studentRepo){
        this.utils = utils;
        this.departmentRepo = departmentRepo;
        this.sessionRepo = sessionRepo;
        this.studentRepo = studentRepo;
    }

    /**
     * @param department The department details to be added
     * */
    public ResponseEntity<Response> addDept(Department department){
        reset();
        Object[] validate = utils.validate(department, new String[]{"createdAt", "updatedAt", "allocated", "notAllocated", "id", "session"});
        error = validate[1];
        data = department;

        if (Boolean.parseBoolean(validate[0].toString())){
            try {
                if (department.getTotalStudent() > 0){
                    message = "Invalid session!";
                    sessionRepo.findById(department.getSessionId()).ifPresent(
                        e->{
                            if(!departmentRepo.findByName(department.getName(), department.getSessionId()).isPresent()){

                                department.setId(UUID.randomUUID().toString());
                                department.setCreatedAt(Timestamp.valueOf(LocalDateTime.now()));
                                department.setUpdatedAt(Timestamp.valueOf(LocalDateTime.now()));
                                department.setSession(e);
                                departmentRepo.save(department);

                                List<Student> students = new ArrayList<>();
                                for (int j = 1; j <= department.getTotalStudent(); j++) {
                                    Student student = new Student();
                                    student.setCreatedAt(Timestamp.valueOf(LocalDateTime.now()));
                                    student.setSession(e);
                                    student.setDepartment(department);
                                    student.setMatricNo(j);
                                    student.setStatus("PENDING");

                                    students.add(student);
                                }
                                studentRepo.saveAll(students);
                                success(data);
                            }else message = "Department already exist for session";
                        }
                    );

                }else message = "Invalid session";

            }catch (Exception e){
                e.printStackTrace();
                data = null;
                code = "500";
                message = "Sorry an error occurred !!!";
            }
        }
        else message = "Invalid fields/parameters";

        return new ResponseEntity<>(new Response(success, code, message, data, error), HttpStatus.OK);
    }


    /**
     *
     * */
    public ResponseEntity<Response> editDept(Department department, String id){
        reset();
        Object[] validate = utils.validate(department, new String[]{"updatedAt", "allocated", "notAllocated", "session", "totalStudent"});
        error = validate[1];
        data = department;

        if (Boolean.parseBoolean(validate[0].toString())){
            try {
                if (department.getTotalStudent() > 0){
                    message = "Invalid department";
                    departmentRepo.findById(id).ifPresent(
                            dep->{
                                message = "Invalid session!";
                                sessionRepo.findById(department.getSessionId()).ifPresent(
                                        e->{
                                            if(!departmentRepo.findByName(department.getName(), department.getSessionId()).isPresent()){
                                                dep.setUpdatedAt(Timestamp.valueOf(LocalDateTime.now()));
                                                dep.setSession(e);
                                                departmentRepo.save(department);
                                                success(data);
                                            }else message = "Department already exist for session";
                                        }
                                );
                            }
                    );
                }else message = "Invalid session";

            }catch (Exception e){
                e.printStackTrace();
                data = null;
                code = "500";
                message = "Sorry an error occurred !!!";
            }
        }
        else message = "Invalid fields/parameters";

        return new ResponseEntity<>(new Response(success, code, message, data, error), HttpStatus.OK);
    }


    /**
     * Get all department
     * */
    public ResponseEntity<Response> getAllDept(Integer pageNo, Integer pageSize){
        reset();
        try {
            success(departmentRepo.findAll(
                PageRequest.of(
                    Optional.of(pageNo).orElse(0),
                    Optional.of(pageSize).orElse(10),
                    Sort.by(Sort.Direction.DESC, "createdAt")
                )
            ));
        }
        catch (Exception e){
            e.printStackTrace();
            data = null;
            code = "500";
            message = "Sorry an error occurred";
        }
        return new ResponseEntity<>(new Response(success, code, message, data, error), HttpStatus.OK);
    }


    public ResponseEntity<Response> deleteDept(String dept, String session){
        reset();

        if (Optional.ofNullable(dept).isPresent() && Optional.ofNullable(session).isPresent()){
            try{
                message = "Department does not exist.";
                departmentRepo.findById(dept).ifPresent(
                    d->{
                        message = "Invalid session!";
                        sessionRepo.findById(session).ifPresent(
                            s->{
                                departmentRepo.deleteById(dept);
                                studentRepo.deleteAllByDept(dept, session);
                                success(null);
                            }
                        );
                    }
                );
            }catch (Exception e){
                code = "500";
                message = "Sorry an error occurred!";
            }
        }else message = "All parameters are required";






        return new ResponseEntity<>(new Response(success, code, message, data, error), HttpStatus.OK);
    }

    private void reset(){
        success = false;
        code = "99";
        message = "FAILED";
        data = null;
        error = null;
    }
    private void success(Object data){
        message = "Success";
        success = true;
        code = "100";
        this.data = data;
    }
}
