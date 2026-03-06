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

        // Set default window size to 900x600 here
        Scene scene = new Scene(fxmlLoader.load(), 900, 600);
        
        stage.setTitle("MotorPH Employee Payroll System");

        // Optional: Set a minimum window size so the UI doesn't squish too much when scaled down
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