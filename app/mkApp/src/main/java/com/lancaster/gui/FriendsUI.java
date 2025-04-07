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
 * UI panel for managing Lancaster's Friends.
 * Provides a table display of friends with search capability and CRUD operations.
 */
public class FriendsUI extends JPanel {
    private JTable friendsTable;
    private DefaultTableModel tableModel;
    private JLabel statusLabel;
    private JTextField searchField;
    public static int friendsNum;

    private Color primaryColor = new Color(47, 54, 64);
    private Color accentColor = new Color(86, 101, 115);
    private Color highlightColor = new Color(52, 152, 219);
    private Color backgroundColor = new Color(245, 246, 250);

    /**
     * Constructs the FriendsUI panel with search functionality and friends table.
     */
    public FriendsUI() {
        setLayout(new BorderLayout());
        setBackground(backgroundColor);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(backgroundColor);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        titlePanel.setOpaque(false);

        JLabel iconLabel = new JLabel("👥");
        iconLabel.setFont(new Font("Segoe UI", Font.PLAIN, 24));

        JLabel titleLabel = new JLabel("Friends of Lancaster's");
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
        searchField.setToolTipText("Search for friends by name or email");

        JButton searchButton = new JButton("Search");
        styleButton(searchButton, highlightColor);
        searchButton.setForeground(Color.BLACK);
        searchButton.addActionListener(e -> searchFriends());

        searchPanel.add(new JLabel("Search: "));
        searchPanel.add(searchField);
        searchPanel.add(searchButton);

        statusLabel = new JLabel("Loading data...");
        statusLabel.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        statusLabel.setForeground(accentColor);

        actionsPanel.add(searchPanel, BorderLayout.CENTER);
        actionsPanel.add(statusLabel, BorderLayout.EAST);

        headerPanel.add(actionsPanel, BorderLayout.EAST);

        String[] columns = {"ID", "Name", "Email", "Status", "Join Date", "Bookings"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 0 || columnIndex == 5) {
                    return Integer.class;
                }
                return String.class;
            }
        };

        friendsTable = new JTable(tableModel);
        styleTable(friendsTable);

        JScrollPane scrollPane = new JScrollPane(friendsTable);
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
        refreshButton.addActionListener(e -> loadFriendsData());

        JButton newFriendButton = new JButton("New Friend");
        styleButton(newFriendButton, Color.BLACK);
        newFriendButton.setForeground(Color.BLACK);
        newFriendButton.addActionListener(e -> showNewUserDialog());

        buttonPanel.add(refreshButton);
        buttonPanel.add(newFriendButton);

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

        JLabel cardTitle = new JLabel("Friends Directory");
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

        loadFriendsData();
    }

    /**
     * Applies styling to the friends table.
     *
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
     * Applies styling to a button with hover effects.
     *
     * @param button The button to style
     * @param color The base color for the button
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
     * Adds a context menu to the friends table.
     */
    private void addTableContextMenu() {
        JPopupMenu contextMenu = new JPopupMenu();

        JMenuItem viewItem = new JMenuItem("View Details");
        viewItem.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        viewItem.addActionListener(e -> {
            int selectedRow = friendsTable.getSelectedRow();
            if (selectedRow >= 0) {
                int friendId = (Integer) friendsTable.getValueAt(selectedRow, 0);
                String name = (String) friendsTable.getValueAt(selectedRow, 1);
                viewFriendDetails(friendId, name);
            } else {
                JOptionPane.showMessageDialog(this, "Please select a friend first", "No Selection", JOptionPane.WARNING_MESSAGE);
            }
        });

        JMenuItem editItem = new JMenuItem("Edit Friend");
        editItem.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        editItem.addActionListener(e -> {
            int selectedRow = friendsTable.getSelectedRow();
            if (selectedRow >= 0) {
                JOptionPane.showMessageDialog(this, "Edit functionality would go here", "Not Implemented", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Please select a friend first", "No Selection", JOptionPane.WARNING_MESSAGE);
            }
        });

        JMenuItem deleteItem = new JMenuItem("Delete Friend");
        deleteItem.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        deleteItem.addActionListener(e -> {
            int selectedRow = friendsTable.getSelectedRow();
            if (selectedRow >= 0) {
                int friendId = (Integer) friendsTable.getValueAt(selectedRow, 0);
                String name = (String) friendsTable.getValueAt(selectedRow, 1);
                deleteFriend(friendId, name);
            } else {
                JOptionPane.showMessageDialog(this, "Please select a friend first", "No Selection", JOptionPane.WARNING_MESSAGE);
            }
        });

        contextMenu.add(viewItem);
        contextMenu.add(editItem);
        contextMenu.addSeparator();
        contextMenu.add(deleteItem);

        friendsTable.setComponentPopupMenu(contextMenu);
    }

    /**
     * Displays details of the selected friend.
     *
     * @param friendId The ID of the selected friend
     * @param name The name of the selected friend
     */
    private void viewFriendDetails(int friendId, String name) {
        JPanel detailsPanel = new JPanel();
        detailsPanel.setLayout(new BoxLayout(detailsPanel, BoxLayout.Y_AXIS));
        detailsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        try (Connection connection = myJDBC.getConnection()) {
            if (connection != null) {
                String query = "SELECT * FROM friends_lancaster WHERE friend_id = ?";
                PreparedStatement pstmt = connection.prepareStatement(query);
                pstmt.setInt(1, friendId);
                ResultSet rs = pstmt.executeQuery();

                if (rs.next()) {
                    addDetailRow(detailsPanel, "Friend ID:", String.valueOf(rs.getInt("friend_id")));
                    addDetailRow(detailsPanel, "Name:", rs.getString("name"));
                    addDetailRow(detailsPanel, "Email:", rs.getString("email"));
                    addDetailRow(detailsPanel, "Status:", rs.getInt("subscription_status") == 1 ? "Active" : "Inactive");
                    addDetailRow(detailsPanel, "Join Date:", rs.getDate("join_date").toString());
                    addDetailRow(detailsPanel, "Bookings:", String.valueOf(rs.getInt("booking_num")));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        JOptionPane.showMessageDialog(this, detailsPanel, "Friend Details: " + name, JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Adds a row with label and value to a details panel.
     *
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
     * Deletes a friend from the database after confirmation.
     *
     * @param friendId The ID of the friend to delete
     * @param name The name of the friend to delete
     */
    private void deleteFriend(int friendId, String name) {
        JPanel confirmPanel = new JPanel(new BorderLayout(10, 10));
        confirmPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel iconLabel = new JLabel(UIManager.getIcon("OptionPane.warningIcon"));
        JLabel messageLabel = new JLabel("<html>Are you sure you want to delete friend:<br><b>" + name + "</b> (ID: " + friendId + ")?</html>");
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
                    String query = "DELETE FROM friends_lancaster WHERE friend_id = ?";
                    PreparedStatement pstmt = connection.prepareStatement(query);
                    pstmt.setInt(1, friendId);
                    int rowsAffected = pstmt.executeUpdate();

                    if (rowsAffected > 0) {
                        loadFriendsData();
                        statusLabel.setText("Friend " + name + " deleted successfully");
                        statusLabel.setForeground(new Color(46, 204, 113));
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error deleting friend: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Searches for friends based on the search term.
     */
    private void searchFriends() {
        String searchTerm = searchField.getText().trim();
        if (searchTerm.isEmpty()) {
            loadFriendsData();
            return;
        }

        try (Connection connection = myJDBC.getConnection()) {
            if (connection != null) {
                String query = "SELECT friend_id, name, email, subscription_status, join_date, booking_num " +
                        "FROM friends_lancaster " +
                        "WHERE name LIKE ? OR email LIKE ?";

                PreparedStatement pstmt = connection.prepareStatement(query);
                String pattern = "%" + searchTerm + "%";
                pstmt.setString(1, pattern);
                pstmt.setString(2, pattern);

                ResultSet rs = pstmt.executeQuery();

                tableModel.setRowCount(0);
                int count = 0;

                while (rs.next()) {
                    Object[] row = {
                            rs.getInt("friend_id"),
                            rs.getString("name"),
                            rs.getString("email"),
                            rs.getInt("subscription_status") == 1 ? "Active" : "Inactive",
                            rs.getDate("join_date"),
                            rs.getInt("booking_num")
                    };
                    tableModel.addRow(row);
                    count++;
                }

                statusLabel.setText(count + " friends found");
                statusLabel.setForeground(new Color(46, 204, 113));
            }
        } catch (Exception e) {
            e.printStackTrace();
            statusLabel.setText("Search error: " + e.getMessage());
            statusLabel.setForeground(new Color(231, 76, 60));
        }
    }

    /**
     * Opens a dialog to create a new friend.
     */
    private void showNewUserDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "New Friend of Lancaster", true);
        dialog.setLayout(new BorderLayout());

        JPanel dialogHeader = new JPanel(new BorderLayout());
        dialogHeader.setBackground(highlightColor);
        dialogHeader.setPreferredSize(new Dimension(dialogHeader.getWidth(), 60));
        dialogHeader.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        JLabel dialogTitle = new JLabel("Add New Friend");
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

        addFormField(formPanel, "Name:", gbc, 0);
        JTextField nameField = createStyledTextField();
        gbc.gridx = 1; gbc.gridy = 0;
        formPanel.add(nameField, gbc);

        addFormField(formPanel, "Email:", gbc, 1);
        JTextField emailField = createStyledTextField();
        gbc.gridx = 1; gbc.gridy = 1;
        formPanel.add(emailField, gbc);

        addFormField(formPanel, "Status:", gbc, 2);
        JComboBox<String> statusCombo = new JComboBox<>(new String[]{"Active", "Inactive"});
        statusCombo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        statusCombo.setBackground(Color.WHITE);
        gbc.gridx = 1; gbc.gridy = 2;
        formPanel.add(statusCombo, gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(230, 230, 230)),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        JButton cancelButton = new JButton("Cancel");
        styleButton(cancelButton, new Color(190, 190, 190));

        JButton saveButton = new JButton("Save Friend");
        styleButton(saveButton, highlightColor);

        saveButton.addActionListener(e -> {
            try {
                String name = nameField.getText().trim();
                String email = emailField.getText().trim();
                int status = statusCombo.getSelectedIndex() == 0 ? 1 : 0;

                if (name.isEmpty() || email.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog,
                            "Please fill in all required fields",
                            "Validation Error",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                insertNewUser(name, email, status);
                loadFriendsData();

                JOptionPane.showMessageDialog(dialog,
                        "New friend has been added successfully!",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);

                dialog.dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog,
                        "Error adding new friend: " + ex.getMessage(),
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
     * Adds a form field label to a panel.
     *
     * @param panel The panel to add the label to
     * @param labelText The text for the label
     * @param gbc The GridBagConstraints to use
     * @param row The row index
     */
    private void addFormField(JPanel panel, String labelText, GridBagConstraints gbc, int row) {
        gbc.gridx = 0;
        gbc.gridy = row;
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        panel.add(label, gbc);
    }

    /**
     * Creates a styled text field.
     *
     * @return A styled JTextField
     */
    private JTextField createStyledTextField() {
        JTextField field = new JTextField(20);
        field.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(200, 200, 200), 1, true),
                new EmptyBorder(8, 10, 8, 10)
        ));
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        return field;
    }

    /**
     * Inserts a new friend into the database.
     *
     * @param name The friend's name
     * @param email The friend's email
     * @param status The subscription status (1 for Active, 0 for Inactive)
     * @throws Exception If there is a database error
     */
    private void insertNewUser(String name, String email, int status) throws Exception {
        String query = "INSERT INTO friends_lancaster (name, email, subscription_status, join_date, booking_num) " +
                "VALUES (?, ?, ?, CURRENT_DATE(), 0)";

        try (Connection connection = myJDBC.getConnection()) {
            if (connection != null) {
                PreparedStatement pstmt = connection.prepareStatement(query);
                pstmt.setString(1, name);
                pstmt.setString(2, email);
                pstmt.setInt(3, status);
                pstmt.executeUpdate();
            }
        }
    }

    /**
     * Loads friends data from the database.
     */
    private void loadFriendsData() {
        statusLabel.setText("Loading data...");
        statusLabel.setForeground(accentColor);

        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() {
                try (Connection connection = myJDBC.getConnection()) {
                    if (connection != null) {
                        String countQuery = "SELECT COUNT(friend_id) FROM friends_lancaster";
                        try (Statement countStmt = connection.createStatement();
                             ResultSet countRs = countStmt.executeQuery(countQuery)) {
                            if (countRs.next()) {
                                friendsNum = countRs.getInt(1);
                            }
                        }

                        String dataQuery = "SELECT friend_id, name, email, subscription_status, join_date, booking_num FROM friends_lancaster";
                        try (Statement dataStmt = connection.createStatement();
                             ResultSet dataRs = dataStmt.executeQuery(dataQuery)) {

                            tableModel.setRowCount(0);

                            while (dataRs.next()) {
                                Object[] row = {
                                        dataRs.getInt("friend_id"),
                                        dataRs.getString("name"),
                                        dataRs.getString("email"),
                                        dataRs.getInt("subscription_status") == 1 ? "Active" : "Inactive",
                                        dataRs.getDate("join_date"),
                                        dataRs.getInt("booking_num")
                                };
                                tableModel.addRow(row);
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    SwingUtilities.invokeLater(() -> {
                        statusLabel.setText("Error: " + e.getMessage());
                        statusLabel.setForeground(new Color(231, 76, 60));
                    });
                }
                return null;
            }

            @Override
            protected void done() {
                statusLabel.setText(friendsNum + " friends loaded");
                statusLabel.setForeground(new Color(46, 204, 113));
            }
        };

        worker.execute();
    }
}