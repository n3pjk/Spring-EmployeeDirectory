package io.zipcoder.persistenceapp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/API/department")
public class DepartmentController {
    // Change the name of a department
    @PutMapping("/{departmentNumber}/name")
    public ResponseEntity<Department> updateDepartmentName(@PathVariable Long departmentNumber, @RequestParam String departmentName) {
        Department department = departmentRepository.findOne(departmentNumber);
        if (department == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        department.setDepartmentName(departmentName);
        Department updated = departmentRepository.save(department);
        return new ResponseEntity<>(updated, HttpStatus.OK);
    }
    // Set a new department manager
    @PutMapping("/{departmentNumber}/manager")
    public ResponseEntity<Department> updateDepartmentManager(@PathVariable Long departmentNumber, @RequestParam Long managerId) {
        Department department = departmentRepository.findOne(departmentNumber);
        if (department == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        department.setManagerId(managerId);
        Department updated = departmentRepository.save(department);
        return new ResponseEntity<>(updated, HttpStatus.OK);
    }

    @Autowired
    private DepartmentRepository departmentRepository;

    @PostMapping
    public ResponseEntity<Department> createDepartment(@RequestBody Department department) {
        Department savedDepartment = departmentRepository.save(department);
        return new ResponseEntity<>(savedDepartment, HttpStatus.CREATED);
    }
}
