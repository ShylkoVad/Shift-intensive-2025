package com.korona;

import java.io.IOException;

public class Main {

    //  java -jar F:\JAVA\ShylkoVad-shift-intensive-2025\out\artifacts\ShylkoVad_shift_intensive_2025_jar\ShylkoVad-shift-intensive-2025.jar
    // java -jar F:\JAVA\ShylkoVad-shift-intensive-2025\out\artifacts\ShylkoVad_shift_intensive_2025_jar\ShylkoVad-shift-intensive-2025.jar --sort=salary --order=asc
    // java -jar F:\JAVA\ShylkoVad-shift-intensive-2025\out\artifacts\ShylkoVad_shift_intensive_2025_jar\ShylkoVad-shift-intensive-2025.jar --s=salary --order=desc
    // java -jar F:\JAVA\ShylkoVad-shift-intensive-2025\out\artifacts\ShylkoVad_shift_intensive_2025_jar\ShylkoVad-shift-intensive-2025.jar --sort=salary --order=desc
    // java -jar F:\JAVA\ShylkoVad-shift-intensive-2025\out\artifacts\ShylkoVad_shift_intensive_2025_jar\ShylkoVad-shift-intensive-2025.jar --sort=name --order=asc
    // java -jar F:\JAVA\ShylkoVad-shift-intensive-2025\out\artifacts\ShylkoVad_shift_intensive_2025_jar\ShylkoVad-shift-intensive-2025.jar --sort=name --order=desc
    // java -jar F:\JAVA\ShylkoVad-shift-intensive-2025\out\artifacts\ShylkoVad_shift_intensive_2025_jar\ShylkoVad-shift-intensive-2025.jar --stat
    // java -jar F:\JAVA\ShylkoVad-shift-intensive-2025\out\artifacts\ShylkoVad_shift_intensive_2025_jar\ShylkoVad-shift-intensive-2025.jar --stat --output=console
    // java -jar F:\JAVA\ShylkoVad-shift-intensive-2025\out\artifacts\ShylkoVad_shift_intensive_2025_jar\ShylkoVad-shift-intensive-2025.jar --stat --output=file
    // java -jar F:\JAVA\ShylkoVad-shift-intensive-2025\out\artifacts\ShylkoVad_shift_intensive_2025_jar\ShylkoVad-shift-intensive-2025.jar --stat --output=file --path=F:\JAVA\ShylkoVad-shift-intensive-2025\statistic.org

    public static void main(String[] args) throws IOException {
        FileManager fileManager = new FileManager();
        CustomFileHandler fileHandler = new CustomFileHandler();

        // Инициализация параметров
        String sortParameter = null;
        String orderParameter = null;
        String statParameter = null;
        String outputParameter = null;
        String outputPath = null;

        // Обработка аргументов командной строки
        for (String arg : args) {
            if (arg.startsWith("--sort=") || arg.startsWith("--s=")) {
                sortParameter = arg.split("=")[1];
            } else if (arg.startsWith("--order=")) {
                orderParameter = arg.split("=")[1];
            } else if (arg.equals("--stat")) {
                statParameter = "stat";  // значение по умолчанию
            } else if (arg.startsWith("--output=") || arg.startsWith("--o=")) {
                outputParameter = arg.split("=")[1];
            } else if (arg.startsWith("--path=")) {
                outputPath = arg.split("=")[1];
            }
        }

        // Проверка на корректность ввода параметров сортировки
        if (sortParameter != null && orderParameter == null) {
            System.out.println("Ошибка: порядок сортировки не указан для параметра сортировки: " + sortParameter);
            return;
        }

        if (orderParameter != null && !orderParameter.equals("asc") && !orderParameter.equals("desc")) {
            System.out.println("Ошибка: неверный параметр порядка сортировки: " + orderParameter);
            return;
        }

        // Устанавливаем критерий сортировки
        EmployeeSorter.SortCriteria criteria = null;
        if ("name".equalsIgnoreCase(sortParameter)) {
            criteria = EmployeeSorter.SortCriteria.NAME;
        } else if ("salary".equalsIgnoreCase(sortParameter)) {
            criteria = EmployeeSorter.SortCriteria.SALARY;
        }

        // Устанавливаем порядок сортировки
        EmployeeSorter.SortOrder order = null;
        if ("asc".equalsIgnoreCase(orderParameter)) {
            order = EmployeeSorter.SortOrder.ASCENDING;
        } else if ("desc".equalsIgnoreCase(orderParameter)) {
            order = EmployeeSorter.SortOrder.DESCENDING;
        }

        // Проверка на корректность ввода параметров статистики
        if (statParameter != null) {
            // Если задан --stat, проверяем корректность output параметров
            if (outputParameter != null && !outputParameter.equals("console") && !outputParameter.equals("file")) {
                System.out.println("Ошибка: неверный параметр вывода: " + outputParameter);
                return;
            }

            if ("file".equals(outputParameter) && outputPath == null) {
                System.out.println("Ошибка: путь к выходному файлу не указан для --output=file");
                return;
            }
        }

        // Очистка файла error.log в начале программы
        fileManager.clearErrorLog();

        // Обрабатываем файлы и создаем департаменты
        fileHandler.processAndPrintFiles(criteria, order);

        // Собираем статистику по уже созданным файлам
        if (statParameter != null) {
            fileHandler.printStatistics(outputParameter, outputPath);
        }
    }
}