package org.example.motorphui.ui;

import javax.swing.SwingUtilities;

public final class MainApplication {

    private MainApplication() {
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LandingPage().setVisible(true));
    }
}
