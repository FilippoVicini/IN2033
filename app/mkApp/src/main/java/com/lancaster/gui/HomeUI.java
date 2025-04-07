package com.lancaster.gui;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import com.lancaster.database.myJDBC;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import com.lancaster.gui.ClientsUI;

public class HomeUI extends JFrame {

    private JPanel mainPanel;
    private JLabel statusLabel;
    private JPanel contentPanel;
    private JPanel dashboardPanel;
    private FriendsUI friendsUI;
    private FilmsUI filmsUI;
    private FilmShowsUI filmShowsUI;
    private RoomAvailabilityUI roomAvailabilityUI;
    private CalendarUI calendarUI;
    private SettingsUI settingsUI;
    private heldSeatsUI heldSeatsUI; // Added HeldSeatsUI reference
    private JPanel sidebarPanel;
    private String username;
    private Map<String, JButton> navButtons = new HashMap<>();
    private Color primaryColor = new Color(47, 54, 64);
    private Color accentColor = new Color(86, 101, 115);
    private Color hoverColor = new Color(39, 60, 117);
    private Color selectedColor = new Color(25, 42, 86);
    private Color cardHeaderColor = new Color(52, 152, 219);

    // Navigation items - flat list without categories (added "Held Seats")
    private final String[] NAV_ITEMS = {
            "Dashboard",
            "Room Availability",
            "Tour Bookings",
            "Meeting Bookings",
            "Film Shows",
            "Films",
            "Marketing Events",
            "Held Seats",
            "Calendar",
            "Invoices",
            "Clients",
            "Settings"
    };

    /**
     * Constructor for the HomeUI
     */
    public HomeUI(String username) throws SQLException, ClassNotFoundException {
        this.username = username;
        setTitle("Lancaster Marketing");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);

        createHeaderPanel();
        createSidebarPanel();

        contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        contentPanel.setBackground(new Color(245, 246, 250));

        // Initialize UI components
        friendsUI = new FriendsUI();
        filmsUI = new FilmsUI();
        filmShowsUI = new FilmShowsUI();
        settingsUI = new SettingsUI();
        calendarUI = new CalendarUI();
        heldSeatsUI = new heldSeatsUI();
        roomAvailabilityUI = new RoomAvailabilityUI();
        dashboardPanel = createDashboardPanel();

        // Add dashboard panel to content by default
        contentPanel.add(dashboardPanel, BorderLayout.CENTER);

        // Add panels to main panel
        mainPanel.add(contentPanel, BorderLayout.CENTER);

        // Add main panel to frame
        add(mainPanel);

