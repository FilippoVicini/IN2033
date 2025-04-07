package com.lancaster.gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import com.lancaster.database.myJDBC;

/**
 * UI panel for managing Lancaster system users.
 * Provides functionality for viewing, searching, adding, and deleting users.
 */
public class SettingsUI extends JPanel {
    private JTable usersTable;
    private DefaultTableModel tableModel;
    private JLabel statusLabel;
    private JTextField searchField;

    private Color primaryColor = new Color(47, 54, 64);
    private Color accentColor = new Color(86, 101, 115);
    private Color highlightColor = new Color(52, 152, 219);
    private Color backgroundColor = new Color(245, 246, 250);

    /**
     * Constructs the SettingsUI panel with user management interface.
     */
    public SettingsUI() {
        setLayout(new BorderLayout());
        setBackground(backgroundColor);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(backgroundColor);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        titlePanel.setOpaque(false);

        JLabel iconLabel = new JLabel("⚙️");
        iconLabel.setFont(new Font("Segoe UI", Font.PLAIN, 24));

        JLabel titleLabel = new JLabel("Users of Lancaster's");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(new Color(52, 73, 94));

        titlePanel.add(iconLabel);
        titlePanel.add(titleLabel);
        headerPanel.add(titlePanel, BorderLayout.WEST);

        JPanel actionsPanel = new JPanel(new BorderLayout(10, 0));
        actionsPanel.setOpaque(false);

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        searchPanel.setOpaque(false);
        searchField = new JTextField(15);
        searchField.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(200, 200, 200), 1, true),
                new EmptyBorder(5, 10, 5, 10)
        ));
        searchField.setToolTipText("Search for users by username");

        JButton searchButton = new JButton("Search");
        styleButton(searchButton, highlightColor);
        searchButton.setForeground(Color.BLACK);
        searchButton.addActionListener(e -> searchUsers());

        searchPanel.add(new JLabel("Search: "));
        searchPanel.add(searchField);
        searchPanel.add(searchButton);

        statusLabel = new JLabel("Loading data...");
        statusLabel.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        statusLabel.setForeground(accentColor);

        actionsPanel.add(searchPanel, BorderLayout.CENTER);
        actionsPanel.add(statusLabel, BorderLayout.EAST);

        headerPanel.add(actionsPanel, BorderLayout.EAST);

        String[] columns = {"ID", "Username", "Created Date"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        usersTable = new JTable(tableModel);
        styleTable(usersTable);

        JScrollPane scrollPane = new JScrollPane(usersTable);
        scrollPane.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(230, 230, 230), 1),
                new EmptyBorder(0, 0, 0, 0)
        ));
        scrollPane.setBackground(Color.WHITE);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 15));
        buttonPanel.setOpaque(false);

        JButton refreshButton = new JButton("Refresh");
        styleButton(refreshButton, Color.BLACK);
        refreshButton.setForeground(Color.BLACK);
        refreshButton.addActionListener(e -> loadUsersData());

        JButton newUserButton = new JButton("New User");
        styleButton(newUserButton, Color.BLACK);
        newUserButton.setForeground(Color.BLACK);
        newUserButton.addActionListener(e -> showNewUserDialog());

        buttonPanel.add(refreshButton);
        buttonPanel.add(newUserButton);

        JPanel cardPanel = new JPanel(new BorderLayout());
        cardPanel.setBackground(Color.WHITE);
        cardPanel.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(230, 230, 230), 1),
                new EmptyBorder(15, 15, 15, 15)
        ));

        JPanel cardHeader = new JPanel(new BorderLayout());
        cardHeader.setBackground(Color.WHITE);
        cardHeader.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(230, 230, 230)));
        cardHeader.setPreferredSize(new Dimension(cardHeader.getWidth(), 40));

        JLabel cardTitle = new JLabel("User Management");
        cardTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        cardTitle.setForeground(new Color(52, 73, 94));
        cardTitle.setBorder(BorderFactory.createEmptyBorder(0, 5, 10, 0));

        cardHeader.add(cardTitle, BorderLayout.WEST);

        cardPanel.add(cardHeader, BorderLayout.NORTH);
        cardPanel.add(scrollPane, BorderLayout.CENTER);

        add(headerPanel, BorderLayout.NORTH);
        add(cardPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        addTableContextMenu();

        loadUsersData();
    }

    /**
     * Applies styling to the table component.
     * @param table The table to style
     */
    private void styleTable(JTable table) {
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setRowHeight(35);
        table.setIntercellSpacing(new Dimension(10, 5));
        table.setFillsViewportHeight(true);
        table.setSelectionBackground(new Color(232, 242, 254));
        table.setSelectionForeground(Color.BLACK);
        table.setShowGrid(false);
        table.setGridColor(new Color(245, 245, 245));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setBackground(new Color(245, 246, 250));
        header.setForeground(new Color(52, 73, 94));
        header.setPreferredSize(new Dimension(header.getWidth(), 40));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(230, 230, 230)));
    }

    /**
     * Applies consistent styling to buttons.
     * @param button The button to style
     * @param color The background color for the button
     */
    private void styleButton(JButton button, Color color) {
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(
                        Math.max(0, color.getRed() - 20),
                        Math.max(0, color.getGreen() - 20),
                        Math.max(0, color.getBlue() - 20)
                ));
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(color);
            }
        });
    }

    /**
     * Searches for users based on the text in the search field.
     */
    private void searchUsers() {
        String searchTerm = searchField.getText().trim();
        if (searchTerm.isEmpty()) {
            loadUsersData();
            return;
        }

        statusLabel.setText("Searching...");
        statusLabel.setForeground(accentColor);

        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() {
                try (Connection connection = myJDBC.getConnection()) {
                    if (connection != null) {
                        String query = "SELECT * FROM users WHERE username LIKE ?";

                        PreparedStatement pstmt = connection.prepareStatement(query);
                        String pattern = "%" + searchTerm + "%";
                        pstmt.setString(1, pattern);

                        ResultSet rs = pstmt.executeQuery();

                        tableModel.setRowCount(0);
                        int count = 0;

                        while (rs.next()) {
                            Object[] row = {
                                    rs.getInt("id"),
                                    rs.getString("username"),
                                    rs.getString("created_at")
                            };
                            tableModel.addRow(row);
                            count++;
                        }

                        final int finalCount = count;
                        SwingUtilities.invokeLater(() -> {
                            statusLabel.setText(finalCount + " users found");
                            statusLabel.setForeground(new Color(46, 204, 113));
                        });
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    SwingUtilities.invokeLater(() -> {
                        statusLabel.setText("Search error: " + e.getMessage());
                        statusLabel.setForeground(new Color(231, 76, 60));
                    });
                }
                return null;
            }
        };

        worker.execute();
    }

    /**
     * Adds a context menu to the users table.
     */
    private void addTableContextMenu() {
        JPopupMenu contextMenu = new JPopupMenu();

        JMenuItem viewItem = new JMenuItem("View User Details");
        viewItem.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        viewItem.addActionListener(e -> {
            int selectedRow = usersTable.getSelectedRow();
            if (selectedRow >= 0) {
                int userId = (Integer) usersTable.getValueAt(selectedRow, 0);
                String username = (String) usersTable.getValueAt(selectedRow, 1);
                viewUserDetails(userId, username);
            } else {
                JOptionPane.showMessageDialog(this, "Please select a user first", "No Selection", JOptionPane.WARNING_MESSAGE);
            }
        });

        JMenuItem deleteItem = new JMenuItem("Delete User");
        deleteItem.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        deleteItem.addActionListener(e -> {
            int selectedRow = usersTable.getSelectedRow();
            if (selectedRow >= 0) {
                int userId = (Integer) usersTable.getValueAt(selectedRow, 0);
                String username = (String) usersTable.getValueAt(selectedRow, 1);
                deleteUser(userId, username);
            } else {
                JOptionPane.showMessageDialog(this, "Please select a user first", "No Selection", JOptionPane.WARNING_MESSAGE);
            }
        });

        JMenuItem changePasswordItem = new JMenuItem("Change Password");
        changePasswordItem.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        changePasswordItem.addActionListener(e -> {
            int selectedRow = usersTable.getSelectedRow();
            if (selectedRow >= 0) {
                int userId = (Integer) usersTable.getValueAt(selectedRow, 0);
                String username = (String) usersTable.getValueAt(selectedRow, 1);
                changePassword(userId, username);
            } else {
                JOptionPane.showMessageDialog(this, "Please select a user first", "No Selection", JOptionPane.WARNING_MESSAGE);
            }
        });

        contextMenu.add(viewItem);
        contextMenu.add(changePasswordItem);
        contextMenu.addSeparator();
        contextMenu.add(deleteItem);

        usersTable.setComponentPopupMenu(contextMenu);
    }

    /**
     * Displays detailed information about a selected user.
     * @param userId ID of the user to view
     * @param username Username of the user to view
     */
    private void viewUserDetails(int userId, String username) {
        JPanel detailsPanel = new JPanel();
        detailsPanel.setLayout(new BoxLayout(detailsPanel, BoxLayout.Y_AXIS));
        detailsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        try (Connection connection = myJDBC.getConnection()) {
            if (connection != null) {
                String query = "SELECT * FROM users WHERE id = ?";
                PreparedStatement pstmt = connection.prepareStatement(query);
                pstmt.setInt(1, userId);
                ResultSet rs = pstmt.executeQuery();

                if (rs.next()) {
                    addDetailRow(detailsPanel, "User ID:", String.valueOf(rs.getInt("id")));
                    addDetailRow(detailsPanel, "Username:", rs.getString("username"));
                    addDetailRow(detailsPanel, "Created Date:", rs.getString("created_at"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        JOptionPane.showMessageDialog(this, detailsPanel, "User Details: " + username, JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Adds a row with label and value to the details panel.
     * @param panel The panel to add the row to
     * @param label The label text
     * @param value The value text
     */
    private void addDetailRow(JPanel panel, String label, String value) {
        JPanel row = new JPanel(new BorderLayout(10, 0));
        row.setOpaque(false);

        JLabel labelComponent = new JLabel(label);
        labelComponent.setFont(new Font("Segoe UI", Font.BOLD, 14));
        labelComponent.setPreferredSize(new Dimension(100, 25));

        JLabel valueComponent = new JLabel(value);
        valueComponent.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        row.add(labelComponent, BorderLayout.WEST);
        row.add(valueComponent, BorderLayout.CENTER);
        panel.add(row);
        panel.add(Box.createVerticalStrut(5));
    }

    /**
     * Deletes a user after confirmation.
     * @param userId ID of the user to delete
     * @param username Username of the user to delete
     */
    private void deleteUser(int userId, String username) {
        JPanel confirmPanel = new JPanel(new BorderLayout(10, 10));
        confirmPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel iconLabel = new JLabel(UIManager.getIcon("OptionPane.warningIcon"));
        JLabel messageLabel = new JLabel("<html>Are you sure you want to delete user:<br><b>" + username + "</b> (ID: " + userId + ")?</html>");
        messageLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        confirmPanel.add(iconLabel, BorderLayout.WEST);
        confirmPanel.add(messageLabel, BorderLayout.CENTER);

        int confirm = JOptionPane.showConfirmDialog(this,
                confirmPanel,
                "Confirm Deletion",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            try (Connection connection = myJDBC.getConnection()) {
                if (connection != null) {
                    String query = "DELETE FROM users WHERE id = ?";
                    PreparedStatement pstmt = connection.prepareStatement(query);
                    pstmt.setInt(1, userId);
                    int rowsAffected = pstmt.executeUpdate();

                    if (rowsAffected > 0) {
                        loadUsersData();
                        statusLabel.setText("User " + username + " deleted successfully");
                        statusLabel.setForeground(new Color(46, 204, 113));
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error deleting user: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Opens a dialog for changing a user's password.
     * @param userId ID of the user to change password for
     * @param username Username of the user to change password for
     */
    private void changePassword(int userId, String username) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Change Password", true);
        dialog.setLayout(new BorderLayout());

        JPanel dialogHeader = new JPanel(new BorderLayout());
        dialogHeader.setBackground(highlightColor);
        dialogHeader.setPreferredSize(new Dimension(dialogHeader.getWidth(), 60));
        dialogHeader.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        JLabel dialogTitle = new JLabel("Change Password for " + username);
        dialogTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        dialogTitle.setForeground(Color.WHITE);
        dialogHeader.add(dialogTitle, BorderLayout.WEST);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));
        formPanel.setBackground(Color.WHITE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 5, 8, 5);
        gbc.weightx = 1.0;

        addFormField(formPanel, "New Password:", gbc, 0);
        JPasswordField newPasswordField = new JPasswordField(20);
        newPasswordField.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(200, 200, 200), 1, true),
                new EmptyBorder(8, 10, 8, 10)
        ));
        gbc.gridx = 1; gbc.gridy = 0;
        formPanel.add(newPasswordField, gbc);

        addFormField(formPanel, "Confirm Password:", gbc, 1);
        JPasswordField confirmPasswordField = new JPasswordField(20);
        confirmPasswordField.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(200, 200, 200), 1, true),
                new EmptyBorder(8, 10, 8, 10)
        ));
        gbc.gridx = 1; gbc.gridy = 1;
        formPanel.add(confirmPasswordField, gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(230, 230, 230)),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        JButton cancelButton = new JButton("Cancel");
        styleButton(cancelButton, new Color(190, 190, 190));

        JButton saveButton = new JButton("Save Changes");
        styleButton(saveButton, highlightColor);

        saveButton.addActionListener(e -> {
            String newPassword = new String(newPasswordField.getPassword());
            String confirmPassword = new String(confirmPasswordField.getPassword());

            if (newPassword.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Password cannot be empty", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (!newPassword.equals(confirmPassword)) {
                JOptionPane.showMessageDialog(dialog, "Passwords do not match", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                updateUserPassword(userId, newPassword);
                JOptionPane.showMessageDialog(dialog, "Password updated successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Error updating password: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        buttonPanel.add(cancelButton);
        buttonPanel.add(Box.createHorizontalStrut(10));
        buttonPanel.add(saveButton);

        dialog.add(dialogHeader, BorderLayout.NORTH);
        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.pack();
        dialog.setMinimumSize(new Dimension(400, dialog.getHeight()));
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    /**
     * Updates a user's password in the database.
     * @param userId ID of the user to update
     * @param newPassword New password to set
     * @throws Exception If there's an error during the database operation
     */
    private void updateUserPassword(int userId, String newPassword) throws Exception {
        String query = "UPDATE users SET password = ? WHERE id = ?";

        try (Connection connection = myJDBC.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, newPassword);
            pstmt.setInt(2, userId);
            pstmt.executeUpdate();
        }
    }

    /**
     * Adds a label to a form field.
     * @param panel The panel to add the field to
     * @param labelText The label text
     * @param gbc GridBagConstraints for layout
     * @param row Row position in the grid
     */
    private void addFormField(JPanel panel, String labelText, GridBagConstraints gbc, int row) {
        gbc.gridx = 0;
        gbc.gridy = row;
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        panel.add(label, gbc);
    }

    /**
     * Opens a dialog for creating a new user.
     */
    private void showNewUserDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "New User of Lancaster", true);
        dialog.setLayout(new BorderLayout());

        JPanel dialogHeader = new JPanel(new BorderLayout());
        dialogHeader.setBackground(highlightColor);
        dialogHeader.setPreferredSize(new Dimension(dialogHeader.getWidth(), 60));
        dialogHeader.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        JLabel dialogTitle = new JLabel("Add New User");
        dialogTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        dialogTitle.setForeground(Color.WHITE);
        dialogHeader.add(dialogTitle, BorderLayout.WEST);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));
        formPanel.setBackground(Color.WHITE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 5, 8, 5);
        gbc.weightx = 1.0;

        gbc.gridx = 0; gbc.gridy = 0;
        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        formPanel.add(usernameLabel, gbc);

        JTextField usernameField = new JTextField(20);
        usernameField.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(200, 200, 200), 1, true),
                new EmptyBorder(8, 10, 8, 10)
        ));
        usernameField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        gbc.gridx = 1; gbc.gridy = 0;
        formPanel.add(usernameField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        formPanel.add(passwordLabel, gbc);

        JPasswordField passwordField = new JPasswordField(20);
        passwordField.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(200, 200, 200), 1, true),
                new EmptyBorder(8, 10, 8, 10)
        ));
        passwordField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        gbc.gridx = 1; gbc.gridy = 1;
        formPanel.add(passwordField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        JLabel dateLabel = new JLabel("Current Date:");
        dateLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        formPanel.add(dateLabel, gbc);

        JLabel currentDateLabel = new JLabel("2025-04-07");
        currentDateLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        gbc.gridx = 1; gbc.gridy = 2;
        formPanel.add(currentDateLabel, gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(230, 230, 230)),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        JButton cancelButton = new JButton("Cancel");
        styleButton(cancelButton, new Color(190, 190, 190));

        JButton saveButton = new JButton("Save User");
        styleButton(saveButton, highlightColor);

        saveButton.addActionListener(e -> {
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword());

            if (username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(dialog,
                        "Please fill in all required fields",
                        "Validation Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                insertNewUser(username, password);
                loadUsersData();

                JOptionPane.showMessageDialog(dialog,
                        "New user has been added successfully!",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);

                dialog.dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog,
                        "Error adding user: " + ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        buttonPanel.add(cancelButton);
        buttonPanel.add(Box.createHorizontalStrut(10));
        buttonPanel.add(saveButton);

        dialog.add(dialogHeader, BorderLayout.NORTH);
        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.pack();
        dialog.setMinimumSize(new Dimension(400, dialog.getHeight()));
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    /**
     * Inserts a new user into the database.
     * @param username Username for the new user
     * @param password Password for the new user
     * @throws Exception If there's an error during the database operation
     */
    private void insertNewUser(String username, String password) throws Exception {
        String query = "INSERT INTO users (username, password, created_at) VALUES (?, ?, CURRENT_DATE())";

        try (Connection connection = myJDBC.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            pstmt.executeUpdate();
        }
    }

    /**
     * Loads user data from the database into the table.
     */
    private void loadUsersData() {
        statusLabel.setText("Loading data...");
        statusLabel.setForeground(accentColor);

        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() {
                try (Connection connection = myJDBC.getConnection()) {
                    if (connection != null) {
                        String dataQuery = "SELECT * FROM users";
                        Statement dataStmt = connection.createStatement();
                        ResultSet dataRs = dataStmt.executeQuery(dataQuery);

                        tableModel.setRowCount(0);
                        int count = 0;

                        while (dataRs.next()) {
                            Object[] row = {
                                    dataRs.getInt("id"),
                                    dataRs.getString("username"),
                                    dataRs.getString("created_at")
                            };
                            tableModel.addRow(row);
                            count++;
                        }

                        final int finalCount = count;
                        SwingUtilities.invokeLater(() -> {
                            statusLabel.setText(finalCount + " users loaded");
                            statusLabel.setForeground(new Color(46, 204, 113));
                        });
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    SwingUtilities.invokeLater(() -> {
                        statusLabel.setText("Error loading data: " + e.getMessage());
                        statusLabel.setForeground(new Color(231, 76, 60));
                    });
                }
                return null;
            }
        };

        worker.execute();
    }
}