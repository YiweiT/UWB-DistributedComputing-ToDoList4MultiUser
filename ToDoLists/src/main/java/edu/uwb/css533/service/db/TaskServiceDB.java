package edu.uwb.css533.service.db;

import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * This class is to connect to the database tier and execute sql query to check and fetch result
 * The result will be sent to service side and then response to client request
 *
 * This class is for Task service only
 */
public class TaskServiceDB extends ListServiceDB {

    public TaskServiceDB() {
    }

    public TaskServiceDB(String db) {
        super(db);
    }

    // check whether the given (taskid, listid) pair exists in Task table
    // return true if such pair exists, false otherwise
    private boolean checkExist(String taskId, String listid){
        String sql = "SELECT TASKID FROM TASKS WHERE TASKID= ? AND LISTID=?;";
        try {

            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setInt(1,Integer.parseInt(taskId));
            stmt.setInt(2, Integer.parseInt(listid));
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String compare = Integer.toString(rs.getInt("taskid"));
                if(taskId.equals(compare)) {
                    return true;
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;
        }
        return false;
    }

    // Check whether there is the same taskname under the same listid
    // return true if there is the same taskname, false otherwise
    private boolean checkDuplicate(String taskName, String listId){
        String sql = "SELECT TASKNAME FROM TASKS WHERE LISTID= ?;";
        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setInt(1, Integer.parseInt(listId));
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                if(taskName.equals(rs.getString("TASKNAME"))) {
                    return true;
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;
        }
        return false;
    }

    /**
     * Get all the information about the given taskid
     * @param username username
     * @param listid id of the current list
     * @param taskid id of the selected task
     * @return String:
     * - Error occur: error message
     * - No such task exist: "Error: no such task (taskid) exists in list (listid)."
     * - Username does not have access to the current list: cannot access message
     * - Get taskid, taskname, content, and status of the given taskid
     */
    public String getTask(String username, String listid, String taskid) {
        if (isConnected()) {
            // check taskid existing
            if (checkExist(taskid, listid)) {
                // check username listid accessibility
                String accessMsg = checkAccess(username, listid);
                if (accessMsg.contains("can access")) {
                    // fetch task information: taskid, taskname, content, status
                    return getTask(taskid);
                } else {
                    return accessMsg;
                }
            } else {
                System.out.println(String.format("Error: no such task (%s) exists in list (%s).", taskid, listid));
                return String.format(String.format("Error: no such task (%s) exists in list (%s).", taskid, listid));
            }
        } else {
            return "Error: Unable to connect to db - " + url;
        }
    }

    // Get all information of the given taskid
    private String getTask(String taskid) {
        String sql = "SELECT TASKID, TASKNAME, CONTENT, STATUS " +
                "FROM TASKS WHERE TASKID=?;";
        try {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, Integer.parseInt(taskid));
            ResultSet rs = statement.executeQuery();
            JSONObject row = new JSONObject();
            while (rs.next()) {
                row.put("taskid", rs.getInt("taskid"));
                row.put("taskname", rs.getString("taskname"));
                row.put("content", rs.getString("content"));
                row.put("status", rs.getString("status"));
            }
            return row.toString();
        } catch (SQLException e) {
            e.printStackTrace();
            return "Error: " + e.getMessage();
        }
    }

    /**
     * Add task to the listid, requested by username
     * @param task_name name of the task to be added
     * @param task_description content of the task to be added
     * @param list_id id of list the task belongs to
     * @param user_name name of user who request the addition
     * @return String
     * - if username does not have access to the listid: access error
     * - if Task name is taken: duplicate error
     * - if failed to update list last_modified_date: update error
     * - successful message
     */
    public String addTask(String task_name, String task_description, String list_id, String user_name){
        JSONObject jsonObject = new JSONObject();
        int taskId = 0;
        String accessMsg = checkAccess(user_name,list_id);
        if(accessMsg.contains("Error")){
            System.out.println(accessMsg);
            return accessMsg;
        }
        if (connection == null) {
            connect();
        }
        if (connection == null) {
            System.out.println("Error: Unable to connect to database.");
            return "Error: Unable to connect to database (" + db + ").";
        }

        boolean taskNameExist = checkDuplicate(task_name,list_id);
        if(taskNameExist){
            System.out.println("Error: taskName has already exists for current list");
            jsonObject.put("Error", String.format("%s has already exists for current list.", task_name));
            return jsonObject.toString();
        }

        String sql = "INSERT INTO TASKS (TASKNAME, CONTENT, LISTID) VALUES ( ?, ?, ?) RETURNING TASKID;";
        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setString(1, task_name);
            stmt.setString(2, task_description);

            stmt.setInt(3, Integer.parseInt(list_id));

            stmt.execute();
            ResultSet id = stmt.getResultSet();
            id.next();
            taskId = id.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();

            jsonObject.put("Error", e.getMessage());
            return jsonObject.toString();
        }

