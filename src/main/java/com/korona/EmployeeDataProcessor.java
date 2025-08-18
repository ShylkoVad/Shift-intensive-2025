package com.korona;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EmployeeDataProcessor {
    private static final String ERROR_LOG = "error.log";
    private Map<String, Department> departments = new HashMap<>();
    private CustomFileHandler fileHandler = new CustomFileHandler(); // Измените на CustomFileHandler

    public EmployeeDataProcessor() throws IOException {
    }

    public static void main(String[] args) throws IOException {
        EmployeeDataProcessor processor = new EmployeeDataProcessor();
        Map<String, String> params = processor.parseArgs(args);

        // Очистка файла error.log в начале программы
        processor.clearErrorLog();

        // Получаем список файлов
        List<File> sbFiles = processor.fileHandler.getSbFiles();

        // Обработка файлов
        for (File file : sbFiles) {
            processor.fileHandler.processFile(file, processor.departments);
        }

        // Генерация выходных файлов
        processor.generateOutputFiles(params);

        // Вывод содержимого всех файлов на экран
        processor.fileHandler.printAllFilesContents(sbFiles); // Передаем список файлов
    }

    private Map<String, String> parseArgs(String[] args) {
        Map<String, String> params = new HashMap<>();
        for (String arg : args) {
            String[] parts = arg.split("=");
            if (parts.length == 2) {
                params.put(parts[0], parts[1]);
            } else {
                params.put(parts[0], null);
            }
        }
        return params;
    }

    private void generateOutputFiles(Map<String, String> params) {
        // Реализуйте логику для генерации выходных файлов и статистики
        // Сортировка и вывод в зависимости от параметров
    }

    private void logError(String message) {
        fileHandler.logError(message, ERROR_LOG);
    }

    private void clearErrorLog() {
        try (PrintWriter out = new PrintWriter(new FileWriter(ERROR_LOG, false))) { // false для перезаписи
            // Просто создаем пустой файл
        } catch (IOException e) {
            System.err.println("Error clearing error log: " + e.getMessage());
        }
    }
}