package com.siwes.allocation.Controller;

import com.siwes.allocation.Message.Response;
import com.siwes.allocation.Model.Department;
import com.siwes.allocation.Service.DepartmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("siwes_allocation/api/department/")
public class DepartmentController {

    private final DepartmentService departmentService;

    @Autowired
    DepartmentController(DepartmentService departmentService){
        this.departmentService = departmentService;
    }

    @PostMapping(value = "add")
    private ResponseEntity<Response> addDept(@RequestBody Department department){
        return departmentService.addDept(department);
    }

    @PutMapping(value = "{id}/edit")
    private ResponseEntity<Response> editDept(@RequestBody Department department, @PathVariable String id){
        return departmentService.editDept(department, id);
    }

    @GetMapping(value = "list")
    private ResponseEntity<Response> getAllDept(@RequestParam Integer pageNo, @RequestParam Integer pageSize){
        return departmentService.getAllDept(pageNo, pageSize);
    }
}
