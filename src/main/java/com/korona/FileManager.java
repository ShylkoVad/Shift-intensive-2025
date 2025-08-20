package com.korona;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

public class FileManager {

    public void createDepartmentFile(String departmentManagerID, List<String> managers) {
        File file = new File(departmentManagerID + ".sb"); // Создаем файл с расширением .sb
        if (createOrClearFile(file)) {
            // Записываем менеджеров в файл
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, true))) {
                for (String manager : managers) {
                    writer.write(manager);
                    writer.newLine();
                }
            } catch (IOException e) {
                System.err.println("Error writing managers to file for department manager ID " + departmentManagerID + ": " + e.getMessage());
            }
        }
    }

    private boolean createOrClearFile(File file) {
        try {
            // Создаем новый файл, если он не существует
            if (!file.exists()) {
                file.createNewFile();
                System.out.println("File created: " + file.getName());
                return true; // Файл создан
            } else {
                // Если файл существует, очищаем его содержимое
                try (PrintWriter writer = new PrintWriter(new FileWriter(file, false))) {
                    System.out.println("File exists. Contents will be overwritten: " + file.getName());
                }
                return true; // Файл очищен
            }
        } catch (IOException e) {
            System.err.println("Error creating or clearing file: " + e.getMessage());
            return false; // Ошибка при создании или очистке файла
        }
    }

    public void appendEmployeeToDepartmentFile(String departmentManagerID, String employeeData) {
        File file = new File(departmentManagerID + ".sb"); // Создаем файл с расширением .sb
        if (!file.exists()) {
            System.err.println("Error: File for department manager ID " + departmentManagerID + " does not exist.");
            return; // Возвращаемся, если файл не существует
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, true))) {
            writer.write(employeeData);
            writer.newLine();
        } catch (IOException e) {
            System.err.println("Error appending employee data for department manager ID " + departmentManagerID + ": " + e.getMessage());
        }
    }

    // Метод для вывода departmentManagerID на экран
    public void printDepartmentManagerID(String departmentManagerID) {
        System.out.println("Department Manager ID: " + departmentManagerID);
    }

    public void clearErrorLog() {
        try (PrintWriter out = new PrintWriter(new FileWriter(Constants.ERROR_LOG, false))) { // false для перезаписи
            // Просто создаем пустой файл
        } catch (IOException e) {
            System.err.println("Error clearing error log: " + e.getMessage());
        }
    }
}
