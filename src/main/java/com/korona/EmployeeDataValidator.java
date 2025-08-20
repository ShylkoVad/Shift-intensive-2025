package com.korona;

public class EmployeeDataValidator {
    private ErrorLogger logger;

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
                logger.logError("Invalid salary for employee (empty value): " + line);
                return false; // Пустая зарплата
            }
            salary = Double.parseDouble(salaryStr);
            if (salary < 0) {
                logger.logError("Invalid salary for employee (negative value): " + line);
                return false; // Отрицательная зарплата
            }
        } catch (NumberFormatException e) {
            logger.logError("Invalid salary for employee (not a number): " + line);
            return false; // Некорректный формат зарплаты
        }

        return true; // Зарплата корректна
    }

}
