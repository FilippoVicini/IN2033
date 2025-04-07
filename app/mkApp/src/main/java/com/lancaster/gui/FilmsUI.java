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

public class FilmsUI extends JPanel {
    private JTable filmsTable;
    private DefaultTableModel tableModel;
    private JLabel statusLabel;
    private JTextField searchField;

    // Colors to match TourBookingsUI
    private Color primaryColor = new Color(47, 54, 64);
    private Color accentColor = new Color(86, 101, 115);
    private Color highlightColor = new Color(52, 152, 219);
    private Color backgroundColor = new Color(245, 246, 250);

    public FilmsUI() {
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

        JLabel iconLabel = new JLabel("🎬");
        iconLabel.setFont(new Font("Segoe UI", Font.PLAIN, 24));

        JLabel titleLabel = new JLabel("Films of Lancaster's");
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
        searchField.setToolTipText("Search for films by name or description");

        JButton searchButton = new JButton("Search");
        styleButton(searchButton, highlightColor);
        searchButton.setForeground(Color.BLACK); // Make text black
        searchButton.addActionListener(e -> searchFilms());

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
        String[] columns = {"ID", "Name", "Description", "Duration (min)", "Price (£)"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table read-only
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                switch(columnIndex) {
                    case 0: return Integer.class; // ID
                    case 3: return Integer.class; // Duration
                    case 4: return Double.class;  // Price
                    default: return String.class;
                }
            }
        };

        // Create table
        filmsTable = new JTable(tableModel);
        styleTable(filmsTable);

        // Add scroll pane with styled border
        JScrollPane scrollPane = new JScrollPane(filmsTable);
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
        refreshButton.setForeground(Color.BLACK); // Make text black
        refreshButton.addActionListener(e -> loadFilmsData());

        JButton filmShowsButton = new JButton("Go to Film Shows");
        styleButton(filmShowsButton, Color.BLACK);
        filmShowsButton.setForeground(Color.BLACK); // Make text black
        filmShowsButton.addActionListener(e -> navigateToFilmShows());

        JButton newFilmButton = new JButton("New Film");
        styleButton(newFilmButton, Color.BLACK);
        newFilmButton.setForeground(Color.BLACK); // Make text black
        newFilmButton.addActionListener(e -> showNewFilmDialog());

        buttonPanel.add(refreshButton);
        buttonPanel.add(newFilmButton);
        buttonPanel.add(filmShowsButton);

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

        JLabel cardTitle = new JLabel("Film Directory");
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

        // Add right-click context menu
        addTableContextMenu();

        loadFilmsData();
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

        // Set column widths for better display
        table.getColumnModel().getColumn(0).setPreferredWidth(50);    // ID
        table.getColumnModel().getColumn(1).setPreferredWidth(150);   // Name
        table.getColumnModel().getColumn(2).setPreferredWidth(300);   // Description
        table.getColumnModel().getColumn(3).setPreferredWidth(100);   // Duration
        table.getColumnModel().getColumn(4).setPreferredWidth(100);   // Price

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
        button.setForeground(Color.WHITE); // Default color, will be overridden for specific buttons
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

