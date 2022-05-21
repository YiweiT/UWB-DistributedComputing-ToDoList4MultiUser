package edu.uwb.css533.service.db;

import java.sql.Array;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ListServiceDB extends DatabaseConnection{

    /**
     * Check whether the given username can access list id
     * Search in users_info table, whether the given listid is in the listids Column of the username row
     * @param username String username
     * @param listid String list id
     * @return accessible if count > 0, error method otherwise
     */
    public String checkAccess(String username, String listid) {
        if (isConnected()) {
            String sql = "SELECT COUNT(*) FROM users_info where ? = ANY(listids) AND username=?;";
            try {
                PreparedStatement statement = connection.prepareStatement(sql);
                statement.setString(1, listid);
                statement.setString(2, username);

                ResultSet rs = statement.executeQuery();
                int cnt = 0;
                while (rs.next()) {
                    cnt = rs.getInt(1);
                }
                if (cnt < 1) {
                    return String.format("Error: %s cannot access %s.", username, listid);
                }

                return String.format("%s can access %s.", username, listid);
            } catch (SQLException e) {
                return "Error: " + e.getMessage();
            }
        } else {

            return "Error: Unable to connect " + url;
        }
    }

    /**
     * Grant the access for a username to a list with id as listid
     * 1. check whether the username already has access to listid.
     * 2. if not have access yet, add listid to username's listids column
     * @param username
     * @param listid
     * @return "Grand access successfully" if the username has no access initially and successful update listids column
     *          error message otherwise.
     */
    public String grantAccess(String username, String listid) {
        if (isConnected()) {
            String checkAccessMsg = checkAccess(username, listid);
            if (checkAccessMsg.contains("cannot access")) {
                String sql = "UPDATE users_info SET listids = array_append(listids,?) WHERE username=?;";
                try {
                    PreparedStatement statement = connection.prepareStatement(sql);
                    statement.setString(1, listid);
                    statement.setString(2, username);

                    int rows = statement.executeUpdate();
                    return String.format("Grant access successfully! Now %s can access %s", username, listid);
                } catch (SQLException e) {
                    return "Error: " + e.getMessage();
                }
            } else if (checkAccessMsg.contains("can access")) {
                return String.format("%s already has access to %s", username, listid);
            } else {
                return checkAccessMsg;
            }
        } else {
            return "Error: Unable to connect " + url;
        }
    }

    /**
     * remove username's access to listid
     * 1. check whether the username already has access to listid.
     * 2. if yes, remove listid from username's listids column
     * @param username
     * @param listid
     * @return
     * "Access removed successfully" if the username has the access to listid initially
     * and successful update listids column
     * error message otherwise
     */
    public String removeAccess(String username, String listid) {
        if (isConnected()) {
            String checkAccessMsg = checkAccess(username, listid);
            if (checkAccessMsg.contains("can access")) {
                String sql = "UPDATE users_info SET listids = array_remove(listids,?) WHERE username=?;";
                try {
                    PreparedStatement statement = connection.prepareStatement(sql);
                    statement.setString(1, listid);
                    statement.setString(2, username);

                    int rows = statement.executeUpdate();
                    return String.format("Access removed successfully! Now %s cannot access %s", username, listid);
                } catch (SQLException e) {
                    return "Error: " + e.getMessage();
                }
            } else if (checkAccessMsg.contains("cannot access")) {
                return String.format("%s does not have access to %s", username, listid);
            } else {
                return checkAccessMsg;
            }
        } else {
            return "Error: Unable to connect " + url;
        }
    }

    /**
     * Return all the users who can access listid
     * @param listid
     * @return Return all the users who can access listid; error message otherwise
     */
    public String getAllUsers(String listid){
        if (isConnected()) {

            String sql = "SELECT USERNAME FROM USERS_INFO WHERE ?=ANY(LISTIDS);";
            try {
                PreparedStatement stmt = connection.prepareStatement(sql);
                stmt.setString(1, listid);
                ResultSet rs = stmt.executeQuery();

                List<String> users = new ArrayList<>();
                while (rs.next()) {
                    users.add(rs.getString("username"));
                }
                return users.toString();
            } catch (SQLException e) {
                e.printStackTrace();
                return "Error: " + e.getMessage();
            }
        } else {

            return "Error: Unable to connect " + url;
        }
    }



}
