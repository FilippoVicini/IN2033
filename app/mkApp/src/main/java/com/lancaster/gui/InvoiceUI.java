package com.lancaster.gui;

import com.lancaster.database.myJDBC;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Vector;

/**
 * UI panel for managing invoices.
 * Provides a table display of invoices with search functionality and CRUD operations.
 */
public class InvoiceUI extends JPanel {
    private JTable invoiceTable;
    private DefaultTableModel tableModel;
    private JLabel statusLabel;
    private JTextField searchField;
    private JTextField totalField;

    private Color primaryColor = new Color(47, 54, 64);
    private Color accentColor = new Color(86, 101, 115);
    private Color highlightColor = new Color(52, 152, 219);
    private Color backgroundColor = new Color(245, 246, 250);

    /**
     * Constructs the InvoiceUI panel with search functionality and invoice table.
     */
    public InvoiceUI() {
        setLayout(new BorderLayout());
        setBackground(backgroundColor);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(backgroundColor);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        titlePanel.setOpaque(false);

        JLabel iconLabel = new JLabel("💰");
        iconLabel.setFont(new Font("Segoe UI", Font.PLAIN, 24));

        JLabel titleLabel = new JLabel("Invoices");
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
        searchField.setToolTipText("Search for invoices by client name");

        JButton searchButton = new JButton("Search");
        styleButton(searchButton, highlightColor);
        searchButton.setForeground(Color.BLACK);
        searchButton.addActionListener(e -> searchInvoices());

        searchPanel.add(new JLabel("Search: "));
        searchPanel.add(searchField);
        searchPanel.add(searchButton);

        statusLabel = new JLabel("Loading data...");
        statusLabel.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        statusLabel.setForeground(accentColor);

        actionsPanel.add(searchPanel, BorderLayout.CENTER);
        actionsPanel.add(statusLabel, BorderLayout.EAST);

        headerPanel.add(actionsPanel, BorderLayout.EAST);

        String[] columns = {"Invoice ID", "Client Name", "Date", "Cost (£)", "Total (£)"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                switch(columnIndex) {
                    case 0: return Integer.class;
                    case 3: return Double.class;
                    case 4: return Double.class;
                    default: return String.class;
                }
            }
        };

        invoiceTable = new JTable(tableModel);
        styleTable(invoiceTable);

