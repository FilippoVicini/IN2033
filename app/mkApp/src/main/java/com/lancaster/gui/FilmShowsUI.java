package com.lancaster.gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.RowFilter;
import com.lancaster.database.myJDBC;

/**
 * UI panel for managing film shows.
 * Provides a table display of film screenings with filtering and CRUD operations.
 */
public class FilmShowsUI extends JPanel {
    private JTable showsTable;
    private DefaultTableModel tableModel;
    private JLabel statusLabel;
    private JTextField searchField;
    private TableRowSorter<DefaultTableModel> sorter;

    private Color primaryColor = new Color(47, 54, 64);
    private Color accentColor = new Color(86, 101, 115);
    private Color highlightColor = new Color(52, 152, 219);
    private Color backgroundColor = new Color(245, 246, 250);

    /**
     * Constructs the FilmShowsUI panel with search functionality and film shows table.
     */
    public FilmShowsUI() {
        setLayout(new BorderLayout());
        setBackground(backgroundColor);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(backgroundColor);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        titlePanel.setOpaque(false);

        JLabel iconLabel = new JLabel("🎬");
        iconLabel.setFont(new Font("Segoe UI", Font.PLAIN, 24));

        JLabel titleLabel = new JLabel("Film Shows");
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
        searchField.setToolTipText("Search for shows by title, date, or room");

        JButton searchButton = new JButton("Search");
        styleButton(searchButton, highlightColor);
        searchButton.setForeground(Color.BLACK);
        searchButton.addActionListener(e -> searchFilter());

        searchPanel.add(new JLabel("Search: "));
        searchPanel.add(searchField);
        searchPanel.add(searchButton);

        statusLabel = new JLabel("Loading data...");
        statusLabel.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        statusLabel.setForeground(accentColor);

        actionsPanel.add(searchPanel, BorderLayout.CENTER);
        actionsPanel.add(statusLabel, BorderLayout.EAST);

        headerPanel.add(actionsPanel, BorderLayout.EAST);

        String[] columns = {"ID", "Film ID", "Film Title", "Show Date", "End Date", "Duration", "Ticket Price", "Room"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 0 || columnIndex == 1 || columnIndex == 5) {
                    return Integer.class;
                } else if (columnIndex == 6) {
                    return Double.class;
                } else if (columnIndex == 3 || columnIndex == 4) {
                    return Date.class;
                }
                return String.class;
            }
        };

        showsTable = new JTable(tableModel);
        sorter = new TableRowSorter<>(tableModel);
        showsTable.setRowSorter(sorter);
        styleTable(showsTable);

        searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                searchFilter();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                searchFilter();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                searchFilter();
            }
        });

        JScrollPane scrollPane = new JScrollPane(showsTable);
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
        refreshButton.addActionListener(e -> loadFilmShowsData());

        JButton newShowButton = new JButton("New Show");
        styleButton(newShowButton, Color.BLACK);
        newShowButton.setForeground(Color.BLACK);
        newShowButton.addActionListener(e -> createNewShow());

        JButton deleteShowButton = new JButton("Delete Show");
        styleButton(deleteShowButton, Color.BLACK);
        deleteShowButton.setForeground(Color.BLACK);
        deleteShowButton.addActionListener(e -> deleteSelectedShow());

        buttonPanel.add(refreshButton);
        buttonPanel.add(newShowButton);
        buttonPanel.add(deleteShowButton);

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

        JLabel cardTitle = new JLabel("Current Film Shows");
        cardTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        cardTitle.setForeground(new Color(52, 73, 94));
        cardTitle.setBorder(BorderFactory.createEmptyBorder(0, 5, 10, 0));

        cardHeader.add(cardTitle, BorderLayout.WEST);

        cardPanel.add(cardHeader, BorderLayout.NORTH);
        cardPanel.add(scrollPane, BorderLayout.CENTER);

        add(headerPanel, BorderLayout.NORTH);
        add(cardPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        addContextMenu();

        loadFilmShowsData();
    }

    /**
     * Applies styling to the film shows table.
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

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                if (isSelected) {
                    c.setBackground(new Color(232, 242, 254));
                    c.setForeground(Color.BLACK);
                } else {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(249, 250, 252));
                    c.setForeground(Color.BLACK);
                }

                if (column == 6 && value != null) {
                    setText("£" + String.format("%.2f", value));
                }

                if (column == 5 && value != null) {
                    setText(value + " min");
                }

                setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
                return c;
            }
        };
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);

        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

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
     * Filters the table based on search text.
     */
    private void searchFilter() {
        String text = searchField.getText().trim().toLowerCase();
        if (text.isEmpty()) {
            sorter.setRowFilter(null);
            statusLabel.setText("Showing all records");
        } else {
            List<RowFilter<Object, Object>> filters = new ArrayList<>();
            filters.add(RowFilter.regexFilter("(?i)" + text, 2));
            filters.add(RowFilter.regexFilter("(?i)" + text, 3));
            filters.add(RowFilter.regexFilter("(?i)" + text, 7));

            RowFilter<Object, Object> combinedFilter = RowFilter.orFilter(filters);
            sorter.setRowFilter(combinedFilter);

            int rowCount = showsTable.getRowCount();
            statusLabel.setText(rowCount + " results found");
            statusLabel.setForeground(new Color(46, 204, 113));
        }
    }

    /**
     * Adds a context menu to the film shows table.
     */
    private void addContextMenu() {
        JPopupMenu contextMenu = new JPopupMenu();

        JMenuItem viewDetailsItem = new JMenuItem("View Details");
        viewDetailsItem.addActionListener(e -> viewShowDetails());

        JMenuItem editItem = new JMenuItem("Edit Show");
        editItem.addActionListener(e -> editSelectedShow());

        JMenuItem deleteItem = new JMenuItem("Delete Show");
        deleteItem.addActionListener(e -> deleteSelectedShow());

        contextMenu.add(viewDetailsItem);
        contextMenu.add(editItem);
        contextMenu.add(deleteItem);

        showsTable.setComponentPopupMenu(contextMenu);
    }

    /**
     * Displays details of the selected film show.
     */
    private void viewShowDetails() {
        int selectedRow = showsTable.getSelectedRow();
        if (selectedRow != -1) {
            int modelRow = showsTable.convertRowIndexToModel(selectedRow);

            int showId = (Integer) tableModel.getValueAt(modelRow, 0);
            String filmTitle = (String) tableModel.getValueAt(modelRow, 2);
            Date showDate = (Date) tableModel.getValueAt(modelRow, 3);
            Date endDate = (Date) tableModel.getValueAt(modelRow, 4);
            int duration = (Integer) tableModel.getValueAt(modelRow, 5);
            double price = (Double) tableModel.getValueAt(modelRow, 6);
            String room = (String) tableModel.getValueAt(modelRow, 7);

            JPanel detailsPanel = new JPanel();
            detailsPanel.setLayout(new BoxLayout(detailsPanel, BoxLayout.Y_AXIS));
            detailsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");

            addDetailRow(detailsPanel, "Show ID:", String.valueOf(showId));
            addDetailRow(detailsPanel, "Film:", filmTitle);
            addDetailRow(detailsPanel, "Show Date:", showDate != null ? dateFormat.format(showDate) : "N/A");
            addDetailRow(detailsPanel, "End Date:", endDate != null ? dateFormat.format(endDate) : "N/A");
            addDetailRow(detailsPanel, "Duration:", duration + " minutes");
            addDetailRow(detailsPanel, "Ticket Price:", "£" + String.format("%.2f", price));
            addDetailRow(detailsPanel, "Room:", room);

            JOptionPane.showMessageDialog(this, detailsPanel, "Show Details", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "Please select a show first", "No Selection", JOptionPane.WARNING_MESSAGE);
        }
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
     * Opens a dialog to edit the selected film show.
     */
    private void editSelectedShow() {
        int selectedRow = showsTable.getSelectedRow();
        if (selectedRow != -1) {
            int modelRow = showsTable.convertRowIndexToModel(selectedRow);

            int showId = (Integer) tableModel.getValueAt(modelRow, 0);
            int filmId = (Integer) tableModel.getValueAt(modelRow, 1);
            String filmTitle = (String) tableModel.getValueAt(modelRow, 2);
            Date showDate = (Date) tableModel.getValueAt(modelRow, 3);
            Date endDate = (Date) tableModel.getValueAt(modelRow, 4);
            int duration = (Integer) tableModel.getValueAt(modelRow, 5);
            double price = (Double) tableModel.getValueAt(modelRow, 6);
            String room = (String) tableModel.getValueAt(modelRow, 7);

            JOptionPane.showMessageDialog(this, "Edit functionality would go here", "Not Implemented", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "Please select a show first", "No Selection", JOptionPane.WARNING_MESSAGE);
        }
    }

    /**
     * Deletes the selected film show after confirmation.
     */
    private void deleteSelectedShow() {
        int selectedRow = showsTable.getSelectedRow();
        if (selectedRow != -1) {
            int modelRow = showsTable.convertRowIndexToModel(selectedRow);
            int showId = (Integer) tableModel.getValueAt(modelRow, 0);
            String filmTitle = (String) tableModel.getValueAt(modelRow, 2);

            JPanel confirmPanel = new JPanel(new BorderLayout(10, 10));
            confirmPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

            JLabel iconLabel = new JLabel(UIManager.getIcon("OptionPane.warningIcon"));
            JLabel messageLabel = new JLabel("<html>Are you sure you want to delete show #" + showId + "?<br><b>" + filmTitle + "</b></html>");
            messageLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));

            confirmPanel.add(iconLabel, BorderLayout.WEST);
            confirmPanel.add(messageLabel, BorderLayout.CENTER);

            int confirm = JOptionPane.showConfirmDialog(this,
                    confirmPanel,
                    "Confirm Deletion",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);

            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    deleteShow(showId);
                    loadFilmShowsData();
                    statusLabel.setText("Show #" + showId + " deleted successfully");
                    statusLabel.setForeground(new Color(46, 204, 113));
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(this, "Error deleting show: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a show first", "No Selection", JOptionPane.WARNING_MESSAGE);
        }
    }

    /**
     * Deletes a show from the database.
     *
     * @param showId The ID of the show to delete
     * @throws Exception If there is a database error
     */
    private void deleteShow(int showId) throws Exception {
        String query = "DELETE FROM film_bookings WHERE showId = ?";

        try (Connection connection = myJDBC.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, showId);
            pstmt.executeUpdate();
        }
    }

    /**
     * Loads film show data from the database.
     */
    private void loadFilmShowsData() {
        statusLabel.setText("Loading data...");
        statusLabel.setForeground(accentColor);

        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() {
                try (Connection connection = myJDBC.getConnection()) {
                    if (connection != null) {
                        String dataQuery = "SELECT showId, filmId, filmTitle, showDate, endDate, duration, ticketPrice, room, venue, description FROM film_bookings";
                        Statement dataStmt = connection.createStatement();
                        ResultSet dataRs = dataStmt.executeQuery(dataQuery);

                        tableModel.setRowCount(0);

                        while (dataRs.next()) {
                            Date showDate = dataRs.getDate("showDate");
                            if (showDate != null && showDate.toString().equals("0000-00-00")) {
                                showDate = null;
                            }

                            Date endDate = dataRs.getDate("endDate");
                            if (endDate != null && endDate.toString().equals("0000-00-00")) {
                                endDate = null;
                            }

                            Object[] row = {
                                    dataRs.getInt("showId"),
                                    dataRs.getInt("filmId"),
                                    dataRs.getString("filmTitle"),
                                    showDate,
                                    endDate,
                                    dataRs.getInt("duration"),
                                    dataRs.getDouble("ticketPrice"),
                                    dataRs.getString("room")
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
                statusLabel.setText(tableModel.getRowCount() + " shows loaded");
                statusLabel.setForeground(new Color(46, 204, 113));
            }
        };

        worker.execute();
    }

    /**
     * Opens a dialog to create a new film show.
     */
    private void createNewShow() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "New Film Show", true);
        dialog.setLayout(new BorderLayout());

        JPanel dialogHeader = new JPanel(new BorderLayout());
        dialogHeader.setBackground(highlightColor);
        dialogHeader.setPreferredSize(new Dimension(dialogHeader.getWidth(), 60));
        dialogHeader.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        JLabel dialogTitle = new JLabel("Create New Film Show");
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

        addFormField(formPanel, "Film ID:", gbc, 0);
        JTextField filmIdField = createStyledTextField();
        gbc.gridx = 1; gbc.gridy = 0;
        formPanel.add(filmIdField, gbc);

        addFormField(formPanel, "Film Title:", gbc, 1);
        JTextField filmTitleField = createStyledTextField();
        gbc.gridx = 1; gbc.gridy = 1;
        formPanel.add(filmTitleField, gbc);

        addFormField(formPanel, "Start Date (YYYY-MM-DD HH:MM:SS):", gbc, 2);
        JTextField startDateField = createStyledTextField();
        gbc.gridx = 1; gbc.gridy = 2;
        formPanel.add(startDateField, gbc);

        addFormField(formPanel, "End Date (YYYY-MM-DD HH:MM:SS):", gbc, 3);
        JTextField endDateField = createStyledTextField();
        gbc.gridx = 1; gbc.gridy = 3;
        formPanel.add(endDateField, gbc);

        addFormField(formPanel, "Duration (min):", gbc, 4);
        JTextField durationField = createStyledTextField();
        gbc.gridx = 1; gbc.gridy = 4;
        formPanel.add(durationField, gbc);

        addFormField(formPanel, "Ticket Price (£):", gbc, 5);
        JTextField ticketPriceField = createStyledTextField();
        gbc.gridx = 1; gbc.gridy = 5;
        formPanel.add(ticketPriceField, gbc);

        addFormField(formPanel, "Room:", gbc, 6);
        JTextField roomField = createStyledTextField();
        gbc.gridx = 1; gbc.gridy = 6;
        formPanel.add(roomField, gbc);

        addFormField(formPanel, "Venue:", gbc, 7);
        JTextField venueField = createStyledTextField();
        gbc.gridx = 1; gbc.gridy = 7;
        formPanel.add(venueField, gbc);

        addFormField(formPanel, "Description:", gbc, 8);
        JTextArea descriptionArea = new JTextArea(3, 20);
        descriptionArea.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(200, 200, 200), 1, true),
                new EmptyBorder(8, 10, 8, 10)
        ));
        descriptionArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);

        JScrollPane descScrollPane = new JScrollPane(descriptionArea);
        descScrollPane.setBorder(BorderFactory.createEmptyBorder());
        gbc.gridx = 1; gbc.gridy = 8;
        formPanel.add(descScrollPane, gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(230, 230, 230)),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        JButton cancelButton = new JButton("Cancel");
        styleButton(cancelButton, new Color(190, 190, 190));

        JButton saveButton = new JButton("Save Show");
        styleButton(saveButton, highlightColor);

        saveButton.addActionListener(e -> {
            try {
                if (filmIdField.getText().trim().isEmpty() || filmTitleField.getText().trim().isEmpty() ||
                        startDateField.getText().trim().isEmpty() || endDateField.getText().trim().isEmpty() ||
                        durationField.getText().trim().isEmpty() || ticketPriceField.getText().trim().isEmpty() ||
                        roomField.getText().trim().isEmpty() || venueField.getText().trim().isEmpty() ||
                        descriptionArea.getText().trim().isEmpty()) {

                    JOptionPane.showMessageDialog(dialog, "All fields are required", "Validation Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                int filmId = Integer.parseInt(filmIdField.getText());
                String filmTitle = filmTitleField.getText();
                String startDateStr = startDateField.getText();
                String endDateStr = endDateField.getText();
                int duration = Integer.parseInt(durationField.getText());
                double ticketPrice = Double.parseDouble(ticketPriceField.getText());
                String room = roomField.getText();
                String venue = venueField.getText();
                String description = descriptionArea.getText();

                if (!startDateStr.matches("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}") ||
                        !endDateStr.matches("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}")) {
                    JOptionPane.showMessageDialog(dialog,
                            "Please enter valid dates and times in the format YYYY-MM-DD HH:MM:SS",
                            "Validation Error",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                java.sql.Timestamp startTimestamp = java.sql.Timestamp.valueOf(startDateStr);
                java.sql.Timestamp endTimestamp = java.sql.Timestamp.valueOf(endDateStr);

                insertNewShow(filmId, filmTitle, startTimestamp, endTimestamp, duration, ticketPrice, room, venue, description);
                loadFilmShowsData();

                JOptionPane.showMessageDialog(dialog,
                        "New film show has been added successfully!",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);

                dialog.dispose();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog,
                        "Please enter valid numbers for Film ID, Duration, and Ticket Price",
                        "Input Error",
                        JOptionPane.ERROR_MESSAGE);
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(dialog,
                        "Please enter valid dates and times in the format YYYY-MM-DD HH:MM:SS",
                        "Validation Error",
                        JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog,
                        "Error adding new show: " + ex.getMessage(),
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
     * Inserts a new film show into the database.
     *
     * @param filmId The film ID
     * @param filmTitle The film title
     * @param startTimestamp The start timestamp
     * @param endTimestamp The end timestamp
     * @param duration The duration in minutes
     * @param ticketPrice The ticket price
     * @param room The room
     * @param venue The venue
     * @param description The description
     * @throws Exception If there is a database error
     */
    private void insertNewShow(int filmId, String filmTitle, java.sql.Timestamp startTimestamp, java.sql.Timestamp endTimestamp, int duration, double ticketPrice, String room, String venue, String description) throws Exception {
        String query = "INSERT INTO film_bookings (filmId, filmTitle, showDate, endDate, duration, ticketPrice, room, venue, description) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection connection = myJDBC.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, filmId);
            pstmt.setString(2, filmTitle);
            pstmt.setTimestamp(3, startTimestamp);
            pstmt.setTimestamp(4, endTimestamp);
            pstmt.setInt(5, duration);
            pstmt.setDouble(6, ticketPrice);
            pstmt.setString(7, room);
            pstmt.setString(8, venue);
            pstmt.setString(9, description);
            pstmt.executeUpdate();
        }
    }
}