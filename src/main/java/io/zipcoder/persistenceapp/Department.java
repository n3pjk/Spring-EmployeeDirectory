package io.zipcoder.persistenceapp;

import javax.persistence.*;

@Entity
public class Department {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long departmentNumber;
    private String departmentName;
    private Long managerId; // Employee id

    // Getters and setters
    public Long getDepartmentNumber() { return departmentNumber; }
    public void setDepartmentNumber(Long departmentNumber) { this.departmentNumber = departmentNumber; }
    public String getDepartmentName() { return departmentName; }
    public void setDepartmentName(String departmentName) { this.departmentName = departmentName; }
    public Long getManagerId() { return managerId; }
    public void setManagerId(Long managerId) { this.managerId = managerId; }
}
