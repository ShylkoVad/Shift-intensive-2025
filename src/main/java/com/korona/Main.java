package com.korona;

import java.io.IOException;

public class Main {

    //  java -jar F:\JAVA\ShylkoVad-shift-intensive-2025\out\artifacts\ShylkoVad_shift_intensive_2025_jar\ShylkoVad-shift-intensive-2025.jar
    // java -jar F:\JAVA\ShylkoVad-shift-intensive-2025\out\artifacts\ShylkoVad_shift_intensive_2025_jar\ShylkoVad-shift-intensive-2025.jar --sort=salary --order=asc
    // java -jar F:\JAVA\ShylkoVad-shift-intensive-2025\out\artifacts\ShylkoVad_shift_intensive_2025_jar\ShylkoVad-shift-intensive-2025.jar --sort=salary --order=desc
    // java -jar F:\JAVA\ShylkoVad-shift-intensive-2025\out\artifacts\ShylkoVad_shift_intensive_2025_jar\ShylkoVad-shift-intensive-2025.jar --sort=name --order=asc
    // java -jar F:\JAVA\ShylkoVad-shift-intensive-2025\out\artifacts\ShylkoVad_shift_intensive_2025_jar\ShylkoVad-shift-intensive-2025.jar --sort=name --order=desc

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
            if (arg.startsWith("--sort=") || arg.startsWith("-s=")) {
                sortParameter = arg.split("=")[1];
            } else if (arg.startsWith("--order=")) {
                orderParameter = arg.split("=")[1];
            } else if (arg.startsWith("--stat=")) {
                statParameter = arg.split("=")[1];
            } else if (arg.startsWith("--output=") || arg.startsWith("-o=")) {
                outputParameter = arg.split("=")[1];
            } else if (arg.startsWith("--path=")) {
                outputPath = arg.split("=")[1];
            }
        }

        // Отладочные сообщения
        System.out.println("Параметр сортировки: " + sortParameter);
        System.out.println("Порядок сортировки: " + orderParameter);
        System.out.println("Порядок сортировки: " + statParameter);
        System.out.println("Параметр вывода: " + outputParameter);
        System.out.println("Путь к выходному файлу: " + outputPath);

        // Проверка на корректность ввода параметров
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

        // Очистка файла error.log в начале программы
        fileManager.clearErrorLog();

        // Обрабатываем файлы и выводим их содержимое
        fileHandler.processAndPrintFiles(criteria, order);
    }

}