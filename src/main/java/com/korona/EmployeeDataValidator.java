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

    private List<String> splitLine(String line) {
        String[] parts = line.split(",");
        List<String> trimmedParts = new ArrayList<>();
        for (String part : parts) {
            trimmedParts.add(part.trim());
        }
        return trimmedParts;
    }

    // Метод для проверки наличия достаточного количества данных
    public boolean hasEnoughData(String line) {
        List<String> parts = splitLine(line);
        if (parts.size() < 5) {
            logger.logError(line);
            return false; // Недостаточно данных
        }
        return true; // Данные достаточны
    }

    // Метод для проверки дубликатов департаментов у менеджеров
    public void validateManagersByDepartment(List<String> validLines) {
        Map<String, List<String>> departmentMap = new HashMap<>(); // Для хранения строк по департаментам
        List<String> finalValidLines = new ArrayList<>(); // Для хранения окончательных валидных строк

        for (String line : validLines) {
            List<String> parts = splitLine(line);

            String type = parts.get(0);
            String departmentManagerID = parts.get(4);

            if (type.equals("Manager")) {
                // Добавляем строку в соответствующий департамент
                departmentMap.computeIfAbsent(departmentManagerID, k -> new ArrayList<>()).add(line);
            } else {
                finalValidLines.add(line); // Добавляем в окончательные валидные строки, если это не менеджер
            }
        }

        // Обрабатываем дублирующиеся записи
        for (List<String> entries : departmentMap.values()) {
            if (entries.size() > 1) {
                // Если есть дубликаты, записываем все записи в журнал ошибок
                for (String duplicateEntry : entries) {
                    logger.logError(duplicateEntry);
                }
            } else {
                // Если это уникальная запись, добавляем в окончательные валидные строки
                finalValidLines.add(entries.get(0));
            }
        }

        // Обновляем validLines с окончательными валидными строками
        validLines.clear();
        validLines.addAll(finalValidLines);
    }

    // Метод для проверки валидности зарплаты
    public boolean isSalaryValid(String line) {
        List<String> parts = splitLine(line);
        String salaryStr = parts.get(3); // Идентификатор — это четвертое значение в строке
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

    // Метод для проверки на дубликаты идентификаторов
    public void checkForDuplicateIds(List<String> lines, Set<String> employeeIds, Set<String> managerIds, List<String> validLines) {
        Map<String, List<String>> idToLinesMap = new HashMap<>(); // Словарь для хранения строк по идентификаторам
        Set<String> duplicates = new HashSet<>(); // Множество для хранения дублирующихся идентификаторов
        Set<String> managerNames = new HashSet<>(); // Множество для хранения имен менеджеров

        for (String line : lines) {
            List<String> parts = splitLine(line);
            String employeeId = parts.get(1);
            String managerName = parts.get(2);

            idToLinesMap.computeIfAbsent(employeeId, k -> new ArrayList<>()).add(line);

            if (managerIds.contains(employeeId) && !managerNames.add(managerName)) {
                duplicates.add(employeeId);
                logger.logError(line);
            }
        }

        validLines.removeIf(line -> duplicates.contains(extractEmployeeId(line)));
    }

    String extractEmployeeId(String line) {
        List<String> parts = splitLine(line);
        return parts.get(1); // Идентификатор — это второе значение в строке
    }
}