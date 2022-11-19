package com.siwes.allocation.Service;

import com.siwes.allocation.Message.Response;
import com.siwes.allocation.Model.Student;
import com.siwes.allocation.Repository.DepartmentRepo;
import com.siwes.allocation.Repository.SessionRepo;
import com.siwes.allocation.Repository.SiwesCenterRepo;
import com.siwes.allocation.Repository.StudentRepo;
import com.siwes.allocation.Utils.Utils;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class StudentService {

    private Boolean success;
    private String code;
    private String message;
    private Object data;
    private Object error;

    private final StudentRepo studentRepo;
    private final SessionRepo sessionRepo;
    private final DepartmentRepo departmentRepo;
    private final Utils utils;
    private final SiwesCenterRepo siwesCenterRepo;

    @Autowired
    StudentService(StudentRepo studentRepo, SessionRepo sessionRepo, DepartmentRepo departmentRepo, Utils utils, SiwesCenterRepo siwesCenterRepo){
        this.studentRepo = studentRepo;
        this.sessionRepo = sessionRepo;
        this.departmentRepo = departmentRepo;
        this.utils = utils;
        this.siwesCenterRepo = siwesCenterRepo;
    }

    /**
     *
     * */
    public ResponseEntity<Response> getAllStudentsByDept(String deptId, String sessionId, Integer pageNo, Integer pageSize){
        reset();

        if (Optional.ofNullable(deptId).isPresent() && Optional.ofNullable(sessionId).isPresent()){
            try{
                message = "Invalid department!";
                departmentRepo.findById(deptId).ifPresent(
                    department->{
                        message = "Invalid session!";
                        sessionRepo.findById(sessionId).ifPresent(
                            session -> {
                                success(
                                        studentRepo.getStudentsByDeptAndSession(deptId, sessionId,
                                            PageRequest.of(
                                                    Optional.ofNullable(pageNo).orElse(0),
                                                    Optional.ofNullable(pageSize).orElse(0)
                                            )
                                        )
                                );
                            }
                        );
                    }
                );
            }catch (Exception e){
                code = "500";
                message = "Sorry an error occurred!";
                data = null;
            }
        }else message = "All parameters are required";

        return new ResponseEntity<>(new Response(success, code, message, data, error), HttpStatus.OK);
    }


    /**
     *
     * */
    public ResponseEntity<Response> getStudentByStatus(String deptId, String sessionId, String status, Integer pageNo, Integer pageSize){
        reset();

        if (Optional.ofNullable(deptId).isPresent() && Optional.ofNullable(sessionId).isPresent() && Optional.ofNullable(status).isPresent()){
            try{
                message = "Invalid department!";
                departmentRepo.findById(deptId).ifPresent(
                        department->{
                            message = "Invalid session!";
                            sessionRepo.findById(sessionId).ifPresent(
                                    session -> {
                                        success(
                                                studentRepo.getStudentByStatus(deptId, sessionId, status,
                                                        PageRequest.of(
                                                                Optional.ofNullable(pageNo).orElse(0),
                                                                Optional.ofNullable(pageSize).orElse(0)
                                                        )
                                                )
                                        );
                                    }
                            );
                        }
                );
            }catch (Exception e){
                code = "500";
                message = "Sorry an error occurred!";
                data = null;
            }
        }else message = "All parameters are required";

        return new ResponseEntity<>(new Response(success, code, message, data, error), HttpStatus.OK);
    }


    /**
     *
     * */
    public ResponseEntity<Response> addStudent(Map<String, Object> student) {
        reset();

        if (student.containsKey("matricNo") && student.containsKey("sessionId") && student.containsKey("departmentId")){
            Object[] validate = utils.validate(student, new String[]{});
            error = validate[1];
            data = student;

            try {
                message = "Invalid session!";
                sessionRepo.findById(student.get("sessionId").toString()).ifPresent(
                        session->{
                            message = "Invalid department!";
                            departmentRepo.findById(student.get("departmentId").toString()).ifPresent(
                                    dept->{

                                        if(!studentRepo.findByMatric(student.get("matricNo").toString(), session.getId()).isPresent()){
                                            Student studentData = new Student();

                                            studentData.setSession(session);
                                            studentData.setDepartment(dept);
                                            studentData.setStatus("PENDING");
                                            studentData.setCreatedAt(Timestamp.valueOf(LocalDateTime.now()));

                                            studentRepo.save(studentData);
                                        }else message = "Students already exist!";

                                    }
                            );
                        }
                );
            }catch (Exception e){
                code = "500";
                message = "Sorry an error occurred";
                e.printStackTrace();
            }
        }
        else message = "All fields are required!";

        return new ResponseEntity<>(new Response(success, code, message, data, error), HttpStatus.OK);
    }


    public ResponseEntity<Response> allocateStudent(String sessionId, String departmentId, List<String> students, String siwesCenter){
        reset();
        message = "Session is required!";
        data = students;
        Map<String, Object> failedStd = new HashMap<>();
        error = failedStd;
        Optional.ofNullable(sessionId).ifPresent(
            sesId->{
                message = "Department is required";
                Optional.ofNullable(departmentId).ifPresent(
                    deptId->{
                           message = "SIWES center is required!";
                       Optional.ofNullable(siwesCenter).ifPresent(
                           center->{
                               try{
                                 message = "Invalid SIWES center";
                                 siwesCenterRepo.findById(siwesCenter).ifPresent(
                                     siwes->{
                                         message = "Invalid session!";
                                         sessionRepo.findById(sessionId).ifPresent(
                                             session->{
                                                 message = "Invalid department!";
                                                 departmentRepo.findById(deptId).ifPresent(
                                                     department -> {
                                                         students.forEach(
                                                             studentId->{
                                                                 failedStd.put(studentId, "Invalid Student ID");
                                                                 studentRepo.findById(studentId).ifPresent(
                                                                     student -> {
                                                                         failedStd.replace(studentId, null);
                                                                         if (student.getStatus().equalsIgnoreCase("PENDING")){
                                                                             student.setSiwesCenter(siwes);
                                                                             student.setStatus("ALLOCATED");
                                                                             studentRepo.save(student);
                                                                             success(data);
                                                                         }else failedStd.replace(studentId, "Student already allocated to a SIWES center!");
                                                                     }
                                                                 );
                                                             }
                                                         );
                                                     }
                                                 );
                                             }
                                         );
                                     }
                                 );
                               }catch(Exception e){
                                   code = "500";
                                   message = "Sorry an error occurred!";
                                   e.printStackTrace();
                               }
                           }
                       );
                    }
                );
            }
        );
        return new ResponseEntity<>(new Response(success, code, message, data, error), HttpStatus.OK);
    }


    public ResponseEntity<Response> deleteStudent(String studentId){
        reset();
        message = "Student ID required!";
        Optional.ofNullable(studentId).ifPresent(
                student->{
                    try{
                        message = "Invalid student ID";
                        studentRepo.findById(studentId).ifPresent(
                                id->{
                                    studentRepo.deleteById(studentId);
                                    success(null);
                                }
                        );
                    }catch (Exception e){
                        code = "500";
                        message = "Sorry an error occurred!";
                        e.printStackTrace();
                    }
                }
        );
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
