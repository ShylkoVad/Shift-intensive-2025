package com.korona;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class ParseLine {

    public String parseLine(String line, Map<String, Department> departments, Set<String> employeeIds, Set<String> managerIds) throws InvalidEmployeeDataException {
        FileManager fileManager = new FileManager();

        String[] parts = line.split(",");
        if (parts.length < 5) {
            throw new InvalidEmployeeDataException("Not enough data: " + line);
        }

        String type = parts[0].trim();
        String id = parts[1].trim();
        String name = parts[2].trim();
        String salaryStr = parts[3].trim();
        String departmentManagerID = parts[4].trim(); // Это ID менеджера

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
            employeeIds.add(id); // Добавляем ID сотрудника в множество

            // Найти менеджера по его ID и получить название департамента
            Manager manager = null;
            for (Department department : departments.values()) {
                if (department.getManager() != null && department.getManager().getId().equals(departmentManagerID)) {
                    manager = department.getManager();
                    break;
                }
            }

            if (manager == null) {
                throw new InvalidEmployeeDataException("Manager not found for ID: " + departmentManagerID);
            }

            // Создаем объект Employee
            Employee employee = new Employee(id, name, salary, departmentManagerID);

            // Получаем имя департамента
            String departmentName = manager.getDepartmentId(); // Имя департамента

            // Записываем данные сотрудника в файл
            fileManager.appendEmployeeToDepartmentFile(departmentName, employee.toString());

        } else if (type.equals("Manager")) {
            if (managerIds.contains(id)) {
                throw new InvalidEmployeeDataException("Duplicate manager ID: " + id);
            }

            // Создаем или получаем департамент
            Department department = departments.computeIfAbsent(departmentManagerID, k -> new Department(departmentManagerID));
            if (department.getManager() != null) {
                throw new InvalidEmployeeDataException("Department already has a manager: " + departmentManagerID);
            }

            Manager manager = new Manager(id, name, salary, departmentManagerID); // Создаем менеджера
            department.setManager(manager); // Устанавливаем менеджера в департамент
            managerIds.add(id); // Добавляем ID менеджера в множество

            // Создаем файл с именем departmentManagerID и записываем менеджера
            fileManager.createDepartmentFile(departmentManagerID, List.of(manager.toString())); // Записываем менеджера

            // Возвращаем departmentManagerID
            return departmentManagerID;
        }

        return null; // Если не менеджер, возвращаем null
    }

    public class InvalidEmployeeDataException extends Exception {
        public InvalidEmployeeDataException(String message) {
            super(message);
        }
    }
}