package it.polimi.tiw.progetto_tiw_js;
import java.sql.*;
public class ConnectionTester {
    public static void main(String[] args) throws SQLException, ClassNotFoundException {
        final String DATABASE = "PlaylistDB";
        final String USER = "Luca";
        final String PASSWORD = "smanettone22";
        Connection connection = null;

        try { Class.forName("org.mariadb.jdbc.Driver"); System.out.println("Driver loaded");
        } catch (ClassNotFoundException e) { System.out.println("Driver not found"); e.printStackTrace();
        }
        try {
            connection = DriverManager.getConnection
                    ("jdbc:mariadb://localhost:3306/" + DATABASE, USER, PASSWORD);
            System.out.println("Database connected");
            connection.close(); } catch (Exception e) {
            System.out.println("Connection failed");
            e.printStackTrace();
        } }
}
