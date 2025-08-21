package com.korona;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class CustomFileHandler {
    FileManager fileManager = new FileManager();
    EmployeeDataValidator employeeDataValidator = new EmployeeDataValidator();
    private final ErrorLogger logger;

    public CustomFileHandler() {
        this.logger = ErrorLogger.getInstance("error.log");
    }

    // Метод для поиска файлов с расширением .sb
    public List<File> getSbFiles() {
        File dir = new File(".");
        return Arrays.stream(dir.listFiles())
                .filter(file -> file.isFile() && file.getName().endsWith(".sb"))
                .collect(Collectors.toList());
    }

    public List<String> processFiles(List<File> files, Map<String, Department> departments) {
        Set<String> employeeIds = new HashSet<>(); // Создаем множество для employee IDs
        Set<String> managerIds = new HashSet<>(); // Создаем множество для manager IDs
        List<String> allLines = new ArrayList<>(); // Список для хранения всех строк из всех файлов
        List<String> validLines = new ArrayList<>(); // Список для хранения строк с корректными данными

        // Читаем строки из всех файлов
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

        // Проверяем строки на корректность
        for (String line : allLines) {
            if (!employeeDataValidator.hasEnoughData(line)) {
                // Если данных недостаточно, логируем ошибку и пропускаем строку
                continue; // Переходим к следующей строке
            }

            // Проверяем строки на корректность зарплаты
            if (employeeDataValidator.isSalaryValid(line)) {
                validLines.add(line); // Если зарплата корректна, добавляем строку в validLines
            }
        }

        // Проверяем на дублирующихся менеджеров по департаментам
        employeeDataValidator.validateManagersByDepartment(validLines);

        // Проверяем на дубликаты идентификаторов в validLines и записываем дубликаты в лог
        employeeDataValidator.checkForDuplicateIds(validLines, employeeIds, managerIds, validLines);

        // Обрабатываем строки из validLines
        try {
            parseLine(validLines, departments, employeeIds, managerIds);
        } catch (InvalidEmployeeDataException e) {
            throw new RuntimeException(e);
        }

        // Возвращаем список созданных файлов
        return fileManager.getCreatedFiles();
    }


    public void printFileContents(File file) {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
        }
    }

    public List<String> processAndPrintFiles(List<File> sbFiles, Map<String, Department> departments) {
        // Обработка файлов и получение списка созданных файлов
        List<String> createdFiles = processFiles(sbFiles, departments);

        // Сортируем созданные файлы по имени
        createdFiles.sort(String::compareTo);

        // Вывод содержимого вновь созданных файлов
        for (String fileName : createdFiles) {
            File newFile = new File(fileName); // Создаем объект File для нового файла
            System.out.println(newFile.getName());
            printFileContents(newFile); // Выводим содержимое файла
            System.out.println(); // Пустая строка для разделения выводов
        }

        printErrorLogContents(); // Вывод содержимого error.log

        return createdFiles; // Возвращаем список созданных файлов
    }


    public void printErrorLogContents() {
        File errorLog = new File(Constants.ERROR_LOG);
        if (!errorLog.exists()) {
            System.out.println("Error log file does not exist.");
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(errorLog))) {
            String line;
            System.out.println("error.log:");
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
        } catch (IOException e) {
            System.err.println("Error reading error log: " + e.getMessage());
        }
    }

    public void parseLine(List<String> lines, Map<String, Department> departments, Set<String> employeeIds, Set<String> managerIds)
            throws InvalidEmployeeDataException {
        // Создаем менеджеров и соответствующие департаменты
        Map<String, Manager> managers = createManagers(lines, departments, managerIds);

        // Теперь добавляем сотрудников
        addEmployees(lines, departments, managers, employeeIds);
    }

    private Map<String, Manager> createManagers(List<String> lines, Map<String, Department> departments, Set<String> managerIds) throws InvalidEmployeeDataException {
        Map<String, Manager> managers = new HashMap<>(); // Создаем новую карту для хранения менеджеров

        for (String line : lines) {
            String[] parts = line.split(",");
            if (parts.length < 5) {
                logger.logError("Not enough data: " + line);
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

            // Создаем или получаем департамент
            Department department = departments.computeIfAbsent(departmentManagerID, k -> new Department(departmentManagerID));

//            // Проверяем, существует ли уже менеджер в департаменте
//            if (department.getManager() != null) {
//                // Логируем информацию о существующем менеджере
//                logger.logError("Department already has a manager: " + departmentManagerID);
//                continue; // Пропускаем добавление менеджера
//            }

            Manager manager = new Manager(id, name, Double.parseDouble(salaryStr), departmentManagerID);
            department.setManager(manager);
            managers.put(id, manager); // Добавляем менеджера в карту
            managerIds.add(id);

            // Создаем файл с именем departmentManagerID и записываем менеджера
            fileManager.createDepartmentFile(departmentManagerID, List.of(manager.toString()));
        }

        return managers; // Возвращаем карту менеджеров
    }

    private void addEmployees(List<String> lines, Map<String, Department> departments, Map<String, Manager> managers, Set<String> employeeIds) {
        for (String line : lines) {
            String[] parts = line.split(",");
            if (parts.length < 5) {
                logger.logError("Not enough data: " + line);
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

            // Создаем объект Employee
            Employee employee = new Employee(id, name, Double.parseDouble(salaryStr), managerId);

            // Проверяем, существует ли менеджер
            Manager manager = managers.get(managerId);
            if (manager != null) {
                String departmentName = manager.getDepartmentId(); // Получаем название департамента менеджера

                // Проверяем, существует ли департамент
                if (departments.containsKey(departmentName)) {
                    // Записываем данные сотрудника в файл
                    fileManager.appendEmployeeToDepartmentFile(departmentName, employee.toString());
                    employeeIds.add(id); // Добавляем ID сотрудника в множество
                } else {
                    logger.logError("Department not found for department name: " + departmentName);
                }
            } else {
                logger.logError("Manager not found for employee: " + line);
            }
        }
    }

    public static class InvalidEmployeeDataException extends Exception {
        public InvalidEmployeeDataException(String message) {
            super(message);
        }
    }
}