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
import com.lancaster.database.myJDBC;

public class ClientsUI extends JPanel {
    private JTable clientsTable;
    private DefaultTableModel tableModel;
    private JLabel statusLabel;
    private JTextField searchField;

    private Color primaryColor = new Color(47, 54, 64);
    private Color accentColor = new Color(86, 101, 115);
    private Color highlightColor = new Color(52, 152, 219);
    private Color backgroundColor = new Color(245, 246, 250);

    public ClientsUI() {
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

        JLabel titleLabel = new JLabel("Clients");
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

        JButton searchButton = new JButton("Search");
        styleButton(searchButton, highlightColor);
        searchButton.addActionListener(e -> searchClients(searchField.getText()));

        searchPanel.add(new JLabel("Search: "));
        searchPanel.add(searchField);
        searchPanel.add(searchButton);

        statusLabel = new JLabel("Loading data...");
        statusLabel.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        statusLabel.setForeground(accentColor);

        actionsPanel.add(searchPanel, BorderLayout.CENTER);
        actionsPanel.add(statusLabel, BorderLayout.EAST);

        headerPanel.add(actionsPanel, BorderLayout.EAST);

        String[] columns = {"Client ID", "Name", "Email", "Phone", "Address", "Type", "Friend"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        clientsTable = new JTable(tableModel);
        styleTable(clientsTable);

        JScrollPane scrollPane = new JScrollPane(clientsTable);
        scrollPane.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(230, 230, 230), 1),
                new EmptyBorder(0, 0, 0, 0)
        ));
        scrollPane.setBackground(Color.WHITE);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 15));
        buttonPanel.setOpaque(false);

        JButton refreshButton = new JButton("Refresh");
        styleButton(refreshButton, Color.BLACK);
        refreshButton.addActionListener(e -> loadClientsData());

        JButton newClientButton = new JButton("New Client");
        styleButton(newClientButton, Color.BLACK);
        newClientButton.addActionListener(e -> createNewClient());

        JButton toggleFriendButton = new JButton("Toggle Friend Status");
        styleButton(toggleFriendButton, new Color(155, 89, 182));
        toggleFriendButton.addActionListener(e -> toggleFriendStatus());

        buttonPanel.add(toggleFriendButton);
        buttonPanel.add(refreshButton);
        buttonPanel.add(newClientButton);

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

        JLabel cardTitle = new JLabel("Current Clients");
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

        loadClientsData();
    }

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

    private void addTableContextMenu() {
        JPopupMenu contextMenu = new JPopupMenu();

        JMenuItem viewItem = new JMenuItem("View Client Details");
        viewItem.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        viewItem.addActionListener(e -> {
            int selectedRow = clientsTable.getSelectedRow();
            if (selectedRow >= 0) {
                int clientId = (Integer) clientsTable.getValueAt(selectedRow, 0);
                viewClientDetails(clientId);
            } else {
                JOptionPane.showMessageDialog(this, "Please select a client first", "No Selection", JOptionPane.WARNING_MESSAGE);
            }
        });

        JMenuItem editItem = new JMenuItem("Edit Client");
        editItem.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        editItem.addActionListener(e -> {
            int selectedRow = clientsTable.getSelectedRow();
            if (selectedRow >= 0) {
                int clientId = (Integer) clientsTable.getValueAt(selectedRow, 0);
                editClient(clientId);
            } else {
                JOptionPane.showMessageDialog(this, "Please select a client first", "No Selection", JOptionPane.WARNING_MESSAGE);
            }
        });

        JMenuItem toggleFriendItem = new JMenuItem("Toggle Friend Status");
        toggleFriendItem.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        toggleFriendItem.addActionListener(e -> toggleFriendStatus());

        JMenuItem deleteItem = new JMenuItem("Delete Client");
        deleteItem.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        deleteItem.addActionListener(e -> {
            int selectedRow = clientsTable.getSelectedRow();
            if (selectedRow >= 0) {
                int clientId = (Integer) clientsTable.getValueAt(selectedRow, 0);
                String clientName = (String) clientsTable.getValueAt(selectedRow, 1);
                deleteClient(clientId, clientName);
            } else {
                JOptionPane.showMessageDialog(this, "Please select a client first", "No Selection", JOptionPane.WARNING_MESSAGE);
            }
        });

        contextMenu.add(viewItem);
        contextMenu.add(editItem);
        contextMenu.add(toggleFriendItem);
        contextMenu.addSeparator();
        contextMenu.add(deleteItem);

        clientsTable.setComponentPopupMenu(contextMenu);
    }

    private void loadClientsData() {
        statusLabel.setText("Loading data...");
        statusLabel.setForeground(accentColor);

        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() {
                try (Connection connection = myJDBC.getConnection()) {
                    if (connection != null) {
                        String dataQuery = "SELECT clientId, name, email, phone, address, type, isFriend FROM clients";
                        Statement dataStmt = connection.createStatement();
                        ResultSet dataRs = dataStmt.executeQuery(dataQuery);

                        tableModel.setRowCount(0);

                        while (dataRs.next()) {
                            boolean isFriend = dataRs.getBoolean("isFriend");
                            String friendStatus = isFriend ? "Yes" : "No";

                            Object[] row = {
                                    dataRs.getInt("clientId"),
                                    dataRs.getString("name"),
                                    dataRs.getString("email"),
                                    dataRs.getString("phone"),
                                    dataRs.getString("address"),
                                    dataRs.getString("type"),
                                    friendStatus
                            };
                            tableModel.addRow(row);
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
                statusLabel.setText(tableModel.getRowCount() + " clients loaded");
                statusLabel.setForeground(new Color(46, 204, 113));
            }
        };

        worker.execute();
    }

    private void searchClients(String searchTerm) {
        if (searchTerm.isEmpty()) {
            loadClientsData();
            return;
        }

        statusLabel.setText("Searching...");
        statusLabel.setForeground(accentColor);

        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() {
                try (Connection connection = myJDBC.getConnection()) {
                    if (connection != null) {
                        String dataQuery = "SELECT clientId, name, email, phone, address, type, isFriend FROM clients " +
                                "WHERE name LIKE ? OR email LIKE ? OR phone LIKE ? OR address LIKE ? OR type LIKE ?";

                        PreparedStatement pstmt = connection.prepareStatement(dataQuery);
                        String searchPattern = "%" + searchTerm + "%";
                        pstmt.setString(1, searchPattern);
                        pstmt.setString(2, searchPattern);
                        pstmt.setString(3, searchPattern);
                        pstmt.setString(4, searchPattern);
                        pstmt.setString(5, searchPattern);

                        ResultSet dataRs = pstmt.executeQuery();

                        tableModel.setRowCount(0);

                        while (dataRs.next()) {
                            boolean isFriend = dataRs.getBoolean("isFriend");
                            String friendStatus = isFriend ? "Yes" : "No";

                            Object[] row = {
                                    dataRs.getInt("clientId"),
                                    dataRs.getString("name"),
                                    dataRs.getString("email"),
                                    dataRs.getString("phone"),
                                    dataRs.getString("address"),
                                    dataRs.getString("type"),
                                    friendStatus
                            };
                            tableModel.addRow(row);
                        }
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

            @Override
            protected void done() {
                statusLabel.setText(tableModel.getRowCount() + " results found");
                statusLabel.setForeground(new Color(46, 204, 113));
            }
        };

        worker.execute();
    }

    private void toggleFriendStatus() {
        int selectedRow = clientsTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select a client to toggle friend status", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int clientId = (Integer) clientsTable.getValueAt(selectedRow, 0);
        String name = (String) clientsTable.getValueAt(selectedRow, 1);
        String currentStatus = (String) clientsTable.getValueAt(selectedRow, 6);
        boolean newFriendStatus = currentStatus.equals("No");

        try (Connection connection = myJDBC.getConnection()) {
            if (connection != null) {
                String updateQuery = "UPDATE clients SET isFriend = ? WHERE clientId = ?";
                PreparedStatement pstmt = connection.prepareStatement(updateQuery);
                pstmt.setBoolean(1, newFriendStatus);
                pstmt.setInt(2, clientId);

                int affectedRows = pstmt.executeUpdate();
                if (affectedRows > 0) {
                    tableModel.setValueAt(newFriendStatus ? "Yes" : "No", selectedRow, 6);

                    String statusMessage = newFriendStatus ?
                            name + " is now marked as a friend" :
                            name + " is no longer marked as a friend";

                    JOptionPane.showMessageDialog(this,
                            statusMessage,
                            "Friend Status Updated",
                            JOptionPane.INFORMATION_MESSAGE);

                    loadClientsData();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Error updating friend status: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void viewClientDetails(int clientId) {
        JPanel detailsPanel = new JPanel();
        detailsPanel.setLayout(new BoxLayout(detailsPanel, BoxLayout.Y_AXIS));
        detailsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        try (Connection connection = myJDBC.getConnection()) {
            if (connection != null) {
                String query = "SELECT * FROM clients WHERE clientId = ?";
                PreparedStatement pstmt = connection.prepareStatement(query);
                pstmt.setInt(1, clientId);
                ResultSet rs = pstmt.executeQuery();

                if (rs.next()) {
                    addDetailRow(detailsPanel, "Client ID:", String.valueOf(rs.getInt("clientId")));
                    addDetailRow(detailsPanel, "Name:", rs.getString("name"));
                    addDetailRow(detailsPanel, "Email:", rs.getString("email"));
                    addDetailRow(detailsPanel, "Phone:", rs.getString("phone"));
                    addDetailRow(detailsPanel, "Address:", rs.getString("address"));
                    addDetailRow(detailsPanel, "Type:", rs.getString("type"));

                    boolean isFriend = rs.getBoolean("isFriend");
                    String friendIcon = isFriend ? "✓" : "✗";
                    String friendStatus = isFriend ? "Yes " + friendIcon : "No " + friendIcon;

                    JPanel friendRow = new JPanel(new BorderLayout(10, 0));
                    friendRow.setOpaque(false);

                    JLabel friendLabel = new JLabel("Friend:");
                    friendLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
                    friendLabel.setPreferredSize(new Dimension(100, 25));

                    JLabel friendValueLabel = new JLabel(friendStatus);
                    friendValueLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
                    if (isFriend) {
                        friendValueLabel.setForeground(new Color(46, 204, 113));
                    } else {
                        friendValueLabel.setForeground(new Color(231, 76, 60));
                    }

                    friendRow.add(friendLabel, BorderLayout.WEST);
                    friendRow.add(friendValueLabel, BorderLayout.CENTER);
                    detailsPanel.add(friendRow);
                    detailsPanel.add(Box.createVerticalStrut(5));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        JOptionPane.showMessageDialog(this, detailsPanel, "Client Details", JOptionPane.INFORMATION_MESSAGE);
    }

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

    private void deleteClient(int clientId, String clientName) {
        JPanel confirmPanel = new JPanel(new BorderLayout(10, 10));
        confirmPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel iconLabel = new JLabel(UIManager.getIcon("OptionPane.warningIcon"));
        JLabel messageLabel = new JLabel("<html>Are you sure you want to delete client:<br><b>" + clientName + "</b>?</html>");
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
                    String query = "DELETE FROM clients WHERE clientId = ?";
                    PreparedStatement pstmt = connection.prepareStatement(query);
                    pstmt.setInt(1, clientId);
                    int rowsAffected = pstmt.executeUpdate();

                    if (rowsAffected > 0) {
                        loadClientsData();
                        statusLabel.setText("Client deleted successfully");
                        statusLabel.setForeground(new Color(46, 204, 113));
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error deleting client: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void editClient(int clientId) {
        String name = "";
        String email = "";
        String phone = "";
        String address = "";
        String type = "";
        boolean isFriend = false;

        try (Connection connection = myJDBC.getConnection()) {
            if (connection != null) {
                String query = "SELECT * FROM clients WHERE clientId = ?";
                PreparedStatement pstmt = connection.prepareStatement(query);
                pstmt.setInt(1, clientId);
                ResultSet rs = pstmt.executeQuery();

                if (rs.next()) {
                    name = rs.getString("name");
                    email = rs.getString("email");
                    phone = rs.getString("phone");
                    address = rs.getString("address");
                    type = rs.getString("type");
                    isFriend = rs.getBoolean("isFriend");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading client data: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Edit Client", true);
        dialog.setLayout(new BorderLayout());

        JPanel dialogHeader = new JPanel(new BorderLayout());
        dialogHeader.setBackground(highlightColor);
        dialogHeader.setPreferredSize(new Dimension(dialogHeader.getWidth(), 60));
        dialogHeader.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        JLabel dialogTitle = new JLabel("Edit Client: " + name);
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
        nameField.setText(name);
        gbc.gridx = 1; gbc.gridy = 0;
        formPanel.add(nameField, gbc);

        addFormField(formPanel, "Email:", gbc, 1);
        JTextField emailField = createStyledTextField();
        emailField.setText(email);
        gbc.gridx = 1; gbc.gridy = 1;
        formPanel.add(emailField, gbc);

        addFormField(formPanel, "Phone:", gbc, 2);
        JTextField phoneField = createStyledTextField();
        phoneField.setText(phone);
        gbc.gridx = 1; gbc.gridy = 2;
        formPanel.add(phoneField, gbc);

        addFormField(formPanel, "Address:", gbc, 3);
        JTextField addressField = createStyledTextField();
        addressField.setText(address);
        gbc.gridx = 1; gbc.gridy = 3;
        formPanel.add(addressField, gbc);

        addFormField(formPanel, "Type:", gbc, 4);
        JTextField typeField = createStyledTextField();
        typeField.setText(type);
        gbc.gridx = 1; gbc.gridy = 4;
        formPanel.add(typeField, gbc);

        addFormField(formPanel, "Friend:", gbc, 5);
        JCheckBox friendCheckbox = new JCheckBox("Mark as a friend");
        friendCheckbox.setSelected(isFriend);
        friendCheckbox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        friendCheckbox.setOpaque(false);
        gbc.gridx = 1; gbc.gridy = 5;
        formPanel.add(friendCheckbox, gbc);

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

        final int finalClientId = clientId;
        saveButton.addActionListener(e -> {
            try {
                String updatedName = nameField.getText();
                String updatedEmail = emailField.getText();
                String updatedPhone = phoneField.getText();
                String updatedAddress = addressField.getText();
                String updatedType = typeField.getText();
                boolean updatedFriend = friendCheckbox.isSelected();

                if (updatedName.isEmpty() || updatedEmail.isEmpty() || updatedPhone.isEmpty()
                        || updatedAddress.isEmpty() || updatedType.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog,
                            "Please fill in all required fields",
                            "Validation Error",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                updateClient(finalClientId, updatedName, updatedEmail, updatedPhone,
                        updatedAddress, updatedType, updatedFriend);

                loadClientsData();

                JOptionPane.showMessageDialog(dialog,
                        "Client updated successfully!",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);

                dialog.dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog,
                        "Error updating client: " + ex.getMessage(),
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
        dialog.setMinimumSize(new Dimension(500, dialog.getHeight()));
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void updateClient(int clientId, String name, String email, String phone,
                              String address, String type, boolean isFriend) throws Exception {
        String query = "UPDATE clients SET name = ?, email = ?, phone = ?, address = ?, type = ?, isFriend = ? WHERE clientId = ?";

        try (Connection connection = myJDBC.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, name);
            pstmt.setString(2, email);
            pstmt.setString(3, phone);
            pstmt.setString(4, address);
            pstmt.setString(5, type);
            pstmt.setBoolean(6, isFriend);
            pstmt.setInt(7, clientId);
            pstmt.executeUpdate();
        }
    }

    private JTextField createStyledTextField() {
        JTextField field = new JTextField(20);
        field.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(200, 200, 200), 1, true),
                new EmptyBorder(8, 10, 8, 10)
        ));
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        return field;
    }

    private void createNewClient() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "New Client", true);
        dialog.setLayout(new BorderLayout());

        JPanel dialogHeader = new JPanel(new BorderLayout());
        dialogHeader.setBackground(highlightColor);
        dialogHeader.setPreferredSize(new Dimension(dialogHeader.getWidth(), 60));
        dialogHeader.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        JLabel dialogTitle = new JLabel("Create New Client");
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

        addFormField(formPanel, "Phone:", gbc, 2);
        JTextField phoneField = createStyledTextField();
        gbc.gridx = 1; gbc.gridy = 2;
        formPanel.add(phoneField, gbc);

        addFormField(formPanel, "Address:", gbc, 3);
        JTextField addressField = createStyledTextField();
        gbc.gridx = 1; gbc.gridy = 3;
        formPanel.add(addressField, gbc);

        addFormField(formPanel, "Type:", gbc, 4);
        JTextField typeField = createStyledTextField();
        gbc.gridx = 1; gbc.gridy = 4;
        formPanel.add(typeField, gbc);

        addFormField(formPanel, "Friend:", gbc, 5);
        JCheckBox friendCheckbox = new JCheckBox("Mark as a friend");
        friendCheckbox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        friendCheckbox.setOpaque(false);
        gbc.gridx = 1; gbc.gridy = 5;
        formPanel.add(friendCheckbox, gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(230, 230, 230)),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        JButton cancelButton = new JButton("Cancel");
        styleButton(cancelButton, new Color(190, 190, 190));

        JButton saveButton = new JButton("Save Client");
        styleButton(saveButton, highlightColor);

        saveButton.addActionListener(e -> {
            try {
                String name = nameField.getText();
                String email = emailField.getText();
                String phone = phoneField.getText();
                String address = addressField.getText();
                String type = typeField.getText();
                boolean isFriend = friendCheckbox.isSelected();

                if (name.isEmpty() || email.isEmpty() || phone.isEmpty() || address.isEmpty() || type.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog,
                            "Please fill in all required fields",
                            "Validation Error",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                insertNewClient(name, email, phone, address, type, isFriend);
                loadClientsData();

                JOptionPane.showMessageDialog(dialog,
                        "New client has been added successfully!",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);

                dialog.dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog,
                        "Error adding new client: " + ex.getMessage(),
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
        dialog.setMinimumSize(new Dimension(500, dialog.getHeight()));
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void addFormField(JPanel panel, String labelText, GridBagConstraints gbc, int row) {
        gbc.gridx = 0;
        gbc.gridy = row;
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        panel.add(label, gbc);
    }

    private void insertNewClient(String name, String email, String phone, String address, String type, boolean isFriend) throws Exception {
        String query = "INSERT INTO clients (name, email, phone, address, type, isFriend) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection connection = myJDBC.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, name);
            pstmt.setString(2, email);
            pstmt.setString(3, phone);
            pstmt.setString(4, address);
            pstmt.setString(5, type);
            pstmt.setBoolean(6, isFriend);
            pstmt.executeUpdate();
        }
    }
}