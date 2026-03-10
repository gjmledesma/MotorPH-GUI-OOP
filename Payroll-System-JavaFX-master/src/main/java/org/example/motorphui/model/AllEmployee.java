package org.example.motorphui.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Abstract employee model.  Extends {@link Person} and holds all 16 remaining
 * employee data fields as PRIVATE StringProperties.
 *
 * OOP PRINCIPLES DEMONSTRATED:
 *   ENCAPSULATION — All fields are private; accessed via public property
 *                   accessors and explicit getters/setters with validation.
 *   INHERITANCE   — Extends Person; subclasses provide concrete employee types.
 *   ABSTRACTION   — Inherits abstract methods from Person; subclasses must
 *                   implement getDisplayRole(), getEmployeeType(),
 *                   and getBenefitMultiplier().
 *   POLYMORPHISM  — toCSVRow() is declared here and can be overridden;
 *                   getFullName() overloads are inherited from Person.
 */
public abstract class AllEmployee extends Person {

    // ── ALL FIELDS PRIVATE (ENCAPSULATION — Advanced) ─────────────────────────
    private final StringProperty employeeNumber;
    private final StringProperty address;
    private final StringProperty phoneNumber;
    private final StringProperty sss;
    private final StringProperty philHealth;
    private final StringProperty tin;
    private final StringProperty pagIbig;
    private final StringProperty status;
    private final StringProperty position;
    private final StringProperty immediateSupervisor;
    private final StringProperty basicSalary;
    private final StringProperty riceSubsidy;
    private final StringProperty phoneAllowance;
    private final StringProperty clothingAllowance;
    private final StringProperty grossSemiMonthlyRate;
    private final StringProperty hourlyRate;

    // ── Constructor ───────────────────────────────────────────────────────────
    protected AllEmployee(String employeeNumber, String lastName, String firstName,
                          String birthday, String address, String phoneNumber,
                          String sss, String philHealth, String tin, String pagIbig,
                          String status, String position, String immediateSupervisor,
                          String basicSalary, String riceSubsidy, String phoneAllowance,
                          String clothingAllowance, String grossSemiMonthlyRate,
                          String hourlyRate) {
        // Person holds firstName, lastName, birthday
        super(firstName, lastName, birthday);

        this.employeeNumber      = new SimpleStringProperty(employeeNumber);
        this.address             = new SimpleStringProperty(address);
        this.phoneNumber         = new SimpleStringProperty(phoneNumber);
        this.sss                 = new SimpleStringProperty(sss);
        this.philHealth          = new SimpleStringProperty(philHealth);
        this.tin                 = new SimpleStringProperty(tin);
        this.pagIbig             = new SimpleStringProperty(pagIbig);
        this.status              = new SimpleStringProperty(status);
        this.position            = new SimpleStringProperty(position);
        this.immediateSupervisor = new SimpleStringProperty(immediateSupervisor);
        this.basicSalary         = new SimpleStringProperty(basicSalary);
        this.riceSubsidy         = new SimpleStringProperty(riceSubsidy);
        this.phoneAllowance      = new SimpleStringProperty(phoneAllowance);
        this.clothingAllowance   = new SimpleStringProperty(clothingAllowance);
        this.grossSemiMonthlyRate= new SimpleStringProperty(grossSemiMonthlyRate);
        this.hourlyRate          = new SimpleStringProperty(hourlyRate);
    }

    // ── JavaFX Property accessors (required by PropertyValueFactory) ──────────
    public StringProperty employeeNumberProperty()      { return employeeNumber; }
    public StringProperty addressProperty()             { return address; }
    public StringProperty phoneNumberProperty()         { return phoneNumber; }
    public StringProperty sssProperty()                 { return sss; }
    public StringProperty philHealthProperty()          { return philHealth; }
    public StringProperty tinProperty()                 { return tin; }
    public StringProperty pagIbigProperty()             { return pagIbig; }
    public StringProperty statusProperty()              { return status; }
    public StringProperty positionProperty()            { return position; }
    public StringProperty immediateSupervisorProperty() { return immediateSupervisor; }
    public StringProperty basicSalaryProperty()         { return basicSalary; }
    public StringProperty riceSubsidyProperty()         { return riceSubsidy; }
    public StringProperty phoneAllowanceProperty()      { return phoneAllowance; }
    public StringProperty clothingAllowanceProperty()   { return clothingAllowance; }
    public StringProperty grossSemiMonthlyRateProperty(){ return grossSemiMonthlyRate; }
    public StringProperty hourlyRateProperty()          { return hourlyRate; }

