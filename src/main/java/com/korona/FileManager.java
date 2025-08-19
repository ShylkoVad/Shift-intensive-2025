package com.korona;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class FileManager {

    public void createDepartmentFile(String departmentManagerID) {
        File file = new File(departmentManagerID + ".sb"); // Создаем файл с расширением .sb
        try {
            if (!file.exists()) {
                file.createNewFile(); // Создаем новый файл, если он не существует
                System.out.println("File created: " + file.getName());
            }
        } catch (IOException e) {
            System.err.println("Error creating file for department manager ID " + departmentManagerID + ": " + e.getMessage());
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
