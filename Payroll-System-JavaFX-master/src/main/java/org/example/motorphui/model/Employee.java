package org.example.motorphui.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import org.example.motorphui.service.NewEmployee;

public abstract class Employee {
    protected final StringProperty employeeNumber;
    protected final StringProperty lastName;
    protected final StringProperty firstName;
    protected final StringProperty birthday;
    protected final StringProperty address;
    protected final StringProperty phoneNumber;
    protected final StringProperty sss;
    protected final StringProperty philHealth;
    protected final StringProperty tin;
    protected final StringProperty pagIbig;
    protected final StringProperty status;
    protected final StringProperty position;
    protected final StringProperty immediateSupervisor;
    protected final StringProperty basicSalary;  // Added back
    protected final StringProperty riceSubsidy;
    protected final StringProperty phoneAllowance;
    protected final StringProperty clothingAllowance;
    protected final StringProperty grossSemiMonthlyRate;
    protected final StringProperty hourlyRate;

    // Constructor
    public Employee(String employeeNumber, String lastName, String firstName, String birthday, String address,
                    String phoneNumber, String sss, String philHealth, String tin, String pagIbig, String status,
                    String position, String immediateSupervisor, String basicSalary, String riceSubsidy,
                    String phoneAllowance, String clothingAllowance, String grossSemiMonthlyRate, String hourlyRate) {
        this.employeeNumber = new SimpleStringProperty(employeeNumber);
        this.lastName = new SimpleStringProperty(lastName);
        this.firstName = new SimpleStringProperty(firstName);
        this.birthday = new SimpleStringProperty(birthday);
        this.address = new SimpleStringProperty(address);
        this.phoneNumber = new SimpleStringProperty(phoneNumber);
        this.sss = new SimpleStringProperty(sss);
        this.philHealth = new SimpleStringProperty(philHealth);
        this.tin = new SimpleStringProperty(tin);
        this.pagIbig = new SimpleStringProperty(pagIbig);
        this.status = new SimpleStringProperty(status);
        this.position = new SimpleStringProperty(position);
        this.immediateSupervisor = new SimpleStringProperty(immediateSupervisor);
        this.basicSalary = new SimpleStringProperty(basicSalary);
        this.riceSubsidy = new SimpleStringProperty(riceSubsidy);
        this.phoneAllowance = new SimpleStringProperty(phoneAllowance);
        this.clothingAllowance = new SimpleStringProperty(clothingAllowance);
        this.grossSemiMonthlyRate = new SimpleStringProperty(grossSemiMonthlyRate);
        this.hourlyRate = new SimpleStringProperty(hourlyRate);
    }

    // Getters and Setters for all properties
    public StringProperty employeeNumberProperty() { return employeeNumber; }
    public StringProperty lastNameProperty() { return lastName; }
    public StringProperty firstNameProperty() { return firstName; }
    public StringProperty birthdayProperty() { return birthday; }
    public StringProperty addressProperty() { return address; }
    public StringProperty phoneNumberProperty() { return phoneNumber; }
    public StringProperty sssProperty() { return sss; }
    public StringProperty philHealthProperty() { return philHealth; }
    public StringProperty tinProperty() { return tin; }
    public StringProperty pagIbigProperty() { return pagIbig; }
    public StringProperty statusProperty() { return status; }
    public StringProperty positionProperty() { return position; }
    public StringProperty immediateSupervisorProperty() { return immediateSupervisor; }
    public StringProperty basicSalaryProperty() { return basicSalary; }  // Getter for basicSalary
    public StringProperty riceSubsidyProperty() { return riceSubsidy; }
    public StringProperty phoneAllowanceProperty() { return phoneAllowance; }
    public StringProperty clothingAllowanceProperty() { return clothingAllowance; }
    public StringProperty grossSemiMonthlyRateProperty() { return grossSemiMonthlyRate; }
    public StringProperty hourlyRateProperty() { return hourlyRate; }

    // Getters for the fields
    public String getEmployeeNumber() { return employeeNumber.get(); }
    public String getLastName() { return lastName.get(); }
    public String getFirstName() { return firstName.get(); }
    public String getBirthday() { return birthday.get(); }
    public String getAddress() { return address.get(); }
    public String getPhoneNumber() { return phoneNumber.get(); }
    public String getSss() { return sss.get(); }
    public String getPhilHealth() { return philHealth.get(); }
    public String getTin() { return tin.get(); }
    public String getPagIbig() { return pagIbig.get(); }
    public String getStatus() { return status.get(); }
    public String getPosition() { return position.get(); }
    public String getImmediateSupervisor() { return immediateSupervisor.get(); }
    public String getRiceSubsidy() { return riceSubsidy.get(); }
    public String getPhoneAllowance() { return phoneAllowance.get(); }
    public String getClothingAllowance() { return clothingAllowance.get(); }
    public String getGrossSemiMonthlyRate() { return grossSemiMonthlyRate.get(); }
    public String getHourlyRate() { return hourlyRate.get(); }
    public String getBasicSalary() { return basicSalary.get(); }  // Getter for basicSalary

    // Setters for all properties
    public void setEmployeeNumber(String employeeNumber) { this.employeeNumber.set(employeeNumber); }
    public void setLastName(String lastName) { this.lastName.set(lastName); }
    public void setFirstName(String firstName) { this.firstName.set(firstName); }
    public void setSss(String sss) { this.sss.set(sss); }
    public void setPhilHealth(String philHealth) { this.philHealth.set(philHealth); }
    public void setTin(String tin) { this.tin.set(tin); }
    public void setPagIbig(String pagIbig) { this.pagIbig.set(pagIbig); }
    public void setStatus(String status) { this.status.set(status); }
    public void setImmediateSupervisor(String immediateSupervisor) { this.immediateSupervisor.set(immediateSupervisor); }
    public void setBirthday(String birthday) { this.birthday.set(birthday); }
    public void setAddress(String address) { this.address.set(address); }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber.set(phoneNumber); }
    public void setPosition(String position) { this.position.set(position); }
    public void setRiceSubsidy(String riceSubsidy) { this.riceSubsidy.set(riceSubsidy); }
    public void setPhoneAllowance(String phoneAllowance) { this.phoneAllowance.set(phoneAllowance); }
    public void setClothingAllowance(String clothingAllowance) { this.clothingAllowance.set(clothingAllowance); }
    public void setGrossSemiMonthlyRate(String grossSemiMonthlyRate) { this.grossSemiMonthlyRate.set(grossSemiMonthlyRate); }
    public void setHourlyRate(String hourlyRate) { this.hourlyRate.set(hourlyRate); }
    public void setBasicSalary(String basicSalary) { this.basicSalary.set(basicSalary); }  // Setter for basicSalary
}
