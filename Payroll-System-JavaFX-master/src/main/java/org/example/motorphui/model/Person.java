package org.example.motorphui.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Abstract base class representing any person in the MotorPH system.
 *
 * OOP PRINCIPLES DEMONSTRATED:
 *   ABSTRACTION   — Declares abstract methods that subclasses must implement.
 *   ENCAPSULATION — All fields are private; accessed only via public getters/setters.
 *   INHERITANCE   — AllEmployee extends this class; RegularEmployee and
 *                   ProbationaryEmployee extend AllEmployee.
 *   POLYMORPHISM  — getFullName() is overloaded (two signatures);
 *                   abstract methods are overridden by every concrete subclass.
 */
public abstract class Person {

    // ── Private fields (ENCAPSULATION) ────────────────────────────────────────
    private final StringProperty firstName;
    private final StringProperty lastName;
    private final StringProperty birthday;

    // ── Constructor ───────────────────────────────────────────────────────────
    protected Person(String firstName, String lastName, String birthday) {
        this.firstName = new SimpleStringProperty(firstName);
        this.lastName  = new SimpleStringProperty(lastName);
        this.birthday  = new SimpleStringProperty(birthday);
    }

    // ── Abstract methods (ABSTRACTION + POLYMORPHISM via overriding) ──────────

    /**
     * Returns the human-readable role label displayed in the UI.
     * Examples: "Regular Employee", "Probationary Employee"
     */
    public abstract String getDisplayRole();

    /**
     * Returns the employment type string stored in the CSV.
     * Examples: "Regular", "Probationary"
     */
    public abstract String getEmployeeType();

    /**
     * Returns a multiplier applied to benefit calculations.
     *   Regular      → 1.00
     *   Probationary → 0.80
     */
    public abstract double getBenefitMultiplier();

    // ── JavaFX property accessors ─────────────────────────────────────────────
    public StringProperty firstNameProperty() { return firstName; }
    public StringProperty lastNameProperty()  { return lastName; }
    public StringProperty birthdayProperty()  { return birthday; }

    // ── Getters ───────────────────────────────────────────────────────────────
    public String getFirstName() { return firstName.get(); }
    public String getLastName()  { return lastName.get(); }
    public String getBirthday()  { return birthday.get(); }

    // ── Setters ───────────────────────────────────────────────────────────────
    public void setFirstName(String v) {
        if (v == null || v.isBlank()) throw new IllegalArgumentException("First name cannot be blank.");
        firstName.set(v.trim());
    }

    public void setLastName(String v) {
        if (v == null || v.isBlank()) throw new IllegalArgumentException("Last name cannot be blank.");
        lastName.set(v.trim());
    }

    public void setBirthday(String v) { birthday.set(v == null ? "" : v.trim()); }

    // ── Overloaded getFullName (POLYMORPHISM via method overloading) ──────────

    /**
     * Returns "FirstName LastName" (default format).
     */
    public String getFullName() {
        return firstName.get() + " " + lastName.get();
    }

    /**
     * Returns the full name using the provided separator between last and first name.
     * Example: getFullName(", ") → "Santos, Maria"
     *
     * @param separator string placed between last name and first name
     */
    public String getFullName(String separator) {
        return lastName.get() + separator + firstName.get();
    }

    /**
     * Returns the full name formatted for a specific display context.
     * formatType "LAST_FIRST" → "LastName, FirstName"
     * formatType "FIRST_LAST" → "FirstName LastName"  (default)
     *
     * @param separator  separator string
     * @param formatType "LAST_FIRST" or "FIRST_LAST"
     */
    public String getFullName(String separator, String formatType) {
        if ("LAST_FIRST".equalsIgnoreCase(formatType)) {
            return lastName.get() + separator + firstName.get();
        }
        return firstName.get() + separator + lastName.get();
    }

    @Override
    public String toString() {
        return getFullName() + " [" + getDisplayRole() + "]";
    }
}