        connectToDatabase();
    }

    private void createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(primaryColor);
        headerPanel.setPreferredSize(new Dimension(getWidth(), 60));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 15));

        // Logo and title
        JLabel logoLabel = new JLabel("Lancaster Marketing");
        logoLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        logoLabel.setForeground(Color.WHITE);
        headerPanel.add(logoLabel, BorderLayout.WEST);

        // User info panel
        JPanel userInfoPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        userInfoPanel.setOpaque(false);

        // User icon and username
        JLabel userIcon = new JLabel("👤");
        userIcon.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        userIcon.setForeground(Color.WHITE);

        JLabel userLabel = new JLabel(username);
        userLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        userLabel.setForeground(Color.WHITE);

        statusLabel = new JLabel("●");
        statusLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        statusLabel.setForeground(new Color(255, 150, 150));

        userInfoPanel.add(userIcon);
        userInfoPanel.add(userLabel);
        userInfoPanel.add(Box.createHorizontalStrut(5));
        userInfoPanel.add(statusLabel);
        userInfoPanel.add(Box.createHorizontalStrut(10));

        headerPanel.add(userInfoPanel, BorderLayout.EAST);
        mainPanel.add(headerPanel, BorderLayout.NORTH);
    }

    private void createSidebarPanel() {
        sidebarPanel = new JPanel();
        sidebarPanel.setLayout(new BoxLayout(sidebarPanel, BoxLayout.Y_AXIS));
        sidebarPanel.setBackground(primaryColor);
        sidebarPanel.setPreferredSize(new Dimension(220, getHeight()));
        sidebarPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, accentColor));

        // Add logo panel
        JPanel brandPanel = new JPanel(new BorderLayout());
        brandPanel.setBackground(new Color(31, 36, 42));
        brandPanel.setMaximumSize(new Dimension(220, 80));
        brandPanel.setPreferredSize(new Dimension(220, 80));

        // Add logo
        try {
            Image logo = ImageIO.read(new File("src/resources/logo.png"));
            // Scale the logo to a smaller size
            Image scaledLogo = logo.getScaledInstance(200, 150, Image.SCALE_SMOOTH);
            JLabel logoLabel = new JLabel(new ImageIcon(scaledLogo));
            brandPanel.add(logoLabel);
        } catch (Exception e) {
            e.printStackTrace();
        }

        sidebarPanel.add(brandPanel);
        sidebarPanel.add(Box.createVerticalStrut(30));

        // Create navigation buttons
        for (String navItem : NAV_ITEMS) {
            JButton navButton = createMenuButton(navItem);
            sidebarPanel.add(navButton);
            navButtons.put(navItem, navButton);

            // Small space between buttons
            sidebarPanel.add(Box.createVerticalStrut(2));
        }

        // Set Dashboard as selected by default
        JButton dashboardButton = navButtons.get("Dashboard");
        dashboardButton.setBackground(selectedColor);
        dashboardButton.setForeground(Color.WHITE);

        // Add bottom padding
        sidebarPanel.add(Box.createVerticalGlue());

        mainPanel.add(sidebarPanel, BorderLayout.WEST);
    }

    private JButton createMenuButton(String text) {
        JButton button = new JButton(text);
        button.setMaximumSize(new Dimension(220, 40));
        button.setPreferredSize(new Dimension(220, 40));
        button.setBackground(primaryColor);
        button.setForeground(Color.LIGHT_GRAY);
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        button.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Add hover effect
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (!button.getBackground().equals(selectedColor)) {
                    button.setBackground(hoverColor);
                    button.setForeground(Color.WHITE);
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (!button.getBackground().equals(selectedColor)) {
                    button.setBackground(primaryColor);
                    button.setForeground(Color.LIGHT_GRAY);
                }
            }
        });

        // Add action listener
        button.addActionListener(e -> handleNavigation(text, button));

        return button;
    }

    private void handleNavigation(String destination, JButton clickedButton) {
        // Reset all buttons
        navButtons.values().forEach(button -> {
            button.setBackground(primaryColor);
            button.setForeground(Color.LIGHT_GRAY);
        });

        // Highlight selected button
        clickedButton.setBackground(selectedColor);
        clickedButton.setForeground(Color.WHITE);

        // Navigate to appropriate panel
        contentPanel.removeAll();

        switch (destination) {
            case "Dashboard":
                contentPanel.add(dashboardPanel, BorderLayout.CENTER);
                break;
            case "Room Availability":
                contentPanel.add(roomAvailabilityUI, BorderLayout.CENTER);
                break;
            case "Films":
                contentPanel.add(filmsUI, BorderLayout.CENTER);
                break;
            case "Film Shows":
                contentPanel.add(filmShowsUI, BorderLayout.CENTER);
                break;
            case "Calendar":
                contentPanel.add(calendarUI, BorderLayout.CENTER);
                break;
            case "Settings":
                contentPanel.add(settingsUI, BorderLayout.CENTER);
                break;
            case "Tour Bookings":
                contentPanel.add(new TourBookingsUI(), BorderLayout.CENTER);
                break;
            case "Meeting Bookings":
                contentPanel.add(new MeetingBookingsUI(), BorderLayout.CENTER);
                break;
            case "Marketing Events":
                contentPanel.add(new MarketingEventsUI(), BorderLayout.CENTER);
                break;
            case "Invoices":
                contentPanel.add(new InvoiceUI(), BorderLayout.CENTER);
                break;
            case "Held Seats":
                contentPanel.add(heldSeatsUI, BorderLayout.CENTER);
                break;
            case "Clients":
                contentPanel.add(new ClientsUI(), BorderLayout.CENTER);
                break;
        }

        // Refresh the content panel
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void connectToDatabase() {
        try (Connection connection = myJDBC.getConnection()) {
            if (connection != null) {
                statusLabel.setText("●");
                statusLabel.setForeground(new Color(100, 255, 100));
            }
        } catch (Exception e) {
            statusLabel.setText("●");
            statusLabel.setForeground(new Color(255, 70, 70));
        }
    }

    /**
     * Create the dashboard panel with summary information
     */
    private JPanel createDashboardPanel() {
        JPanel dashboardContent = new JPanel(new BorderLayout());
        dashboardContent.setBackground(new Color(245, 246, 250));

        // Dashboard header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        headerPanel.setOpaque(false);

        JLabel welcomeLabel = new JLabel("Welcome, " + username);
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        welcomeLabel.setForeground(new Color(52, 73, 94));

        JLabel dateLabel = new JLabel("Today: " + java.time.LocalDate.now().toString());
        dateLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        dateLabel.setForeground(new Color(86, 101, 115));

        headerPanel.add(welcomeLabel, BorderLayout.NORTH);
        headerPanel.add(dateLabel, BorderLayout.CENTER);

        // Summary cards panel
        JPanel cardsPanel = new JPanel(new GridLayout(2, 3, 20, 20));
        cardsPanel.setOpaque(false);

        // Create summary cards with more modern styling
        JPanel friendsCard = createModernCard("Friends of Lancaster",
                Integer.toString(FriendsUI.friendsNum),
                new Color(52, 152, 219),
                "👥");

        JPanel todaysEventsCard = createModernCard("Today's Events",
                Integer.toString(new myJDBC().getTodaysEventCount()),
                new Color(46, 204, 113),
                "📅");

        JPanel tourBookingsCard = createModernCard("Tour Bookings",
                getTourBookingsCount(),
                new Color(155, 89, 182),
                "🚌");

        JPanel filmShowsCard = createModernCard("Film Shows",
                getFilmShowsCount(),
                new Color(231, 76, 60),
                "🎬");

        JPanel marketingEventsCard = createModernCard("Marketing Events",
                getMarketingEventsCount(),
                new Color(243, 156, 18),
                "📣");

        // Add a new card for Held Seats
        JPanel heldSeatsCard = createModernCard("Held Seats",
                getHeldSeatsCount(),
                new Color(22, 160, 133),
                "🪑");

        // Add cards to panel
        cardsPanel.add(friendsCard);
        cardsPanel.add(todaysEventsCard);
        cardsPanel.add(marketingEventsCard);
        cardsPanel.add(tourBookingsCard);
        cardsPanel.add(filmShowsCard);
        cardsPanel.add(heldSeatsCard); // Add the new card to the dashboard

        // Create activity overview panel
        JPanel activityPanel = new JPanel(new BorderLayout());
        activityPanel.setBackground(Color.WHITE);
        activityPanel.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(230, 230, 230), 1),
                new EmptyBorder(15, 15, 15, 15)
        ));

        JPanel activityHeaderPanel = new JPanel(new BorderLayout());
        activityHeaderPanel.setOpaque(false);

        JLabel activityTitleLabel = new JLabel("Activity Overview");
        activityTitleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        activityTitleLabel.setForeground(new Color(52, 73, 94));

        activityHeaderPanel.add(activityTitleLabel, BorderLayout.WEST);

        // Create table to display marketing events
        JTable eventsTable = createMarketingEventsTable();
        JScrollPane scrollPane = new JScrollPane(eventsTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        activityPanel.add(activityHeaderPanel, BorderLayout.NORTH);
        activityPanel.add(scrollPane, BorderLayout.CENTER);

        // Arrange panels in main dashboard
        JPanel centerPanel = new JPanel(new BorderLayout(0, 20));
        centerPanel.setOpaque(false);
        centerPanel.add(cardsPanel, BorderLayout.NORTH);
        centerPanel.add(activityPanel, BorderLayout.CENTER);

        dashboardContent.add(headerPanel, BorderLayout.NORTH);
        dashboardContent.add(centerPanel, BorderLayout.CENTER);

        return dashboardContent;
    }

    // New method to get the count of held seats
    private String getHeldSeatsCount() {
        try (Connection connection = myJDBC.getConnection()) {
            if (connection != null) {
                String query = "SELECT COUNT(*) FROM heldSeats";
                try (Statement stmt = connection.createStatement();
                     ResultSet rs = stmt.executeQuery(query)) {
                    if (rs.next()) {
                        return Integer.toString(rs.getInt(1));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "0";
    }

    private JTable createMarketingEventsTable() {
        // Column names
        String[] columnNames = {"Event Name", "Date", "Location", "Attendees", "Status"};

        // Sample data - in a real app, this would come from the database-4
        Object[][] data = getMarketingEventsData();

        // Create table
        JTable table = new JTable(data, columnNames);
        styleTable(table);

        return table;
    }

    private Object[][] getMarketingEventsData() {
        // List to store rows temporarily since we don't know final count initially
        ArrayList<Object> dataList = new ArrayList<>();

        // Query database for marketing events
        try (Connection connection = myJDBC.getConnection()) {
            if (connection != null) {
                String query = "SELECT eventID, type, startDate, endDate, room, duration, name, peopleNum, venue FROM marketing_events ORDER BY startDate DESC LIMIT 5";
                try (Statement stmt = connection.createStatement();
                     ResultSet rs = stmt.executeQuery(query)) {

                    // Iterate through result set and add each row to our list
                    while (rs.next()) {
                        Object[] row = new Object[5]; // Create array for current row with 5 columns
                        row[0] = rs.getString("type");
                        row[1] = rs.getDate("startDate");
                        row[2] = rs.getString("name");
                        row[3] = rs.getDate("endDate");
                        row[4] = rs.getString("venue");
                        dataList.add(row);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Convert list to 2D array
        Object[][] data = new Object[dataList.size()][5];
        for (int i = 0; i < dataList.size(); i++) {
            data[i] = (Object[]) dataList.get(i);
        }

        return data;
    }

    private JPanel createModernCard(String title, String value, Color color, String icon) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(230, 230, 230), 1),
                new EmptyBorder(15, 15, 15, 15)
        ));

        // Top panel with icon and title
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);

        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("Segoe UI", Font.PLAIN, 22));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titleLabel.setForeground(new Color(52, 73, 94));

        topPanel.add(iconLabel, BorderLayout.WEST);
        topPanel.add(titleLabel, BorderLayout.CENTER);

        // Value panel
        JPanel valuePanel = new JPanel(new BorderLayout());
        valuePanel.setOpaque(false);
        valuePanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        valueLabel.setForeground(color);

        valuePanel.add(valueLabel, BorderLayout.WEST);

        // Add a colored indicator bar at the bottom
        JPanel indicatorPanel = new JPanel();
        indicatorPanel.setBackground(color);
        indicatorPanel.setPreferredSize(new Dimension(card.getWidth(), 5));

        // Assemble the card
        card.add(topPanel, BorderLayout.NORTH);
        card.add(valuePanel, BorderLayout.CENTER);
        card.add(indicatorPanel, BorderLayout.SOUTH);

        return card;
    }

    // Database query methods
    private String getTourBookingsCount() {
        try (Connection connection = myJDBC.getConnection()) {
            if (connection != null) {
                String query = "SELECT COUNT(*) FROM tour_bookings";
                try (Statement stmt = connection.createStatement();
                     ResultSet rs = stmt.executeQuery(query)) {
                    if (rs.next()) {
                        return Integer.toString(rs.getInt(1));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "0";
    }

    private String getFilmShowsCount() {
        try (Connection connection = myJDBC.getConnection()) {
            if (connection != null) {
                String query = "SELECT COUNT(*) FROM film_bookings";
                try (Statement stmt = connection.createStatement();
                     ResultSet rs = stmt.executeQuery(query)) {
                    if (rs.next()) {
                        return Integer.toString(rs.getInt(1));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "0";
    }

    private String getMarketingEventsCount() {
        try (Connection connection = myJDBC.getConnection()) {
            if (connection != null) {
                String query = "SELECT COUNT(*) FROM marketing_events";
                try (Statement stmt = connection.createStatement();
                     ResultSet rs = stmt.executeQuery(query)) {
                    if (rs.next()) {
                        return Integer.toString(rs.getInt(1));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "0";
    }

    // Table styling method
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

    public static void main(String[] args) {
        try {
            // Set system look and feel
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            String loggedInUser = "Guest";
            if (args.length > 0) {
                loggedInUser = args[0];
            }
            HomeUI homeUI = null;
            try {
                homeUI = new HomeUI(loggedInUser);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
            homeUI.setVisible(true);
        });
    }
}