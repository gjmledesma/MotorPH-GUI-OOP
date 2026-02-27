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
        // FIX: Use the path relative to the 'resources' folder.
        // Assuming your file is located at: src/main/resources/org/example/landing_page.fxml
        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("/org/example/motorphui/landing_page.fxml"));

        // Debugging Code (Optional): Check if file is found before loading
        // if (fxmlLoader.getLocation() == null) {
        //    throw new IllegalStateException("Cannot find FXML file. Check the path in getResource()!");
        // }

        Scene scene = new Scene(fxmlLoader.load());

        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        
        stage.setTitle("MotorPH Employee Payroll System");

        // Resize stage to screen
        stage.setX(screenBounds.getMinX());
        stage.setY(screenBounds.getMinY());
        stage.setWidth(screenBounds.getWidth());
        stage.setHeight(screenBounds.getHeight());
        stage.setMaximized(true);
        
        // Set the scene for the stage (window) and show it
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(); 
    }
}