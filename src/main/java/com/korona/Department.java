package com.korona;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class Department {

    private String name;
    private String managerId;
    private List<Employee> employees;

    public Department(String name, String id) {
    }

    public void addEmployee(Employee employee) {
        employees.add(employee);
    }
}
