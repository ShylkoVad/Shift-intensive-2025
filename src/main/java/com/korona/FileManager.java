package com.korona;

import java.io.File;
import java.io.IOException;

public class FileManager {

    public void createDepartmentFile(String departmentManagerID) {
        File file = new File(departmentManagerID + ".sb"); // Создаем файл с расширением .sb
        try {
            if (!file.exists()) {
                file.createNewFile(); // Создаем новый файл, если он не существует
                System.out.println("File created: " + file.getName());
            }
        } catch (IOException e) {
            System.err.println("Error creating file for department manager ID " + departmentManagerID + ": " + e.getMessage());
        }
    }
}
