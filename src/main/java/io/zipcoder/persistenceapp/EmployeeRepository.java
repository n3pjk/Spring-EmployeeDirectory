package io.zipcoder.persistenceapp;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployeeRepository extends CrudRepository<Employee, Long> {
	Iterable<Employee> findByManagerId(Long managerId);
	Iterable<Employee> findByManagerIdIsNull();
	Iterable<Employee> findByDepartmentNumber(Long departmentNumber);
}
