package com.korona;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class Department {

    private String name;
    private String managerId;
    private List<Employee> employees;
    private Manager manager; // Поле для хранения менеджера

    public Department(String departmentManagerID) {
    }
}