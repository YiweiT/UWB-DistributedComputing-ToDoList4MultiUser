package edu.uwb.css533.service.db;

import javax.xml.transform.Result;
import java.sql.*;

public class DatabaseConnection {

    public String url = "jdbc:postgresql://localhost:5432/";
    private String username = "postgres";
    private String password = "password";
    private int connectionTries = 3;
    public String db;

    Connection connection;

    public DatabaseConnection() {
        this.url = this.url + "todolistdb";
        this.db = "todolistdb";
    }

    public DatabaseConnection(String db) {
        this.url = this.url + db;
        this.db = db;
    }

    /**
     * Connect to the database
     */
    public void connect() {
        Connection result = null;
        try {
            result = DriverManager.getConnection(url, username, password);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return;
        }
        connection = result;

        autoUpdateModifiedFunc();
        System.out.println("Connected to " + url);
    }

    public void createTables() {
        if(isConnected()) {
            createUserTable();
            createListTable();
            createTaskTable();
        } else {
            System.out.println("Error: Unable to connect to db " + url);
        }
    }
    /**
     * Check whether the db is connected. If not connected, try to connect at max 3 times
     * @return true if connection is accomplished, false otherwise
     */
    public Boolean isConnected() {
        int cnt = connectionTries;
        while (connection == null & cnt > 0) {
            connect();
            cnt--;
        }
        if (connection == null) {

            return false;
        }
        return true;

    }

    // Create a function to auto update last_modified_date for Lists and Tasks table
    private void autoUpdateModifiedFunc() {
        String sql =
                "CREATE OR REPLACE FUNCTION update_modified_column()"
        + "RETURNS TRIGGER AS $$"
        + "BEGIN"
        + " NEW.last_modified_date = now();"
        + "RETURN NEW;"
        + "END;"
        + "$$ language 'plpgsql';" +
         "CREATE OR REPLACE TRIGGER update_list_modtime BEFORE UPDATE ON " +
         "lists FOR EACH ROW EXECUTE PROCEDURE  update_modified_column();" +
         "CREATE OR REPLACE TRIGGER update_task_modtime BEFORE UPDATE ON tasks" +
         " FOR EACH ROW EXECUTE PROCEDURE  update_modified_column();";

        try {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    // Create users_info table
    private void createUserTable() {
        String sql = "CREATE TABLE IF NOT EXISTS USERS_INFO ("
                + "USERNAME VARCHAR(255) PRIMARY KEY,"
                + "PASSWORD VARCHAR (255) NOT NULL,"
                + "LISTIDS TEXT[]);";

        try {
            PreparedStatement statement = connection.prepareStatement(sql);

            int rows = statement.executeUpdate();
            if (rows > 0) {
                System.out.println("Successfully create Users_info table");
            }

        } catch (SQLException e) {
            System.out.println("Error: Unable to create table Users_info\n" + e.getMessage());
        }
    }

    // Create Lists table
    private void createListTable() {
        String sql = "CREATE TABLE IF NOT EXISTS LISTS ("
                +"LISTID SERIAL PRIMARY KEY,"
                +"LISTNAME VARCHAR (255) NOT NULL,"
                +"LAST_MODIFIED_DATE TIMESTAMPTZ NOT NULL DEFAULT NOW());";

        try {
            PreparedStatement statement = connection.prepareStatement(sql);

            int rows = statement.executeUpdate();
            if (rows > 0) {
                System.out.println("Successfully create Lists table");
            }

        } catch (SQLException e) {
            System.out.println("Error: Unable to create table Lists\n" + e.getMessage());
        }
    }

    // Create tasks table
    private void createTaskTable() {
        String sql = "CREATE TABLE IF NOT EXISTS TASKS ("
                +"TASKID SERIAL PRIMARY KEY,"
                +"TASKNAME VARCHAR (255) NOT NULL,"
                +"CONTENT TEXT,"
                +"STATUS VARCHAR (20) NOT NULL DEFAULT 'Not Started',"
                +"LISTID SERIAL NOT NULL,"
                +"LAST_MODIFIED_DATE TIMESTAMPTZ NOT NULL DEFAULT NOW(),"
                +"FOREIGN KEY (LISTID) REFERENCES LISTS (LISTID));";

        try {
            PreparedStatement statement = connection.prepareStatement(sql);

            int rows = statement.executeUpdate();
            if (rows > 0) {
                System.out.println("Successfully create Tasks table");
            }

        } catch (SQLException e) {
            System.out.println("Error: Unable to create table Tasks\n" + e.getMessage());
        }
    }

    // Drop tables in testdb
    public void dropTestTables() {
        if(isConnected()) {
            String sql = "DROP TABLE IF EXISTS TASKS;" +
                    "DROP TABLE IF EXISTS LISTS;" +
                    "DROP TABLE IF EXISTS USERS_INFO;";
            try {
                PreparedStatement statement = connection.prepareStatement(sql);
                statement.executeUpdate();
            } catch (SQLException e) {
                System.out.println("Error: Failed to drop tables for testdb\n" + e.getMessage());
            }
        } else {
            System.out.println("Error: Unable to connect to db " +url);
        }

    }

}
