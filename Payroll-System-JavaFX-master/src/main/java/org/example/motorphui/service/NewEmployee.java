/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.example.motorphui.service;

import org.example.motorphui.model.Employee;

/**
 *
 * @author gabrielledesma
 */
public class NewEmployee extends Employee {
    
    public NewEmployee(String employeeNumber, String lastName, String firstName, String birthday, String address,
                         String phoneNumber, String sss, String philHealth, String tin, String pagIbig, String status,
                         String position, String immediateSupervisor, String basicSalary, String riceSubsidy,
                         String phoneAllowance, String clothingAllowance, String grossSemiMonthlyRate, String hourlyRate) {
        
        super(employeeNumber, lastName, firstName, birthday, address, phoneNumber, sss, philHealth, tin, pagIbig, 
              status, position, immediateSupervisor, basicSalary, riceSubsidy, phoneAllowance, 
              clothingAllowance, grossSemiMonthlyRate, hourlyRate);
    }
}
