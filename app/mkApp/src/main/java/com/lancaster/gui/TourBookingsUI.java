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

public class TourBookingsUI extends JPanel {
    private JTable bookingsTable;
    private DefaultTableModel tableModel;
    private JLabel statusLabel;
    private JTextField searchField;

    // Colors to match HomeUI
    private Color primaryColor = new Color(47, 54, 64);
    private Color accentColor = new Color(86, 101, 115);
    private Color highlightColor = new Color(52, 152, 219);
    private Color backgroundColor = new Color(245, 246, 250);

    public TourBookingsUI() {
        setLayout(new BorderLayout());
        setBackground(backgroundColor);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Create header panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(backgroundColor);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        // Title with icon
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        titlePanel.setOpaque(false);

        JLabel iconLabel = new JLabel("🚌");
        iconLabel.setFont(new Font("Segoe UI", Font.PLAIN, 24));

        JLabel titleLabel = new JLabel("Tour Bookings");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(new Color(52, 73, 94));

        titlePanel.add(iconLabel);
        titlePanel.add(titleLabel);
        headerPanel.add(titlePanel, BorderLayout.WEST);

        // Create actions panel with search and status
        JPanel actionsPanel = new JPanel(new BorderLayout(10, 0));
        actionsPanel.setOpaque(false);

        // Search field
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        searchPanel.setOpaque(false);
        searchField = new JTextField(15);
        searchField.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(200, 200, 200), 1, true),
                new EmptyBorder(5, 10, 5, 10)
        ));

        JButton searchButton = new JButton("Search");
        styleButton(searchButton, highlightColor);
        searchButton.addActionListener(e -> searchBookings(searchField.getText()));

        searchPanel.add(new JLabel("Search: "));
        searchPanel.add(searchField);
        searchPanel.add(searchButton);

        // Status label
        statusLabel = new JLabel("Loading data...");
        statusLabel.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        statusLabel.setForeground(accentColor);

        actionsPanel.add(searchPanel, BorderLayout.CENTER);
        actionsPanel.add(statusLabel, BorderLayout.EAST);

        headerPanel.add(actionsPanel, BorderLayout.EAST);

        // Create table model
        String[] columns = {"ID", "Organization Type", "Organization Name", "Start Date", "End Date", "People", "Room", "Venue"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table read-only
            }
        };

        // Create table
        bookingsTable = new JTable(tableModel);
        styleTable(bookingsTable);

        // Add scroll pane with styled border
        JScrollPane scrollPane = new JScrollPane(bookingsTable);
        scrollPane.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(230, 230, 230), 1),
                new EmptyBorder(0, 0, 0, 0)
        ));
        scrollPane.setBackground(Color.WHITE);

        // Create button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 15));
        buttonPanel.setOpaque(false);

        JButton refreshButton = new JButton("Refresh");
        styleButton(refreshButton, Color.BLACK);
        refreshButton.addActionListener(e -> loadTourBookingsData());

        JButton newBookingButton = new JButton("New Booking");
        styleButton(newBookingButton, Color.BLACK);
        newBookingButton.addActionListener(e -> createNewBooking());

        buttonPanel.add(refreshButton);
        buttonPanel.add(newBookingButton);

        // Create card panel to wrap the table
        JPanel cardPanel = new JPanel(new BorderLayout());
        cardPanel.setBackground(Color.WHITE);
        cardPanel.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(230, 230, 230), 1),
                new EmptyBorder(15, 15, 15, 15)
        ));

        // Add a subtle header to the card
        JPanel cardHeader = new JPanel(new BorderLayout());
        cardHeader.setBackground(Color.WHITE);
        cardHeader.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(230, 230, 230)));
        cardHeader.setPreferredSize(new Dimension(cardHeader.getWidth(), 40));

        JLabel cardTitle = new JLabel("Current Bookings");
        cardTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        cardTitle.setForeground(new Color(52, 73, 94));
        cardTitle.setBorder(BorderFactory.createEmptyBorder(0, 5, 10, 0));

        cardHeader.add(cardTitle, BorderLayout.WEST);

        // Assemble the card
        cardPanel.add(cardHeader, BorderLayout.NORTH);
        cardPanel.add(scrollPane, BorderLayout.CENTER);

        // Add components to main panel
        add(headerPanel, BorderLayout.NORTH);
        add(cardPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        loadTourBookingsData();
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

        // Style table header
        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setBackground(new Color(245, 246, 250));
        header.setForeground(new Color(52, 73, 94));
        header.setPreferredSize(new Dimension(header.getWidth(), 40));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(230, 230, 230)));
    }

    private void styleButton(JButton button, Color color) {
        button.setBackground(color);
        button.setForeground(Color.BLACK);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));

        // Add hover effect
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

    private void loadTourBookingsData() {
        statusLabel.setText("Loading data...");
        statusLabel.setForeground(accentColor);

        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() {
                try (Connection connection = myJDBC.getConnection()) {
                    if (connection != null) {
                        String dataQuery = "SELECT bookingID, organizationType, organizationName, startDate, endDate, people, room, venue FROM tour_bookings";
                        Statement dataStmt = connection.createStatement();
                        ResultSet dataRs = dataStmt.executeQuery(dataQuery);

                        tableModel.setRowCount(0); // Clear existing data

                        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy HH:mm");

                        while (dataRs.next()) {
                            Object[] row = {
                                    dataRs.getInt("bookingID"),
                                    dataRs.getString("organizationType"),
                                    dataRs.getString("organizationName"),
                                    dateFormat.format(dataRs.getTimestamp("startDate")),
                                    dateFormat.format(dataRs.getTimestamp("endDate")),
                                    dataRs.getInt("people"),
                                    dataRs.getString("room"),
                                    dataRs.getString("venue")
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
                statusLabel.setText(tableModel.getRowCount() + " bookings loaded");
                statusLabel.setForeground(new Color(46, 204, 113));
            }
        };

        worker.execute();
    }

    private void searchBookings(String searchTerm) {
        if (searchTerm.isEmpty()) {
            loadTourBookingsData();
            return;
        }

        statusLabel.setText("Searching...");
        statusLabel.setForeground(accentColor);

        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() {
                try (Connection connection = myJDBC.getConnection()) {
                    if (connection != null) {
                        String dataQuery = "SELECT bookingID, organizationType, organizationName, startDate, endDate, people, room, venue FROM tour_bookings " +
                                "WHERE organizationName LIKE ? OR organizationType LIKE ? OR venue LIKE ? OR room LIKE ?";

                        PreparedStatement pstmt = connection.prepareStatement(dataQuery);
                        String searchPattern = "%" + searchTerm + "%";
                        pstmt.setString(1, searchPattern);
                        pstmt.setString(2, searchPattern);
                        pstmt.setString(3, searchPattern);
                        pstmt.setString(4, searchPattern);

                        ResultSet dataRs = pstmt.executeQuery();

                        tableModel.setRowCount(0); // Clear existing data

                        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy HH:mm");

                        while (dataRs.next()) {
                            Object[] row = {
                                    dataRs.getInt("bookingID"),
                                    dataRs.getString("organizationType"),
                                    dataRs.getString("organizationName"),
                                    dateFormat.format(dataRs.getTimestamp("startDate")),
                                    dateFormat.format(dataRs.getTimestamp("endDate")),
                                    dataRs.getInt("people"),
                                    dataRs.getString("room"),
                                    dataRs.getString("venue")
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

    private void createNewBooking() {
        // Create dialog for new booking input with improved styling
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "New Tour Booking", true);
        dialog.setLayout(new BorderLayout());

        // Add dialog header
        JPanel dialogHeader = new JPanel(new BorderLayout());
        dialogHeader.setBackground(highlightColor);
        dialogHeader.setPreferredSize(new Dimension(dialogHeader.getWidth(), 60));
        dialogHeader.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        JLabel dialogTitle = new JLabel("Create New Tour Booking");
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

        // Create form fields with improved styling
        // Organization Type field
        addFormField(formPanel, "Organization Type:", gbc, 0);
        JTextField organizationTypeField = createStyledTextField();
        gbc.gridx = 1; gbc.gridy = 0;
        formPanel.add(organizationTypeField, gbc);

        // Organization Name field
        addFormField(formPanel, "Organization Name:", gbc, 1);
        JTextField organizationNameField = createStyledTextField();
        gbc.gridx = 1; gbc.gridy = 1;
        formPanel.add(organizationNameField, gbc);

        // Start Date field
        addFormField(formPanel, "Start Date (YYYY-MM-DD HH:MM:SS):", gbc, 2);
        JTextField startDateField = createStyledTextField();
        gbc.gridx = 1; gbc.gridy = 2;
        formPanel.add(startDateField, gbc);

        // End Date field
        addFormField(formPanel, "End Date (YYYY-MM-DD HH:MM:SS):", gbc, 3);
        JTextField endDateField = createStyledTextField();
        gbc.gridx = 1; gbc.gridy = 3;
        formPanel.add(endDateField, gbc);

        // People field
        addFormField(formPanel, "Number of People:", gbc, 4);
        JTextField peopleField = createStyledTextField();
        gbc.gridx = 1; gbc.gridy = 4;
        formPanel.add(peopleField, gbc);

        // Room field
        addFormField(formPanel, "Room:", gbc, 5);
        JTextField roomField = createStyledTextField();
        gbc.gridx = 1; gbc.gridy = 5;
        formPanel.add(roomField, gbc);

        // Venue field
        addFormField(formPanel, "Venue:", gbc, 6);
        JTextField venueField = createStyledTextField();
        gbc.gridx = 1; gbc.gridy = 6;
        formPanel.add(venueField, gbc);

        // Buttons panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(230, 230, 230)),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        JButton cancelButton = new JButton("Cancel");
        styleButton(cancelButton, new Color(190, 190, 190));

        JButton saveButton = new JButton("Save Booking");
        styleButton(saveButton, highlightColor);

        saveButton.addActionListener(e -> {
            try {
                // Get values from form
                String organizationType = organizationTypeField.getText();
                String organizationName = organizationNameField.getText();
                String startDateStr = startDateField.getText();
                String endDateStr = endDateField.getText();
                int people = Integer.parseInt(peopleField.getText());
                String room = roomField.getText();
                String venue = venueField.getText();

                // Validate input fields
                if (organizationType.isEmpty() || organizationName.isEmpty() ||
                        startDateStr.isEmpty() || endDateStr.isEmpty() ||
                        room.isEmpty() || venue.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog,
                            "Please fill in all required fields",
                            "Validation Error",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Validate timestamp format
                if (!startDateStr.matches("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}") ||
                        !endDateStr.matches("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}")) {
                    JOptionPane.showMessageDialog(dialog,
                            "Please enter valid dates and times in the format YYYY-MM-DD HH:MM:SS",
                            "Validation Error",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Convert startDateStr and endDateStr to Timestamp
                java.sql.Timestamp startTimestamp = java.sql.Timestamp.valueOf(startDateStr);
                java.sql.Timestamp endTimestamp = java.sql.Timestamp.valueOf(endDateStr);

                // Insert new booking into the database
                insertNewBooking(organizationType, organizationName, startTimestamp, endTimestamp, people, room, venue);
                loadTourBookingsData(); // Refresh the table data

                // Show success message with improved styling
                JOptionPane.showMessageDialog(dialog,
                        "New tour booking has been added successfully!",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);

                dialog.dispose(); // Close the dialog after successful insertion
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog,
                        "Please enter a valid number for People",
                        "Input Error",
                        JOptionPane.ERROR_MESSAGE);
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(dialog,
                        "Please enter valid dates and times in the format YYYY-MM-DD HH:MM:SS",
                        "Validation Error",
                        JOptionPane.ERROR_MESSAGE);

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog,
                        "Error adding new booking: " + ex.getMessage(),
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

    private JTextField createStyledTextField() {
        JTextField field = new JTextField(20);
        field.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(200, 200, 200), 1, true),
                new EmptyBorder(8, 10, 8, 10)
        ));
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        return field;
    }

    private void insertNewBooking(String organizationType, String organizationName, java.sql.Timestamp startTimestamp, java.sql.Timestamp endTimestamp, int people, String room, String venue) throws Exception {
        String query = "INSERT INTO tour_bookings (organizationType, organizationName, startDate, endDate, people, room, venue) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection connection = myJDBC.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, organizationType);
            pstmt.setString(2, organizationName);
            pstmt.setTimestamp(3, startTimestamp);
            pstmt.setTimestamp(4, endTimestamp);
            pstmt.setInt(5, people);
            pstmt.setString(6, room);
            pstmt.setString(7, venue);
            pstmt.executeUpdate();
        }
    }
}