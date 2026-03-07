package org.example.motorphui.ui;

import org.example.motorphui.model.AllEmployee;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.text.NumberFormat;
import java.util.Locale;

/**
 * Controller for the Employee Payslip modal window.
 *
 * All data is pushed in by the parent (EmployeeViewSalary) via
 * {@link #setPayslipData} — no re-calculation happens here.
 */
public class EmployeePayslip {

    private static final NumberFormat PESO =
            NumberFormat.getCurrencyInstance(new Locale("fil", "PH"));

    // ── FXML — Header ────────────────────────────────────────────────────────
    @FXML private Label period_label;

    // ── FXML — Employee Info ──────────────────────────────────────────────────
    @FXML private Label ps_empnum_label;
    @FXML private Label ps_name_label;
    @FXML private Label ps_position_label;
    @FXML private Label ps_status_label;
    @FXML private Label ps_supervisor_label;

    @FXML private Label ps_sss_label;
    @FXML private Label ps_philhealth_label;
    @FXML private Label ps_tin_label;
    @FXML private Label ps_pagibig_label;

    // ── FXML — Earnings ───────────────────────────────────────────────────────
    @FXML private Label ps_hours_label;
    @FXML private Label ps_rate_label;
    @FXML private Label ps_basicpay_label;
    @FXML private Label ps_rice_label;
    @FXML private Label ps_phone_label;
    @FXML private Label ps_clothing_label;
    @FXML private Label ps_gross_label;

    // ── FXML — Deductions ─────────────────────────────────────────────────────
    @FXML private Label ps_sss_con_label;
    @FXML private Label ps_ph_con_label;
    @FXML private Label ps_pi_con_label;
    @FXML private Label ps_tax_label;
    @FXML private Label ps_totaldeduct_label;

    // ── FXML — Net Pay ────────────────────────────────────────────────────────
    @FXML private Label ps_net_label;

    // ── Public API ────────────────────────────────────────────────────────────

    /**
     * Called by {@link EmployeeViewSalary} before the modal is shown.
     * Populates every label on the payslip with the pre-computed values.
     */
    public void setPayslipData(AllEmployee emp,
                               String month,
                               double hours,
                               double gross,
                               double sss,
                               double ph,
                               double pi,
                               double tax,
                               double net) {

        double hourlyRate  = parse(emp.getHourlyRate());
        double basicPay    = hours * hourlyRate;
        double rice        = parse(emp.getRiceSubsidy());
        double phoneAllow  = parse(emp.getPhoneAllowance());
        double clothing    = parse(emp.getClothingAllowance());
        double totalDeduct = sss + ph + pi + tax;

        // Header
        period_label.setText(month + " 2024");

        // Employee info
        ps_empnum_label    .setText(emp.getEmployeeNumber());
        ps_name_label      .setText(emp.getFirstName() + " " + emp.getLastName());
        ps_position_label  .setText(emp.getPosition());
        ps_status_label    .setText(emp.getStatus());
        ps_supervisor_label.setText(emp.getImmediateSupervisor());
        ps_sss_label       .setText(emp.getSss());
        ps_philhealth_label.setText(emp.getPhilHealth());
        ps_tin_label       .setText(emp.getTin());
        ps_pagibig_label   .setText(emp.getPagIbig());

        // Earnings
        ps_hours_label   .setText(String.format("%.2f hrs", hours));
        ps_rate_label    .setText(PESO.format(hourlyRate));
        ps_basicpay_label.setText(PESO.format(basicPay));
        ps_rice_label    .setText(PESO.format(rice));
        ps_phone_label   .setText(PESO.format(phoneAllow));
        ps_clothing_label.setText(PESO.format(clothing));
        ps_gross_label   .setText(PESO.format(gross));

        // Deductions
        ps_sss_con_label   .setText(PESO.format(sss));
        ps_ph_con_label    .setText(PESO.format(ph));
        ps_pi_con_label    .setText(PESO.format(pi));
        ps_tax_label       .setText(PESO.format(tax));
        ps_totaldeduct_label.setText(PESO.format(totalDeduct));

        // Net pay
        ps_net_label.setText(PESO.format(net));
    }

    // ── Utility ───────────────────────────────────────────────────────────────

    private double parse(String v) {
        if (v == null) return 0.0;
        try { return Double.parseDouble(v.replace(",", "").trim()); }
        catch (NumberFormatException e) { return 0.0; }
    }
}
