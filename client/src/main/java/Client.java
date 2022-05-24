import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.HashMap;
import java.util.Locale;
import java.util.Scanner;
import java.util.Stack;
import java.util.zip.CheckedOutputStream;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.util.JSONPObject;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class Client {

    private static boolean loggedIn;
    private static boolean quitListsService;
    private static boolean quitSingleListService;
    private static boolean quitTaskService;

    private static String curUsername;
    static String url = "http://localhost:8080";
    private static String currentListId;
    private static String currentTaskId;


    private static int doGet(String url) {
        try {
            HttpClient client = HttpClient.newBuilder()
                    .version(HttpClient.Version.HTTP_1_1)
                    .connectTimeout(Duration.ofSeconds(20))
                    .followRedirects(HttpClient.Redirect.NORMAL)
                    .build();
            HttpRequest request = HttpRequest.newBuilder()
                    .GET()
                    .uri(URI.create(url))
                    .build();
            HttpResponse<String>  response = client.send(request, HttpResponse.BodyHandlers.ofString());

            System.out.println(response.body());
            if (isValidJson(String.valueOf(response.body()))) {
                ObjectMapper mapper = new ObjectMapper();

                System.out.println(mapper.writerWithDefaultPrettyPrinter()
                        .writeValueAsString(mapper.readTree(response.body())));
            } else {
                System.out.println(response.body());
            }

            return response.statusCode();
        } catch (IOException e) {
            System.out.println("IO Exceptions: " + e.getMessage());
            return -1;
        } catch (InterruptedException e) {
            System.out.println("Interrupted: " + e.getMessage());
            return -1;
        } catch (Exception e) {
            System.out.println("Unknown Exceptions: " + e.getMessage());
            return -1;
        }
    }

//    private static int doPut1(String link, String payload) {
//        HttpURLConnection connection = null;
//        try {
//            // create connection
//            URL url = new URL(link);
//            connection = (HttpURLConnection) url.openConnection();
//            connection.setRequestMethod("PUT");
//            connection.setRequestProperty("Content-Type",
//                    "application/json; charset=UTF-8");
//            connection.setRequestProperty("Accept", "application/json");
//            connection.setDoOutput(true);
//            OutputStream os = connection.getOutputStream();
////            byte[] input = payload.getBytes("utf-8");
//            os.write(payload.getBytes(StandardCharsets.UTF_8));
//
//            // read the response from input stream
//            BufferedReader br = new BufferedReader(
//                    new InputStreamReader(connection.getInputStream(), "utf-8"));
//            StringBuilder response = new StringBuilder();
//            String responseLine = null;
//            while ((responseLine = br.readLine()) != null) {
//                response.append(responseLine.trim());
//            }
//            if (isValidJson(String.valueOf(response))) {
//                ObjectMapper mapper = new ObjectMapper();
//
//                System.out.println(mapper.writerWithDefaultPrettyPrinter()
//                        .writeValueAsString(mapper.readTree(String.valueOf(response))));
//            } else {
//                System.out.println(response.toString());
//            }
//
//            connection.disconnect();
//            return connection.getResponseCode();
//
//        } catch (MalformedURLException e) {
//            e.printStackTrace();
//            System.out.println("Malformed URL Exception: " + e.getMessage());
//            return -1;
//
//        } catch (IOException e) {
//            System.out.println("IO Exceptions: " + e.getMessage());
//            return -1;
//        }
//    }

    private static int doPut(String link, String payload) {
        try {
            HttpClient client = HttpClient.newBuilder()
                    .version(HttpClient.Version.HTTP_1_1)
                    .connectTimeout(Duration.ofSeconds(20))
                    .followRedirects(HttpClient.Redirect.NORMAL)
                    .build();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(link))
                    .header("Content-Type", "application/json")
                    .PUT(HttpRequest.BodyPublishers.ofString(payload))
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println(response.body());
            if (isValidJson(String.valueOf(response.body()))) {
                ObjectMapper mapper = new ObjectMapper();

                System.out.println(mapper.writerWithDefaultPrettyPrinter()
                        .writeValueAsString(mapper.readTree(response.body())));
            } else {
                System.out.println(response.body());
            }
            return response.statusCode();
        }catch (IOException e) {
            System.out.println("IO Exceptions: " + e.getMessage());
            return -1;
        } catch (InterruptedException e) {
            System.out.println("Interrupted: " + e.getMessage());
            return -1;
        } catch (Exception e) {
            System.out.println("Unknown Exceptions: " + e.getMessage());
            return -1;
        }
    }

    private static int doPost2(String link, String payload) {
        try {
            HttpClient client = HttpClient.newBuilder()
                    .version(HttpClient.Version.HTTP_1_1)
                    .connectTimeout(Duration.ofSeconds(20))
                    .followRedirects(HttpClient.Redirect.NORMAL)
                    .build();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(link))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(payload))
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println(response.body());
            if (isValidJson(String.valueOf(response.body()))) {
                ObjectMapper mapper = new ObjectMapper();

                System.out.println(mapper.writerWithDefaultPrettyPrinter()
                        .writeValueAsString(mapper.readTree(response.body())));
            } else {
                System.out.println(response.body());
            }
            return response.statusCode();
        }catch (IOException e) {
            System.out.println("IO Exceptions: " + e.getMessage());
            return -1;
        } catch (InterruptedException e) {
            System.out.println("Interrupted: " + e.getMessage());
            return -1;
        } catch (Exception e) {
            System.out.println("Unknown Exceptions: " + e.getMessage());
            return -1;
        }
    }

