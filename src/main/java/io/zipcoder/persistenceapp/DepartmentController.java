package io.zipcoder.persistenceapp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/API/department")
public class DepartmentController {
    // Merge departments: move manager of B to report to manager of A, update all employees to be members of department A
    @PutMapping("/merge")
    public ResponseEntity<String> mergeDepartments(@RequestParam String deptA, @RequestParam String deptB) {
        // Find departments by name
        Department departmentA = null;
        Department departmentB = null;
        for (Department d : departmentRepository.findAll()) {
            if (deptA.equals(d.getDepartmentName())) departmentA = d;
            if (deptB.equals(d.getDepartmentName())) departmentB = d;
        }
        if (departmentA == null || departmentB == null) {
            return new ResponseEntity<>("One or both departments not found", HttpStatus.NOT_FOUND);
        }
        // Move manager of B to report to manager of A
        Long managerAId = departmentA.getManagerId();
        Long managerBId = departmentB.getManagerId();
        if (managerAId == null || managerBId == null) {
            return new ResponseEntity<>("One or both departments do not have a manager", HttpStatus.BAD_REQUEST);
        }
        Employee managerB = employeeRepository.findOne(managerBId);
        if (managerB == null) {
            return new ResponseEntity<>("Manager of department B not found", HttpStatus.NOT_FOUND);
        }
        managerB.setManagerId(managerAId);
        managerB.setDepartmentNumber(departmentA.getDepartmentNumber());
        employeeRepository.save(managerB);

        // Update all employees in B to be in department A
        Iterable<Employee> employeesInB = employeeRepository.findByDepartmentNumber(departmentB.getDepartmentNumber());
        for (Employee e : employeesInB) {
            e.setDepartmentNumber(departmentA.getDepartmentNumber());
            employeeRepository.save(e);
        }
        return new ResponseEntity<>("Departments merged successfully", HttpStatus.OK);
    }
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

    @Autowired
    private EmployeeRepository employeeRepository;

    @PostMapping
    public ResponseEntity<Department> createDepartment(@RequestBody Department department) {
        Department savedDepartment = departmentRepository.save(department);
        return new ResponseEntity<>(savedDepartment, HttpStatus.CREATED);
    }
}
