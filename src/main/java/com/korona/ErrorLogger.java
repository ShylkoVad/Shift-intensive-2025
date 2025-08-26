package com.korona;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class ErrorLogger {
    private static ErrorLogger instance;
    private String logFilePath;

    private ErrorLogger(String logFilePath) {
        this.logFilePath = logFilePath;
    }

    public static synchronized ErrorLogger getInstance(String logFilePath) {
        if (instance == null) {
            instance = new ErrorLogger(logFilePath);
        }
        return instance;
    }

    public void logError(String message) {
        try (FileWriter fw = new FileWriter(logFilePath, true); // true для добавления в конец файла
             BufferedWriter bw = new BufferedWriter(fw);
             PrintWriter out = new PrintWriter(bw)) {
            out.println(message); // Записываем сообщение об ошибке
        } catch (IOException e) {
            System.err.println("Ошибка записи в файл журнала: " + e.getMessage());
        }
    }
}