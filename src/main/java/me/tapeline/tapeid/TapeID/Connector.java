package me.tapeline.tapeid.TapeID;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Connector {
    public Connection connection;

    public Connector() {
        try {
            connection = DriverManager.getConnection(
                    "jdbc:mysql://localhost/tapeid", "root", "dj233kil");

        } catch (SQLException exception) {
            throw new RuntimeException(exception);
        }
    }

    public void execute(String s) {
        try {
            connection.createStatement().execute(s);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public ResultSet query(String s) {
        try {
            return connection.createStatement().executeQuery(s);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void close() {
        try {
            connection.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
