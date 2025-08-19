package com.korona;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class Manager {
    private String id;
    private String name;
    private double salary;
    private String departmentId;

    @Override
    public String toString() {
        return String.format("Manager,%s,%s,%s,%s", id, name, (salary % 1 == 0) ? String.format("%.0f", salary) : salary, departmentId);
    }
}