    // ── Getters ───────────────────────────────────────────────────────────────
    public String getEmployeeNumber()      { return employeeNumber.get(); }
    public String getAddress()             { return address.get(); }
    public String getPhoneNumber()         { return phoneNumber.get(); }
    public String getSss()                 { return sss.get(); }
    public String getPhilHealth()          { return philHealth.get(); }
    public String getTin()                 { return tin.get(); }
    public String getPagIbig()             { return pagIbig.get(); }
    public String getStatus()              { return status.get(); }
    public String getPosition()            { return position.get(); }
    public String getImmediateSupervisor() { return immediateSupervisor.get(); }
    public String getBasicSalary()         { return basicSalary.get(); }
    public String getRiceSubsidy()         { return riceSubsidy.get(); }
    public String getPhoneAllowance()      { return phoneAllowance.get(); }
    public String getClothingAllowance()   { return clothingAllowance.get(); }
    public String getGrossSemiMonthlyRate(){ return grossSemiMonthlyRate.get(); }
    public String getHourlyRate()          { return hourlyRate.get(); }

    // ── Setters with validation (ENCAPSULATION — Advanced) ───────────────────
    public void setEmployeeNumber(String v) {
        if (v == null || v.isBlank()) throw new IllegalArgumentException("Employee number cannot be blank.");
        employeeNumber.set(v.trim());
    }
    public void setAddress(String v)             { address.set(v == null ? "" : v); }
    public void setPhoneNumber(String v)         { phoneNumber.set(v == null ? "" : v.trim()); }
    public void setSss(String v)                 { sss.set(v == null ? "" : v.trim()); }
    public void setPhilHealth(String v)          { philHealth.set(v == null ? "" : v.trim()); }
    public void setTin(String v)                 { tin.set(v == null ? "" : v.trim()); }
    public void setPagIbig(String v)             { pagIbig.set(v == null ? "" : v.trim()); }
    public void setStatus(String v)              { status.set(v == null ? "" : v.trim()); }
    public void setPosition(String v)            { position.set(v == null ? "" : v.trim()); }
    public void setImmediateSupervisor(String v) { immediateSupervisor.set(v == null ? "" : v); }
    public void setBasicSalary(String v)         { basicSalary.set(v == null ? "0" : v.trim()); }
    public void setRiceSubsidy(String v)         { riceSubsidy.set(v == null ? "0" : v.trim()); }
    public void setPhoneAllowance(String v)      { phoneAllowance.set(v == null ? "0" : v.trim()); }
    public void setClothingAllowance(String v)   { clothingAllowance.set(v == null ? "0" : v.trim()); }
    public void setGrossSemiMonthlyRate(String v){ grossSemiMonthlyRate.set(v == null ? "0" : v.trim()); }
    public void setHourlyRate(String v)          { hourlyRate.set(v == null ? "0" : v.trim()); }

    // ── CSV serialisation helper ───────────────────────────────────────────────
    /**
     * Serialises all 19 employee fields to a single CSV row in the format
     * expected by motorph_employee_data.csv.  May be overridden by subclasses.
     */
    public String toCSVRow() {
        return String.join(",",
                getEmployeeNumber(), getLastName(), getFirstName(),
                getBirthday(), getAddress(), getPhoneNumber(),
                getSss(), getPhilHealth(), getTin(), getPagIbig(),
                getStatus(), getPosition(), getImmediateSupervisor(),
                getBasicSalary(), getRiceSubsidy(), getPhoneAllowance(),
                getClothingAllowance(), getGrossSemiMonthlyRate(), getHourlyRate()
        );
    }

    /**
     * Factory method — creates the correct concrete subtype based on status.
     * "Regular" → RegularEmployee, anything else → ProbationaryEmployee.
     */
    public static AllEmployee create(String employeeNumber, String lastName,
            String firstName, String birthday, String address, String phoneNumber,
            String sss, String philHealth, String tin, String pagIbig,
            String status, String position, String immediateSupervisor,
            String basicSalary, String riceSubsidy, String phoneAllowance,
            String clothingAllowance, String grossSemiMonthlyRate, String hourlyRate) {

        if ("Regular".equalsIgnoreCase(status != null ? status.trim() : "")) {
            return new RegularEmployee(employeeNumber, lastName, firstName, birthday,
                    address, phoneNumber, sss, philHealth, tin, pagIbig, status,
                    position, immediateSupervisor, basicSalary, riceSubsidy,
                    phoneAllowance, clothingAllowance, grossSemiMonthlyRate, hourlyRate);
        }
        return new ProbationaryEmployee(employeeNumber, lastName, firstName, birthday,
                address, phoneNumber, sss, philHealth, tin, pagIbig, status,
                position, immediateSupervisor, basicSalary, riceSubsidy,
                phoneAllowance, clothingAllowance, grossSemiMonthlyRate, hourlyRate);
    }
}
