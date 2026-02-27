/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.example.motorphui.service;

/**
 *
 * @author gabrielledesma
 */
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.Month;
import org.example.motorphui.dao.AttendanceService;

public class AttendanceServiceCSV implements AttendanceService {

    private static final String CSV_PATH = "/org/example/motorphui/data/motorph_attendance_records.csv";

    @Override
    public double getMonthlyHours(String empNumber, String monthName, String year) {
        double totalHours = 0.0;
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(getClass().getResourceAsStream(CSV_PATH)))) {

            String line;
            boolean isFirstLine = true;
            while ((line = reader.readLine()) != null) {
                if (isFirstLine) { isFirstLine = false; continue; }
                
                String[] data = line.split(",");
                if (data.length >= 6 && data[0].trim().equals(empNumber)) {
                    if (isDateMatch(data[3].trim(), monthName, year)) {
                        double login = parseTimeToDecimal(data[4].trim());
                        double logout = parseTimeToDecimal(data[5].trim());
                        if (logout > login) {
                            totalHours += (logout - login);
                        }
                    }
                }
            }
        } catch (IOException | NullPointerException e) {
            System.err.println("Error reading attendance: " + e.getMessage());
        }
        return totalHours;
    }

    @Override
    public boolean isDateMatch(String dateStr, String monthName, String targetYear) {
        String[] parts = dateStr.split("/");
        if (parts.length != 3) return false;
        
        String fileMonth = parts[0].trim();
        String fileYear = parts[2].trim();
        
        int monthNum = Month.valueOf(monthName.toUpperCase()).getValue();
        String expectedMonth = String.format("%02d", monthNum);
        
        return fileMonth.equals(expectedMonth) && fileYear.equals(targetYear);
    }
    
    @Override
    public double parseTimeToDecimal(String time) {
        try {
            String[] parts = time.split(":");
            return Integer.parseInt(parts[0]) + (Integer.parseInt(parts[1]) / 60.0);
        } catch (Exception e) {
            return 0.0;
        }
    }
}