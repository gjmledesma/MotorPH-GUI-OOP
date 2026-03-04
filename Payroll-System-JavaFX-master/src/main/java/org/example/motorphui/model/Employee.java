package org.example.motorphui.model;

public abstract class Employee {
    protected String employeeNumber;
    protected String lastName;
    protected String firstName;
    protected String birthday;
    protected String address;
    protected String phoneNumber;
    protected String sss;
    protected String philHealth;
    protected String tin;
    protected String pagIbig;
    protected String status;
    protected String position;
    protected String immediateSupervisor;
    protected String basicSalary;
    protected String riceSubsidy;
    protected String phoneAllowance;
    protected String clothingAllowance;
    protected String grossSemiMonthlyRate;
    protected String hourlyRate;

    public Employee(String employeeNumber, String lastName, String firstName, String birthday, String address,
                    String phoneNumber, String sss, String philHealth, String tin, String pagIbig, String status,
                    String position, String immediateSupervisor, String basicSalary, String riceSubsidy,
                    String phoneAllowance, String clothingAllowance, String grossSemiMonthlyRate, String hourlyRate) {
        this.employeeNumber = employeeNumber;
        this.lastName = lastName;
        this.firstName = firstName;
        this.birthday = birthday;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.sss = sss;
        this.philHealth = philHealth;
        this.tin = tin;
        this.pagIbig = pagIbig;
        this.status = status;
        this.position = position;
        this.immediateSupervisor = immediateSupervisor;
        this.basicSalary = basicSalary;
        this.riceSubsidy = riceSubsidy;
        this.phoneAllowance = phoneAllowance;
        this.clothingAllowance = clothingAllowance;
        this.grossSemiMonthlyRate = grossSemiMonthlyRate;
        this.hourlyRate = hourlyRate;
    }

    public String employeeNumberProperty() { return employeeNumber; }
    public String lastNameProperty() { return lastName; }
    public String firstNameProperty() { return firstName; }
    public String birthdayProperty() { return birthday; }
    public String addressProperty() { return address; }
    public String phoneNumberProperty() { return phoneNumber; }
    public String sssProperty() { return sss; }
    public String philHealthProperty() { return philHealth; }
    public String tinProperty() { return tin; }
    public String pagIbigProperty() { return pagIbig; }
    public String statusProperty() { return status; }
    public String positionProperty() { return position; }
    public String immediateSupervisorProperty() { return immediateSupervisor; }
    public String basicSalaryProperty() { return basicSalary; }
    public String riceSubsidyProperty() { return riceSubsidy; }
    public String phoneAllowanceProperty() { return phoneAllowance; }
    public String clothingAllowanceProperty() { return clothingAllowance; }
    public String grossSemiMonthlyRateProperty() { return grossSemiMonthlyRate; }
    public String hourlyRateProperty() { return hourlyRate; }

    public String getEmployeeNumber() { return employeeNumber; }
    public String getLastName() { return lastName; }
    public String getFirstName() { return firstName; }
    public String getBirthday() { return birthday; }
    public String getAddress() { return address; }
    public String getPhoneNumber() { return phoneNumber; }
    public String getSss() { return sss; }
    public String getPhilHealth() { return philHealth; }
    public String getTin() { return tin; }
    public String getPagIbig() { return pagIbig; }
    public String getStatus() { return status; }
    public String getPosition() { return position; }
    public String getImmediateSupervisor() { return immediateSupervisor; }
    public String getRiceSubsidy() { return riceSubsidy; }
    public String getPhoneAllowance() { return phoneAllowance; }
    public String getClothingAllowance() { return clothingAllowance; }
    public String getGrossSemiMonthlyRate() { return grossSemiMonthlyRate; }
    public String getHourlyRate() { return hourlyRate; }
    public String getBasicSalary() { return basicSalary; }

    public void setEmployeeNumber(String employeeNumber) { this.employeeNumber = employeeNumber; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public void setSss(String sss) { this.sss = sss; }
    public void setPhilHealth(String philHealth) { this.philHealth = philHealth; }
    public void setTin(String tin) { this.tin = tin; }
    public void setPagIbig(String pagIbig) { this.pagIbig = pagIbig; }
    public void setStatus(String status) { this.status = status; }
    public void setImmediateSupervisor(String immediateSupervisor) { this.immediateSupervisor = immediateSupervisor; }
    public void setBirthday(String birthday) { this.birthday = birthday; }
    public void setAddress(String address) { this.address = address; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public void setPosition(String position) { this.position = position; }
    public void setRiceSubsidy(String riceSubsidy) { this.riceSubsidy = riceSubsidy; }
    public void setPhoneAllowance(String phoneAllowance) { this.phoneAllowance = phoneAllowance; }
    public void setClothingAllowance(String clothingAllowance) { this.clothingAllowance = clothingAllowance; }
    public void setGrossSemiMonthlyRate(String grossSemiMonthlyRate) { this.grossSemiMonthlyRate = grossSemiMonthlyRate; }
    public void setHourlyRate(String hourlyRate) { this.hourlyRate = hourlyRate; }
    public void setBasicSalary(String basicSalary) { this.basicSalary = basicSalary; }
}
