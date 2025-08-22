package com.korona;

import java.util.ArrayList;
import java.util.List;

public class Statistics {
    private String departmentName;
    private List<Double> salaries;

    public Statistics(String departmentName) {
        this.departmentName = departmentName;
        this.salaries = new ArrayList<>();
    }

    public void addSalary(double salary) {
        if (salary >= 0) { // Добавляем только корректные зарплаты
            salaries.add(salary);
        }
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public double getMinSalary() {
        if (salaries.isEmpty()) return 0.0;
        return salaries.stream().min(Double::compare).orElse(0.0);
    }

    public double getMaxSalary() {
        if (salaries.isEmpty()) return 0.0;
        return salaries.stream().max(Double::compare).orElse(0.0);
    }

    public double getAverageSalary() {
        if (salaries.isEmpty()) return 0.0;
        return salaries.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
    }
}
