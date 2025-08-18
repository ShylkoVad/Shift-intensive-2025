package com.korona;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CustomFileHandler {

    public static final String ERROR_LOG = "error.log"; // Путь к файлу

    public List<File> getSbFiles() {
        File dir = new File(".");
        return Arrays.stream(dir.listFiles())
                .filter(file -> file.isFile() && file.getName().endsWith(".sb"))
                .collect(Collectors.toList());
    }

    public void processFile(File file, Map<String, Department> departments) {
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                try {
                    // Попытка распарсить и обработать строку
                    parseLine(line, departments);
                } catch (InvalidEmployeeDataException e) {
                    // Логируем ошибку
                    logError(e.getMessage(), Constants.ERROR_LOG);
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading file: " + file.getName() + " - " + e.getMessage());
        }
    }


    public void parseLine(String line, Map<String, Department> departments) throws InvalidEmployeeDataException {
        String[] parts = line.split(",");
        if (parts.length < 5) {
            throw new InvalidEmployeeDataException("Not enough data: " + line);
        }

        String type = parts[0];
        String id = parts[1];
        String name = parts[2];
        String salaryStr = parts[3];
        String departmentId = parts[4];

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
            // Создать и добавить сотрудника
        } else if (type.equals("Manager")) {
            // Создать и добавить менеджера
        }
    }

    private String getDepartmentNameByManagerId(Map<String, Department> departments, String managerId) {
        for (Department department : departments.values()) {
            if (department.getManagerId().equals(managerId)) {
                return department.getName();
            }
        }
        return null;
    }

    public void logError(String message, String logFile) {
        try (PrintWriter out = new PrintWriter(new FileWriter(logFile, true))) { // Используем true для добавления
            out.println(message);
        } catch (IOException e) {
            System.err.println("Error writing to error log: " + e.getMessage());
        }
    }

    public void printFileContents(File file) {
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                System.out.println(line);
            }
        } catch (IOException e) {
            logError("Error reading file " + file.getName() + ": " + e.getMessage());
        }
    }

    private void logError(String s) {
    }

    public void printAllFilesContents(List<File> files) {
        for (File file : files) {
            System.out.println("Contents of file: " + file.getName());
            printFileContents(file);
            System.out.println(); // Пустая строка для разделения выводов разных файлов
        }
    }

    public class InvalidEmployeeDataException extends Exception {
        public InvalidEmployeeDataException(String message) {
            super(message);
        }
    }
}
