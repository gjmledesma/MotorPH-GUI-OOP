package org.example.motorphui.ui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.InputStream;


public class LandingPage {
    @FXML private Button employees_button;
    @FXML private Button hr_button;
    @FXML private Button finance_button;
    @FXML private Button it_button;
    @FXML private Button exit_button;

    // Method for handling employee button click
    @FXML
    private void handleEmployeeButton() {
        try {
            // Load the employee_login.fxml
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/motorphui/employee_login.fxml"));
            Parent root = loader.load();  // Load the FXML file

            // Create a new scene with the loaded root (employee_login.fxml)
            Scene scene = new Scene(root);

            // Get the current stage (window) and set the new scene
            Stage stage = (Stage) employees_button.getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();  // Handle the exception
        }
    }

    // Method for handling human recourses button click
    @FXML
    private void handleHRButton() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/motorphui/hr_login.fxml"));
            Parent root = loader.load();  // Load the FXML file

            Scene scene = new Scene(root);

            // Get the current stage (window) and set the new scene
            Stage stage = (Stage) hr_button.getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();  // Handle the exception
        }
    }

    // Method for handling finance button click
    @FXML
    private void handleFinanceButton() {
        try {
            // Load the employee_login.fxml
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/motorphui/finance_login.fxml"));
            Parent root = loader.load();  // Load the FXML file

            // Create a new scene with the loaded root (employee_login.fxml)
            Scene scene = new Scene(root);

            // Get the current stage (window) and set the new scene
            Stage stage = (Stage) finance_button.getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();  // Handle the exception
        }
    }
    
    // Method for handling IT button click
    @FXML
    private void handleITButton() {
        try {
            // Load the employee_login.fxml
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/motorphui/it_login.fxml"));
            Parent root = loader.load();  // Load the FXML file

            // Create a new scene with the loaded root (employee_login.fxml)
            Scene scene = new Scene(root);

            // Get the current stage (window) and set the new scene
            Stage stage = (Stage) it_button.getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();  // Handle the exception
        }
    }
    
    // Method for handling the Exit button click
    @FXML
    private void handleExitButton() {
        Stage stage = (Stage) exit_button.getScene().getWindow();
        stage.close(); //closes the application
    }
    @FXML
    public void initialize() {
        try {
            String imagePath = "/org/example/motorphui/images/LandingPage1.jpg";
            InputStream stream = getClass().getResourceAsStream(imagePath);
            if (stream == null) {
                System.out.println("Image not found at: " + imagePath);
            } else {
                System.out.println("Image found successfully!");
                stream.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}