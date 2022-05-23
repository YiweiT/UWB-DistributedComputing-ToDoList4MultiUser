package edu.uwb.css533.service.db;

import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import javax.xml.transform.Result;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * This class is to connect to the database tier and execute sql query to check and fetch result
 * The result will be sent to service side and then response to client request
 *
 * This class is for List service only
 */
public class ListServiceDB extends DatabaseConnection{
    public ListServiceDB() {
    }

    public ListServiceDB(String db) {
        super(db);
    }

    // check whether the given listid is exsting in Lists table
    // return 1 if existing, 0 if not, -1 if error occurs
    private int isExisting(String listid) {
        if (isConnected()) {
            String sql = "SELECT COUNT(*) FROM Lists where listid=?";

            try {
                PreparedStatement statement = connection.prepareStatement(sql);
                statement.setInt(1, Integer.parseInt(listid));
                ResultSet rs = statement.executeQuery();
                int cnt = 0;
                if (rs.next()) {
                    cnt = rs.getInt(1);
//
                }
                System.out.println("count of " + listid + " in lists table is: " + cnt);
                if (cnt > 0) {
                    return 1;
                } else {
                    return 0;
                }

            } catch (SQLException e) {
                e.printStackTrace();
                return -1;
            }
        }
        return -1;
    }
    /**
     * Check whether the given username can access list id
     * Search in users_info table, whether the given listid is in the listids Column of the username row
     * @param username String username
     * @param listid String list id
     * @return accessible if count > 0, error method otherwise
     */
    public String checkAccess(String username, String listid) {
        if (isConnected()) {
            int existing = isExisting(listid);
            System.out.println("checkAccess: " + existing);
            if (existing == 1) {
                String sql = "SELECT COUNT(*) FROM users_info where ? = ANY(listids) AND username=?;";
                try {
                    PreparedStatement statement = connection.prepareStatement(sql);
                    statement.setString(1, listid);
                    statement.setString(2, username);

                    ResultSet rs = statement.executeQuery();
                    int cnt = 0;

                    if (rs.next()) {
                        cnt = rs.getInt(1);
                    }
                    if (cnt < 1) {
                        return String.format("Error: %s cannot access %s.", username, listid);
                    }

                    return String.format("%s can access %s.", username, listid);
                } catch (SQLException e) {
                    return "Error: " + e.getMessage();
                }
            } else if (existing == 0) {
                return "Error: no such list exists.";
            } else {
                return "Error: unable to access.";
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
                    System.out.println("row updated: " + rows);
                    if (rows > 0) {
                        return String.format("Successfully granted access! Now %s can access %s", username, listid);
                    }
                    return "Error: " + username + " does not exist.";
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
            System.out.println("remove access " + checkAccessMsg);
            if (checkAccessMsg.contains("can access")) {
                String sql = "UPDATE users_info SET listids = array_remove(listids,?) WHERE username=?;";
                try {
                    PreparedStatement statement = connection.prepareStatement(sql);
                    statement.setString(1, listid);
                    statement.setString(2, username);

                    int rows = statement.executeUpdate();
                    return String.format("Successfully removed access! Now %s cannot access %s", username, listid);
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

    /**
     * Delete all the lists owned by username
     * @param userId username
     * @return
     * - if no accessible listid for username: no list under the user.
     * - Success message
     */
    public String deleteAllLists(String userId){
        if (isConnected()) {
            String sql = "SELECT listids FROM Users_info WHERE username=?;";
            try {
                PreparedStatement stmt = connection.prepareStatement(sql);
                stmt.setString(1,userId);
                ResultSet rs = stmt.executeQuery();
                List<Integer> listids = new ArrayList<>();
                while (rs.next()) {
                    Array result = rs.getArray(1);
                    String[] result_2 = (String[]) result.getArray();
                    if(result_2.length == 0){

                        return String.format("No list under the user (%s).", userId);
                    }
                    for (String listid : result_2){

                        deleteList2(userId,listid);
                    }
                }
                return String.format("Successfully delete all lists under by %s", userId);
            }catch(SQLException e) {
                return "Error: " + e.getMessage();
            }

        } else {
            return "Error: Unable to connect " + url;
        }

    }

    /**
     * Create a new list for username with listname
     * @param username
     * @param listname
     * @return
     * - if the listname is taken: duplicate error
     * - if creation failed: creation error
     * - if grant access failed: access error
     * - success message
     */
    public String addList(String username, String listname) throws SQLException {
        if (isConnected()) {
            // check whether the listname is unique for this username
            if (!isExistingListname(username, listname)) {
                // create a list with the given listname and list_type.
                // Return listid to add access for username to listid in users_info table
                String sql = "INSERT INTO lists (listname) VALUES (?) RETURNING listid;";
                try  {
                    PreparedStatement statement = connection.prepareStatement(sql);
                    statement.setString(1, listname);


                    ResultSet rs = statement.executeQuery();
                    String listid = "";
                    while(rs.next()) {
                        listid = String.valueOf(rs.getInt("listid"));
                    }
                    // grant user access to listid
                    String grantAccessMsg = grantAccess(username, listid);
                    if (grantAccessMsg.contains("Error")) {
                        return "Error: Unable to create list";
                    }
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("Message", "Successfully create list: " + listname);
                    jsonObject.put("listid", listid);
                    return jsonObject.toString();
                } catch (SQLException e) {
                    return "Error: " + e.getMessage();
                }
            }
            return String.format(
                    "Error: listname (%s) has been taken. Please choose another listname", listname);
        } else {
            return "Error: Unable to connect " + url;
        }

//

    }

    /**
     * Check whether the given listname is existing under the given username
     * @param username
     * @param listname
     * @return true if the listname exists under the given username. False otherwise
     * @throws SQLException
     */
    private boolean isExistingListname(String username, String listname) throws SQLException {
        if (isConnected()) {
            System.out.println("isExistingListname " + username + ", " + listname);
            String sql1 = "SELECT COUNT(*) FROM(SELECT l.listname FROM "
                    + "(SELECT UNNEST(listids)::INTEGER AS listid FROM "
                    + "Users_info Where username = ?) AS i "
                    + "INNER JOIN Lists AS l ON i.listid = l.listid) listnames "
                    + "WHERE listname = ?;";
            try {
                PreparedStatement statement = connection.prepareStatement(sql1);
                statement.setString(1, username);
                statement.setString(2, listname);
                System.out.println(statement.toString());
                ResultSet rs = statement.executeQuery();
                int cnt = 0;
                while (rs.next()) {
                    cnt = rs.getInt(1);
                    System.out.println(cnt);
                }
                if (cnt > 0) {
                    System.out.println(listname + " exists in " + username);
                    return true;
                }
                System.out.println(listname + " does not exist in " + username);
                return false;

            } catch (SQLException e) {
                System.out.println(e.getMessage());
                return false;
            }
        } else {
            System.out.println("Error: Unable to connect " + url);
            return false;
        }
    }

    /**
     * delete a single list with listid, request by username
     * @param username
     * @param listid
     * @return
     * - if username does not have access to the listid: access error
     * - if remove access failed: access error
     * - if deletion failed: deletion error
     * - success message
     */
    public String deleteList2(String username, String listid) {
        if (isConnected()) {
            System.out.println("delete " + listid + " requested by " + username);
            // check whether the listid accessible by user
            String accessMsg = checkAccess(username, listid);
            if (accessMsg.contains("cannot access")) {
                return accessMsg;
            }
            // delete all tasks under such list
            TaskServiceDB taskServiceDB = new TaskServiceDB();
            String taskDeletion = taskServiceDB.deleteAllTasks(username, listid);
            if (!taskDeletion.contains("Successfully")) {
                return taskDeletion;
            }
            // remove access for all users how can access listid to this listid
            String[] usernames = getAllUsers(listid).split(",");
            boolean accessRemoved = true;

            int i = 0;
            String removeAccessMsg = "";
            while (i < usernames.length && accessRemoved) {
                String curUser = usernames[i].trim();
                if (curUser.startsWith("[")) {
                    System.out.println(curUser + " starts with ]");
                    curUser = curUser.substring(1);
                }
                if (curUser.endsWith("]")) {
                    System.out.println(curUser + " ends with [");

                    curUser = curUser.substring(0, curUser.length()-1);
                }

                System.out.println("remove "+ curUser + "'s access to " + listid);
                removeAccessMsg = removeAccess(curUser, listid);
                System.out.println(removeAccessMsg);
                if (removeAccessMsg.contains("Error")) {
                    System.out.println("delete fails " + removeAccessMsg);
                    accessRemoved = false;
                }
                i ++;
            }

            if (!accessRemoved) {
                return removeAccessMsg;
            }


            String sql = "DELETE FROM Lists WHERE listid=?;";

            try {
                PreparedStatement statement = connection.prepareStatement(sql);

                statement.setInt(1, Integer.parseInt(listid));
                int row = statement.executeUpdate();

                System.out.println("delete successfully " + listid);
                return "Successfully deleted " + listid;
            } catch (SQLException e) {
                e.printStackTrace();
                return "Error: " + e.getMessage();
            }
        } else {
            return "Error: Unable to connect to " + url;
        }
    }

    /**
     * Get all listids, listnames accessible by username
     * @param username
     * @return String
     * all listids, listnames accessible by username
     * error message otherwise
     */
    public String getAllLists(String username) {
        if (isConnected()) {
            String sql = "select l.listid, l.listname, " +
                    "l.list_type from lists as l inner join(SELECT " +
                    "UNNEST(listids)::INTEGER as listid from " +
                    "users_info where username=?) i on l.listid = i.listid " +
                    "order by l.last_modified_date desc;";

            try {
                PreparedStatement statement = connection.prepareStatement(sql);
                statement.setString(1, username);
                ResultSet rs = statement.executeQuery();

                JSONArray result = new JSONArray();
                while(rs.next()) {
                    JSONObject row = new JSONObject();
                    row.put("listid", rs.getInt("listid"));
                    row.put("listname", rs.getString("listname"));
                    result.put(row);
                }
                return result.toString();
            } catch (SQLException e) {
                return "Error: " + e.getMessage();
            }
        } else {
            return "Error: Unable to connect to " + url;
        }
    }
}
