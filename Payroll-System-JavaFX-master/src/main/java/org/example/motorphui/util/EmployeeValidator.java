package org.example.motorphui.util;

import java.util.ArrayList;
import java.util.List;

/**
 * Centralised validation rules for employee form fields.
 *
 * Every validate* method returns a human-readable error string
 * or null if the value is valid.  validateAll() collects every
 * failing rule so all errors can be shown at once.
 *
 * OOP PRINCIPLES DEMONSTRATED:
 *   ENCAPSULATION — Regex patterns are private constants; helpers are private static.
 *   POLYMORPHISM  — validateName() is overloaded with three signatures:
 *                     validateName(label, value)          — standard
 *                     validateName(value)                 — no label variant
 *                     validateName(label, value, maxLen)  — with max-length cap
 */
public class EmployeeValidator {

    // ── Regex patterns ────────────────────────────────────────────────────────
    private static final String EMP_NUM_PATTERN    = "\\d{1,6}";
    private static final String NAME_PATTERN       = "[A-Za-z][A-Za-z .''\\-]*";
    private static final String BIRTHDAY_PATTERN   = "\\d{1,2}/\\d{1,2}/\\d{4}";
    private static final String PHONE_PATTERN      = "\\d{3}-\\d{3}-\\d{3}";
    private static final String SSS_PATTERN        = "\\d{2}-\\d{7}-\\d";
    private static final String PHILHEALTH_PATTERN = "\\d{12}";
    private static final String TIN_PATTERN        = "\\d{3}-\\d{3}-\\d{3}-\\d{3}";
    private static final String PAGIBIG_PATTERN    = "\\d{12}";
    private static final String NUMERIC_PATTERN    = "\\d+(\\.\\d+)?";

    // ── Individual field validators ───────────────────────────────────────────

    public static String validateEmpNumber(String v) {
        if (blank(v))                    return "Employee Number is required.";
        if (!v.matches(EMP_NUM_PATTERN)) return "Employee Number must be digits only (e.g. 10001).";
        return null;
    }

    // OVERLOAD 1 — with label (original signature)
    public static String validateName(String label, String v) {
        if (blank(v))                  return label + " is required.";
        if (!v.matches(NAME_PATTERN))  return label + " may only contain letters, spaces, periods, or hyphens.";
        return null;
    }

    /**
     * OVERLOAD 2 — without label; uses "Name" as a default label.
     * POLYMORPHISM — overloaded method.
     */
    public static String validateName(String v) {
        return validateName("Name", v);
    }

    /**
     * OVERLOAD 3 — with label and a maximum character length.
     * POLYMORPHISM — overloaded method, adds the maxLength parameter.
     *
     * @param label   human-readable field name shown in error messages
     * @param v       the value to validate
     * @param maxLen  maximum allowed character count
     */
    public static String validateName(String label, String v, int maxLen) {
        String base = validateName(label, v);
        if (base != null) return base;
        if (v.length() > maxLen)
            return label + " must not exceed " + maxLen + " characters.";
        return null;
    }

    public static String validateAddress(String v) {
        if (blank(v)) return "Address is required.";
        return null;
    }

    public static String validatePhone(String v) {
        if (blank(v))                   return "Phone Number is required.";
        if (!v.matches(PHONE_PATTERN))  return "Phone Number must be in NNN-NNN-NNN format (e.g. 966-860-270).";
        return null;
    }

    public static String validateBirthday(String v) {
        if (blank(v))                      return "Birthday is required.";
        if (!v.matches(BIRTHDAY_PATTERN))  return "Birthday must be in M/D/YYYY format (e.g. 10/11/1983).";
        return null;
    }

    public static String validateSSS(String v) {
        if (blank(v))                return "SSS # is required.";
        if (!v.matches(SSS_PATTERN)) return "SSS # must be in NN-NNNNNNN-N format (e.g. 44-4506057-3).";
        return null;
    }

    public static String validatePhilHealth(String v) {
        if (blank(v))                       return "PhilHealth # is required.";
        if (!v.matches(PHILHEALTH_PATTERN)) return "PhilHealth # must be exactly 12 digits (e.g. 820126853951).";
        return null;
    }

    public static String validateTIN(String v) {
        if (blank(v))                return "TIN # is required.";
        if (!v.matches(TIN_PATTERN)) return "TIN # must be in NNN-NNN-NNN-NNN format (e.g. 442-605-657-000).";
        return null;
    }

    public static String validatePagIbig(String v) {
        if (blank(v))                    return "Pag-Ibig # is required.";
        if (!v.matches(PAGIBIG_PATTERN)) return "Pag-Ibig # must be exactly 12 digits (e.g. 691295330870).";
        return null;
    }

    public static String validateStatus(String v) {
        if (blank(v)) return "Status is required.";
        if (!v.equals("Regular") && !v.equals("Probationary"))
            return "Status must be either 'Regular' or 'Probationary'.";
        return null;
    }

    public static String validatePosition(String v) {
        if (blank(v)) return "Position is required.";
        return null;
    }

    public static String validateSupervisor(String v) {
        if (blank(v)) return "Immediate Supervisor is required (use 'N/A' if none).";
        return null;
    }

    public static String validateNumeric(String label, String v) {
        if (blank(v))                    return label + " is required.";
        if (!v.matches(NUMERIC_PATTERN)) return label + " must be a positive number (e.g. 90000 or 535.71).";
        return null;
    }

    // ── Bulk validator ────────────────────────────────────────────────────────

    public static List<String> validateAll(
            String empNum, String lastName, String firstName, String birthday,
            String address, String phone, String sss, String philHealth,
            String tin, String pagIbig, String status, String position,
            String supervisor, String basicSalary, String riceSubsidy,
            String phoneAllowance, String clothingAllowance, String hourlyRate,
            boolean checkEmpNumDuplicate, java.util.Set<String> existingEmpNumbers) {

        List<String> errors = new ArrayList<>();
        addIfError(errors, validateEmpNumber(empNum));

        if (checkEmpNumDuplicate && empNum != null
                && existingEmpNumbers != null
                && existingEmpNumbers.contains(empNum.trim())) {
            errors.add("Employee Number " + empNum.trim() + " already exists.");
        }

        addIfError(errors, validateName("Last Name",  lastName));
        addIfError(errors, validateName("First Name", firstName));
        addIfError(errors, validateBirthday(birthday));
        addIfError(errors, validateAddress(address));
        addIfError(errors, validatePhone(phone));
        addIfError(errors, validateSSS(sss));
        addIfError(errors, validatePhilHealth(philHealth));
        addIfError(errors, validateTIN(tin));
        addIfError(errors, validatePagIbig(pagIbig));
        addIfError(errors, validateStatus(status));
        addIfError(errors, validatePosition(position));
        addIfError(errors, validateSupervisor(supervisor));
        addIfError(errors, validateNumeric("Basic Salary",       basicSalary));
        addIfError(errors, validateNumeric("Rice Subsidy",       riceSubsidy));
        addIfError(errors, validateNumeric("Phone Allowance",    phoneAllowance));
        addIfError(errors, validateNumeric("Clothing Allowance", clothingAllowance));
        addIfError(errors, validateNumeric("Hourly Rate",        hourlyRate));
        return errors;
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private static boolean blank(String v) {
        return v == null || v.trim().isEmpty();
    }

    private static void addIfError(List<String> list, String error) {
        if (error != null) list.add(error);
    }
}
