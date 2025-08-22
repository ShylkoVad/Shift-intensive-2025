package com.korona;

import java.util.Comparator;
import java.util.List;
import java.util.Set;

public class EmployeeSorter {
    public enum SortCriteria {
        NAME,
        SALARY
    }

    public enum SortOrder {
        ASCENDING,
        DESCENDING
    }

    public static void sortEmployees(List<Employee> employees, SortCriteria criteria, SortOrder order) {
        Comparator<Employee> comparator = null;

        switch (criteria) {
            case NAME:
                comparator = Comparator.comparing(Employee::getName);
                break;
            case SALARY:
                comparator = Comparator.comparingDouble(Employee::getSalary);
                break;
        }

        if (order == SortOrder.DESCENDING && comparator != null) {
            comparator = comparator.reversed();
        }

        if (comparator != null) {
            employees.sort(comparator);
        }
    }
}