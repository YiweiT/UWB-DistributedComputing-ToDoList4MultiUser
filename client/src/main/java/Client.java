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

            ObjectMapper mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);

            String json = mapper.writeValueAsString(response);
            System.out.println(json);
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

    private static int doPut(String link, String payload) {
        HttpURLConnection connection = null;
        try {
            // create connection
            URL url = new URL(link);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("PUT");
            connection.setRequestProperty("Content-Type",
                    "application/json; charset=UTF-8");
            connection.setRequestProperty("Accept", "application/json");
            connection.setDoOutput(true);
            OutputStream os = connection.getOutputStream();
//            byte[] input = payload.getBytes("utf-8");
            os.write(payload.getBytes(StandardCharsets.UTF_8));

            // read the response from input stream
            BufferedReader br = new BufferedReader(
                    new InputStreamReader(connection.getInputStream(), "utf-8"));
            StringBuilder response = new StringBuilder();
            String responseLine = null;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }
            System.out.println(response.toString());
            connection.disconnect();
            return connection.getResponseCode();

        } catch (MalformedURLException e) {
            e.printStackTrace();
            System.out.println("Malformed URL Exception: " + e.getMessage());
            return -1;

        } catch (IOException e) {
            System.out.println("IO Exceptions: " + e.getMessage());
            return -1;
        }
    }

    private static int doPost2(String link, String payload) {
        HttpURLConnection connection = null;
        try {
            // create connection
            URL url = new URL(link);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type",
                    "application/json; charset=UTF-8");
            connection.setRequestProperty("Accept", "application/json");
            connection.setDoOutput(true);
            OutputStream os = connection.getOutputStream();
//            byte[] input = payload.getBytes("utf-8");
            os.write(payload.getBytes(StandardCharsets.UTF_8));

            // read the response from input stream
            BufferedReader br = new BufferedReader(
                    new InputStreamReader(connection.getInputStream(), "utf-8"));
            StringBuilder response = new StringBuilder();
            String responseLine = null;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }
            System.out.println(response.toString());
            connection.disconnect();
            return connection.getResponseCode();

        } catch (MalformedURLException e) {
            e.printStackTrace();
            System.out.println("Malformed URL Exception: " + e.getMessage());
            return -1;
        } catch (IOException e) {
            System.out.println("IO Exceptions: " + e.getMessage());
            return -1;
        }
    }



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
        String currentTaskName = currentTaskId.split("_")[2];

        System.out.println("--------------------------------------------------------");
        System.out.println("In task " + currentTaskName +  " you can:");
        System.out.println("1. Update task content");
        System.out.println("2. Update status");
        System.out.println("3. Update task name");
        System.out.println("4. Display details of a task");
        System.out.println("--------------------------------------------------------");

        String userRespond;
        userRespond = scanner.nextLine().toLowerCase().trim();

        switch (userRespond) {
            case "1" -> {
                // code to update task content
            }
            case "2" -> {
                // code to update task status
            }
            case "3" -> {
                // code to update task name
            }
            case "4" -> {
                // code to display a task
            }
            default -> {
                System.out.println("Code is not added yet, remove it after complete above cases!");
                quitTaskService = true;
            }

        }

    }

    private static void singleListService(Scanner scanner) {
        // <currentListId> = <username>_<listId>
        String currentListName = currentListId.split("_")[1];
        System.out.println("--------------------------------------------------------");
        System.out.println("In ToDoList " + currentListName +  " you can:");
        System.out.println("1. Check whether someone has access of this ToDoList");
        System.out.println("2. Grant access of this ToDoList to someone");
        System.out.println("3. Remove someone's access of this ToDoList");
        System.out.println("4. Get all users of this ToDoList");
        System.out.println("5. Get all tasks of this ToDoList");
        System.out.println("6. Add a task into this ToDoList");
        System.out.println("7. Delete a task from this ToDoList");
        System.out.println("8. Delete all tasks from this ToDoList");
        System.out.println("9. Enter into a task");
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
                    System.out.println("Successfully returned removeAccess, do you want to try something else in this list? (Yes/No)");
                    String response = scanner.nextLine().toLowerCase().trim();
                    if (response.equals("yes")) {
                        quitSingleListService = false;
                    } else {
                        quitSingleListService = true;
                        System.out.println("Coming back to the ToDoLists main menu......");

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
                String query = String.format("username=%s&listId=%s", curUsername, currentListId);
                String request = url + "/displayAllTasksNames?" + query;
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
                String query = String.format("username=%s&listId=%s&taskName=%s&taskDescription=%s", curUsername, currentListId, taskName, taskDescription);
                String request = url + "/addTask?" + query;
                System.out.println(request);
                if (doGet(request) == 200) {
                    System.out.println("Successfully added the task to this ToDoList, do you want to try something else in this list? (Yes/No)");
                    String response = scanner.nextLine().toLowerCase().trim();
                    if (response.equals("yes")) {
                        quitSingleListService = false;
                    } else {
                        quitSingleListService = true;
                        System.out.println("Coming back to the ToDoLists main menu......");

                    }

                } else {
                    System.out.println("Adding this task failed for some reasons,Do you want to try something else in this list? (Yes/No)");
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
                System.out.println("To delete a task from ToDoList " + currentListId + ", please enter the task name:");
                String taskName = scanner.nextLine().trim();
                String taskId = curUsername + "_" + currentListName + "_" + taskName;
                String query = String.format("username=%s&listId=%s&taskId=%s", curUsername, currentListId, taskId);
                String request = url + "/deleteTask?" + query;
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
                String query = String.format("username=%s&listId=%s", curUsername, currentListId);
                String request = url + "/deleteAllTasks?" + query;
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
                System.out.println("Please enter the task name you want to enter:");
                String taskName = scanner.nextLine().trim();
                currentTaskId = curUsername + "_" + currentListName + "_" + taskName;
                System.out.println("You entered into list: " + taskName);

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
                String query = String.format("username=%s&listname=%s", curUsername, listName);
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
                String listid = scanner.nextLine().trim();
//                String listId = curUsername + "_" + listName;
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("username", curUsername);
                jsonObject.put("listid", listid);
//                String query = String.format("username=%s&listid=%s", curUsername, listId);
                String request = url + "/lists/deleteList";
                System.out.println(request);
                System.out.println(jsonObject.toString());
                if (doPut(request, jsonObject.toString()) == 200) {
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
                System.out.println("Please enter the list name you want to enter:");
                String listName = scanner.nextLine().trim();
                currentListId = curUsername + "_" + listName;
                System.out.println("You entered into list: " + listName);
            }
        }

    }

    public static void userService(Scanner scanner) {
        // read, eval, print, loop
        System.out.print("Existing user (-logIn) or new user (-signUp)? ");
        String userRespond;
        try {
            userRespond = scanner.nextLine().toLowerCase().trim();
            if (userRespond.equals("-login")) {
                String request = url + "/users/logIn?" + loginInput(scanner);
                System.out.println(request);
                if (doGet(request) == 200) {
                    loggedIn = true;

                } else {
                    curUsername = null;
                }
            } else if (userRespond.equals("-signup")) {
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

        } catch (IOException e) {
            System.out.println("Unable to get commond..." + e.getMessage());

        }
    }
    private static String loginInput(Scanner inputReader) throws IOException {
        String username;
        String password;

        System.out.print("Enter username: ");
        username = inputReader.nextLine().trim();
        System.out.print("Enter password: ");
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
}
