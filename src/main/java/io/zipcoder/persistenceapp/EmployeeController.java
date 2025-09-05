package io.zipcoder.persistenceapp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.ArrayList;

@RestController
@RequestMapping("/API/employee")
public class EmployeeController {
    // Get all employees of a particular department
    @GetMapping("/department/{departmentNumber}")
    public ResponseEntity<Iterable<Employee>> getEmployeesByDepartment(@PathVariable Long departmentNumber) {
        Iterable<Employee> employees = employeeRepository.findByDepartmentNumber(departmentNumber);
        return new ResponseEntity<>(employees, HttpStatus.OK);
    }
    // Get a list of employees with no assigned manager
    @GetMapping("/no-manager")
    public ResponseEntity<Iterable<Employee>> getEmployeesWithNoManager() {
        Iterable<Employee> employees = employeeRepository.findByManagerIdIsNull();
        return new ResponseEntity<>(employees, HttpStatus.OK);
    }
    // Get the entire reporting hierarchy for an employee
    @GetMapping("/{id}/hierarchy")
    public ResponseEntity<List<Employee>> getReportingHierarchy(@PathVariable Long id) {
        List<Employee> hierarchy = new ArrayList<>();
        Employee employee = employeeRepository.findOne(id);
        while (employee != null && employee.getManagerId() != null) {
            Employee manager = employeeRepository.findOne(employee.getManagerId());
            if (manager != null) {
                hierarchy.add(manager);
                employee = manager;
            } else {
                break;
            }
        }
        return new ResponseEntity<>(hierarchy, HttpStatus.OK);
    }
    // Get the list of employees under a particular manager
    @GetMapping("/manager/{managerId}")
    public ResponseEntity<Iterable<Employee>> getEmployeesByManager(@PathVariable Long managerId) {
        Iterable<Employee> employees = employeeRepository.findByManagerId(managerId);
        return new ResponseEntity<>(employees, HttpStatus.OK);
    }
    // Update other employee fields (excluding manager)
    @PutMapping("/{id}")
    public ResponseEntity<Employee> updateEmployee(@PathVariable Long id, @RequestBody Employee updatedEmployee) {
        Employee employee = employeeRepository.findOne(id);
        if (employee == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        // Update fields except managerId
        employee.setEmployeeNumber(updatedEmployee.getEmployeeNumber());
        employee.setFirstName(updatedEmployee.getFirstName());
        employee.setLastName(updatedEmployee.getLastName());
        employee.setTitle(updatedEmployee.getTitle());
        employee.setPhoneNumber(updatedEmployee.getPhoneNumber());
        employee.setEmail(updatedEmployee.getEmail());
        employee.setHireDate(updatedEmployee.getHireDate());
        employee.setDepartmentNumber(updatedEmployee.getDepartmentNumber());
        Employee saved = employeeRepository.save(employee);
        return new ResponseEntity<>(saved, HttpStatus.OK);
    }

    @Autowired
    private EmployeeRepository employeeRepository;

    @PostMapping
    public ResponseEntity<Employee> createEmployee(@RequestBody Employee employee) {
        Employee savedEmployee = employeeRepository.save(employee);
        return new ResponseEntity<>(savedEmployee, HttpStatus.CREATED);
    }

    // Update an employee's manager
    @PutMapping("/{id}/manager")
    public ResponseEntity<Employee> updateManager(@PathVariable Long id, @RequestParam Long managerId) {
        Employee employee = employeeRepository.findOne(id);
        if (employee == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        employee.setManagerId(managerId);
        Employee updated = employeeRepository.save(employee);
        return new ResponseEntity<>(updated, HttpStatus.OK);
    }
}
