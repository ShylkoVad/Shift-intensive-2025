package com.korona;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class CustomFileHandler {

    ParseLine parseLine = new ParseLine();

    public List<File> getSbFiles() {
        File dir = new File(".");
        return Arrays.stream(dir.listFiles())
                .filter(file -> file.isFile() && file.getName().endsWith(".sb"))
                .collect(Collectors.toList());
    }

    public void processFile(File file, Map<String, Department> departments) {
        Set<String> employeeIds = new HashSet<>(); // Создаем множество для employee IDs
        Set<String> managerIds = new HashSet<>(); // Создаем множество для manager IDs

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                try {
                    // Попытка распарсить и обработать строку
                    parseLine.parseLine(line, departments, employeeIds, managerIds); // Передаем все необходимые аргументы
                } catch (ParseLine.InvalidEmployeeDataException e) {
                    // Логируем ошибку
                    logError(e.getMessage(), Constants.ERROR_LOG);
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading file: " + file.getName() + " - " + e.getMessage());
        }
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
            logError("Error reading file " + file.getName() + ": " + e.getMessage(), Constants.ERROR_LOG); // Передаем имя файла журнала
        }
    }

    public void printAllFilesContents(List<File> files) {
        for (File file : files) {
            System.out.println("Contents of file: " + file.getName());
            printFileContents(file);
            System.out.println(); // Пустая строка для разделения выводов разных файлов
        }
    }
}