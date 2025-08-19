package com.korona;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EmployeeDataProcessor {
    private Map<String, Department> departments = new HashMap<>();
    private CustomFileHandler fileHandler = new CustomFileHandler();
    private FileManager fileManager = new FileManager();

    public EmployeeDataProcessor() throws IOException {
    }

    public static void main(String[] args) throws IOException {
        EmployeeDataProcessor processor = new EmployeeDataProcessor();
        Map<String, String> params = processor.parseArgs(args);

        // Очистка файла error.log в начале программы
        processor.fileManager.clearErrorLog();

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


}