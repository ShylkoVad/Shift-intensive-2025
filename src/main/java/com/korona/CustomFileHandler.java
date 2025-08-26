package com.korona;

import com.korona.EmployeeSorter.SortCriteria;
import com.korona.EmployeeSorter.SortOrder;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
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
    private Map<String, Department> departments; // Добавляем поле

    public CustomFileHandler() {
        this.logger = ErrorLogger.getInstance("error.log");
        this.departments = new HashMap<>(); // Инициализируем
    }

    // Метод для поиска файлов с расширением .sb
    public List<File> getSbFiles() {
        File dir = new File(".");
        return Arrays.stream(dir.listFiles())
                .filter(file -> file.isFile() && file.getName().endsWith(".sb"))
                .collect(Collectors.toList());
    }

    public List<String> processFiles(List<File> files, SortCriteria criteria, SortOrder order) {
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
            // Вместо передачи departments используем this.departments
            parseLine(validLines, this.departments, employeeIds, managerIds, criteria, order);
        } catch (InvalidEmployeeDataException e) {
            throw new RuntimeException(e);
        }

        // Возвращаем список созданных файлов
        return fileManager.getCreatedFiles();
    }

    public void processAndPrintFiles(SortCriteria criteria, SortOrder order) {

        // Инициализируем поле departments
        this.departments = new HashMap<>();

        // Получаем список файлов .sb для обработки
        List<File> sbFiles = getSbFiles();

        // Обработка файлов и получение списка созданных файлов
        List<String> createdFiles = processFiles(sbFiles, criteria, order);

        // Сортируем созданные файлы по имени
        createdFiles.sort(String::compareTo);
    }

    public void parseLine(List<String> lines, Map<String, Department> departments,
                          Set<String> employeeIds, Set<String> managerIds,
                          SortCriteria criteria, SortOrder order) throws InvalidEmployeeDataException {
        // Сначала создаем всех менеджеров и департаменты
        Map<String, Manager> managers = createManagers(lines, departments, managerIds);

        // Затем добавляем сотрудников (уже с сортировкой)
        addEmployees(lines, departments, managers, employeeIds, criteria, order);
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

            Manager manager = new Manager(id, name, Double.parseDouble(salaryStr), departmentManagerID);
            department.setManager(manager);
            managers.put(id, manager); // Добавляем менеджера в карту
            managerIds.add(id);

            // Создаем файл с именем departmentManagerID и записываем менеджера
            fileManager.createDepartmentFile(departmentManagerID, List.of(manager.toString()));
        }

        return managers; // Возвращаем карту менеджеров
    }

    public void addEmployees(List<String> lines, Map<String, Department> departments, Map<String, Manager> managers, Set<String> employeeIds, SortCriteria criteria, SortOrder order) {
        List<Employee> employees = new ArrayList<>(); // Список для хранения всех сотрудников

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
            employees.add(employee);
            employeeIds.add(id);
        }

        // Применяем сортировку ко всем сотрудникам
        if (criteria != null && order != null) {
            EmployeeSorter.sortEmployees(employees, criteria, order);
        }

        // Добавляем отсортированных сотрудников в департаменты
        for (Employee employee : employees) {
            Manager manager = managers.get(employee.getManagerId());
            if (manager != null) {
                String departmentName = manager.getDepartmentId();
                if (departments.containsKey(departmentName)) {
                    // Записываем данные сотрудника в файл
                    fileManager.appendEmployeeToDepartmentFile(departmentName, employee.toString());
                } else {
                    logger.logError("Department not found for department name: " + departmentName);
                }
            } else {
                logger.logError("Manager not found for employee: " + employee.getId());
            }
        }
    }

    public void printStatistics(String outputParameter, String outputPath) {
        // Обрабатываем файлы, чтобы получить данные
        List<File> sbFiles = getSbFiles();

        // Создаем временный список для хранения всех строк
        List<String> allLines = new ArrayList<>();

        // Читаем все строки из всех .sb файлов
        for (File file : sbFiles) {
            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = br.readLine()) != null) {
                    allLines.add(line);
                }
            } catch (IOException e) {
                System.err.println("Error reading file: " + file.getName() + " - " + e.getMessage());
            }
        }

        Map<String, Statistics> statisticsMap = new HashMap<>();
        Map<Integer, String> departmentMap = new HashMap<>(); // Для хранения департаментов менеджеров

        // Сначала собираем информацию о менеджерах и их департаментах
        for (String line : allLines) {
            String[] parts = line.split(",");
            if (parts.length >= 5 && parts[0].trim().equals("Manager")) {
                int managerId = Integer.parseInt(parts[1].trim());
                String departmentName = parts[4].trim(); // Департамент у менеджера
                departmentMap.put(managerId, departmentName);
            }
        }

        // Собираем статистику по сотрудникам (только Employee)
        for (String line : allLines) {
            String[] parts = line.split(",");
            if (parts.length >= 5 && parts[0].trim().equals("Employee")) {
                int managerId = Integer.parseInt(parts[4].trim()); // ID менеджера

                // Получаем название департамента через managerId
                String departmentName = departmentMap.get(managerId);
                if (departmentName == null) {
                    continue; // Если департамент не найден, пропускаем сотрудника
                }

                // Проверяем валидность зарплаты
                try {
                    double salary = Double.parseDouble(parts[3].trim());
                    if (salary < 0) continue; // Пропускаем отрицательные зарплаты

                    // Создаем или получаем статистику для департамента
                    Statistics stats = statisticsMap.computeIfAbsent(departmentName,
                            k -> new Statistics(departmentName));
                    stats.addSalary(salary);

                } catch (NumberFormatException e) {
                    // Пропускаем некорректные зарплаты
                    System.err.println("Invalid salary format: " + line);
                }
            }
        }

        // Подготовка вывода
        StringBuilder output = new StringBuilder();
        output.append("department,min,max,mid\n");

        // Сортировка и вывод статистики
        statisticsMap.entrySet().stream()
                .sorted(Map.Entry.comparingByKey()) // Сортировка по имени департамента
                .forEach(entry -> {
                    Statistics stats = entry.getValue();
                    double min = Math.ceil(stats.getMinSalary() * 100.0) / 100.0;
                    double max = Math.ceil(stats.getMaxSalary() * 100.0) / 100.0;
                    double avg = Math.ceil(stats.getAverageSalary() * 100.0) / 100.0;
                    output.append(String.format("%s,%.2f,%.2f,%.2f\n",
                            stats.getDepartmentName(), min, max, avg));
                });

        // Определение способа вывода
        if (outputParameter == null || "console".equalsIgnoreCase(outputParameter)) {
            System.out.println(output.toString());
        } else if ("file".equalsIgnoreCase(outputParameter)) {
            if (outputPath == null) {
                System.out.println("Ошибка: путь к выходному файлу не указан.");
                return;
            }
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputPath))) {
                writer.write(output.toString());
                System.out.println("Статистика сохранена в файл: " + outputPath);
            } catch (IOException e) {
                System.err.println("Ошибка при записи в файл: " + e.getMessage());
            }
        } else {
            System.out.println("Ошибка: неверный параметр вывода: " + outputParameter);
        }
    }

    public static class InvalidEmployeeDataException extends Exception {
        public InvalidEmployeeDataException(String message) {
            super(message);
        }
    }
}