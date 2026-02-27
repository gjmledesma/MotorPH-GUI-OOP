/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.example.motorphui.dao;

/**
 *
 * @author gabrielledesma
 */
public interface ImpSalaryTaxCalculator {
    double calculateSSSContribution(double basicSalary);
    double calculatePhilHealthContribution(double basicSalary);
    double calculatePagibigContribution(double basicSalary);
    double calculateWithholdingTax(double basicSalary, double sss, double philHealth, double pagIbig);
}