    private void searchFilms() {
        String searchTerm = searchField.getText().trim();
        if (searchTerm.isEmpty()) {
            loadFilmsData();
            return;
        }

        statusLabel.setText("Searching...");
        statusLabel.setForeground(accentColor);

        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() {
                try (Connection connection = myJDBC.getConnection()) {
                    if (connection != null) {
                        String query = "SELECT id, name, description, duration, price FROM films " +
                                "WHERE name LIKE ? OR description LIKE ?";

                        PreparedStatement pstmt = connection.prepareStatement(query);
                        String pattern = "%" + searchTerm + "%";
                        pstmt.setString(1, pattern);
                        pstmt.setString(2, pattern);

                        ResultSet rs = pstmt.executeQuery();

                        tableModel.setRowCount(0);
                        int count = 0;

                        while (rs.next()) {
                            Object[] row = {
                                    rs.getInt("id"),
                                    rs.getString("name"),
                                    rs.getString("description"),
                                    rs.getInt("duration"),
                                    rs.getDouble("price")
                            };
                            tableModel.addRow(row);
                            count++;
                        }

                        final int finalCount = count;
                        SwingUtilities.invokeLater(() -> {
                            statusLabel.setText(finalCount + " films found");
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

    private void addTableContextMenu() {
        JPopupMenu contextMenu = new JPopupMenu();

        JMenuItem viewItem = new JMenuItem("View Film Details");
        viewItem.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        viewItem.addActionListener(e -> {
            int selectedRow = filmsTable.getSelectedRow();
            if (selectedRow >= 0) {
                int filmId = (Integer) filmsTable.getValueAt(selectedRow, 0);
                String filmName = (String) filmsTable.getValueAt(selectedRow, 1);
                viewFilmDetails(filmId, filmName);
            } else {
                JOptionPane.showMessageDialog(this, "Please select a film first", "No Selection", JOptionPane.WARNING_MESSAGE);
            }
        });

        JMenuItem showsItem = new JMenuItem("View Film Shows");
        showsItem.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        showsItem.addActionListener(e -> {
            int selectedRow = filmsTable.getSelectedRow();
            if (selectedRow >= 0) {
                int filmId = (Integer) filmsTable.getValueAt(selectedRow, 0);
                String filmName = (String) filmsTable.getValueAt(selectedRow, 1);
                viewFilmShows(filmId, filmName);
            } else {
                JOptionPane.showMessageDialog(this, "Please select a film first", "No Selection", JOptionPane.WARNING_MESSAGE);
            }
        });

        JMenuItem editItem = new JMenuItem("Edit Film");
        editItem.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        editItem.addActionListener(e -> {
            int selectedRow = filmsTable.getSelectedRow();
            if (selectedRow >= 0) {
                int filmId = (Integer) filmsTable.getValueAt(selectedRow, 0);
                String filmName = (String) filmsTable.getValueAt(selectedRow, 1);
                editFilm(filmId, filmName);
            } else {
                JOptionPane.showMessageDialog(this, "Please select a film first", "No Selection", JOptionPane.WARNING_MESSAGE);
            }
        });

        contextMenu.add(viewItem);
        contextMenu.add(showsItem);
        contextMenu.addSeparator();
        contextMenu.add(editItem);

        filmsTable.setComponentPopupMenu(contextMenu);
    }

    private void viewFilmDetails(int filmId, String filmName) {
        // Create styled details panel
        JPanel detailsPanel = new JPanel();
        detailsPanel.setLayout(new BoxLayout(detailsPanel, BoxLayout.Y_AXIS));
        detailsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        try (Connection connection = myJDBC.getConnection()) {
            if (connection != null) {
                String query = "SELECT * FROM films WHERE id = ?";
                PreparedStatement pstmt = connection.prepareStatement(query);
                pstmt.setInt(1, filmId);
                ResultSet rs = pstmt.executeQuery();

                if (rs.next()) {
                    addDetailRow(detailsPanel, "Film ID:", String.valueOf(rs.getInt("id")));
                    addDetailRow(detailsPanel, "Name:", rs.getString("name"));
                    addDetailRow(detailsPanel, "Description:", rs.getString("description"));
                    addDetailRow(detailsPanel, "Duration:", rs.getInt("duration") + " minutes");
                    addDetailRow(detailsPanel, "Price:", "£" + String.format("%.2f", rs.getDouble("price")));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Show details in a dialog
        JOptionPane.showMessageDialog(this, detailsPanel, "Film Details: " + filmName, JOptionPane.INFORMATION_MESSAGE);
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

    private void viewFilmShows(int filmId, String filmName) {
        // This would navigate to the film shows for this film
        JOptionPane.showMessageDialog(this,
                "Navigate to shows for film: " + filmName,
                "Film Shows",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void editFilm(int filmId, String filmName) {
        JOptionPane.showMessageDialog(this,
                "Edit functionality for film: " + filmName,
                "Not Implemented",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void navigateToFilmShows() {
        // This would navigate to the general film shows screen
        JOptionPane.showMessageDialog(this,
                "Navigate to Film Shows functionality would go here",
                "Film Shows",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void showNewFilmDialog() {
        // Create dialog for new film input with improved styling
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "New Film", true);
        dialog.setLayout(new BorderLayout());

        // Add dialog header
        JPanel dialogHeader = new JPanel(new BorderLayout());
        dialogHeader.setBackground(highlightColor);
        dialogHeader.setPreferredSize(new Dimension(dialogHeader.getWidth(), 60));
        dialogHeader.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        JLabel dialogTitle = new JLabel("Add New Film");
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
        // Name field
        addFormField(formPanel, "Film Name:", gbc, 0);
        JTextField nameField = createStyledTextField();
        gbc.gridx = 1; gbc.gridy = 0;
        formPanel.add(nameField, gbc);

        // Description field
        addFormField(formPanel, "Description:", gbc, 1);
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
        gbc.gridx = 1; gbc.gridy = 1;
        formPanel.add(descScrollPane, gbc);

        // Duration field
        addFormField(formPanel, "Duration (min):", gbc, 2);
        JTextField durationField = createStyledTextField();
        gbc.gridx = 1; gbc.gridy = 2;
        formPanel.add(durationField, gbc);

        // Price field
        addFormField(formPanel, "Price (£):", gbc, 3);
        JTextField priceField = createStyledTextField();
        gbc.gridx = 1; gbc.gridy = 3;
        formPanel.add(priceField, gbc);

        // Buttons panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(230, 230, 230)),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        JButton cancelButton = new JButton("Cancel");
        styleButton(cancelButton, new Color(190, 190, 190));

        JButton saveButton = new JButton("Save Film");
        styleButton(saveButton, highlightColor);

        saveButton.addActionListener(e -> {
            try {
                // Get values from form
                String name = nameField.getText().trim();
                String description = descriptionArea.getText().trim();

                // Validate input fields
                if (name.isEmpty() || description.isEmpty() ||
                        durationField.getText().trim().isEmpty() ||
                        priceField.getText().trim().isEmpty()) {

                    JOptionPane.showMessageDialog(dialog,
                            "Please fill in all required fields",
                            "Validation Error",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Parse numeric fields
                int duration;
                double price;

                try {
                    duration = Integer.parseInt(durationField.getText().trim());
                    price = Double.parseDouble(priceField.getText().trim());
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(dialog,
                            "Duration must be a whole number and price must be a valid number",
                            "Validation Error",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Check for reasonable values
                if (duration <= 0 || price <= 0) {
                    JOptionPane.showMessageDialog(dialog,
                            "Duration and price must be positive values",
                            "Validation Error",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Insert new film into the database
                insertNewFilm(name, description, duration, price);
                loadFilmsData(); // Refresh the table data

                // Show success message
                JOptionPane.showMessageDialog(dialog,
                        "New film has been added successfully!",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);

                dialog.dispose(); // Close the dialog after successful insertion
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog,
                        "Error adding new film: " + ex.getMessage(),
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
        dialog.setMinimumSize(new Dimension(450, dialog.getHeight()));
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

    private void insertNewFilm(String name, String description, int duration, double price) throws Exception {
        String query = "INSERT INTO films (name, description, duration, price) VALUES (?, ?, ?, ?)";

        try (Connection connection = myJDBC.getConnection()) {
            if (connection != null) {
                PreparedStatement pstmt = connection.prepareStatement(query);
                pstmt.setString(1, name);
                pstmt.setString(2, description);
                pstmt.setInt(3, duration);
                pstmt.setDouble(4, price);
                pstmt.executeUpdate();
            }
        }
    }

    private void loadFilmsData() {
        statusLabel.setText("Loading data...");
        statusLabel.setForeground(accentColor);

        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() {
                try (Connection connection = myJDBC.getConnection()) {
                    if (connection != null) {
                        String countQuery = "SELECT COUNT(id) FROM films";
                        try (Statement countStmt = connection.createStatement();
                             ResultSet countRs = countStmt.executeQuery(countQuery)) {

                            int filmCount = 0;
                            if (countRs.next()) {
                                filmCount = countRs.getInt(1);
                            }

                            String dataQuery = "SELECT id, name, description, duration, price FROM films";
                            try (Statement dataStmt = connection.createStatement();
                                 ResultSet dataRs = dataStmt.executeQuery(dataQuery)) {

                                tableModel.setRowCount(0); // Clear existing data

                                while (dataRs.next()) {
                                    Object[] row = {
                                            dataRs.getInt("id"),
                                            dataRs.getString("name"),
                                            dataRs.getString("description"),
                                            dataRs.getInt("duration"),
                                            dataRs.getDouble("price")
                                    };
                                    tableModel.addRow(row);
                                }

                                final int finalCount = filmCount;
                                SwingUtilities.invokeLater(() -> {
                                    statusLabel.setText(finalCount + " films loaded");
                                    statusLabel.setForeground(new Color(46, 204, 113));
                                });
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
        };

        worker.execute();
    }
}