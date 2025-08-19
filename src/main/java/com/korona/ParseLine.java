package com.korona;

import java.util.Map;
import java.util.Set;

public class ParseLine {

    public void parseLine(String line, Map<String, Department> departments, Set<String> employeeIds, Set<String> managerIds) throws InvalidEmployeeDataException {

        FileManager fileManager = new FileManager();

        String[] parts = line.split(",");
        if (parts.length < 5) {
            throw new InvalidEmployeeDataException("Not enough data: " + line);
        }

        String type = parts[0].trim();
        String id = parts[1].trim();
        String name = parts[2].trim();
        String salaryStr = parts[3].trim();
        String departmentManagerID = parts[4].trim();

        // Проверка зарплаты
        double salary;
        try {
            salary = Double.parseDouble(salaryStr);
            if (salary < 0) {
                throw new InvalidEmployeeDataException("Invalid salary for employee: " + line);
            }
        } catch (NumberFormatException e) {
            throw new InvalidEmployeeDataException("Invalid salary for employee: " + line);
        }

        // Обработка данных в зависимости от типа
        if (type.equals("Employee")) {
            if (employeeIds.contains(id)) {
                throw new InvalidEmployeeDataException("Duplicate employee ID: " + id);
            }
            // Сотрудники не имеют прямой связи с департаментами
            employeeIds.add(id); // Добавляем ID сотрудника в множество
        } else if (type.equals("Manager")) {
            if (managerIds.contains(id)) {
                throw new InvalidEmployeeDataException("Duplicate manager ID: " + id);
            }

            // Создаем или получаем департамент
            Department department = departments.computeIfAbsent(departmentId, k -> new Department(departmentId, "Название департамента")); // Замените "Название департамента" на реальное название
            if (department.getManager() != null) {
                throw new InvalidEmployeeDataException("Department already has a manager: " + departmentId);
            }

            Manager manager = new Manager(id, name, salary, departmentId); // Создаем менеджера
            department.setManager(manager); // Устанавливаем менеджера в департамент
            managerIds.add(id); // Добавляем ID менеджера в множество

            // Создаем файл с именем departmentManagerID
            fileManager.createDepartmentFile(departmentManagerID);
        }
    }

    public class InvalidEmployeeDataException extends Exception {
        public InvalidEmployeeDataException(String message) {
            super(message);
        }
    }
}
