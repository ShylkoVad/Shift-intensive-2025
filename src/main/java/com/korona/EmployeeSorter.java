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

    public static void sortEmployees(List<Employee> employees, Set<Integer> managerIndexes, SortCriteria criteria, SortOrder order) {
        Comparator<Employee> comparator;

        switch (criteria) {
            case NAME:
                comparator = Comparator.comparing(Employee::getName);
                break;
            case SALARY:
                comparator = Comparator.comparingDouble(Employee::getSalary);
                break;
            default:
                throw new IllegalArgumentException("Unknown sort criteria: " + criteria);
        }

        if (order == SortOrder.DESCENDING) {
            comparator = comparator.reversed();
        }

        // Сортируем сотрудников
        employees.sort(comparator);
    }
}