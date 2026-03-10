package org.example.motorphui.ui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.Screen;
import javafx.geometry.Rectangle2D;
import java.io.IOException;
import java.net.URL;

public class MainApplication extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("/org/example/motorphui/landing_page.fxml"));

        // Set default window size to 1200x700 here
        Scene scene = new Scene(fxmlLoader.load(), 1200, 700);
        
        stage.setTitle("MotorPH Employee Payroll System");
        stage.setMinWidth(1200);
        stage.setMinHeight(700);
        
        // Set the scene for the stage (window) and show it
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(); 
    }
}