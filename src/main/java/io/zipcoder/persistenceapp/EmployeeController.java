package io.zipcoder.persistenceapp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/API/employee")
public class EmployeeController {
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
