package com.korona;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class CustomFileHandler {

    // Метод для поиска файлов с расширением .sb
    public List<File> getSbFiles() {
        File dir = new File(".");
        return Arrays.stream(dir.listFiles())
                .filter(file -> file.isFile() && file.getName().endsWith(".sb"))
                .collect(Collectors.toList());
    }

    public void processFiles(List<File> files, Map<String, Department> departments) {
        Set<String> employeeIds = new HashSet<>(); // Создаем множество для employee IDs
        Set<String> managerIds = new HashSet<>(); // Создаем множество для manager IDs
        List<String> allLines = new ArrayList<>(); // Список для хранения всех строк из всех файлов

        // Читаем строки из всех файлов и добавляем их в общий список
        for (File file : files) {
            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = br.readLine()) != null) {
                    allLines.add(line);
                }
            } catch (IOException e) {
                System.err.println("Error reading file: " + file.getName() + " - " + e.getMessage());
            }
        }

        // Обрабатываем все строки из общего списка
        try {
            parseLine(allLines, departments, employeeIds, managerIds);
        } catch (InvalidEmployeeDataException e) {
            throw new RuntimeException(e);
        }

        // Выводим содержимое всех строк на экран
        System.out.println("Contents of all files:");
        for (String line : allLines) {
            System.out.println(line);
        }
    }

    public void logError(String message) {
        try (FileWriter fw = new FileWriter("error.log", true); // true для добавления в конец файла
             BufferedWriter bw = new BufferedWriter(fw);
             PrintWriter out = new PrintWriter(bw)) {
            out.println(message); // Записываем сообщение об ошибке
        } catch (IOException e) {
            System.err.println("Error writing to log file: " + e.getMessage());
        }
    }

    public void printFileContents(File file) {
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                System.out.println(line);
            }
        } catch (IOException e) {
            logError("Error reading file " + file.getName() + ": " + e.getMessage()); // Убрали второй аргумент
        }
    }

    public void printAllFilesContents(List<File> files) {
        for (File file : files) {
            System.out.println("Contents of file: " + file.getName());
            printFileContents(file);
            System.out.println(); // Пустая строка для разделения выводов разных файлов
        }
    }

    public void parseLine(List<String> lines, Map<String, Department> departments, Set<String> employeeIds, Set<String> managerIds)
            throws InvalidEmployeeDataException {
        FileManager fileManager = new FileManager();

        // Создаем менеджеров и соответствующие департаменты
        Map<String, Manager> managers = createManagers(lines, departments, managerIds, fileManager);

        // Теперь добавляем сотрудников
        addEmployees(lines, departments, managers, employeeIds, fileManager);
    }

    private Map<String, Manager> createManagers(List<String> lines, Map<String, Department> departments, Set<String> managerIds, FileManager fileManager)
            throws InvalidEmployeeDataException {
        Map<String, Manager> managers = new HashMap<>(); // Создаем новую карту для хранения менеджеров

        for (String line : lines) {
            String[] parts = line.split(",");
            if (parts.length < 5) {
                logError("Not enough data: " + line);
                continue; // Пропустить строку с недостаточными данными
            }

            String type = parts[0].trim();
            if (!type.equals("Manager")) {
                continue; // Пропустить, если это не менеджер
            }

            String id = parts[1].trim();
            String name = parts[2].trim();
            String salaryStr = parts[3].trim();
            String departmentManagerID = parts[4].trim();

            // Проверка на корректный ID департамента
            if (departmentManagerID == null || departmentManagerID.isEmpty()) {
                logError("Department Manager ID is null or empty: " + line);
                continue; // Пропускаем строку с пустым ID департамента
            }

            // Создаем или получаем департамент
            Department department = departments.computeIfAbsent(departmentManagerID, k -> new Department(departmentManagerID));
            if (department.getManager() != null) {
                throw new InvalidEmployeeDataException("Department already has a manager: " + departmentManagerID);
            }

            Manager manager = new Manager(id, name, Double.parseDouble(salaryStr), departmentManagerID);
            department.setManager(manager);
            managers.put(id, manager); // Добавляем менеджера в карту
            managerIds.add(id);

            // Создаем файл с именем departmentManagerID и записываем менеджера
            fileManager.createDepartmentFile(departmentManagerID, List.of(manager.toString()));
        }

        return managers; // Возвращаем карту менеджеров
    }

    private void addEmployees(List<String> lines, Map<String, Department> departments, Map<String, Manager> managers, Set<String> employeeIds, FileManager fileManager) {
        for (String line : lines) {
            String[] parts = line.split(",");
            if (parts.length < 5) {
                logError("Not enough data: " + line);
                continue; // Пропускаем строку с недостаточными данными
            }

            String type = parts[0].trim();
            if (!type.equals("Employee")) {
                continue; // Пропустить, если это не сотрудник
            }

            String id = parts[1].trim();
            String name = parts[2].trim();
            String salaryStr = parts[3].trim();
            String managerId = parts[4].trim();

            if (employeeIds.contains(id)) {
                logError("Duplicate employee ID: " + id);
                continue; // Пропускаем дублирующийся ID
            }

            // Проверка зарплаты
            double salary;
            try {
                if (salaryStr.isEmpty()) {
                    logError("Invalid salary for employee (empty value): " + line);
                    continue; // Пропускаем строку с пустой зарплатой
                }
                salary = Double.parseDouble(salaryStr);
                if (salary < 0) {
                    logError("Invalid salary for employee (negative value): " + line);
                    continue; // Пропускаем строку с отрицательной зарплатой
                }
            } catch (NumberFormatException e) {
                logError("Invalid salary for employee (not a number): " + line);
                continue; // Пропускаем строку с некорректным форматом зарплаты
            }

            // Создаем объект Employee
            Employee employee = new Employee(id, name, salary, managerId);

            // Проверяем, существует ли менеджер
            Manager manager = managers.get(managerId);
            if (manager != null) {
                System.out.println("Manager found: " + manager.getName());
                String departmentName = manager.getDepartmentId(); // Получаем название департамента менеджера

                // Проверяем, существует ли департамент
                if (departments.containsKey(departmentName)) {
                    System.out.println("Department found: " + departmentName);
                    // Записываем данные сотрудника в файл
                    fileManager.appendEmployeeToDepartmentFile(departmentName, employee.toString());
                    employeeIds.add(id); // Добавляем ID сотрудника в множество
                } else {
                    logError("Department not found for department name: " + departmentName);
                }
            } else {
                logError("Manager not found for employee: " + line);
            }
        }
    }

    public class InvalidEmployeeDataException extends Exception {
        public InvalidEmployeeDataException(String message) {
            super(message);
        }
    }
}