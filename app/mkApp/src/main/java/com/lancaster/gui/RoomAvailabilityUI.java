package com.lancaster.gui;

import com.lancaster.database.myJDBC;
import BoxOfficeInterface.JDBC;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;

/**
 * UI panel for displaying and managing room availability.
 * Provides functionality for viewing room availability by date and time, with filtering options.
 */
public class RoomAvailabilityUI extends JPanel {
    private static final Color PRIMARY_COLOR = new Color(41, 128, 185);
    private static final Color SECONDARY_COLOR = new Color(52, 152, 219);
    private static final Color BACKGROUND_COLOR = new Color(248, 249, 250);
    private static final Color TEXT_COLOR = new Color(52, 58, 64);
    private static final Color ACCENT_COLOR = new Color(46, 204, 113);
    private static final Color HEADER_COLOR = new Color(33, 37, 41);
    private static final Color HOVER_COLOR = new Color(236, 240, 241);
    private static final Color BORDER_COLOR = new Color(222, 226, 230);
    private static final Color AVAILABLE_COLOR = new Color(46, 204, 113);
    private static final Color BOOKED_COLOR = new Color(231, 76, 60);

    private static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 22);
    private static final Font SUBTITLE_FONT = new Font("Segoe UI", Font.BOLD, 16);
    private static final Font REGULAR_FONT = new Font("Segoe UI", Font.PLAIN, 14);
    private static final Font SMALL_FONT = new Font("Segoe UI", Font.PLAIN, 12);
    private static final Font BUTTON_FONT = new Font("Segoe UI", Font.BOLD, 13);

    private JTable availabilityTable;
    private DefaultTableModel tableModel;
    private JLabel statusLabel;
    private JComboBox<String> roomFilterComboBox;
    private JComboBox<String> timeSlotFilterComboBox;
    private LocalDate selectedDate;

    private JComboBox<Integer> dayComboBox;
    private JComboBox<Month> monthComboBox;
    private JComboBox<Integer> yearComboBox;
    private JLabel dateDisplayLabel;

    /**
     * Constructs the RoomAvailabilityUI panel with all controls and table.
     */
    public RoomAvailabilityUI() {
        setLayout(new BorderLayout(0, 15));
        setBackground(BACKGROUND_COLOR);
        setBorder(new EmptyBorder(20, 20, 20, 20));

        selectedDate = LocalDate.now();

        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);

        JPanel tablePanel = createTablePanel();
        add(tablePanel, BorderLayout.CENTER);

        JPanel buttonPanel = createButtonPanel();
        add(buttonPanel, BorderLayout.SOUTH);

        loadAvailabilityData();
    }

    /**
     * Creates the header panel containing title, date picker, and filters.
     * @return Panel with header elements
     */
    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 15));
        panel.setBackground(BACKGROUND_COLOR);
        panel.setBorder(new EmptyBorder(0, 0, 15, 0));

        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        titlePanel.setOpaque(false);

        JLabel iconLabel = new JLabel("🔍");
        iconLabel.setFont(new Font("Segoe UI", Font.PLAIN, 24));

        JLabel titleLabel = new JLabel("Room Availability");
        titleLabel.setFont(TITLE_FONT);
        titleLabel.setForeground(HEADER_COLOR);

        titlePanel.add(iconLabel);
        titlePanel.add(titleLabel);
        panel.add(titlePanel, BorderLayout.NORTH);

        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 0));
        controlPanel.setOpaque(false);

        JPanel datePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        datePanel.setOpaque(false);

        JLabel dateLabel = new JLabel("Select Date:");
        dateLabel.setFont(SUBTITLE_FONT);
        dateLabel.setForeground(TEXT_COLOR);

        JPanel datePicker = createCustomDatePicker();

        datePanel.add(dateLabel);
        datePanel.add(datePicker);

        dateDisplayLabel = new JLabel();
        dateDisplayLabel.setFont(REGULAR_FONT);
        dateDisplayLabel.setForeground(PRIMARY_COLOR);
        updateDateDisplayLabel();
        datePanel.add(dateDisplayLabel);

        JPanel roomFilterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        roomFilterPanel.setOpaque(false);

        JLabel roomLabel = new JLabel("Room:");
        roomLabel.setFont(REGULAR_FONT);

        roomFilterComboBox = new JComboBox<>(new String[]{"All Rooms", "Room 1", "Room 2", "Room 3", "Room 4", "Room 5"});
        roomFilterComboBox.setFont(REGULAR_FONT);
        roomFilterComboBox.setPreferredSize(new Dimension(120, 30));
        roomFilterComboBox.addActionListener(e -> filterTableData());

        roomFilterPanel.add(roomLabel);
        roomFilterPanel.add(roomFilterComboBox);

        JPanel timeSlotPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        timeSlotPanel.setOpaque(false);

        JLabel timeSlotLabel = new JLabel("Time Slot:");
        timeSlotLabel.setFont(REGULAR_FONT);

        String[] timeSlots = {"All Time Slots", "Morning (8AM-12PM)", "Afternoon (12PM-5PM)", "Evening (5PM-10PM)"};
        timeSlotFilterComboBox = new JComboBox<>(timeSlots);
        timeSlotFilterComboBox.setFont(REGULAR_FONT);
        timeSlotFilterComboBox.setPreferredSize(new Dimension(160, 30));
        timeSlotFilterComboBox.addActionListener(e -> filterTableData());

        timeSlotPanel.add(timeSlotLabel);
        timeSlotPanel.add(timeSlotFilterComboBox);

        statusLabel = new JLabel("Loading availability data...");
        statusLabel.setFont(SMALL_FONT);
        statusLabel.setForeground(SECONDARY_COLOR);

        controlPanel.add(datePanel);
        controlPanel.add(roomFilterPanel);
        controlPanel.add(timeSlotPanel);

        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statusPanel.setOpaque(false);
        statusPanel.add(statusLabel);

        panel.add(controlPanel, BorderLayout.CENTER);
        panel.add(statusPanel, BorderLayout.SOUTH);

        return panel;
    }

    /**
     * Creates a custom date picker with day, month, and year combo boxes.
     * @return Panel containing date picker components
     */
    private JPanel createCustomDatePicker() {
        JPanel datePicker = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        datePicker.setOpaque(false);

        Integer[] days = new Integer[31];
        for (int i = 0; i < 31; i++) {
            days[i] = i + 1;
        }
        dayComboBox = new JComboBox<>(days);
        dayComboBox.setSelectedItem(selectedDate.getDayOfMonth());
        dayComboBox.setFont(REGULAR_FONT);
        dayComboBox.setPreferredSize(new Dimension(60, 30));

        monthComboBox = new JComboBox<>(Month.values());
        monthComboBox.setSelectedItem(selectedDate.getMonth());
        monthComboBox.setFont(REGULAR_FONT);
        monthComboBox.setPreferredSize(new Dimension(110, 30));

        Integer[] years = new Integer[10];
        int currentYear = selectedDate.getYear();
        for (int i = 0; i < 10; i++) {
            years[i] = currentYear - 3 + i;
        }
        yearComboBox = new JComboBox<>(years);
        yearComboBox.setSelectedItem(currentYear);
        yearComboBox.setFont(REGULAR_FONT);
        yearComboBox.setPreferredSize(new Dimension(80, 30));

        ActionListener dateChangeListener = e -> updateSelectedDateFromPicker();

        dayComboBox.addActionListener(dateChangeListener);
        monthComboBox.addActionListener(dateChangeListener);
        yearComboBox.addActionListener(dateChangeListener);

        datePicker.add(dayComboBox);
        datePicker.add(monthComboBox);
        datePicker.add(yearComboBox);

        return datePicker;
    }

    /**
     * Updates the selected date based on date picker values.
     */
    private void updateSelectedDateFromPicker() {
        try {
            int day = (Integer) dayComboBox.getSelectedItem();
            Month month = (Month) monthComboBox.getSelectedItem();
            int year = (Integer) yearComboBox.getSelectedItem();

            int maxDay = month.length(LocalDate.of(year, month, 1).isLeapYear());
            if (day > maxDay) {
                day = maxDay;
                dayComboBox.setSelectedItem(day);
            }

            selectedDate = LocalDate.of(year, month, day);
            updateDateDisplayLabel();
            loadAvailabilityData();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Updates the date display label with formatted selected date.
     */
    private void updateDateDisplayLabel() {
        String formattedDate = selectedDate.format(
                DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy")
        );
        dateDisplayLabel.setText("(" + formattedDate + ")");
    }

    /**
     * Creates the panel containing the availability table.
     * @return Panel with styled table for availability data
     */
    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(new LineBorder(BORDER_COLOR, 1));

        String[] columns = {"Room", "Time Slot", "Availability", "Event", "Venue", "Duration"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 2) {
                    return Boolean.class;
                }
                return String.class;
            }
        };

        availabilityTable = new JTable(tableModel);
        styleTable(availabilityTable);

        availabilityTable.getColumnModel().getColumn(2).setCellRenderer(new AvailabilityCellRenderer());

        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(tableModel);
        availabilityTable.setRowSorter(sorter);

        JScrollPane scrollPane = new JScrollPane(availabilityTable);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(Color.WHITE);

        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    /**
     * Creates the button panel with action buttons.
     * @return Panel with Today and Refresh buttons
     */
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        panel.setOpaque(false);

        JButton todayButton = createStyledButton("Today");
        todayButton.addActionListener(e -> {
            selectedDate = LocalDate.now();
            dayComboBox.setSelectedItem(selectedDate.getDayOfMonth());
            monthComboBox.setSelectedItem(selectedDate.getMonth());
            yearComboBox.setSelectedItem(selectedDate.getYear());
            updateDateDisplayLabel();
            loadAvailabilityData();
        });

        JButton refreshButton = createStyledButton("Refresh");
        refreshButton.addActionListener(e -> loadAvailabilityData());

        panel.add(todayButton);
        panel.add(refreshButton);

        return panel;
    }

    /**
     * Creates a styled button with consistent appearance.
     * @param text Text for the button
     * @return Styled JButton
     */
    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(BUTTON_FONT);
        button.setBackground(PRIMARY_COLOR);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(100, 36));

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(SECONDARY_COLOR);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(PRIMARY_COLOR);
            }
        });

        return button;
    }

    /**
     * Applies styling to the availability table.
     * @param table Table to style
     */
    private void styleTable(JTable table) {
        table.setFont(REGULAR_FONT);
        table.setRowHeight(40);
        table.setIntercellSpacing(new Dimension(10, 5));
        table.setFillsViewportHeight(true);
        table.setSelectionBackground(new Color(232, 242, 254));
        table.setSelectionForeground(TEXT_COLOR);
        table.setShowGrid(false);
        table.setGridColor(BORDER_COLOR);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JTableHeader header = table.getTableHeader();
        header.setFont(SUBTITLE_FONT);
        header.setBackground(PRIMARY_COLOR);
        header.setForeground(Color.WHITE);
        header.setPreferredSize(new Dimension(header.getWidth(), 40));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_COLOR));

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);

        for (int i = 0; i < table.getColumnCount(); i++) {
            if (i != 2) {
                table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
            }
        }

        table.getColumnModel().getColumn(0).setPreferredWidth(100);
        table.getColumnModel().getColumn(1).setPreferredWidth(150);
        table.getColumnModel().getColumn(2).setPreferredWidth(100);
        table.getColumnModel().getColumn(3).setPreferredWidth(200);
        table.getColumnModel().getColumn(4).setPreferredWidth(150);
        table.getColumnModel().getColumn(5).setPreferredWidth(100);
    }

    /**
     * Loads availability data for the selected date.
     */
    private void loadAvailabilityData() {
        statusLabel.setText("Loading availability data...");
        tableModel.setRowCount(0);

        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            private List<Object[]> availabilityDataList = new ArrayList<>();

            @Override
            protected Void doInBackground() {
                try {
                    JDBC jdbc = new JDBC();

                    Date sqlDate = java.sql.Date.valueOf(selectedDate);

                    List<String> roomAvailabilities = jdbc.getCalendarAvailability(sqlDate);

                    for (String availabilityStr : roomAvailabilities) {
                        if (availabilityStr.startsWith("SlotTime:")) {
                            String timeStr = availabilityStr.substring("SlotTime:".length()).trim();

                            Object[] row = new Object[6];
                            row[0] = "All Rooms";
                            row[1] = formatTimeSlot(timeStr);
                            row[2] = true;
                            row[3] = "Available";
                            row[4] = "Main Venue";
                            row[5] = "60 min";

                            availabilityDataList.add(row);
                        }
                        else if (availabilityStr.contains("|")) {
                            String[] parts = availabilityStr.split("\\|");

                            if (parts.length >= 6) {
                                String roomId = "Room " + parts[0];
                                String roomName = parts[1];
                                String venue = parts[2];
                                String startTimeStr = parts[3];
                                String endTimeStr = parts[4];
                                String eventType = parts[5];

                                String duration = calculateDuration(startTimeStr, endTimeStr) + " min";

                                Object[] row = new Object[6];
                                row[0] = roomId + " (" + roomName + ")";
                                row[1] = formatTimeSlot(startTimeStr, endTimeStr);
                                row[2] = false;
                                row[3] = eventType;
                                row[4] = venue;
                                row[5] = duration;

                                availabilityDataList.add(row);
                            }
                        }
                    }

                    loadMarketingEventsData();

                    return null;
                } catch (Exception e) {
                    e.printStackTrace();
                    SwingUtilities.invokeLater(() ->
                            statusLabel.setText("Error loading data: " + e.getMessage())
                    );
                    return null;
                }
            }

            /**
             * Loads additional marketing events data from the database.
             */
            private void loadMarketingEventsData() {
                try (Connection connection = myJDBC.getConnection()) {
                    if (connection != null) {
                        String query = "SELECT * FROM marketing_events WHERE DATE(startDate) = ?";
                        PreparedStatement pstmt = connection.prepareStatement(query);
                        pstmt.setDate(1, java.sql.Date.valueOf(selectedDate));

                        ResultSet rs = pstmt.executeQuery();

                        while (rs.next()) {
                            String roomId = "Room " + rs.getInt("room");
                            String venue = rs.getString("venue");
                            String type = rs.getString("type");
                            int duration = rs.getInt("duration");
                            Timestamp startDate = rs.getTimestamp("startDate");
                            Timestamp endDate = rs.getTimestamp("endDate");

                            String timeSlot = formatTimeSlot(
                                    startDate.toLocalDateTime().toLocalTime().toString(),
                                    endDate.toLocalDateTime().toLocalTime().toString()
                            );

                            Object[] row = new Object[6];
                            row[0] = roomId;
                            row[1] = timeSlot;
                            row[2] = false;
                            row[3] = type;
                            row[4] = venue;
                            row[5] = duration + " min";

                            availabilityDataList.add(row);
                        }
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

            @Override
            protected void done() {
                for (Object[] row : availabilityDataList) {
                    tableModel.addRow(row);
                }

                filterTableData();

                statusLabel.setText(availabilityDataList.size() + " time slots loaded for " +
                        selectedDate.format(DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy")));
            }
        };

        worker.execute();
    }

    /**
     * Formats a single time value into a readable time slot.
     * @param startTime Start time string in HH:mm:ss format
     * @return Formatted time slot string
     */
    private String formatTimeSlot(String startTime) {
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("HH:mm:ss");
            SimpleDateFormat outputFormat = new SimpleDateFormat("h:mm a");
            Date time = (Date) inputFormat.parse(startTime);
            return outputFormat.format(time) + " - " + outputFormat.format(new Date(time.getTime() + 3600000));
        } catch (Exception e) {
            return startTime;
        }
    }

    /**
     * Formats a time range into a readable time slot.
     * @param startTime Start time string in HH:mm format
     * @param endTime End time string in HH:mm format
     * @return Formatted time slot string
     */
    private String formatTimeSlot(String startTime, String endTime) {
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("HH:mm");
            SimpleDateFormat outputFormat = new SimpleDateFormat("h:mm a");
            Date start = (Date) inputFormat.parse(startTime);
            Date end = (Date) inputFormat.parse(endTime);
            return outputFormat.format(start) + " - " + outputFormat.format(end);
        } catch (Exception e) {
            return startTime + " - " + endTime;
        }
    }

    /**
     * Calculates the duration between two time values in minutes.
     * @param startTime Start time string
     * @param endTime End time string
     * @return Duration in minutes
     */
    private int calculateDuration(String startTime, String endTime) {
        try {
            SimpleDateFormat format = new SimpleDateFormat("HH:mm");
            Date start = (Date) format.parse(startTime);
            Date end = (Date) format.parse(endTime);

            long diffMs = end.getTime() - start.getTime();
            return (int) (diffMs / (1000 * 60));
        } catch (Exception e) {
            return 60;
        }
    }

    /**
     * Filters table data based on selected room and time slot filters.
     */
    private void filterTableData() {
        String roomFilter = (String) roomFilterComboBox.getSelectedItem();
        String timeSlotFilter = (String) timeSlotFilterComboBox.getSelectedItem();

        RowFilter<DefaultTableModel, Object> filter = new RowFilter<>() {
            @Override
            public boolean include(Entry<? extends DefaultTableModel, ? extends Object> entry) {
                boolean roomMatch = "All Rooms".equals(roomFilter) ||
                        entry.getStringValue(0).startsWith(roomFilter);

                boolean timeMatch = true;
                if (!"All Time Slots".equals(timeSlotFilter)) {
                    String timeValue = entry.getStringValue(1);
                    if ("Morning (8AM-12PM)".equals(timeSlotFilter)) {
                        timeMatch = timeValue.contains("AM");
                    } else if ("Afternoon (12PM-5PM)".equals(timeSlotFilter)) {
                        timeMatch = timeValue.contains("12") ||
                                timeValue.contains("1:") ||
                                timeValue.contains("2:") ||
                                timeValue.contains("3:") ||
                                timeValue.contains("4:") ||
                                timeValue.contains("5:");
                    } else if ("Evening (5PM-10PM)".equals(timeSlotFilter)) {
                        timeMatch = timeValue.contains("5:") ||
                                timeValue.contains("6:") ||
                                timeValue.contains("7:") ||
                                timeValue.contains("8:") ||
                                timeValue.contains("9:") ||
                                timeValue.contains("10:");
                    }
                }

                return roomMatch && timeMatch;
            }
        };

        TableRowSorter<DefaultTableModel> sorter = (TableRowSorter<DefaultTableModel>) availabilityTable.getRowSorter();
        sorter.setRowFilter(filter);

        statusLabel.setText(availabilityTable.getRowCount() + " time slots displayed for " +
                selectedDate.format(DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy")));
    }

    /**
     * Custom renderer for the availability column to show status indicators.
     */
    private class AvailabilityCellRenderer extends DefaultTableCellRenderer {
        private final JLabel availableLabel = new JLabel("Available");
        private final JLabel bookedLabel = new JLabel("Booked");

        /**
         * Constructs the cell renderer with configured labels.
         */
        public AvailabilityCellRenderer() {
            availableLabel.setOpaque(true);
            availableLabel.setBackground(new Color(230, 255, 230));
            availableLabel.setForeground(AVAILABLE_COLOR);
            availableLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
            availableLabel.setHorizontalAlignment(JLabel.CENTER);
            availableLabel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));

            bookedLabel.setOpaque(true);
            bookedLabel.setBackground(new Color(255, 235, 230));
            bookedLabel.setForeground(BOOKED_COLOR);
            bookedLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
            bookedLabel.setHorizontalAlignment(JLabel.CENTER);
            bookedLabel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int column) {

            if (value instanceof Boolean) {
                boolean available = (Boolean) value;
                if (available) {
                    if (isSelected) {
                        availableLabel.setBackground(new Color(220, 245, 220));
                    } else {
                        availableLabel.setBackground(new Color(230, 255, 230));
                    }
                    return availableLabel;
                } else {
                    if (isSelected) {
                        bookedLabel.setBackground(new Color(245, 225, 220));
                    } else {
                        bookedLabel.setBackground(new Color(255, 235, 230));
                    }
                    return bookedLabel;
                }
            }

            return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        }
    }
}