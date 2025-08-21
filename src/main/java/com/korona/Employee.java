package com.korona;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class Employee {

    private String id;
    private String name;
    private double salary;
    private String managerId;

    @Override
    public String toString() {
        return String.format("Employee,%s, %s, %.0f, %s", id, name, salary, managerId);
    }
}