        jsonObject.put("Message", "Successfully create the task: \"" + task_name+ "\" in current list: "+list_id);
        jsonObject.put("taskid", taskId);
        String listUpdate = updateListDate(list_id);
        if(!listUpdate.contains("Successfully")){

            jsonObject.put("Error", listUpdate);
        }
        return jsonObject.toString();
    }

    /**
     * Delete the taskid under listid, requested by username
     * @param task_id id of to-be-deleted task
     * @param list_id id of list where the to-be-delete task is
     * @param userName username
     * @return
     * - If username does not have access to listid: Invalid user error
     * - If the given taskid does not exist under such listid: task not exists error
     * - Successful deletion: Success message
     */
    public String deleteTask(String task_id, String list_id,String userName){
        String accessMsg = checkAccess(userName,list_id);
        if(accessMsg.contains("Error")){
            System.out.println(accessMsg);
            return accessMsg;
        }
        if (connection == null) {
            connect();
        }
        if (connection == null) {
            System.out.println("Error: Unable to connect to database.");
            return "Error: Unable to connect to database.";
        }
        boolean taskNameExist = checkExist(task_id, list_id);

        if(!taskNameExist){
            System.out.println(String.format("Error: Task (%s) does not exists for current list (%s)", task_id, list_id));
            return String.format("Error: Task (%s) does not exists for current list (%s)", task_id, list_id);
        }

        String sql = "DELETE FROM TASKS WHERE TASKID= ? and LISTID=?;";
        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setInt(1,Integer.parseInt(task_id));
            stmt.setInt(2, Integer.parseInt(list_id));
            int rows = stmt.executeUpdate();
            if (rows > 0) {
                String listUpdate = updateListDate(list_id);
                if(!listUpdate.contains("Successfully")){
                    return listUpdate;
                }
                return "Successfully deleted task " + task_id +" of list " + list_id + " for user: " + userName;
            } else {
                return String.format("Error: task (%s) of list (%s) does not exist.", task_id, list_id);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return e.getMessage();
        }
    }

    /**
     * Delete all the tasks under listid, requested by username
     * @param user_name username of who request the deletion
     * @param list_id listid where all the to-be-deleted tasks is
     * @return
     * - If username does not have access to listid, access error
     * - Successful deletion: successful deletion message
     */
    public String deleteAllTasks(String user_name, String list_id){
        String accessMsg = checkAccess(user_name,list_id);
        if(accessMsg.contains("Error")){
            System.out.println(accessMsg);
            return accessMsg;
        }
        if (connection == null) {
            connect();
        }
        if (connection == null) {
            System.out.println(("Error: Unable to connect to database."));
            return "Error: Unable to connect database.";
        }
        String sql = "DELETE FROM TASKS WHERE LISTID=?;";
        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setInt(1, Integer.parseInt(list_id));
            stmt.executeUpdate();
        }catch(SQLException e) {
            e.printStackTrace();
            return e.getMessage();
        }
        String listUpdate = updateListDate(list_id);
        if(!listUpdate.contains("Successfully")){
            return listUpdate;
        }
        return "Successfully deleted all tasks of  list " +list_id;
    }



    /**
     * Display all tasknames, taskid, contents and status for the listid
     * @param user_name username of who requests display
     * @param list_id listid of current list
     * @return
     * - If username cannot access listid, access error
     * - If no task under such listid, "Error: No task found in such listid"
     * - display all information of all tasks under listid
     */
    public String displayAllTaskNames(String user_name, String list_id){
        String accessMsg = checkAccess(user_name,list_id);
        if(accessMsg.contains("Error")){
            System.out.println(accessMsg);
            return accessMsg;
        }
        if (connection == null) {
            connect();
        }
        if (connection == null) {
            System.out.println(("Error: Unable to connect to database."));
            return "Error: Unable to connect to database.";
        }
        String sql_get_task_name = "SELECT TASKID, TASKNAME, CONTENT, STATUS FROM TASKS " +
                "WHERE LISTID=? ORDER BY LAST_MODIFIED_DATE DESC;";
        try {
            PreparedStatement stmt = connection.prepareStatement(sql_get_task_name);
            stmt.setInt(1, Integer.parseInt(list_id));
            ResultSet rs = stmt.executeQuery();

            System.out.println("Here is all your tasks:");
            JSONArray result = new JSONArray();
            while (rs.next()) {
                System.out.println("Task Name: " + rs.getString("taskname"));
                JSONObject row = new JSONObject();
                row.put("taskid", rs.getInt("taskid"));
                row.put("taskname", rs.getString("taskname"));
                row.put("content", rs.getString("content"));
                row.put("status", rs.getString("status"));
                result.put(row);

                System.out.println("Task Name: " + rs.getString("taskname"));
            }
            if (result.isEmpty()) {
                System.out.println("No task found for current list.");
                return "No task found for current list.";
            }
            return result.toString();
        }catch(SQLException e){
            e.printStackTrace();
            return e.getMessage();
        }

    }

    /**
     * Update task content for the given taskid
     * @param user_name user who requests the update
     * @param list_id listid to that the taskid belong
     * @param content new content
     * @param task_id task to be updated content
     * @return
     * - if username does not have access to listid, access error
     * - if execute update row is 0, no such task exists
     * - Success message
     */
    public String updateTaskContent(String user_name, String list_id, String content, String task_id){
        String accessMsg = checkAccess(user_name,list_id);
        if(accessMsg.contains("Error")){
            System.out.println(accessMsg);
            return accessMsg;
        }
        if (connection == null) {
            connect();
        }
        if (connection == null) {
            System.out.println("Unable to connect database.");
            return "Unable to connect to database.";
        }
        String update_task_status_sql = "UPDATE Tasks SET CONTENT=? WHERE TASKID=?;";
        try{
            PreparedStatement stmt = connection.prepareStatement(update_task_status_sql);
            stmt.setString(1,content);

            stmt.setInt(2, Integer.parseInt(task_id));
            int row = stmt.executeUpdate();
            if (row < 1) {
                return String.format("No such task (%s) exists in list (%s).", task_id, list_id);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return e.getMessage();
        }
        String listUpdate = updateListDate(list_id);
        if(!listUpdate.contains("Successfully")){
            return listUpdate;
        }
        return "Successfully updated task content:\n "+content+"\nfor task "+task_id;
    }


    /**
     * Update task status for the given taskid
     * @param user_name user who requests the update
     * @param list_id listid to that the taskid belong
     * @param status new status
     * @param task_id task to be updated content
     * @return
     * - if username does not have access to listid, access error
     * - if execute update row is 0, no such task exists
     * - Success message
     */
    public String updateTaskStatus(String user_name, String list_id,String status, String task_id){
        boolean notAllowed = checkAccess(user_name,list_id).contains("Error");
        if(notAllowed){
            System.out.println("Invalid user");
            return "Invalid user";
        }
        if (connection == null) {
            connect();
        }
        if (connection == null) {
            System.out.println("Unable to connect to database.");
            return "Unable to connect to database.";
        }
        if(! (status.equals("Not Started") || status.equals("Completed" )||status.equals("In-Progress" ))){
            System.out.println("Task type can only be Completed, Not Started or In-Progress");
            return "Task type can only be Completed, Not Started or In-Progress";
        }
        String update_task_status_sql = "UPDATE Tasks SET STATUS=? WHERE TASKID=?;";
        try{
            PreparedStatement stmt = connection.prepareStatement(update_task_status_sql);
            stmt.setString(1,status);

            stmt.setInt(2, Integer.parseInt(task_id));
            int row = stmt.executeUpdate();
            if (row < 1) {
                return String.format("No such task (%s) exists in list (%s).", task_id, list_id);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return e.getMessage();
        }
        String listUpdate = updateListDate(list_id);
        if(!listUpdate.contains("Successfully")){
            return listUpdate;
        }

        return "Successfully updated task status: \""+status+"\" for task "+task_id;
    }

    // Update the last modified date for listid
    private String updateListDate(String listid){
        String sql = "UPDATE Lists SET LAST_MODIFIED_DATE=NOW() WHERE listid=?;";
        try{
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setInt(1,Integer.parseInt(listid));
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return e.getMessage();
        }
        return "Successfully update the last modified date of current list";
    }
}
