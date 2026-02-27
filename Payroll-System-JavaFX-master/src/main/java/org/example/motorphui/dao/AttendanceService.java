/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.example.motorphui.dao;

/**
 *
 * @author gabrielledesma
 */
public interface AttendanceService {
    double getMonthlyHours(String empNumber, String monthName, String year);
    double parseTimeToDecimal(String time);
    boolean isDateMatch(String dateStr, String monthName, String targetYear);
}
