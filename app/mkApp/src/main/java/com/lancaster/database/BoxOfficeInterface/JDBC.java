package com.lancaster.database.BoxOfficeInterface;

import com.lancaster.database.Films;
import io.github.cdimascio.dotenv.Dotenv;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

public class JDBC {
    private final Connection connection;
    private final BoxOfficeData boxOfficeData;
    // Load environment variables from .env file
    private static final Dotenv dotenv = Dotenv.load();
    private static final String URL = "jdbc:mysql://sst-stuproj.city.ac.uk:3306/in2033t27";
    private static final String USER = dotenv.get("ADMIN_USER");
    private static final String PASSWORD = dotenv.get("ADMIN_PASSWORD");


    public JDBC() throws SQLException, ClassNotFoundException {
        Class.forName("com.mysql.cj.jdbc.Driver");
        this.connection = DriverManager.getConnection(URL, USER, PASSWORD);
        this.boxOfficeData = new BoxOfficeData();

    }

    public List<Films.FilmInformation> getFilmSchedule(LocalDateTime startDate, LocalDateTime endDate) throws SQLException {
        return boxOfficeData.getFilmSchedule(startDate,endDate,connection);
    }

    public static void main(String[] args) throws SQLException, ClassNotFoundException {

        System.out.println("Hello from marketing");
        com.lancaster.database.OperationsInterface.JDBC jdbc = new com.lancaster.database.OperationsInterface.JDBC();


    }
}
