package com.korona;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {

    // java -jar F:\JAVA\ShylkoVad-shift-intensive-2025\out\artifacts\ShylkoVad_shift_intensive_2025_jar\ShylkoVad-shift-intensive-2025.jar
    // java -jar F:\JAVA\ShylkoVad-shift-intensive-2025\out\artifacts\ShylkoVad_shift_intensive_2025_jar\ShylkoVad-shift-intensive-2025.jar --sort=salary --order=asc
    // java -jar F:\JAVA\ShylkoVad-shift-intensive-2025\out\artifacts\ShylkoVad_shift_intensive_2025_jar\ShylkoVad-shift-intensive-2025.jar --s=salary --order=desc
    // java -jar F:\JAVA\ShylkoVad-shift-intensive-2025\out\artifacts\ShylkoVad_shift_intensive_2025_jar\ShylkoVad-shift-intensive-2025.jar --sort=salary --order=desc
    // java -jar F:\JAVA\ShylkoVad-shift-intensive-2025\out\artifacts\ShylkoVad_shift_intensive_2025_jar\ShylkoVad-shift-intensive-2025.jar --sort=name --order=asc
    // java -jar F:\JAVA\ShylkoVad-shift-intensive-2025\out\artifacts\ShylkoVad_shift_intensive_2025_jar\ShylkoVad-shift-intensive-2025.jar --sort=name --order=desc
    // java -jar F:\JAVA\ShylkoVad-shift-intensive-2025\out\artifacts\ShylkoVad_shift_intensive_2025_jar\ShylkoVad-shift-intensive-2025.jar --stat
    // java -jar F:\JAVA\ShylkoVad-shift-intensive-2025\out\artifacts\ShylkoVad_shift_intensive_2025_jar\ShylkoVad-shift-intensive-2025.jar --stat --output=console
    // java -jar F:\JAVA\ShylkoVad-shift-intensive-2025\out\artifacts\ShylkoVad_shift_intensive_2025_jar\ShylkoVad-shift-intensive-2025.jar --stat --output=file
    // java -jar F:\JAVA\ShylkoVad-shift-intensive-2025\out\artifacts\ShylkoVad_shift_intensive_2025_jar\ShylkoVad-shift-intensive-2025.jar --stat --output=file --path=F:\JAVA\ShylkoVad-shift-intensive-2025\statistic.org

    private static final Logger logger = Logger.getLogger(Main.class.getName());

    // Допустимые параметры командной строки
    private static final Map<String, String> VALID_PARAMETERS = new HashMap<>();

    static {
        VALID_PARAMETERS.put("--sort", "name|salary");
        VALID_PARAMETERS.put("--s", "name|salary");
        VALID_PARAMETERS.put("--order", "asc|desc");
        VALID_PARAMETERS.put("--stat", "");
        VALID_PARAMETERS.put("--output", "console|file");
        VALID_PARAMETERS.put("--o", "console|file");
        VALID_PARAMETERS.put("--path", "");
    }

    public static void main(String[] args) {
        if (args.length == 0) {
            return;
        }

        // Инициализация параметров
        Map<String, String> params = new HashMap<>();

        // Парсинг аргументов
        try {
            for (String arg : args) {
                if (arg.contains("=")) {
                    String[] parts = arg.split("=", 2);
                    String key = parts[0];
                    String value = parts[1];

                    // Проверка валидности параметра
                    if (!VALID_PARAMETERS.containsKey(key)) {
                        throw new IllegalArgumentException("Неизвестный параметр: " + key);
                    }

                    // Проверка валидности значения
                    String expectedValues = VALID_PARAMETERS.get(key);
                    if (!expectedValues.isEmpty() && !expectedValues.contains(value)) {
                        throw new IllegalArgumentException("Неверное значение для параметра " + key +
                                ": " + value + ". Ожидается: " + expectedValues);
                    }

                    params.put(key, value);
                } else {
                    // Флаги без значений
                    if (!VALID_PARAMETERS.containsKey(arg) || !VALID_PARAMETERS.get(arg).isEmpty()) {
                        throw new IllegalArgumentException("Неверный формат параметра: " + arg);
                    }
                    params.put(arg, "true");
                }
            }
        } catch (IllegalArgumentException e) {
            System.err.println("Ошибка: " + e.getMessage());
            return;
        }

        // Извлечение параметров
        String sortParameter = getParameter(params, "--sort", "--s");
        String orderParameter = params.get("--order");
        boolean statParameter = params.containsKey("--stat");
        String outputParameter = getParameter(params, "--output", "--o");
        String outputPath = params.get("--path");

        // Валидация зависимостей параметров
        if (sortParameter != null && orderParameter == null) {
            System.err.println("Ошибка: параметр --order обязателен при использовании --sort");
            return;
        }

        if (orderParameter != null && sortParameter == null) {
            System.err.println("Ошибка: параметр --sort обязателен при использовании --order");
            return;
        }

        if (statParameter && outputParameter == null) {
            System.err.println("Ошибка: параметр --output обязателен при использовании --stat");
            return;
        }

        if ("file".equals(outputParameter) && outputPath == null) {
            System.err.println("Ошибка: параметр --path обязателен при использовании --output=file");
            return;
        }

        // Здесь можно продолжить выполнение основной логики программы
        try {
            // Очистка файла error.log
            FileManager fileManager = new FileManager();
            fileManager.clearErrorLog();

            // Установка критериев сортировки
            EmployeeSorter.SortCriteria criteria = null;
            if (sortParameter != null) {
                criteria = "name".equalsIgnoreCase(sortParameter) ?
                        EmployeeSorter.SortCriteria.NAME : EmployeeSorter.SortCriteria.SALARY;
            }

            // Установка порядка сортировки
            EmployeeSorter.SortOrder order = null;
            if (orderParameter != null) {
                order = "asc".equalsIgnoreCase(orderParameter) ?
                        EmployeeSorter.SortOrder.ASCENDING : EmployeeSorter.SortOrder.DESCENDING;
            }

            // Обработка файлов
            CustomFileHandler fileHandler = new CustomFileHandler();
            fileHandler.processAndPrintFiles(criteria, order);

            // Вывод статистики если требуется
            if (statParameter) {
                fileHandler.printStatistics(outputParameter, outputPath);
            }

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Произошла ошибка во время выполнения: " + e.getMessage(), e);
        }
    }

    // Вспомогательный метод для получения параметра с приоритетом
    private static String getParameter(Map<String, String> params, String primaryKey, String secondaryKey) {
        if (params.containsKey(primaryKey)) {
            return params.get(primaryKey);
        }
        if (params.containsKey(secondaryKey)) {
            return params.get(secondaryKey);
        }
        return null;
    }
}