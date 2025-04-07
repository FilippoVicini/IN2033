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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import com.lancaster.database.myJDBC;

/**
 * UI panel for managing held seats.
 * Provides a table display of reserved seats with search functionality and CRUD operations.
 */
public class heldSeatsUI extends JPanel {
    private JTable heldSeatsTable;
    private DefaultTableModel tableModel;
    private JLabel statusLabel;
    private JTextField searchField;

    private Color primaryColor = new Color(47, 54, 64);
    private Color accentColor = new Color(86, 101, 115);
    private Color highlightColor = new Color(52, 152, 219);
    private Color backgroundColor = new Color(245, 246, 250);

    /**
     * Constructs the heldSeatsUI panel with search functionality and held seats table.
     */
    public heldSeatsUI() {
        setLayout(new BorderLayout());
        setBackground(backgroundColor);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(backgroundColor);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        titlePanel.setOpaque(false);

        JLabel iconLabel = new JLabel("🪑");
        iconLabel.setFont(new Font("Segoe UI", Font.PLAIN, 24));

        JLabel titleLabel = new JLabel("Held Seats Management");
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
        searchField.setToolTipText("Search for held seats by room or event");

        JButton searchButton = new JButton("Search");
        styleButton(searchButton, highlightColor);
        searchButton.setForeground(Color.BLACK);
        searchButton.addActionListener(e -> searchHeldSeats());

        searchPanel.add(new JLabel("Search: "));
        searchPanel.add(searchField);
        searchPanel.add(searchButton);

        statusLabel = new JLabel("Loading data...");
        statusLabel.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        statusLabel.setForeground(accentColor);

        actionsPanel.add(searchPanel, BorderLayout.CENTER);
        actionsPanel.add(statusLabel, BorderLayout.EAST);

        headerPanel.add(actionsPanel, BorderLayout.EAST);

        String[] columns = {"Held ID", "Room", "Seats", "Date", "Event"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        heldSeatsTable = new JTable(tableModel);
        styleTable(heldSeatsTable);

        JScrollPane scrollPane = new JScrollPane(heldSeatsTable);
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
        refreshButton.addActionListener(e -> loadHeldSeatsData());

        JButton newHeldSeatButton = new JButton("Add Held Seats");
        styleButton(newHeldSeatButton, Color.BLACK);
        newHeldSeatButton.setForeground(Color.BLACK);
        newHeldSeatButton.addActionListener(e -> showNewHeldSeatDialog());

        buttonPanel.add(refreshButton);
        buttonPanel.add(newHeldSeatButton);

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

        JLabel cardTitle = new JLabel("Seat Reservations");
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

        loadHeldSeatsData();
    }

    /**
     * Applies styling to the held seats table.
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
     * Searches for held seats based on the search term.
     */
    private void searchHeldSeats() {
        String searchTerm = searchField.getText().trim();
        if (searchTerm.isEmpty()) {
            loadHeldSeatsData();
            return;
        }

        statusLabel.setText("Searching...");
        statusLabel.setForeground(accentColor);

        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() {
                try (Connection connection = myJDBC.getConnection()) {
                    if (connection != null) {
                        String query = "SELECT * FROM heldSeats WHERE room LIKE ? OR event LIKE ?";

                        PreparedStatement pstmt = connection.prepareStatement(query);
                        String pattern = "%" + searchTerm + "%";
                        pstmt.setString(1, pattern);
                        pstmt.setString(2, pattern);

                        ResultSet rs = pstmt.executeQuery();

                        tableModel.setRowCount(0);
                        int count = 0;

                        while (rs.next()) {
                            Object[] row = {
                                    rs.getInt("heldID"),
                                    rs.getString("room"),
                                    rs.getInt("seats"),
                                    rs.getString("date"),
                                    rs.getString("event")
                            };
                            tableModel.addRow(row);
                            count++;
                        }

                        final int finalCount = count;
                        SwingUtilities.invokeLater(() -> {
                            statusLabel.setText(finalCount + " records found");
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
     * Adds a context menu to the held seats table.
     */
    private void addTableContextMenu() {
        JPopupMenu contextMenu = new JPopupMenu();

        JMenuItem viewItem = new JMenuItem("View Details");
        viewItem.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        viewItem.addActionListener(e -> {
            int selectedRow = heldSeatsTable.getSelectedRow();
            if (selectedRow >= 0) {
                int heldId = (Integer) heldSeatsTable.getValueAt(selectedRow, 0);
                viewHeldSeatDetails(heldId);
            } else {
                JOptionPane.showMessageDialog(this, "Please select a record first", "No Selection", JOptionPane.WARNING_MESSAGE);
            }
        });

        JMenuItem editItem = new JMenuItem("Edit Held Seats");
        editItem.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        editItem.addActionListener(e -> {
            int selectedRow = heldSeatsTable.getSelectedRow();
            if (selectedRow >= 0) {
                int heldId = (Integer) heldSeatsTable.getValueAt(selectedRow, 0);
                editHeldSeat(heldId);
            } else {
                JOptionPane.showMessageDialog(this, "Please select a record first", "No Selection", JOptionPane.WARNING_MESSAGE);
            }
        });

        JMenuItem deleteItem = new JMenuItem("Delete Held Seats");
        deleteItem.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        deleteItem.addActionListener(e -> {
            int selectedRow = heldSeatsTable.getSelectedRow();
            if (selectedRow >= 0) {
                int heldId = (Integer) heldSeatsTable.getValueAt(selectedRow, 0);
                String room = (String) heldSeatsTable.getValueAt(selectedRow, 1);
                String event = (String) heldSeatsTable.getValueAt(selectedRow, 4);
                deleteHeldSeat(heldId, room, event);
            } else {
                JOptionPane.showMessageDialog(this, "Please select a record first", "No Selection", JOptionPane.WARNING_MESSAGE);
            }
        });

        contextMenu.add(viewItem);
        contextMenu.add(editItem);
        contextMenu.addSeparator();
        contextMenu.add(deleteItem);

        heldSeatsTable.setComponentPopupMenu(contextMenu);
    }

    /**
     * Displays details of the selected held seat record.
     *
     * @param heldId The ID of the held seat record to display
     */
    private void viewHeldSeatDetails(int heldId) {
        JPanel detailsPanel = new JPanel();
        detailsPanel.setLayout(new BoxLayout(detailsPanel, BoxLayout.Y_AXIS));
        detailsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        try (Connection connection = myJDBC.getConnection()) {
            if (connection != null) {
                String query = "SELECT * FROM heldSeats WHERE heldID = ?";
                PreparedStatement pstmt = connection.prepareStatement(query);
                pstmt.setInt(1, heldId);
                ResultSet rs = pstmt.executeQuery();

                if (rs.next()) {
                    addDetailRow(detailsPanel, "Held ID:", String.valueOf(rs.getInt("heldID")));
                    addDetailRow(detailsPanel, "Room:", rs.getString("room"));
                    addDetailRow(detailsPanel, "Seats:", String.valueOf(rs.getInt("seats")));
                    addDetailRow(detailsPanel, "Date:", rs.getString("date"));
                    addDetailRow(detailsPanel, "Event:", rs.getString("event"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        JOptionPane.showMessageDialog(this, detailsPanel, "Held Seats Details", JOptionPane.INFORMATION_MESSAGE);
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
     * Deletes a held seat record after confirmation.
     *
     * @param heldId The ID of the record to delete
     * @param room The room associated with the record
     * @param event The event associated with the record
     */
    private void deleteHeldSeat(int heldId, String room, String event) {
        JPanel confirmPanel = new JPanel(new BorderLayout(10, 10));
        confirmPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel iconLabel = new JLabel(UIManager.getIcon("OptionPane.warningIcon"));
        JLabel messageLabel = new JLabel("<html>Are you sure you want to delete held seats for:<br><b>" + room +
                "</b> (Event: " + event + ")?</html>");
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
                    String query = "DELETE FROM heldSeats WHERE heldID = ?";
                    PreparedStatement pstmt = connection.prepareStatement(query);
                    pstmt.setInt(1, heldId);
                    int rowsAffected = pstmt.executeUpdate();

                    if (rowsAffected > 0) {
                        loadHeldSeatsData();
                        statusLabel.setText("Held seat record deleted successfully");
                        statusLabel.setForeground(new Color(46, 204, 113));
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error deleting record: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Opens a dialog to edit an existing held seat record.
     *
     * @param heldId The ID of the record to edit
     */
    private void editHeldSeat(int heldId) {
        String room = "";
        int seats = 0;
        String date = "";
        String event = "";

        try (Connection connection = myJDBC.getConnection()) {
            if (connection != null) {
                String query = "SELECT * FROM heldSeats WHERE heldID = ?";
                PreparedStatement pstmt = connection.prepareStatement(query);
                pstmt.setInt(1, heldId);
                ResultSet rs = pstmt.executeQuery();

                if (rs.next()) {
                    room = rs.getString("room");
                    seats = rs.getInt("seats");
                    date = rs.getString("date");
                    event = rs.getString("event");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading record details: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Edit Held Seats", true);
        dialog.setLayout(new BorderLayout());

        JPanel dialogHeader = new JPanel(new BorderLayout());
        dialogHeader.setBackground(highlightColor);
        dialogHeader.setPreferredSize(new Dimension(dialogHeader.getWidth(), 60));
        dialogHeader.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        JLabel dialogTitle = new JLabel("Edit Held Seats Record #" + heldId);
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

        String finalRoom = room;
        addFormField(formPanel, "Room:", gbc, 0);
        JTextField roomField = new JTextField(finalRoom, 20);
        styleTextField(roomField);
        gbc.gridx = 1; gbc.gridy = 0;
        formPanel.add(roomField, gbc);

        addFormField(formPanel, "Seats:", gbc, 1);
        JSpinner seatsSpinner = new JSpinner(new SpinnerNumberModel(seats, 1, 1000, 1));
        styleSpinner(seatsSpinner);
        gbc.gridx = 1; gbc.gridy = 1;
        formPanel.add(seatsSpinner, gbc);

        addFormField(formPanel, "Date (YYYY-MM-DD):", gbc, 2);
        JTextField dateField = new JTextField(date, 20);
        styleTextField(dateField);
        gbc.gridx = 1; gbc.gridy = 2;
        formPanel.add(dateField, gbc);

        String finalEvent = event;
        addFormField(formPanel, "Event:", gbc, 3);
        JTextField eventField = new JTextField(finalEvent, 20);
        styleTextField(eventField);
        gbc.gridx = 1; gbc.gridy = 3;
        formPanel.add(eventField, gbc);

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
            String updatedRoom = roomField.getText().trim();
            int updatedSeats = (Integer) seatsSpinner.getValue();
            String updatedDate = dateField.getText().trim();
            String updatedEvent = eventField.getText().trim();

            if (updatedRoom.isEmpty() || updatedDate.isEmpty() || updatedEvent.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "All fields are required", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (!isValidDate(updatedDate)) {
                JOptionPane.showMessageDialog(dialog, "Date must be in YYYY-MM-DD format", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                updateHeldSeat(heldId, updatedRoom, updatedSeats, updatedDate, updatedEvent);
                loadHeldSeatsData();
                JOptionPane.showMessageDialog(dialog, "Record updated successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Error updating record: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
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

    /**
     * Updates a held seat record in the database.
     *
     * @param heldId The ID of the record to update
     * @param room The room name
     * @param seats The number of seats
     * @param date The date in YYYY-MM-DD format
     * @param event The event name
     * @throws Exception If there is a database error
     */
    private void updateHeldSeat(int heldId, String room, int seats, String date, String event) throws Exception {
        String query = "UPDATE heldSeats SET room = ?, seats = ?, date = ?, event = ? WHERE heldID = ?";

        try (Connection connection = myJDBC.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, room);
            pstmt.setInt(2, seats);
            pstmt.setString(3, date);
            pstmt.setString(4, event);
            pstmt.setInt(5, heldId);
            pstmt.executeUpdate();
        }
    }

    /**
     * Applies styling to a text field.
     *
     * @param textField The text field to style
     */
    private void styleTextField(JTextField textField) {
        textField.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(200, 200, 200), 1, true),
                new EmptyBorder(8, 10, 8, 10)
        ));
        textField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
    }

    /**
     * Applies styling to a spinner component.
     *
     * @param spinner The spinner to style
     */
    private void styleSpinner(JSpinner spinner) {
        spinner.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(200, 200, 200), 1, true),
                new EmptyBorder(5, 5, 5, 5)
        ));
        spinner.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        JComponent editor = spinner.getEditor();
        if (editor instanceof JSpinner.DefaultEditor) {
            JSpinner.DefaultEditor spinnerEditor = (JSpinner.DefaultEditor) editor;
            spinnerEditor.getTextField().setFont(new Font("Segoe UI", Font.PLAIN, 14));
        }
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
     * Validates if a string is in YYYY-MM-DD format.
     *
     * @param dateStr The date string to validate
     * @return true if the date is valid, false otherwise
     */
    private boolean isValidDate(String dateStr) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        format.setLenient(false);
        try {
            format.parse(dateStr);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }

    /**
     * Opens a dialog to create a new held seat record.
     */
    private void showNewHeldSeatDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Add New Held Seats", true);
        dialog.setLayout(new BorderLayout());

        JPanel dialogHeader = new JPanel(new BorderLayout());
        dialogHeader.setBackground(highlightColor);
        dialogHeader.setPreferredSize(new Dimension(dialogHeader.getWidth(), 60));
        dialogHeader.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        JLabel dialogTitle = new JLabel("Add New Held Seats");
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

        addFormField(formPanel, "Room:", gbc, 0);
        JTextField roomField = new JTextField(20);
        styleTextField(roomField);
        gbc.gridx = 1; gbc.gridy = 0;
        formPanel.add(roomField, gbc);

        addFormField(formPanel, "Seats:", gbc, 1);
        JSpinner seatsSpinner = new JSpinner(new SpinnerNumberModel(10, 1, 1000, 1));
        styleSpinner(seatsSpinner);
        gbc.gridx = 1; gbc.gridy = 1;
        formPanel.add(seatsSpinner, gbc);

        addFormField(formPanel, "Date (YYYY-MM-DD):", gbc, 2);
        JTextField dateField = new JTextField(20);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        dateField.setText(sdf.format(new Date()));
        styleTextField(dateField);
        gbc.gridx = 1; gbc.gridy = 2;
        formPanel.add(dateField, gbc);

        addFormField(formPanel, "Event:", gbc, 3);
        JTextField eventField = new JTextField(20);
        styleTextField(eventField);
        gbc.gridx = 1; gbc.gridy = 3;
        formPanel.add(eventField, gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(230, 230, 230)),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        JButton cancelButton = new JButton("Cancel");
        styleButton(cancelButton, new Color(190, 190, 190));

        JButton saveButton = new JButton("Save");
        styleButton(saveButton, highlightColor);

        saveButton.addActionListener(e -> {
            String room = roomField.getText().trim();
            int seats = (Integer) seatsSpinner.getValue();
            String date = dateField.getText().trim();
            String event = eventField.getText().trim();

            if (room.isEmpty() || date.isEmpty() || event.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "All fields are required", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (!isValidDate(date)) {
                JOptionPane.showMessageDialog(dialog, "Date must be in YYYY-MM-DD format", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                insertNewHeldSeat(room, seats, date, event);
                loadHeldSeatsData();
                JOptionPane.showMessageDialog(dialog, "New held seats added successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Error adding record: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
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

    /**
     * Inserts a new held seat record into the database.
     *
     * @param room The room name
     * @param seats The number of seats
     * @param date The date in YYYY-MM-DD format
     * @param event The event name
     * @throws Exception If there is a database error
     */
    private void insertNewHeldSeat(String room, int seats, String date, String event) throws Exception {
        String query = "INSERT INTO heldSeats (room, seats, date, event) VALUES (?, ?, ?, ?)";

        try (Connection connection = myJDBC.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, room);
            pstmt.setInt(2, seats);
            pstmt.setString(3, date);
            pstmt.setString(4, event);
            pstmt.executeUpdate();
        }
    }

    /**
     * Loads held seats data from the database.
     */
    private void loadHeldSeatsData() {
        statusLabel.setText("Loading data...");
        statusLabel.setForeground(accentColor);

        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() {
                try (Connection connection = myJDBC.getConnection()) {
                    if (connection != null) {
                        String dataQuery = "SELECT * FROM heldSeats";
                        Statement dataStmt = connection.createStatement();
                        ResultSet dataRs = dataStmt.executeQuery(dataQuery);

                        tableModel.setRowCount(0);
                        int count = 0;

                        while (dataRs.next()) {
                            Object[] row = {
                                    dataRs.getInt("heldID"),
                                    dataRs.getString("room"),
                                    dataRs.getInt("seats"),
                                    dataRs.getString("date"),
                                    dataRs.getString("event")
                            };
                            tableModel.addRow(row);
                            count++;
                        }

                        final int finalCount = count;
                        SwingUtilities.invokeLater(() -> {
                            statusLabel.setText(finalCount + " records loaded");
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