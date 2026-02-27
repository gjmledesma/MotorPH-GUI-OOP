module org.example.motorphui {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires javafx.graphics;

    opens org.example.motorphui.dao to javafx.graphics;
    opens org.example.motorphui.model to javafx.graphics;
    opens org.example.motorphui.service to javafx.graphics;
    opens org.example.motorphui.ui to javafx.graphics;
    
//    opens org.example.motorphui.dao to javafx.graphics;
//    opens org.example.motorphui.model to javafx.graphics;
//    opens org.example.motorphui.service to javafx.graphics;
//    opens org.example.motorphui.ui to javafx.graphics;
    
    exports org.example.motorphui.dao;
    exports org.example.motorphui.model;
    exports org.example.motorphui.service;
    exports org.example.motorphui.ui;
}
