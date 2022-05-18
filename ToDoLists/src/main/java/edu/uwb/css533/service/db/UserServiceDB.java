package edu.uwb.css533.service.db;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserServiceDB extends DatabaseConnection {




    public String addUser(String username, String password) {
        if(isConnected()) {
            String sql = "INSERT INTO USERS_INFO (username, password) VALUES (?, ?);";
            try {
                PreparedStatement statement = connection.prepareStatement(sql);

                statement.setString(1, username);
                statement.setString(2, password);

                int rows = statement.executeUpdate();
                return "Successfully add user: " + username;
            } catch (SQLException e) {
//                e.printStackTrace();
                return "Error: " + e.getMessage();
            }
        } else {

            return "Error: Unable to connect " + url;
        }


    }

    private String usernameOccurence(String username) {
        if (isConnected()) {
            String sql = "SELECT * FROM USERS_INFO WHERE USERNAME=?;";
            try {
                PreparedStatement statement = connection.prepareStatement(sql);
                statement.setString(1, username);

                int rows = statement.executeUpdate();
                return Integer.toString(rows);
            } catch (SQLException e) {
                return "Error: " + e.getMessage();
            }
        } else {
            return "Error: Unable to connect " + url;
        }
    }

    public String getAllUsernames(){
        if (isConnected()) {

            String sql = "SELECT USERNAME FROM USERS_INFO ORDER BY USERNAME;";
            try {
                PreparedStatement stmt = connection.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery();

                String res = "";
                while (rs.next()) {
                    res += rs.getString("username") + "\n";
                }
                return res;
            } catch (SQLException e) {
                e.printStackTrace();
                return "Error: " + e.getMessage();
            }
        } else {

            return "Error: Unable to connect " + url;
        }
    }

    /**
     * 1. Search (username, password) pair in Users_info table and count the occurance
     * @param username
     * @param password
     * @return successful logged in message if count = 1, Error message otherwise
     */
    public String logIn(String username, String password) {
        if (isConnected()) {
            String sql = "SELECT COUNT(*) FROM USERS_INFO WHERE USERNAME=? AND PASSWORD=?;";
            try {
                PreparedStatement statement = connection.prepareStatement(sql);

                statement.setString(1, username);
                statement.setString(2, password);

                ResultSet rs = statement.executeQuery();
                int cnt = 0;
                while (rs.next()) {
                    cnt = rs.getInt(1);

                }
                if (cnt > 0) {
                    return "Successfully logged in user: " + username;
                }
                return "Error: username or password not matching.";

            } catch (SQLException e) {
//                e.printStackTrace();
                return "Error: " + e.getMessage();
            }
        } else {

            return "Error: Unable to connect " + url;
        }

    }

    public String resetPassword (String username, String oldPassword, String newPassword) {
        if (isConnected()) {
            String res;
            // check (username, oldPassword) existence in Users_info table
            res = logIn(username, oldPassword);
            System.out.println("login: " + res);
            if (res.contains("Error")) {
                return String.format("Error: username (%s) and password do not match.", username);
            }
            String sql = "UPDATE Users_info SET password=? WHERE username=? AND password=?;";
            try{
                PreparedStatement statement = connection.prepareStatement(sql);

                statement.setString(1, newPassword);
                statement.setString(2, username);
                statement.setString(3, oldPassword);

                int rows = statement.executeUpdate();
                return String.format("Password reseted sucessfully for username (%s)", username);
            } catch (SQLException e) {
                return "Error: " + e.getMessage();
            }

        } else {
            return "Error: Unable to connect " + url;
        }
    }
}
