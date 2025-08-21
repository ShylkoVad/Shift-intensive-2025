package com.korona;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main {

    //  java -jar F:\JAVA\ShylkoVad-shift-intensive-2025\out\artifacts\ShylkoVad_shift_intensive_2025_jar\ShylkoVad-shift-intensive-2025.jar

    public static void main(String[] args) throws IOException {

        FileManager fileManager = new FileManager();
        CustomFileHandler fileHandler = new CustomFileHandler();
        Map<String, Department> departments = new HashMap<>();

        // Очистка файла error.log в начале программы
        fileManager.clearErrorLog();

        // Получаем список файлов .sb для обработки
        List<File> sbFiles = fileHandler.getSbFiles();

        // Обрабатываем файлы и выводим их содержимое
       fileHandler.processAndPrintFiles(sbFiles, departments);

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
}