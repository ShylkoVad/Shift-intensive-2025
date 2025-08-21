package com.korona;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main {

    //  java -jar F:\JAVA\ShylkoVad-shift-intensive-2025\out\artifacts\ShylkoVad_shift_intensive_2025_jar\ShylkoVad-shift-intensive-2025.jar
    private Map<String, Department> departments = new HashMap<>();
    private CustomFileHandler fileHandler = new CustomFileHandler();
    private FileManager fileManager = new FileManager();

    public Main() throws IOException {
    }

    public static void main(String[] args) throws IOException {
        Main processor = new Main();

        // Очистка файла error.log в начале программы
        processor.fileManager.clearErrorLog();

        // Получаем список файлов .sb для обработки
        List<File> sbFiles = processor.fileHandler.getSbFiles();

        // Обрабатываем файлы и выводим их содержимое
        processor.fileHandler.processAndPrintFiles(sbFiles, processor.departments);

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