//    private static int doPost1(String link, String payload) {
//        HttpURLConnection connection = null;
//        try {
//            // create connection
//            URL url = new URL(link);
//            connection = (HttpURLConnection) url.openConnection();
//            connection.setRequestMethod("POST");
//            connection.setRequestProperty("Content-Type",
//                    "application/json; charset=UTF-8");
//            connection.setRequestProperty("Accept", "application/json");
//            connection.setDoOutput(true);
//            OutputStream os = connection.getOutputStream();
////            byte[] input = payload.getBytes("utf-8");
//            os.write(payload.getBytes(StandardCharsets.UTF_8));
//
//            // read the response from input stream
//            BufferedReader br = new BufferedReader(
//                    new InputStreamReader(connection.getInputStream(), "utf-8"));
//            StringBuilder response = new StringBuilder();
//            String responseLine = null;
//            while ((responseLine = br.readLine()) != null) {
//                response.append(responseLine.trim());
//            }
//            if (isValidJson(String.valueOf(response))) {
//                ObjectMapper mapper = new ObjectMapper();
//
//                System.out.println(mapper.writerWithDefaultPrettyPrinter()
//                        .writeValueAsString(mapper.readTree(String.valueOf(response))));
//            } else {
//                System.out.println(response.toString());
//            }
//
//            connection.disconnect();
//            return connection.getResponseCode();
//
//        } catch (MalformedURLException e) {
//            e.printStackTrace();
//            System.out.println("Malformed URL Exception: " + e.getMessage());
//            return -1;
//        }
//        catch (IOException e) {
//            System.out.println("IO Exceptions: " + e.getMessage());
//            return -1;
//        }
//    }



    public static void main(String[] args) {
        System.out.println("Welcome to to-do list.");
        loggedIn = false;
        currentListId = null;
        BufferedReader inputReader = new BufferedReader(new InputStreamReader(System.in));
        Scanner scanner = new Scanner(System.in);

        while(!loggedIn) {
            userService(scanner);
            quitListsService = false;
            while ((!quitListsService) && loggedIn && currentListId == null) {
                listsService(scanner);
                quitSingleListService = false;
                while ((!quitSingleListService) && loggedIn && currentListId != null) {
                    singleListService(scanner);
                    quitTaskService = false;
                    while ((!quitTaskService) && loggedIn && currentListId != null && currentTaskId != null) {
                        taskService(scanner);
                    }
                    currentTaskId = null;
                }
                currentListId = null;
            }
            loggedIn = false;
        }

    }

    private static void taskService(Scanner scanner) {
        //String currentTaskName = currentTaskId.split("_")[2];

        System.out.println("--------------------------------------------------------");
        System.out.println("In task " + currentTaskId +  " you can:");
        System.out.println("1. Update task content");
        System.out.println("2. Update status");
        System.out.println("3. Display details of a task");
        System.out.println("4. Come back to the ToDoList");
        System.out.println("--------------------------------------------------------");

        String userRespond;
        userRespond = scanner.nextLine().toLowerCase().trim();

        switch (userRespond) {
            case "1" -> {
                // code to update task content
                System.out.println("To update task content of taskid: "+ currentTaskId +", please enter task content:");
                String taskContent = scanner.nextLine().trim();
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("username", curUsername);
                jsonObject.put("listid", currentListId);
                jsonObject.put("content", taskContent);
                jsonObject.put("taskid", currentTaskId);
                String request = url + "/tasks/updateTaskContent";
                System.out.println(request);
                System.out.println(jsonObject.toString());
                if (doPut(request, jsonObject.toString()) == 200) {
                    System.out.println("Successfully update task content, do you want to try something else in this task? (Yes/No)");
                    String response = scanner.nextLine().toLowerCase().trim();
                    if (response.equals("yes")) {
                        quitTaskService = false;
                    } else {
                        quitTaskService = true;
                        System.out.println("Coming back to the current ToDoList menu......");
                    }
                } else {
                    System.out.println("Update task content failed for some reasons,Do you want to try something else in this task? (Yes/No)");
                    String response = scanner.nextLine().toLowerCase().trim();
                    if (response.equals("yes")) {
                        quitTaskService = false;
                    } else {
                        quitTaskService = true;
                        System.out.println("Coming back to the current ToDoList menu......");
                    }
                }
            }
            case "2" -> {
                // code to update task status
                System.out.println("To update task status of taskid: "+ currentTaskId +", please enter task status:");
                System.out.println("Note: Task type can only be Completed, Not Started or In-Progress");
                String taskStatus = scanner.nextLine().trim();
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("username", curUsername);
                jsonObject.put("listid", currentListId);
                jsonObject.put("taskid", currentTaskId);
                jsonObject.put("status", taskStatus);
                String request = url + "/tasks/updateTaskStatus";
                System.out.println(request);
                System.out.println(jsonObject.toString());
                if (doPut(request, jsonObject.toString()) == 200) {
                    System.out.println("Successfully update task status, do you want to try something else in this task? (Yes/No)");
                    String response = scanner.nextLine().toLowerCase().trim();
                    if (response.equals("yes")) {
                        quitTaskService = false;
                    } else {
                        quitTaskService = true;
                        System.out.println("Coming back to the current ToDoList menu......");
                    }
                } else {
                    System.out.println("Update task status failed for some reasons,Do you want to try something else in this task? (Yes/No)");
                    String response = scanner.nextLine().toLowerCase().trim();
                    if (response.equals("yes")) {
                        quitTaskService = false;
                    } else {
                        quitTaskService = true;
                        System.out.println("Coming back to the current ToDoList menu......");
                    }
                }
            }
            case "3" -> {
                // code to display a task
                System.out.println("Displaying current taskid: " + currentTaskId);
                String query = String.format("username=%s&listid=%s&taskid=%s", curUsername, currentListId, currentTaskId);
                String request = url + "/tasks/getTask?" + query;
                System.out.println(request);

                if (doGet(request) == 200) {
                    System.out.println("Successfully display task details, do you want to try something else in this task? (Yes/No)");
                    String response = scanner.nextLine().toLowerCase().trim();
                    if (response.equals("yes")) {
                        quitTaskService = false;
                    } else {
                        quitTaskService = true;
                        System.out.println("Coming back to the current ToDoList menu......");
                    }
                } else {
                    System.out.println("Display task details failed for some reasons,Do you want to try something else in this task? (Yes/No)");
                    String response = scanner.nextLine().toLowerCase().trim();
                    if (response.equals("yes")) {
                        quitTaskService = false;
                    } else {
                        quitTaskService = true;
                        System.out.println("Coming back to the current ToDoList menu......");

                    }
                }
            }

            case "4" -> {
                System.out.println("Routing back to the current ToDoList menu......");
                quitTaskService = true;
            }

        }

    }

    private static void singleListService(Scanner scanner) {
        // <currentListId> = <username>_<listId>
        // String currentListName = currentListId.split("_")[1];
        System.out.println("--------------------------------------------------------");
        System.out.println("In ToDoList " + currentListId +  " you can:");
        System.out.println("1. Check whether someone has access of this ToDoList");
        System.out.println("2. Grant access of this ToDoList to someone");
        System.out.println("3. Remove someone's access of this ToDoList");
        System.out.println("4. Get all users of this ToDoList");
        System.out.println("5. Get all tasks of this ToDoList");
        System.out.println("6. Add a task into this ToDoList");
        System.out.println("7. Delete a task from this ToDoList");
        System.out.println("8. Delete all tasks from this ToDoList");
        System.out.println("9. Enter into a task");
        System.out.println("10. Back to ToDoList main menu");
        System.out.println("11. Logout");

        System.out.println("--------------------------------------------------------");
        System.out.println("Please enter the number of the action you want:");
        String userRespond;
        userRespond = scanner.nextLine().toLowerCase().trim();

        switch (userRespond) {
            case "1" -> {
                // code for checkAccess
                System.out.println("To check access of someone, please enter his/her username:");
                String username = scanner.nextLine().trim();
                String request = url + "/lists/" + username + "/checkAccess/" + currentListId;
                System.out.println(request);
                if (doGet(request) == 200) {
                    System.out.println("Successfully returned checkAccess, do you want to try something else in this list? (Yes/No)");
                    String response = scanner.nextLine().toLowerCase().trim();
                    if (response.equals("yes")) {
                        quitSingleListService = false;
                    } else {
                        quitSingleListService = true;
                        System.out.println("Coming back to the ToDoLists main menu......");

                    }

                } else {
                    System.out.println("CheckAccess failed for some reasons,Do you want to try something else in this list? (Yes/No)");
                    String response = scanner.nextLine().toLowerCase().trim();
                    if (response.equals("yes")) {
                        quitSingleListService = false;
                    } else {
                        quitSingleListService = true;
                        System.out.println("Coming back to the ToDoLists main menu......");

                    }
                }
            }
            case "2" -> {
                // code for grantAccess
                System.out.println("To grant access to someone, please enter his/her username:");
                String username = scanner.nextLine().trim();
                String request = url + "/lists/" + username + "/grantAccess/" + currentListId;
                System.out.println(request);
                if (doGet(request) == 200) {
                    System.out.println("Successfully returned grantAccess, do you want to try something else in this list? (Yes/No)");
                    String response = scanner.nextLine().toLowerCase().trim();
                    if (response.equals("yes")) {
                        quitSingleListService = false;
                    } else {
                        quitSingleListService = true;
                        System.out.println("Coming back to the ToDoLists main menu......");

                    }

                } else {
                    System.out.println("GrantAccess failed for some reasons,Do you want to try something else in this list? (Yes/No)");
                    String response = scanner.nextLine().toLowerCase().trim();
                    if (response.equals("yes")) {
                        quitSingleListService = false;
                    } else {
                        quitSingleListService = true;
                        System.out.println("Coming back to the ToDoLists main menu......");

                    }
                }
            }
            case "3" -> {
                // code for removeAccess
                System.out.println("To remove access to someone, please enter his/her username:");
                String username = scanner.nextLine().trim();
                String request = url + "/lists/" + username + "/removeAccess/" + currentListId;
                System.out.println(request);
                if (doGet(request) == 200) {
                    if (username.equals(curUsername)) {
                        System.out.println("You removed yourself from current list, routing back to main menu......");
                        quitSingleListService = true;
                    } else {
                        System.out.println("Successfully returned removeAccess, do you want to try something else in this list? (Yes/No)");
                        String response = scanner.nextLine().toLowerCase().trim();
                        if (response.equals("yes")) {
                            quitSingleListService = false;
                        } else {
                            quitSingleListService = true;
                            System.out.println("Coming back to the ToDoLists main menu......");

                        }
                    }

                } else {
                    System.out.println("RemoveAccess failed for some reasons,Do you want to try something else in this list? (Yes/No)");
                    String response = scanner.nextLine().toLowerCase().trim();
                    if (response.equals("yes")) {
                        quitSingleListService = false;
                    } else {
                        quitSingleListService = true;
                        System.out.println("Coming back to the ToDoLists main menu......");
                    }
                }
            }
            case "4" -> {
                // code for getAllUsers
                System.out.println("Returning all users of ToDoList " + currentListId);
                String request = url + "/lists/getAllUsers/"  + currentListId;
                System.out.println(request);
                if (doGet(request) == 200) {
                    System.out.println("Successfully returned all users of this ToDoList, do you want to try something else in this list? (Yes/No)");
                    String response = scanner.nextLine().toLowerCase().trim();
                    if (response.equals("yes")) {
                        quitSingleListService = false;
                    } else {
                        quitSingleListService = true;
                        System.out.println("Coming back to the ToDoLists main menu......");

                    }

                } else {
                    System.out.println("Get all users failed for some reasons,Do you want to try something else in this list? (Yes/No)");
                    String response = scanner.nextLine().toLowerCase().trim();
                    if (response.equals("yes")) {
                        quitSingleListService = false;
                    } else {
                        quitSingleListService = true;
                        System.out.println("Coming back to the ToDoLists main menu......");
                    }
                }
            }
            case "5" -> {
                // code for displayAllTasks
                System.out.println("Returning all tasks of ToDoList " + currentListId);
                String query = String.format("username=%s&listid=%s", curUsername, currentListId);
                String request = url + "/tasks/displayAllTasksNames?" + query;
                System.out.println(request);
                if (doGet(request) == 200) {
                    System.out.println("Successfully displayed all tasks of this ToDoList, do you want to try something else in this list? (Yes/No)");
                    String response = scanner.nextLine().toLowerCase().trim();
                    if (response.equals("yes")) {
                        quitSingleListService = false;
                    } else {
                        quitSingleListService = true;
                        System.out.println("Coming back to the ToDoLists main menu......");

                    }

                } else {
                    System.out.println("Display all tasks failed for some reasons,Do you want to try something else in this list? (Yes/No)");
                    String response = scanner.nextLine().toLowerCase().trim();
                    if (response.equals("yes")) {
                        quitSingleListService = false;
                    } else {
                        quitSingleListService = true;
                        System.out.println("Coming back to the ToDoLists main menu......");
                    }
                }
            }
            case "6" -> {
                // code for add a task
                System.out.println("To add a task of ToDoList " + currentListId + ", please enter the task name:");
                String taskName = scanner.nextLine().trim();
                System.out.println("Please also enter the task description:");
                String taskDescription = scanner.nextLine().trim();
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("taskname", taskName);
                jsonObject.put("content", taskDescription);
                jsonObject.put("listid", currentListId);
                jsonObject.put("username", curUsername);
                // String query = String.format("username=%s&listId=%s&taskName=%s&taskDescription=%s", curUsername, currentListId, taskName, taskDescription);
                String request = url + "/tasks/addTask";
                System.out.println(request);
                System.out.println(jsonObject.toString());

                if (doPost2(request, jsonObject.toString()) == 200) {
                    System.out.println("Successfully add a task, do you want totry something else in this list? (Yes/No)");
                    String response = scanner.nextLine().toLowerCase().trim();
                    if (response.equals("yes")) {
                        quitSingleListService = false;
                    } else {
                        quitSingleListService = true;
                        System.out.println("Coming back to the ToDoLists main menu......");
                    }
                } else {
                    System.out.println("Add this task failed for some reasons, Do you want to try something else in this list? (Yes/No)");
                    String response = scanner.nextLine().toLowerCase().trim();
                    if (response.equals("yes")) {
                        quitSingleListService = false;
                    } else {
                        quitSingleListService = true;
                        System.out.println("Coming back to the ToDoLists main menu......");
                    }
                }
            }

            case "7" -> {
                // code for delete a task
                System.out.println("To delete a task from ToDoList " + currentListId + ", please enter the task id:");
                String taskId = scanner.nextLine().trim();
                String query = String.format("username=%s&listid=%s&taskid=%s", curUsername, currentListId, taskId);
                String request = url + "/tasks/deleteTask?" + query;
                System.out.println(request);
                if (doGet(request) == 200) {
                    System.out.println("Successfully delete the task from this ToDoList, do you want to try something else in this list? (Yes/No)");
                    String response = scanner.nextLine().toLowerCase().trim();
                    if (response.equals("yes")) {
                        quitSingleListService = false;
                    } else {
                        quitSingleListService = true;
                        System.out.println("Coming back to the ToDoLists main menu......");

                    }

                } else {
                    System.out.println("Deleting this task failed for some reasons,Do you want to try something else in this list? (Yes/No)");
                    String response = scanner.nextLine().toLowerCase().trim();
                    if (response.equals("yes")) {
                        quitSingleListService = false;
                    } else {
                        quitSingleListService = true;
                        System.out.println("Coming back to the ToDoLists main menu......");
                    }
                }

            }
            case "8" -> {
                // code for deleteAllTasks
                System.out.println("Deleting all tasks from ToDoList " + currentListId);
                String query = String.format("username=%s&listid=%s", curUsername, currentListId);
                String request = url + "/tasks/deleteAllTasks?" + query;
                System.out.println(request);
                if (doGet(request) == 200) {
                    System.out.println("Successfully delete all tasks from this ToDoList, do you want to try something else in this list? (Yes/No)");
                    String response = scanner.nextLine().toLowerCase().trim();
                    if (response.equals("yes")) {
                        quitSingleListService = false;
                    } else {
                        quitSingleListService = true;
                        System.out.println("Coming back to the ToDoLists main menu......");

                    }

                } else {
                    System.out.println("Deleting all tasks failed for some reasons,Do you want to try something else in this list? (Yes/No)");
                    String response = scanner.nextLine().toLowerCase().trim();
                    if (response.equals("yes")) {
                        quitSingleListService = false;
                    } else {
                        quitSingleListService = true;
                        System.out.println("Coming back to the ToDoLists main menu......");
                    }
                }
            }

            case "9" -> {
                //code to enter a specific task
                System.out.println("Please input the task id you want to enter:");
                String taskid = scanner.nextLine().trim();
                String query = String.format("username=%s&listid=%s&taskid=%s", curUsername, currentListId, taskid);
                String request = url + "/tasks/getTask?" + query;
                if (doGet(request) == 200) {
                    System.out.println("Your input taskId is valid, routing to the task menu");
                    currentTaskId = taskid;
                } else {
                    System.out.println("Your input taskId is invalid, routing back to ToDoList " + currentListId + " menu");
                    quitSingleListService = false;
                    currentTaskId = null;
                }

            }

            case "10" -> {
                //code to come back to ToDoList main menu
                System.out.println("Routing back to the ToDoList main menu......");
                quitSingleListService = true;
            }

            case "11" -> {
                //code to enter a specific task
                System.out.println("Logging out......");
                quitSingleListService = true;
                loggedIn = false;
            }
        }


    }

    private static void listsService(Scanner scanner) {
        System.out.println("--------------------------------------------------------");
        System.out.println("Here in ToDoList main menu, you can:");
        System.out.println("1. Get all your ToDoLists");
        System.out.println("2. Add a ToDoList");
        System.out.println("3. Delete a ToDoList");
        System.out.println("4. Delete all ToDoLists");
        System.out.println("5. Enter into a ToDoList");
        System.out.println("6. Logout");
        System.out.println("--------------------------------------------------------");
        System.out.println("Please enter the number of the action you want:");
        String userRespond;
        userRespond = scanner.nextLine().toLowerCase().trim();

        switch (userRespond) {
            case "1" -> {
                // code for getAllLists
                System.out.println("Displaying all your ToDoLists ......");
                String query = String.format("username=%s", curUsername);
                String request = url + "/lists/getAllLists?" + query;
                System.out.println(request);
                if (doGet(request) == 200) {
                    System.out.println("Successfully display all your ToDoLists, do you want to come back to the main menu? (Yes/No)");
                    String response = scanner.nextLine().toLowerCase().trim();
                    if (response.equals("yes")) {
                        quitListsService = false;
                    } else {
                        quitListsService = true;
                        System.out.println("Logging out......");
                    }

                } else {
                    System.out.println("Display all your ToDoLists failed for some reasons,Do you want to come back to the main menu? (Yes/No)");
                    String response = scanner.nextLine().toLowerCase().trim();
                    if (response.equals("yes")) {
                        quitListsService = false;
                    } else {
                        quitListsService = true;
                        System.out.println("Logging out......");
                    }
                }
            }

            case "2" -> {
                // code for addList
                System.out.println("To add a ToDoList, please enter the ToDoList name:");
                String listName = scanner.nextLine().trim();
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("username", curUsername);
                jsonObject.put("listname", listName);
                // String query = String.format("username=%s&listname=%s", curUsername, listName);
                String request = url + "/lists/addList";
                System.out.println(request);
                System.out.println(jsonObject.toString());
                if (doPost2(request, jsonObject.toString()) == 200) {
                    System.out.println("Successfully add a ToDoList, do you want to come back to the main menu? (Yes/No)");
                    String response = scanner.nextLine().toLowerCase().trim();
                    if (response.equals("yes")) {
                        quitListsService = false;
                    } else {
                        quitListsService = true;
                        System.out.println("Logging out......");
                    }

                } else {
                    System.out.println("Adding a ToDoList failed for some reasons,Do you want to come back to the main menu? (Yes/No)");
                    String response = scanner.nextLine().toLowerCase().trim();
                    if (response.equals("yes")) {
                        quitListsService = false;
                    } else {
                        quitListsService = true;
                        System.out.println("Logging out......");
                    }
                }
            }

            case "3" -> {
                // code for deleteList
                System.out.println("To delete a ToDoList, please enter the ToDoList id:");
                String listId = scanner.nextLine().trim();
                String query = String.format("username=%s&listid=%s", curUsername, listId);
                String request = url + "/lists/deleteList?" + query;
                System.out.println(request);
                if (doGet(request) == 200) {
                    System.out.println("Successfully delete a ToDoList, do you want to come back to the main menu? (Yes/No)");
                    String response = scanner.nextLine().toLowerCase().trim();
                    if (response.equals("yes")) {
                        quitListsService = false;
                    } else {
                        quitListsService = true;
                        System.out.println("Logging out......");
                    }

                } else {
                    System.out.println("Deleting a ToDoList failed for some reasons,Do you want to come back to the main menu? (Yes/No)");
                    String response = scanner.nextLine().toLowerCase().trim();
                    if (response.equals("yes")) {
                        quitListsService = false;
                    } else {
                        quitListsService = true;
                        System.out.println("Logging out......");
                    }
                }

            }
            case "4" -> {
                // code for deleteAllLists
                System.out.println("Deleting all your ToDoLists......");
                String query = String.format("username=%s", curUsername);
                String request = url + "/lists/deleteAllLists?" + query;
                System.out.println(request);
                if (doGet(request) == 200) {
                    System.out.println("Successfully delete all your ToDoLists, do you want to come back to the main menu? (Yes/No)");
                    String response = scanner.nextLine().toLowerCase().trim();
                    if (response.equals("yes")) {
                        quitListsService = false;
                    } else {
                        quitListsService = true;
                        System.out.println("Logging out......");
                    }

                } else {
                    System.out.println("Deleting all your ToDoLists failed for some reasons,Do you want to come back to the main menu? (Yes/No");
                    String response = scanner.nextLine().toLowerCase().trim();
                    if (response.equals("yes")) {
                        quitListsService = false;
                    } else {
                        quitListsService = true;
                        System.out.println("Logging out......");
                    }
                }
            }
            case "5" -> {
                // code for enterList
                System.out.println("Please enter the listid you want to enter:");
                String listid = scanner.nextLine().trim();
                String request = url + "/lists/" + curUsername + "/checkAccess/" + listid;
                System.out.println(request);
                if (doGet(request) == 200) {
                    currentListId = listid;
                    System.out.println("You entered into list: " + currentListId);
                } else {
                    System.out.println("You don't have the access to " + listid + ", or the listid you input is wrong.");
                    quitListsService = false;
                    System.out.println("Routing back to the main menu");
                }
            }
            case "6" -> {
                // code for logout
                System.out.println("Logging out");
                loggedIn = false;
            }
        }

    }

    public static void userService(Scanner scanner) {
        // read, eval, print, loop
        System.out.println("--------------------------------------------------------");
        System.out.println("Here you can:");
        System.out.println("1. Login");
        System.out.println("2. Signup");
        System.out.println("3. Change Password");
        System.out.println("--------------------------------------------------------");
        System.out.println("Please enter(the number) which operation do you want: ");

        String userRespond;
        try {
            userRespond = scanner.nextLine().toLowerCase().trim();
            switch (userRespond) {
                case "1" -> {
                    //code for login
                    String request = url + "/users/logIn?" + loginInput(scanner);
                    System.out.println(request);
                    if (doGet(request) == 200) {
                        loggedIn = true;

                    } else {
                        System.out.println("User and Password are not matching!");
                        curUsername = null;
                    }
                }
                case "2" -> {
                    //code for signup
                    String request = url + "/users/addUser" ;
                    System.out.println(request);
                    String payload = signUp(scanner);
                    System.out.println(payload);
                    if (doPost2(request, payload) == 200) {
                        loggedIn = true;
                        System.out.println(
                                String.format("Successfully registered and logged in as %s!", curUsername));
                    } else {
                        curUsername = null;
                    }
                }
                case "3" -> {
                    //code for change password
                    String request = url + "/users/changePassword";
                    System.out.println(request);
                    if (doPut(request, changePassword(scanner)) == 200) {
                        System.out.println("Successfully change your password, routing back to the login menu......");
                    } else {
                        System.out.println("Failed to change your password, routing back to the login menu......");

                    }

                }
            }
        } catch (IOException e) {
            System.out.println("Unable to get commond..." + e.getMessage());

        }
    }
    private static String loginInput(Scanner inputReader) throws IOException {
        String username;
        String password;

        System.out.println("Enter username: ");
        username = inputReader.nextLine().trim();
        System.out.println("Enter password: ");
        password = inputReader.nextLine().trim();
        curUsername = username;

        return String.format("username=%s&password=%s", username, password);
    }

    private static String signUp (Scanner inputReader) throws IOException {
        String username;
        String password;

        System.out.print("Enter username: ");
        username = inputReader.nextLine().trim();
        System.out.print("Enter password: ");
        password = inputReader.next().trim();
        curUsername = username;
        JSONObject user = new JSONObject();
        user.put("username", username);
        user.put("password", password);

        return user.toString();
    }

    private static String changePassword(Scanner inputReader) {
        String username;
        String oldPassword;
        String newPassword;

        System.out.print("Enter username: ");
        username = inputReader.nextLine().trim();
        System.out.print("Enter the old password: ");
        oldPassword = inputReader.next().trim();
        System.out.print("Enter the new password: ");
        newPassword = inputReader.next().trim();
        JSONObject user = new JSONObject();
        user.put("username", username);
        user.put("oldPassword", oldPassword);
        user.put("newPassword", newPassword);
        return user.toString();
//        return String.format("username=%s&oldPassword=%s&newPassword=%s");

    }

    private static boolean isValidJson(String json) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            mapper.readTree(json);
        } catch (IOException e) {
            System.out.println("Invalid JSON object or array");
            return false;
        }
        System.out.println("Valid JSON object or array");
        return true;
    }
}
