package com.korona;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class EmployeeDataValidator {
    private final ErrorLogger logger;

    public EmployeeDataValidator() {
        this.logger = ErrorLogger.getInstance("error.log");
    }

    public boolean isSalaryValid(String line) {
        String[] parts = line.split(",");
        if (parts.length < 5) {
            logger.logError("Not enough data: " + line);
            return false; // Недостаточно данных
        }

        String salaryStr = parts[3].trim();
        double salary;
        try {
            if (salaryStr.isEmpty()) {
                logger.logError(line);
                return false; // Пустая зарплата
            }
            salary = Double.parseDouble(salaryStr);
            if (salary < 0) {
                logger.logError(line);
                return false; // Отрицательная зарплата
            }
        } catch (NumberFormatException e) {
            logger.logError(line);
            return false; // Некорректный формат зарплаты
        }

        return true; // Зарплата корректна
    }

    public void checkForDuplicateIds(List<String> lines, Set<String> employeeIds, Set<String> managerIds, List<String> validLines) {
        Map<String, List<String>> idToLinesMap = new HashMap<>(); // Словарь для хранения строк по идентификаторам
        Set<String> duplicates = new HashSet<>(); // Множество для хранения дублирующихся идентификаторов

        for (String line : lines) {
            String employeeId = extractEmployeeId(line); // Извлекаем ID из строки
            idToLinesMap.putIfAbsent(employeeId, new ArrayList<>()); // Инициализируем список строк для данного ID
            idToLinesMap.get(employeeId).add(line); // Добавляем строку в список строк для данного ID
        }

        // Проверяем на дубликаты
        for (Map.Entry<String, List<String>> entry : idToLinesMap.entrySet()) {
            if (entry.getValue().size() > 1) { // Если больше одной строки с одинаковым ID
                duplicates.add(entry.getKey()); // Добавляем ID в множество дубликатов
                for (String duplicateLine : entry.getValue()) {
                    logger.logError(duplicateLine); // Записываем строку в лог
                }
            }
        }
        // Удаляем дублирующиеся строки из validLines
        validLines.removeIf(line -> duplicates.contains(extractEmployeeId(line)));
    }


    String extractEmployeeId(String line) {
        String[] parts = line.split(",");
        return parts[1].trim(); // Предположим, идентификатор — это второе значение в строке
    }
}
