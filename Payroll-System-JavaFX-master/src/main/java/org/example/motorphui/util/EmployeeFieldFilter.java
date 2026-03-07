package org.example.motorphui.util;

import javafx.scene.control.TextFormatter;
import javafx.scene.control.TextField;

/**
 * Factory for {@link TextFormatter} filters used on employee form fields.
 *
 * Each filter intercepts every keystroke BEFORE it is applied to the field
 * and silently discards characters that are not allowed for that field type.
 * Returning {@code null} from the filter cancels the change; returning the
 * change object unchanged allows it through.
 *
 * Usage (in a controller's initialize()):
 * <pre>
 *   EmployeeFieldFilter.lettersOnly(lastNameField);
 *   EmployeeFieldFilter.digitsOnly(employeeNumberField);
 * </pre>
 */
public final class EmployeeFieldFilter {

    private EmployeeFieldFilter() {}

    // ── Public apply methods ──────────────────────────────────────────────────

    /**
     * Letters, spaces, hyphens, periods, apostrophes — for name fields.
     * Blocks all digit input.
     */
    public static void lettersOnly(TextField field) {
        field.setTextFormatter(new TextFormatter<>(change -> {
            String added = change.getText();
            if (added.isEmpty()) return change;           // deletions always allowed
            return added.matches("[A-Za-z .''\\-]*") ? change : null;
        }));
    }

    /**
     * Digits only — for Employee #, PhilHealth #, Pag-Ibig #.
     * Blocks all letter and symbol input.
     */
    public static void digitsOnly(TextField field) {
        field.setTextFormatter(new TextFormatter<>(change -> {
            String added = change.getText();
            if (added.isEmpty()) return change;
            return added.matches("\\d*") ? change : null;
        }));
    }

    /**
     * Digits and hyphens — for Phone Number, SSS #, TIN #.
     * Blocks letters and all other symbols.
     */
    public static void digitsAndHyphens(TextField field) {
        field.setTextFormatter(new TextFormatter<>(change -> {
            String added = change.getText();
            if (added.isEmpty()) return change;
            return added.matches("[\\d\\-]*") ? change : null;
        }));
    }

    /**
     * Digits and forward-slashes — for Birthday (MM/DD/YYYY).
     * Blocks letters and all other symbols.
     */
    public static void digitsAndSlash(TextField field) {
        field.setTextFormatter(new TextFormatter<>(change -> {
            String added = change.getText();
            if (added.isEmpty()) return change;
            return added.matches("[\\d/]*") ? change : null;
        }));
    }

    /**
     * Digits and a single decimal point — for monetary/rate fields
     * (Basic Salary, Rice Subsidy, Phone Allowance, Clothing Allowance, Hourly Rate).
     * Blocks letters and all other symbols.
     * Also prevents a second decimal point from being typed.
     */
    public static void digitsAndDecimal(TextField field) {
        field.setTextFormatter(new TextFormatter<>(change -> {
            String added = change.getText();
            if (added.isEmpty()) return change;
            if (!added.matches("[\\d.]*")) return null;
            // Prevent a second decimal point
            String result = change.getControlNewText();
            long dotCount = result.chars().filter(c -> c == '.').count();
            return dotCount <= 1 ? change : null;
        }));
    }

    /**
     * Letters and spaces only — for Position and Status fields.
     * Blocks all digit and symbol input.
     */
    public static void lettersAndSpaces(TextField field) {
        field.setTextFormatter(new TextFormatter<>(change -> {
            String added = change.getText();
            if (added.isEmpty()) return change;
            return added.matches("[A-Za-z ]*") ? change : null;
        }));
    }

    /**
     * Free text — no character is blocked.
     * Used for Address and Immediate Supervisor where mixed input is valid.
     */
    public static void freeText(TextField field) {
        // No formatter needed — this is a no-op, kept for readability at call sites.
    }
}
