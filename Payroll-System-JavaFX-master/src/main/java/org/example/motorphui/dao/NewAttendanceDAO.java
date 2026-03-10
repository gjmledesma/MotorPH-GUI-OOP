package org.example.motorphui.dao;

import org.example.motorphui.model.AttendanceRecord;
import javafx.collections.ObservableList;

/**
 * Backward-compatible wrapper for the original NewAttendanceDAO.
 *
 * Delegates all operations to {@link AttendanceDAOImpl}.  New code should
 * use {@link IAttendanceDAO} or {@link AttendanceDAOImpl} directly.
 *
 * OOP — INHERITANCE: Extends AttendanceDAOImpl.
 */
public class NewAttendanceDAO extends AttendanceDAOImpl {

    /** Returns all attendance records — delegates to AttendanceDAOImpl. */
    public ObservableList<AttendanceRecord> getAllAttendanceRecords() {
        return super.getAllAttendanceRecords();
    }
}
