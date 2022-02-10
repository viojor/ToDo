package database;

import java.sql.*;

public class DBConnector {

    private static final String USER_DB = "root";
    private static final String PASSWORD_DB = "c0ntr4s3ñ4_p4r4_MySQL";
    private static final String URL_DB = "jdbc:mysql://localhost/to_do_db";
    private static final String DRIVER_NAME = "com.mysql.cj.jdbc.Driver";

    public static Connection getConnection() {

        Connection connection = null;

        try {

            Class.forName(DRIVER_NAME);
        } catch (ClassNotFoundException e) {

            System.out.println("Error registering MySQL's driver: " + e);
        }

        try {

            connection = DriverManager.getConnection(URL_DB, USER_DB, PASSWORD_DB);
        } catch (SQLException e) {

            System.out.println("Error while connecting with the DB: " + e);
        }
        return connection;
    }

    public static boolean isTableNotCreated(String tableName, Connection connection) {

        boolean isTableOnSchema = false;
        try {

            DatabaseMetaData meta = connection.getMetaData();
            ResultSet resultSet = meta.getTables(null, null, tableName, new String[]{"TABLE"});

            isTableOnSchema = resultSet.next();
        } catch (SQLException e) {

            System.out.println("Error checking if the table exist in the DB: " + e);
        }
        return !isTableOnSchema;
    }

    public static void disconnectDB(Connection connection) {

        if (connection != null) {

            try {
                if (!connection.isClosed()) {

                    connection.close();
                }
            } catch (SQLException e) {

                System.out.println("Error while closing the connection with the DB: " + e);
            }
        }
    }
}
