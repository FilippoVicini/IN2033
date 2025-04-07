package com.lancaster.database.BoxOfficeInterface;

import com.lancaster.database.Bookings;
import com.lancaster.database.Films;
import com.lancaster.database.Promotions;
import com.lancaster.database.myJDBC;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BoxOfficeData implements BoxOfficeInterface {

    @Override
    public Map<String, Promotions.PromotionDetails> getUpcomingEventPromotions() {
        return null;
    }

    @Override
    public List<Bookings.PriorityBookings> getFriendsReservations(String membershipId) {
        List<Bookings.PriorityBookings> reservations = new ArrayList<>();
        String query = "SELECT p.priorityID, p.room, p.row, p.seat, p.eventID, p.date, p.friendID " +
                      "FROM priority_bookings p " +
                      "WHERE p.friendID = ? AND p.date >= CURRENT_DATE() " +
                      "ORDER BY p.date ASC";

        try (Connection connection = myJDBC.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            
            statement.setString(1, membershipId);
            
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    Bookings.PriorityBookings booking = new Bookings.PriorityBookings(
                        resultSet.getInt("priorityID"),
                        resultSet.getString("room"),
                        resultSet.getInt("row"),
                        resultSet.getInt("seat"),
                        resultSet.getInt("eventID"),
                        resultSet.getDate("date"),
                        resultSet.getInt("friendID")
                    );
                    reservations.add(booking);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return reservations;
    }

    @Override
    public Promotions.PromotionStats getPromotionalSalesTracking(String promotionCode) {

        return null;
    }

    @Override
    public List<Bookings.GroupBookings> getGroupHolds(int minimumGroupSize) {
        List<Bookings.GroupBookings> groupHolds = new ArrayList<>();
        String query = "SELECT g.booking_id, g.people, g.date, g.room, g.event, g.reservedRows " +
                      "FROM group_bookings g " +
                      "WHERE g.people >= ? AND g.date >= CURRENT_DATE() " +
                      "ORDER BY g.date ASC";

        try (Connection connection = myJDBC.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            
            statement.setInt(1, minimumGroupSize);
            
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    Bookings.GroupBookings booking = new Bookings.GroupBookings(
                        resultSet.getInt("booking_id"),
                        resultSet.getInt("people"),
                        resultSet.getDate("date"),
                        resultSet.getString("room"),
                        resultSet.getString("event")
                    );
                    groupHolds.add(booking);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return groupHolds;
    }

    public List<String> getReservedRows(String groupId, Connection connection) throws SQLException {
        List<String> reservedRows = new ArrayList<>();
        String query = "SELECT reservedRows FROM group_bookings WHERE booking_id = ?";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, groupId);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    reservedRows.add(resultSet.getString("reservedRows"));
                }
            }
        }

        return reservedRows;
    }
    @Override
    public Map<String, Double> getInstitutionalBookings(String institutionType) {
        Map<String, Double> institutionalBookings = new HashMap<>();
        String query = "SELECT b.booking_id, b.ticketPrice, b.discount " +
                      "FROM bookings b " +
                      "JOIN institutions i ON b.institutionId = i.institutionId " +
                      "WHERE i.type = ? AND b.bookingDate >= DATE_SUB(CURRENT_DATE(), INTERVAL 1 YEAR)";

        try (Connection connection = myJDBC.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            
            statement.setString(1, institutionType);
            
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    String bookingId = resultSet.getString("booking_id");
                    double originalPrice = resultSet.getDouble("ticketPrice");
                    double discount = resultSet.getDouble("discount");
                    double finalPrice = originalPrice * (1 - discount);
                    
                    institutionalBookings.put(bookingId, finalPrice);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return institutionalBookings;
    }


    public List<Films.FilmInformation> getFilmSchedule(LocalDateTime startDate, LocalDateTime endDate, Connection connection) throws SQLException {
        List<Films.FilmInformation> filmInformations = new ArrayList<>();
        String query = "SELECT * FROM films WHERE showDate BETWEEN ? AND ?";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setObject(1, startDate);
            statement.setObject(2, endDate);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    Films.FilmInformation filmInformation = new Films.FilmInformation(
                            resultSet.getInt("showId"),
                            resultSet.getInt("filmId"),
                            resultSet.getString("filmTitle"),
                            resultSet.getDate("showDate"),
                            resultSet.getInt("duration"),
                            resultSet.getDate("endDate"),
                            resultSet.getInt("ticketPrice")
                    );
                    filmInformations.add(filmInformation);
                }
            }
        }

        return filmInformations;
    }

    @Override
    public Films.FilmEngagement getFilmEngagement(String filmId) {

        return null;
    }

    @Override
    public Films.FilmFinancials getFilmFinancials(String filmId) {

        return null;
    }

    public Map<String, Integer> getHeldSeats(int eventId) {
        Map<String, Integer> heldSeatsMap = new HashMap<>();
        String query = "SELECT e.eventName, SUM(h.seats) as totalSeats " +
                      "FROM heldSeats h " +
                      "JOIN events e ON h.event = e.eventId " +
                      "WHERE h.event = ? " +
                      "GROUP BY e.eventName";

        try (Connection connection = myJDBC.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            
            statement.setInt(1, eventId);
            
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    String eventName = resultSet.getString("eventName");
                    int totalSeats = resultSet.getInt("totalSeats");
                    heldSeatsMap.put(eventName, totalSeats);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return heldSeatsMap;
    }
}
