package com.lancaster.gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.sql.*;
import java.text.SimpleDateFormat;

import com.lancaster.database.myJDBC;

/**
 * UI panel for displaying and managing marketing events.
 * Provides functionality for viewing, searching, and deleting events.
 */
public class MarketingEventsUI extends JPanel {
    private JTable eventsTable;
    private DefaultTableModel tableModel;
    private JLabel statusLabel;
    private JTextField searchField;

    private Color primaryColor = new Color(47, 54, 64);
    private Color accentColor = new Color(86, 101, 115);
    private Color highlightColor = new Color(52, 152, 219);
    private Color backgroundColor = new Color(245, 246, 250);

    /**
     * Constructs the MarketingEventsUI panel with table and controls.
     */
    public MarketingEventsUI() {

        setLayout(new BorderLayout());
        setBackground(backgroundColor);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(backgroundColor);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        titlePanel.setOpaque(false);

        JLabel iconLabel = new JLabel("📊");
        iconLabel.setFont(new Font("Segoe UI", Font.PLAIN, 24));

        JLabel titleLabel = new JLabel("Marketing Events");
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
        searchField.setToolTipText("Search for events by name or type");

        JButton searchButton = new JButton("Search");
        styleButton(searchButton, highlightColor);
        searchButton.setForeground(Color.BLACK);
        searchButton.addActionListener(e -> searchEvents());

        searchPanel.add(new JLabel("Search: "));
        searchPanel.add(searchField);
        searchPanel.add(searchButton);

        statusLabel = new JLabel("Loading data...");
        statusLabel.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        statusLabel.setForeground(accentColor);

        actionsPanel.add(searchPanel, BorderLayout.CENTER);
        actionsPanel.add(statusLabel, BorderLayout.EAST);

        headerPanel.add(actionsPanel, BorderLayout.EAST);

        String[] columns = {"Event ID", "Type", "Name", "Start Date", "End Date", "Room", "People", "Venue"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        eventsTable = new JTable(tableModel);
        styleTable(eventsTable);

        JScrollPane scrollPane = new JScrollPane(eventsTable);
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
        refreshButton.addActionListener(e -> loadMarketingEventsData());

        JButton fetchEventsButton = new JButton("Fetch New Events");
        styleButton(fetchEventsButton, Color.BLACK);
        fetchEventsButton.setForeground(Color.BLACK);
        fetchEventsButton.addActionListener(e -> fetchAndInsertEvents());

        buttonPanel.add(refreshButton);
        buttonPanel.add(fetchEventsButton);

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

        JLabel cardTitle = new JLabel("Events Directory");
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

        loadMarketingEventsData();
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
     * Searches for events based on the text in the search field.
     */
    private void searchEvents() {
        String searchTerm = searchField.getText().trim();
        if (searchTerm.isEmpty()) {
            loadMarketingEventsData();
            return;
        }

        statusLabel.setText("Searching...");
        statusLabel.setForeground(accentColor);

        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() {
                try (Connection connection = myJDBC.getConnection()) {
                    if (connection != null) {
                        String query = "SELECT * FROM marketing_events " +
                                "WHERE name LIKE ? OR type LIKE ? OR venue LIKE ?";

                        PreparedStatement pstmt = connection.prepareStatement(query);
                        String pattern = "%" + searchTerm + "%";
                        pstmt.setString(1, pattern);
                        pstmt.setString(2, pattern);
                        pstmt.setString(3, pattern);

                        ResultSet rs = pstmt.executeQuery();

                        tableModel.setRowCount(0);
                        int count = 0;

                        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy HH:mm");

                        while (rs.next()) {
                            Object[] row = {
                                    rs.getInt("eventId"),
                                    rs.getString("type"),
                                    rs.getString("name"),
                                    dateFormat.format(rs.getTimestamp("startDate")),
                                    dateFormat.format(rs.getTimestamp("endDate")),
                                    rs.getInt("room"),
                                    rs.getInt("peopleNum"),
                                    rs.getString("venue")
                            };
                            tableModel.addRow(row);
                            count++;
                        }

                        final int finalCount = count;
                        SwingUtilities.invokeLater(() -> {
                            statusLabel.setText(finalCount + " events found");
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
     * Adds a context menu to the events table.
     */
    private void addTableContextMenu() {
        JPopupMenu contextMenu = new JPopupMenu();

        JMenuItem viewItem = new JMenuItem("View Event Details");
        viewItem.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        viewItem.addActionListener(e -> {
            int selectedRow = eventsTable.getSelectedRow();
            if (selectedRow >= 0) {
                int eventId = (Integer) eventsTable.getValueAt(selectedRow, 0);
                String eventName = (String) eventsTable.getValueAt(selectedRow, 2);
                viewEventDetails(eventId, eventName);
            } else {
                JOptionPane.showMessageDialog(this, "Please select an event first", "No Selection", JOptionPane.WARNING_MESSAGE);
            }
        });

        JMenuItem deleteItem = new JMenuItem("Delete Event");
        deleteItem.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        deleteItem.addActionListener(e -> {
            int selectedRow = eventsTable.getSelectedRow();
            if (selectedRow >= 0) {
                int eventId = (Integer) eventsTable.getValueAt(selectedRow, 0);
                String eventName = (String) eventsTable.getValueAt(selectedRow, 2);
                deleteEvent(eventId, eventName);
            } else {
                JOptionPane.showMessageDialog(this, "Please select an event first", "No Selection", JOptionPane.WARNING_MESSAGE);
            }
        });

        contextMenu.add(viewItem);
        contextMenu.addSeparator();
        contextMenu.add(deleteItem);

        eventsTable.setComponentPopupMenu(contextMenu);
    }

    /**
     * Displays detailed information about a selected event.
     * @param eventId ID of the event to view
     * @param eventName Name of the event to view
     */
    private void viewEventDetails(int eventId, String eventName) {
        JPanel detailsPanel = new JPanel();
        detailsPanel.setLayout(new BoxLayout(detailsPanel, BoxLayout.Y_AXIS));
        detailsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        try (Connection connection = myJDBC.getConnection()) {
            if (connection != null) {
                String query = "SELECT * FROM marketing_events WHERE eventId = ?";
                PreparedStatement pstmt = connection.prepareStatement(query);
                pstmt.setInt(1, eventId);
                ResultSet rs = pstmt.executeQuery();

                if (rs.next()) {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy HH:mm");

                    addDetailRow(detailsPanel, "Event ID:", String.valueOf(rs.getInt("eventId")));
                    addDetailRow(detailsPanel, "Type:", rs.getString("type"));
                    addDetailRow(detailsPanel, "Name:", rs.getString("name"));
                    addDetailRow(detailsPanel, "Start Date:", dateFormat.format(rs.getTimestamp("startDate")));
                    addDetailRow(detailsPanel, "End Date:", dateFormat.format(rs.getTimestamp("endDate")));
                    addDetailRow(detailsPanel, "Room:", String.valueOf(rs.getInt("room")));
                    addDetailRow(detailsPanel, "People:", String.valueOf(rs.getInt("peopleNum")));
                    addDetailRow(detailsPanel, "Venue:", rs.getString("venue"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        JOptionPane.showMessageDialog(this, detailsPanel, "Event Details: " + eventName, JOptionPane.INFORMATION_MESSAGE);
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
     * Deletes an event after confirmation.
     * @param eventId ID of the event to delete
     * @param eventName Name of the event to delete
     */
    private void deleteEvent(int eventId, String eventName) {
        JPanel confirmPanel = new JPanel(new BorderLayout(10, 10));
        confirmPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel iconLabel = new JLabel(UIManager.getIcon("OptionPane.warningIcon"));
        JLabel messageLabel = new JLabel("<html>Are you sure you want to delete event:<br><b>" + eventName + "</b> (ID: " + eventId + ")?</html>");
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
                    String query = "DELETE FROM marketing_events WHERE eventId = ?";
                    PreparedStatement pstmt = connection.prepareStatement(query);
                    pstmt.setInt(1, eventId);
                    int rowsAffected = pstmt.executeUpdate();

                    if (rowsAffected > 0) {
                        loadMarketingEventsData();
                        statusLabel.setText("Event " + eventName + " deleted successfully");
                        statusLabel.setForeground(new Color(46, 204, 113));
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error deleting event: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Loads marketing events data from the database.
     */
    private void loadMarketingEventsData() {
        statusLabel.setText("Loading data...");
        statusLabel.setForeground(accentColor);

        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() {
                try (Connection connection = myJDBC.getConnection()) {
                    if (connection != null) {
                        tableModel.setRowCount(0);

                        String query = "SELECT * FROM marketing_events";
                        Statement stmt = connection.createStatement();
                        ResultSet rs = stmt.executeQuery(query);

                        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy HH:mm");

                        while (rs.next()) {
                            Object[] row = {
                                    rs.getInt("eventId"),
                                    rs.getString("type"),
                                    rs.getString("name"),
                                    dateFormat.format(rs.getTimestamp("startDate")),
                                    dateFormat.format(rs.getTimestamp("endDate")),
                                    rs.getInt("room"),
                                    rs.getInt("peopleNum"),
                                    rs.getString("venue")
                            };
                            tableModel.addRow(row);
                        }

                        SwingUtilities.invokeLater(() -> {
                            statusLabel.setText(tableModel.getRowCount() + " events loaded");
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

    /**
     * Fetches events from other booking tables and adds them to marketing events.
     */
    private void fetchAndInsertEvents() {
        statusLabel.setText("Fetching events...");
        statusLabel.setForeground(accentColor);

        SwingWorker<Integer, Void> worker = new SwingWorker<Integer, Void>() {
            @Override
            protected Integer doInBackground() throws Exception {
                int insertedCount = 0;

                try (Connection connection = myJDBC.getConnection()) {
                    if (connection != null) {
                        String filmQuery = "SELECT 'Film' AS type, filmTitle AS name, showDate AS startDate, endDate, room, duration AS peopleNum, venue FROM film_bookings";
                        Statement filmStmt = connection.createStatement();
                        ResultSet filmRs = filmStmt.executeQuery(filmQuery);
                        insertedCount += insertEvents(filmRs, connection);

                        String groupQuery = "SELECT 'Group' AS type, event AS name, startDate, endDate, room, people AS peopleNum, venue FROM group_bookings";
                        Statement groupStmt = connection.createStatement();
                        ResultSet groupRs = groupStmt.executeQuery(groupQuery);
                        insertedCount += insertEvents(groupRs, connection);

                        String meetingQuery = "SELECT 'Meeting' AS type, meetingName AS name, startDate, endDate, room, peopleNum, venue FROM meeting_bookings";
                        Statement meetingStmt = connection.createStatement();
                        ResultSet meetingRs = meetingStmt.executeQuery(meetingQuery);
                        insertedCount += insertEvents(meetingRs, connection);

                        String tourQuery = "SELECT 'Tour' AS type, organizationName AS name, startDate, endDate, room, people AS peopleNum, venue FROM tour_bookings";
                        Statement tourStmt = connection.createStatement();
                        ResultSet tourRs = tourStmt.executeQuery(tourQuery);
                        insertedCount += insertEvents(tourRs, connection);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    throw e;
                }
                return insertedCount;
            }

            @Override
            protected void done() {
                try {
                    int insertedCount = get();
                    if (insertedCount > 0) {
                        statusLabel.setText(insertedCount + " new events added");
                        statusLabel.setForeground(new Color(46, 204, 113));
                    } else {
                        statusLabel.setText("No new events to add");
                        statusLabel.setForeground(accentColor);
                    }
                    loadMarketingEventsData();
                } catch (Exception e) {
                    statusLabel.setText("Error fetching events: " + e.getMessage());
                    statusLabel.setForeground(new Color(231, 76, 60));
                }
            }
        };

        worker.execute();
    }

    /**
     * Inserts events from a result set into the marketing_events table.
     * @param rs Result set containing event data
     * @param connection Database connection
     * @return Number of events inserted
     */
    private int insertEvents(ResultSet rs, Connection connection) throws Exception {
        String insertQuery = "INSERT INTO marketing_events (type, name, startDate, endDate, room, peopleNum, venue) VALUES (?, ?, ?, ?, ?, ?, ?)";
        String checkQuery = "SELECT COUNT(*) FROM marketing_events WHERE type = ? AND name = ? AND startDate = ? AND endDate = ? AND room = ? AND peopleNum = ? AND venue = ?";

        int insertedCount = 0;

        try (PreparedStatement pstmt = connection.prepareStatement(insertQuery);
             PreparedStatement checkStmt = connection.prepareStatement(checkQuery)) {
            while (rs.next()) {
                String type = rs.getString("type");
                String name = rs.getString("name");
                Timestamp startDate = rs.getTimestamp("startDate");
                Timestamp endDate = rs.getTimestamp("endDate");
                int room = rs.getInt("room");
                int peopleNum = rs.getInt("peopleNum");
                String venue = rs.getString("venue");

                if (type == null || name == null || startDate == null || endDate == null || venue == null) {
                    continue;
                }

                checkStmt.setString(1, type);
                checkStmt.setString(2, name);
                checkStmt.setTimestamp(3, startDate);
                checkStmt.setTimestamp(4, endDate);
                checkStmt.setInt(5, room);
                checkStmt.setInt(6, peopleNum);
                checkStmt.setString(7, venue);

                ResultSet checkRs = checkStmt.executeQuery();
                if (checkRs.next() && checkRs.getInt(1) == 0) {
                    pstmt.setString(1, type);
                    pstmt.setString(2, name);
                    pstmt.setTimestamp(3, startDate);
                    pstmt.setTimestamp(4, endDate);
                    pstmt.setInt(5, room);
                    pstmt.setInt(6, peopleNum);
                    pstmt.setString(7, venue);
                    pstmt.executeUpdate();
                    insertedCount++;
                }
            }
        }

        return insertedCount;
    }
}