        JScrollPane scrollPane = new JScrollPane(invoiceTable);
        scrollPane.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(230, 230, 230), 1),
                new EmptyBorder(0, 0, 0, 0)
        ));
        scrollPane.setBackground(Color.WHITE);

        JPanel totalPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        totalPanel.setOpaque(false);
        totalPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        JLabel totalLabel = new JLabel("Total: £");
        totalLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        totalLabel.setForeground(new Color(52, 73, 94));
        totalPanel.add(totalLabel);

        totalField = new JTextField(10);
        totalField.setEditable(false);
        totalField.setFont(new Font("Segoe UI", Font.BOLD, 16));
        totalField.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(200, 200, 200), 1, true),
                new EmptyBorder(5, 10, 5, 10)
        ));
        totalField.setBackground(new Color(240, 240, 240));
        totalPanel.add(totalField);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 15));
        buttonPanel.setOpaque(false);

        JButton refreshButton = new JButton("Refresh");
        styleButton(refreshButton, Color.BLACK);
        refreshButton.setForeground(Color.BLACK);
        refreshButton.addActionListener(e -> loadInvoiceData());

        JButton newInvoiceButton = new JButton("New Invoice");
        styleButton(newInvoiceButton, Color.BLACK);
        newInvoiceButton.setForeground(Color.BLACK);
        newInvoiceButton.addActionListener(e -> createNewInvoice());

        buttonPanel.add(refreshButton);
        buttonPanel.add(newInvoiceButton);

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

        JLabel cardTitle = new JLabel("Invoice Records");
        cardTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        cardTitle.setForeground(new Color(52, 73, 94));
        cardTitle.setBorder(BorderFactory.createEmptyBorder(0, 5, 10, 0));

        cardHeader.add(cardTitle, BorderLayout.WEST);

        cardPanel.add(cardHeader, BorderLayout.NORTH);
        cardPanel.add(scrollPane, BorderLayout.CENTER);
        cardPanel.add(totalPanel, BorderLayout.SOUTH);

        add(headerPanel, BorderLayout.NORTH);
        add(cardPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        addTableContextMenu();

        loadInvoiceData();
    }

    /**
     * Applies styling to the invoice table.
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
     * Searches for invoices based on client name.
     */
    private void searchInvoices() {
        String searchTerm = searchField.getText().trim();
        if (searchTerm.isEmpty()) {
            loadInvoiceData();
            return;
        }

        statusLabel.setText("Searching...");
        statusLabel.setForeground(accentColor);

        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() {
                try (Connection connection = myJDBC.getConnection()) {
                    if (connection != null) {
                        String query = "SELECT * FROM invoices WHERE clientName LIKE ? ORDER BY date DESC";

                        PreparedStatement pstmt = connection.prepareStatement(query);
                        String pattern = "%" + searchTerm + "%";
                        pstmt.setString(1, pattern);

                        ResultSet rs = pstmt.executeQuery();

                        tableModel.setRowCount(0);
                        int count = 0;
                        double totalSum = 0;

                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                        while (rs.next()) {
                            Object[] row = {
                                    rs.getInt("invoiceID"),
                                    rs.getString("clientName"),
                                    dateFormat.format(rs.getTimestamp("date")),
                                    rs.getDouble("cost"),
                                    rs.getDouble("total")
                            };
                            tableModel.addRow(row);
                            count++;
                            totalSum += rs.getDouble("total");
                        }

                        final int finalCount = count;
                        final double finalTotal = totalSum;
                        SwingUtilities.invokeLater(() -> {
                            totalField.setText(String.format("%.2f", finalTotal));
                            statusLabel.setText(finalCount + " invoices found");
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
     * Adds a context menu to the invoice table.
     */
    private void addTableContextMenu() {
        JPopupMenu contextMenu = new JPopupMenu();

        JMenuItem viewItem = new JMenuItem("View Invoice Details");
        viewItem.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        viewItem.addActionListener(e -> {
            int selectedRow = invoiceTable.getSelectedRow();
            if (selectedRow >= 0) {
                int invoiceId = (Integer) invoiceTable.getValueAt(selectedRow, 0);
                String clientName = (String) invoiceTable.getValueAt(selectedRow, 1);
                viewInvoiceDetails(invoiceId, clientName);
            } else {
                JOptionPane.showMessageDialog(this, "Please select an invoice first", "No Selection", JOptionPane.WARNING_MESSAGE);
            }
        });

        JMenuItem deleteItem = new JMenuItem("Delete Invoice");
        deleteItem.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        deleteItem.addActionListener(e -> {
            int selectedRow = invoiceTable.getSelectedRow();
            if (selectedRow >= 0) {
                int invoiceId = (Integer) invoiceTable.getValueAt(selectedRow, 0);
                String clientName = (String) invoiceTable.getValueAt(selectedRow, 1);
                deleteInvoice(invoiceId, clientName);
            } else {
                JOptionPane.showMessageDialog(this, "Please select an invoice first", "No Selection", JOptionPane.WARNING_MESSAGE);
            }
        });

        contextMenu.add(viewItem);
        contextMenu.addSeparator();
        contextMenu.add(deleteItem);

        invoiceTable.setComponentPopupMenu(contextMenu);
    }

    /**
     * Displays details of the selected invoice.
     *
     * @param invoiceId The ID of the invoice to display
     * @param clientName The name of the client
     */
    private void viewInvoiceDetails(int invoiceId, String clientName) {
        JPanel detailsPanel = new JPanel();
        detailsPanel.setLayout(new BoxLayout(detailsPanel, BoxLayout.Y_AXIS));
        detailsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        try (Connection connection = myJDBC.getConnection()) {
            if (connection != null) {
                String query = "SELECT * FROM invoices WHERE invoiceID = ?";
                PreparedStatement pstmt = connection.prepareStatement(query);
                pstmt.setInt(1, invoiceId);
                ResultSet rs = pstmt.executeQuery();

                if (rs.next()) {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                    addDetailRow(detailsPanel, "Invoice ID:", String.valueOf(rs.getInt("invoiceID")));
                    addDetailRow(detailsPanel, "Client Name:", rs.getString("clientName"));
                    addDetailRow(detailsPanel, "Date:", dateFormat.format(rs.getTimestamp("date")));
                    addDetailRow(detailsPanel, "Cost:", "£" + String.format("%.2f", rs.getDouble("cost")));
                    addDetailRow(detailsPanel, "Total:", "£" + String.format("%.2f", rs.getDouble("total")));

                    addDetailRow(detailsPanel, "Created By:", "FilippoVicini");
                    addDetailRow(detailsPanel, "Status:", "Paid");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        JOptionPane.showMessageDialog(this, detailsPanel, "Invoice Details: #" + invoiceId, JOptionPane.INFORMATION_MESSAGE);
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
     * Deletes an invoice after confirmation.
     *
     * @param invoiceId The ID of the invoice to delete
     * @param clientName The name of the client
     */
    private void deleteInvoice(int invoiceId, String clientName) {
        JPanel confirmPanel = new JPanel(new BorderLayout(10, 10));
        confirmPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel iconLabel = new JLabel(UIManager.getIcon("OptionPane.warningIcon"));
        JLabel messageLabel = new JLabel("<html>Are you sure you want to delete invoice:<br><b>#" + invoiceId + " for " + clientName + "</b>?</html>");
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
                    String query = "DELETE FROM invoices WHERE invoiceID = ?";
                    PreparedStatement pstmt = connection.prepareStatement(query);
                    pstmt.setInt(1, invoiceId);
                    int rowsAffected = pstmt.executeUpdate();

                    if (rowsAffected > 0) {
                        loadInvoiceData();
                        statusLabel.setText("Invoice #" + invoiceId + " deleted successfully");
                        statusLabel.setForeground(new Color(46, 204, 113));
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error deleting invoice: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Opens a dialog to create a new invoice.
     */
    private void createNewInvoice() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "New Invoice", true);
        dialog.setLayout(new BorderLayout());

        JPanel dialogHeader = new JPanel(new BorderLayout());
        dialogHeader.setBackground(highlightColor);
        dialogHeader.setPreferredSize(new Dimension(dialogHeader.getWidth(), 60));
        dialogHeader.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        JLabel dialogTitle = new JLabel("Create New Invoice");
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

        addFormField(formPanel, "Client Name:", gbc, 0);
        JTextField clientNameField = createStyledTextField();
        gbc.gridx = 1; gbc.gridy = 0;
        formPanel.add(clientNameField, gbc);

        addFormField(formPanel, "Cost (£):", gbc, 1);
        JTextField costField = createStyledTextField();
        gbc.gridx = 1; gbc.gridy = 1;
        formPanel.add(costField, gbc);

        addFormField(formPanel, "Date:", gbc, 2);
        JTextField dateField = createStyledTextField();
        dateField.setText("2025-04-07 07:41:47");
        dateField.setEditable(false);
        dateField.setBackground(new Color(245, 245, 245));
        gbc.gridx = 1; gbc.gridy = 2;
        formPanel.add(dateField, gbc);

        addFormField(formPanel, "Created By:", gbc, 3);
        JTextField createdByField = createStyledTextField();
        createdByField.setText("FilippoVicini");
        createdByField.setEditable(false);
        createdByField.setBackground(new Color(245, 245, 245));
        gbc.gridx = 1; gbc.gridy = 3;
        formPanel.add(createdByField, gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(230, 230, 230)),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        JButton cancelButton = new JButton("Cancel");
        styleButton(cancelButton, new Color(190, 190, 190));

        JButton saveButton = new JButton("Save Invoice");
        styleButton(saveButton, highlightColor);

        saveButton.addActionListener(e -> {
            try {
                if (clientNameField.getText().trim().isEmpty() || costField.getText().trim().isEmpty()) {
                    JOptionPane.showMessageDialog(dialog,
                            "Please fill in all required fields",
                            "Validation Error",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                String clientName = clientNameField.getText().trim();
                double cost;

                try {
                    cost = Double.parseDouble(costField.getText().trim());
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(dialog,
                            "Please enter a valid number for Cost",
                            "Validation Error",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (cost <= 0) {
                    JOptionPane.showMessageDialog(dialog,
                            "Cost must be greater than zero",
                            "Validation Error",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                double total = cost;

                insertNewInvoice(clientName, cost, total);
                loadInvoiceData();

                JOptionPane.showMessageDialog(dialog,
                        "New invoice has been added successfully!",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);

                dialog.dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog,
                        "Error adding new invoice: " + ex.getMessage(),
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
     * Inserts a new invoice into the database.
     *
     * @param clientName The name of the client
     * @param cost The cost amount
     * @param total The total amount
     * @throws Exception If there is a database error
     */
    private void insertNewInvoice(String clientName, double cost, double total) throws Exception {
        String query = "INSERT INTO invoices (clientName, date, cost, total) VALUES (?, NOW(), ?, ?)";

        try (Connection connection = myJDBC.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, clientName);
            pstmt.setDouble(2, cost);
            pstmt.setDouble(3, total);
            pstmt.executeUpdate();
        }
    }

    /**
     * Loads invoice data from the database.
     */
    private void loadInvoiceData() {
        statusLabel.setText("Loading data...");
        statusLabel.setForeground(accentColor);

        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() {
                try (Connection connection = myJDBC.getConnection()) {
                    if (connection != null) {
                        String query = "SELECT * FROM invoices ORDER BY date DESC";
                        Statement stmt = connection.createStatement();
                        ResultSet rs = stmt.executeQuery(query);

                        tableModel.setRowCount(0);
                        double totalSum = 0;
                        int count = 0;

                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                        while (rs.next()) {
                            Object[] row = {
                                    rs.getInt("invoiceID"),
                                    rs.getString("clientName"),
                                    dateFormat.format(rs.getTimestamp("date")),
                                    rs.getDouble("cost"),
                                    rs.getDouble("total")
                            };
                            tableModel.addRow(row);
                            totalSum += rs.getDouble("total");
                            count++;
                        }

                        final double finalTotalSum = totalSum;
                        final int finalCount = count;
                        SwingUtilities.invokeLater(() -> {
                            totalField.setText(String.format("%.2f", finalTotalSum));
                            statusLabel.setText(finalCount + " invoices loaded");
                            statusLabel.setForeground(new Color(46, 204, 113));
                        });
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
        };

        worker.execute();
    }
}