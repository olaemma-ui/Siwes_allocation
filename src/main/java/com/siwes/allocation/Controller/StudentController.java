package com.siwes.allocation.Controller;

import com.siwes.allocation.Message.Response;
import com.siwes.allocation.Service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("siwes_allocation/api/student/")
public class StudentController {

    private final StudentService studentService;

    @Autowired
    StudentController(StudentService studentService){
        this.studentService = studentService;
    }

    @GetMapping(value="list")
    private ResponseEntity<Response> getStudentsByDept(@RequestParam String deptId, @RequestParam String sessionId, @RequestParam Integer pageNo, @RequestParam Integer pageSize){
        return studentService.getAllStudentsByDept(deptId, sessionId, pageNo, pageSize);
    }

    @GetMapping(value="status/list")
    private ResponseEntity<Response> getStudentByStatus(@RequestParam String deptId, @RequestParam String status, @RequestParam String sessionId, @RequestParam Integer pageNo, @RequestParam Integer pageSize){
        return studentService.getStudentByStatus(deptId, sessionId, status, pageNo, pageSize);
    }


    @PostMapping(value = "add")
    private ResponseEntity<Response> addStudents(@RequestBody Map<String, Object> students){
        return studentService.addStudent(students);
    }

    @PostMapping(value="allocate")
    private ResponseEntity<Response> allocateStudent(@RequestParam String sessionId,@RequestParam String departmentId, @RequestBody List<String> students, @RequestParam String siwesCenter){
        return studentService.allocateStudent(sessionId,departmentId, students, siwesCenter);
    }

    @DeleteMapping(value = "delete")
    private ResponseEntity<Response> deleteStudent(@RequestParam String studentId){
        return studentService.deleteStudent(studentId);
    }

}
