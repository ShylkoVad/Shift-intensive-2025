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

    private String[] splitLine(String line) {
        String[] parts = line.split(","); // Разбиваем строку
        for (int i = 0; i < parts.length; i++) {
            parts[i] = parts[i].trim(); // Удаляем пробелы в начале и в конце каждого элемента
        }
        return parts;
    }

    public boolean isSalaryValid(String line) {
        String[] parts = splitLine(line);
        String salaryStr = parts[3]; // Идентификатор — это четвертое значение в строке
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
        Set<String> managerNames = new HashSet<>(); // Множество для хранения имен менеджеров

        for (String line : lines) {
            String[] parts = splitLine(line);
            String employeeId = parts[1]; // Предполагаем, что ID — это второе значение
            String managerName = parts[2]; // Предполагаем, что имя менеджера — это третье значение

            // Проверяем дубликаты по ID
            idToLinesMap.putIfAbsent(employeeId, new ArrayList<>()); // Инициализируем список строк для данного ID
            idToLinesMap.get(employeeId).add(line); // Добавляем строку в список строк для данного ID

            // Проверка на дубликаты имен менеджеров
            if (managerIds.contains(employeeId)) { // Если ID менеджера уже существует
                if (managerNames.contains(managerName)) {
                    duplicates.add(employeeId); // Добавляем ID в множество дубликатов
                    logger.logError(line); // Записываем строку в лог
                } else {
                    managerNames.add(managerName); // Добавляем имя менеджера в множество
                }
            }
        }

        // Удаляем дублирующиеся строки из validLines
        validLines.removeIf(line -> duplicates.contains(extractEmployeeId(line)));
    }

    String extractEmployeeId(String line) {
        String[] parts = splitLine(line); // Используем новый метод
        return parts[1]; // Идентификатор — это второе значение в строке
    }

    public boolean hasEnoughData(String line) {
        String[] parts = splitLine(line); // Используем новый метод
        if (parts.length < 5) {
            logger.logError(line);
            return false; // Недостаточно данных
        }
        return true; // Данные достаточны
    }
}