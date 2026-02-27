/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.example.motorphui.service;

import org.example.motorphui.dao.ImpSalaryTaxCalculator;

/**
 *
 * @author gabrielledesma
 */
public class SalaryTaxCalculator implements ImpSalaryTaxCalculator {
    
    @Override
    public double calculateSSSContribution(double basicSalary) {
        if (basicSalary < 3250) return 135.00;
        else if (basicSalary < 3750) return 157.50;
        else if (basicSalary < 4250) return 180.00;
        else if (basicSalary < 4750) return 202.50;
        else if (basicSalary < 5250) return 225.00;
        else if (basicSalary < 5750) return 247.50;
        else if (basicSalary < 6250) return 270.00;
        else if (basicSalary < 6750) return 292.50;
        else if (basicSalary < 7250) return 315.00;
        else if (basicSalary < 7750) return 337.50;
        else if (basicSalary < 8250) return 360.00;
        else if (basicSalary < 8750) return 382.50;
        else if (basicSalary < 9250) return 405.00;
        else if (basicSalary < 9750) return 427.50;
        else if (basicSalary < 10250) return 450.00;
        else if (basicSalary < 10750) return 472.50;
        else if (basicSalary < 11250) return 495.00;
        else if (basicSalary < 11750) return 517.50;
        else if (basicSalary < 12250) return 540.00;
        else if (basicSalary < 12750) return 562.50;
        else if (basicSalary < 13250) return 585.00;
        else if (basicSalary < 13750) return 607.50;
        else if (basicSalary < 14250) return 630.00;
        else if (basicSalary < 14750) return 652.50;
        else if (basicSalary < 15250) return 675.00;
        else if (basicSalary < 15750) return 697.50;
        else if (basicSalary < 16250) return 720.00;
        else if (basicSalary < 16750) return 742.50;
        else if (basicSalary < 17250) return 765.00;
        else if (basicSalary < 17750) return 787.50;
        else if (basicSalary < 18250) return 810.00;
        else if (basicSalary < 18750) return 832.50;
        else if (basicSalary < 19250) return 855.00;
        else if (basicSalary < 19750) return 877.50;
        else if (basicSalary < 20250) return 900.00;
        else if (basicSalary < 20750) return 922.50;
        else if (basicSalary < 21250) return 945.00;
        else if (basicSalary < 21750) return 967.50;
        else if (basicSalary < 22250) return 990.00;
        else if (basicSalary < 22750) return 1012.50;
        else if (basicSalary < 23250) return 1035.00;
        else if (basicSalary < 23750) return 1057.50;
        else if (basicSalary < 24250) return 1080.00;
        else if (basicSalary < 24750) return 1102.50;
        else return 1125.00;
    }

    @Override
    public double calculatePhilHealthContribution(double basicSalary) {
        double premium = basicSalary * 0.03;
        if (premium > 1800) premium = 1800;
        return premium / 2;
    }
    
    @Override
    public double calculatePagibigContribution(double basicSalary) {
        if (basicSalary >= 1000 && basicSalary <= 1500) return basicSalary * 0.01;
        else if (basicSalary > 1500) return basicSalary * 0.02;
        else return 0;
    }

    @Override
    public double calculateWithholdingTax(double basicSalary, double sss, double philhealth, double pagibig) {
        double taxableIncome = basicSalary - (sss + philhealth + pagibig);
        double tax = 0;

        if (taxableIncome <= 20832) {
            tax = 0;
        } else if (taxableIncome < 33333) {
            tax = (taxableIncome - 20833) * 0.20;
        } else if (taxableIncome < 66667) {
            tax = 2500 + (taxableIncome - 33333) * 0.25;
        } else if (taxableIncome < 166667) {
            tax = (10833 + (taxableIncome - 66667)) * 0.30;
        } else if (taxableIncome < 666667) {
            tax = (40833.33 + (taxableIncome - 166667)) * 0.32;
        } else {
            tax = (200833.33 + (taxableIncome - 666667)) * 0.35;
        }
        return tax;
    }
}
