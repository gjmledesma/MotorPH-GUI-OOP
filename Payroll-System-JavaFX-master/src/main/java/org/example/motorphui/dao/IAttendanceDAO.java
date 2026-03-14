package org.example.motorphui.dao;

import org.example.motorphui.model.AttendanceRecord;
import javafx.collections.ObservableList;

/**
 * Data-access contract for attendance record operations.
 *
 */
public interface IAttendanceDAO {
    /** Returns every attendance record in the data store. */
    ObservableList<AttendanceRecord> getAllAttendanceRecords();

    /** Returns only the records belonging to the specified employee. */
    ObservableList<AttendanceRecord> getRecordsForEmployee(String empId);

    /**
     * Returns today's record for the given employee, or null if none exists.
     */
    AttendanceRecord getTodayRecord(String empId);

    /**
     * Appends a new time-in row for the employee.
     * @return the created record, or null on failure.
     */
    AttendanceRecord writeTimeIn(String empId, String lastName, String firstName);

    /**
     * Fills the log-out field for today's open record.
     * @return the updated record, or null on failure.
     */
    AttendanceRecord writeTimeOut(String empId);

    /**
     * Calculates the total hours worked in the specified month/year.
     */
    double getMonthlyHours(String empNumber, String monthName, String year);
}
