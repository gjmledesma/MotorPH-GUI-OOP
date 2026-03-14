package org.example.motorphui.dao;

import org.example.motorphui.service.AttendanceService;

public class AttendanceDAO extends AttendanceDAOImpl implements AttendanceService {

    private static final String CSV_PATH =
            "/org/example/motorphui/data/motorph_attendance_records.csv";

    // ── AttendanceService interface (kept for backward compat) ────────────────

    @Override
    public double getMonthlyHours(String empNumber, String monthName, String year) {
        return super.getMonthlyHours(empNumber, monthName, year);
    }

    @Override
    public boolean isDateMatch(String dateStr, String monthName, String targetYear) {
        return super.isDateMatch(dateStr, monthName, targetYear);
    }

    @Override
    public double parseTimeToDecimal(String time) {
        return super.parseTimeToDecimal(time);
    }
}
