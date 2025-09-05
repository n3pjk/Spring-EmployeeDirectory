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
    // Get the department, title, or other attributes of a particular employee
    @GetMapping("/{id}/attributes")
    public ResponseEntity<Employee> getEmployeeAttributes(@PathVariable Long id) {
        Employee employee = employeeRepository.findOne(id);
        if (employee == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(employee, HttpStatus.OK);
    }
    // Remove all direct reports to a manager. Reassign their reports to the next manager up the hierarchy.
    @DeleteMapping("/manager/{managerId}/direct-reports")
    public ResponseEntity<Void> deleteDirectReports(@PathVariable Long managerId) {
        Iterable<Employee> directReports = employeeRepository.findByManagerId(managerId);
        for (Employee direct : directReports) {
            // Reassign their reports to the next manager up
            Iterable<Employee> subReports = employeeRepository.findByManagerId(direct.getId());
            for (Employee sub : subReports) {
                sub.setManagerId(managerId);
                employeeRepository.save(sub);
            }
            employeeRepository.delete(direct.getId());
        }
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    // Remove all employees under a particular manager (including indirect reports)
    @DeleteMapping("/manager/{managerId}/all-reports")
    public ResponseEntity<Void> deleteAllReports(@PathVariable Long managerId) {
        List<Employee> toDelete = new ArrayList<>();
        collectReports(managerId, toDelete);
        for (Employee e : toDelete) {
            employeeRepository.delete(e.getId());
        }
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    // Remove all employees from a particular department
    @DeleteMapping("/department/{departmentNumber}")
    public ResponseEntity<Void> deleteEmployeesByDepartment(@PathVariable Long departmentNumber) {
        Iterable<Employee> employees = employeeRepository.findByDepartmentNumber(departmentNumber);
        for (Employee e : employees) {
            employeeRepository.delete(e.getId());
        }
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    // Remove a particular employee or list of employees
    @DeleteMapping
    public ResponseEntity<Void> deleteEmployees(@RequestParam List<Long> ids) {
        for (Long id : ids) {
            employeeRepository.delete(id);
        }
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    // Get all employees who report directly or indirectly to a particular manager
    @GetMapping("/manager/{managerId}/all-reports")
    public ResponseEntity<List<Employee>> getAllReports(@PathVariable Long managerId) {
        List<Employee> result = new ArrayList<>();
        collectReports(managerId, result);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    private void collectReports(Long managerId, List<Employee> result) {
        Iterable<Employee> directReports = employeeRepository.findByManagerId(managerId);
        for (Employee e : directReports) {
            result.add(e);
            collectReports(e.getId(), result);
        }
    }
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
