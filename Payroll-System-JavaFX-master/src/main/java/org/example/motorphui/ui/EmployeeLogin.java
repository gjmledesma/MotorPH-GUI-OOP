package org.example.motorphui.ui;

public class EmployeeLogin extends javax.swing.JFrame {

    public EmployeeLogin() {
        initComponents();
    }

    @SuppressWarnings("unchecked")
    private void initComponents() {
        titleLabel = new javax.swing.JLabel();
        descriptionLabel = new javax.swing.JLabel();
        buttonPanel = new javax.swing.JPanel();
        employeedashboardButton = new javax.swing.JButton();
        landingpageButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("EmployeeLogin");

        titleLabel.setFont(new java.awt.Font("Segoe UI", 1, 20));
        titleLabel.setText("EmployeeLogin");

        descriptionLabel.setText("Swing-based screen generated for Apache NetBeans GUI editing.");

        buttonPanel.setLayout(new java.awt.GridLayout(0, 2, 8, 8));
        employeedashboardButton.setText("Open EmployeeDashboard");
        employeedashboardButton.addActionListener(evt -> openFrame(new EmployeeDashboard()));
        landingpageButton.setText("Open LandingPage");
        landingpageButton.addActionListener(evt -> openFrame(new LandingPage()));
        buttonPanel.add(employeedashboardButton);
        buttonPanel.add(landingpageButton);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(buttonPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 640, Short.MAX_VALUE)
                        .addComponent(titleLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(descriptionLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(titleLabel)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(descriptionLabel)
                    .addGap(18, 18, 18)
                    .addComponent(buttonPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 350, Short.MAX_VALUE)
                    .addContainerGap())
        );

        pack();
        setLocationRelativeTo(null);
    }

    protected void openFrame(javax.swing.JFrame frame) {
        frame.setVisible(true);
        dispose();
    }

    private javax.swing.JPanel buttonPanel;
    private javax.swing.JLabel descriptionLabel;
    private javax.swing.JLabel titleLabel;
    private javax.swing.JButton employeedashboardButton;
    private javax.swing.JButton landingpageButton;
}
