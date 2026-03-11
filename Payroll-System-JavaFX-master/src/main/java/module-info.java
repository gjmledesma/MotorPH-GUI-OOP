module org.example.motorphui {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires javafx.graphics;

    opens org.example.motorphui.dao     to javafx.graphics, javafx.fxml;
    opens org.example.motorphui.model   to javafx.graphics, javafx.fxml;
    opens org.example.motorphui.service to javafx.graphics, javafx.fxml;
    opens org.example.motorphui.ui      to javafx.graphics, javafx.fxml;
    opens org.example.motorphui.util    to javafx.graphics, javafx.fxml;
    opens org.example.motorphui.session to javafx.graphics, javafx.fxml;

    exports org.example.motorphui.dao;
    exports org.example.motorphui.model;
    exports org.example.motorphui.service;
    exports org.example.motorphui.ui;
    exports org.example.motorphui.util;
    exports org.example.motorphui.session;
}