import java.io.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Scanner;

import com.fasterxml.jackson.databind.ObjectMapper;
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


    public static void main(String[] args) {
        System.out.println(ConsoleColors.PURPLE_BOLD_BRIGHT + "Welcome to to-do list." + ConsoleColors.RESET);
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

//            System.out.println(response.body());
            if (isValidJson(String.valueOf(response.body()))) {
                ObjectMapper mapper = new ObjectMapper();
                if (url.contains("getAllLists")) {
                    ToDoList[] toDoLists = mapper.readValue(response.body(), ToDoList[].class);
                    buildListTable(toDoLists);
                } else if (url.contains("displayAllTasksNames")) {
                    Task[] tasks = mapper.readValue(response.body(), Task[].class);
                    buildTaskTable(tasks);
                } else {
                    System.out.println(mapper.writerWithDefaultPrettyPrinter()
                            .writeValueAsString(mapper.readTree(response.body())));
                }


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
//            System.out.println(response.body());
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
//            System.out.println(response.body());
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


    private static void taskService(Scanner scanner) {
        //String currentTaskName = currentTaskId.split("_")[2];

        System.out.println("--------------------------------------------------------");
        System.out.println(ConsoleColors.YELLOW_UNDERLINED + "In task " + currentTaskId +  " you can:" + ConsoleColors.RESET);
        System.out.println(ConsoleColors.BLUE_BOLD_BRIGHT + "1. Update task content" + ConsoleColors.RESET);
        System.out.println(ConsoleColors.BLUE_BOLD_BRIGHT +"2. Update status"+ ConsoleColors.RESET);
        System.out.println(ConsoleColors.BLUE_BOLD_BRIGHT +"3. Display details of a task"+ ConsoleColors.RESET);
        System.out.println(ConsoleColors.YELLOW_BOLD_BRIGHT +"4. Come back to the ToDoList"+ ConsoleColors.RESET);
        System.out.println("--------------------------------------------------------");
        System.out.println(ConsoleColors.RED_BOLD_BRIGHT + "Please enter(the number) which operation do you want: " + ConsoleColors.RESET);

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
                    System.out.println(ConsoleColors.GREEN_BOLD_BRIGHT + "Successfully update task content, do you want to try something else in this task? (Yes/No)" + ConsoleColors.RESET);
                    String response = scanner.nextLine().toLowerCase().trim();
                    if (response.equals("yes")) {
                        quitTaskService = false;
                    } else {
                        quitTaskService = true;
                        System.out.println(ConsoleColors.YELLOW_BOLD_BRIGHT + "Coming back to the current ToDoList menu......" + ConsoleColors.RESET);
                    }
                } else {
                    System.out.println(ConsoleColors.RED_BOLD_BRIGHT + "Update task content failed for some reasons,Do you want to try something else in this task? (Yes/No)" + ConsoleColors.RESET);
                    String response = scanner.nextLine().toLowerCase().trim();
                    if (response.equals("yes")) {
                        quitTaskService = false;
                    } else {
                        quitTaskService = true;
                        System.out.println(ConsoleColors.YELLOW_BOLD_BRIGHT + "Coming back to the current ToDoList menu......" + ConsoleColors.RESET);
                    }
                }
            }
            case "2" -> {
                // code to update task status
                System.out.println(ConsoleColors.RED_BOLD_BRIGHT + "To update task status of taskid: "+ currentTaskId +", please enter task status:" + ConsoleColors.RESET);
                System.out.println(ConsoleColors.YELLOW_BOLD_BRIGHT + "Note: Task type can only be Completed, Not Started or In-Progress" + ConsoleColors.RESET);
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
                    System.out.println(ConsoleColors.GREEN_BOLD_BRIGHT + "Successfully update task status, do you want to try something else in this task? (Yes/No)" + ConsoleColors.RESET);
                    String response = scanner.nextLine().toLowerCase().trim();
                    if (response.equals("yes")) {
                        quitTaskService = false;
                    } else {
                        quitTaskService = true;
                        System.out.println(ConsoleColors.YELLOW_BOLD_BRIGHT + "Coming back to the current ToDoList menu......" + ConsoleColors.RESET);
                    }
                } else {
                    System.out.println(ConsoleColors.RED_BOLD_BRIGHT + "Update task status failed for some reasons,Do you want to try something else in this task? (Yes/No)" + ConsoleColors.RESET);
                    String response = scanner.nextLine().toLowerCase().trim();
                    if (response.equals("yes")) {
                        quitTaskService = false;
                    } else {
                        quitTaskService = true;
                        System.out.println(ConsoleColors.YELLOW_BOLD_BRIGHT + "Coming back to the current ToDoList menu......" + ConsoleColors.RESET);
                    }
                }
            }
            case "3" -> {
                // code to display a task
                System.out.println(ConsoleColors.YELLOW_BOLD_BRIGHT + "Displaying current taskid: " + currentTaskId + ConsoleColors.RESET) ;
                String query = String.format("username=%s&listid=%s&taskid=%s", curUsername, currentListId, currentTaskId);
                String request = url + "/tasks/getTask?" + query;
                System.out.println(request);

                if (doGet(request) == 200) {
                    System.out.println(ConsoleColors.GREEN_BOLD_BRIGHT + "Successfully display task details, do you want to try something else in this task? (Yes/No)" + ConsoleColors.RESET);
                    String response = scanner.nextLine().toLowerCase().trim();
                    if (response.equals("yes")) {
                        quitTaskService = false;
                    } else {
                        quitTaskService = true;
                        System.out.println(ConsoleColors.YELLOW_BOLD_BRIGHT + "Coming back to the current ToDoList menu......" + ConsoleColors.RESET);
                    }
                } else {
                    System.out.println(ConsoleColors.RED_BOLD_BRIGHT + "Display task details failed for some reasons,Do you want to try something else in this task? (Yes/No)" + ConsoleColors.RESET);
                    String response = scanner.nextLine().toLowerCase().trim();
                    if (response.equals("yes")) {
                        quitTaskService = false;
                    } else {
                        quitTaskService = true;
                        System.out.println(ConsoleColors.YELLOW_BOLD_BRIGHT + "Coming back to the current ToDoList menu......" + ConsoleColors.RESET);

                    }
                }
            }

            case "4" -> {
                System.out.println(ConsoleColors.YELLOW_BOLD_BRIGHT + "Routing back to the current ToDoList menu......" + ConsoleColors.RESET);
                quitTaskService = true;
            }

        }

    }

    private static void singleListService(Scanner scanner) {
        // <currentListId> = <username>_<listId>
        // String currentListName = currentListId.split("_")[1];
        System.out.println("--------------------------------------------------------");
        System.out.println(ConsoleColors.YELLOW_UNDERLINED + "In ToDoList " + currentListId +  " you can:" + ConsoleColors.RESET);
        System.out.println(ConsoleColors.BLUE_BOLD_BRIGHT + "1. Check whether someone has access of this ToDoList" + ConsoleColors.RESET);
        System.out.println(ConsoleColors.BLUE_BOLD_BRIGHT + "2. Grant access of this ToDoList to someone" + ConsoleColors.RESET);
        System.out.println(ConsoleColors.RED_BOLD_BRIGHT + "3. Remove someone's access of this ToDoList" + ConsoleColors.RESET);
        System.out.println(ConsoleColors.BLUE_BOLD_BRIGHT + "4. Get all users of this ToDoList" + ConsoleColors.RESET);
        System.out.println(ConsoleColors.BLUE_BOLD_BRIGHT + "5. Get all tasks of this ToDoList" + ConsoleColors.RESET);
        System.out.println(ConsoleColors.BLUE_BOLD_BRIGHT + "6. Add a task into this ToDoList" + ConsoleColors.RESET);
        System.out.println(ConsoleColors.RED_BOLD_BRIGHT + "7. Delete a task from this ToDoList" + ConsoleColors.RESET);
        System.out.println(ConsoleColors.RED_BOLD_BRIGHT + "8. Delete all tasks from this ToDoList" + ConsoleColors.RESET);
        System.out.println(ConsoleColors.GREEN_BOLD_BRIGHT + "9. Enter into a task" + ConsoleColors.RESET);
        System.out.println(ConsoleColors.YELLOW_BOLD_BRIGHT + "10. Back to ToDoList main menu" + ConsoleColors.RESET);
        System.out.println(ConsoleColors.YELLOW_BOLD_BRIGHT + "11. Logout" + ConsoleColors.RESET);

        System.out.println("--------------------------------------------------------");
        System.out.println(ConsoleColors.RED_BOLD_BRIGHT + "Please enter the number of the action you want:" + ConsoleColors.RESET);
        String userRespond;
        userRespond = scanner.nextLine().toLowerCase().trim();

        switch (userRespond) {
            case "1" -> {
                // code for checkAccess
                System.out.println(ConsoleColors.RED_BOLD_BRIGHT + "To check access of someone, please enter his/her username:" + ConsoleColors.RESET);
                String username = scanner.nextLine().trim();
                String request = url + "/lists/" + username + "/checkAccess/" + currentListId;
                System.out.println(request);
                if (doGet(request) == 200) {
                    System.out.println(ConsoleColors.GREEN_BOLD_BRIGHT + "Successfully returned checkAccess, do you want to try something else in this list? (Yes/No)"+ ConsoleColors.RESET);
                    String response = scanner.nextLine().toLowerCase().trim();
                    if (response.equals("yes")) {
                        quitSingleListService = false;
                    } else {
                        quitSingleListService = true;
                        System.out.println(ConsoleColors.YELLOW_BOLD_BRIGHT + "Coming back to the ToDoLists main menu......" + ConsoleColors.RESET);

                    }

                } else {
                    System.out.println(ConsoleColors.RED_BOLD_BRIGHT + "CheckAccess failed for some reasons,Do you want to try something else in this list? (Yes/No)" + ConsoleColors.RESET);
                    String response = scanner.nextLine().toLowerCase().trim();
                    if (response.equals("yes")) {
                        quitSingleListService = false;
                    } else {
                        quitSingleListService = true;
                        System.out.println(ConsoleColors.YELLOW_BOLD_BRIGHT + "Coming back to the ToDoLists main menu......"+ ConsoleColors.RESET);

                    }
                }
            }
            case "2" -> {
                // code for grantAccess
                System.out.println(ConsoleColors.RED_BOLD_BRIGHT + "To grant access to someone, please enter his/her username:" + ConsoleColors.RESET);
                String username = scanner.nextLine().trim();
                String request = url + "/lists/" + username + "/grantAccess/" + currentListId;
                System.out.println(request);
                if (doGet(request) == 200) {
                    System.out.println(ConsoleColors.GREEN_BOLD_BRIGHT + "Successfully returned grantAccess, do you want to try something else in this list? (Yes/No)" + ConsoleColors.RESET);
                    String response = scanner.nextLine().toLowerCase().trim();
                    if (response.equals("yes")) {
                        quitSingleListService = false;
                    } else {
                        quitSingleListService = true;
                        System.out.println(ConsoleColors.YELLOW_BOLD_BRIGHT + "Coming back to the ToDoLists main menu......" + ConsoleColors.RESET);

                    }

                } else {
                    System.out.println(ConsoleColors.RED_BOLD_BRIGHT + "GrantAccess failed for some reasons,Do you want to try something else in this list? (Yes/No)"+ ConsoleColors.RESET);
                    String response = scanner.nextLine().toLowerCase().trim();
                    if (response.equals("yes")) {
                        quitSingleListService = false;
                    } else {
                        quitSingleListService = true;
                        System.out.println(ConsoleColors.YELLOW_BOLD_BRIGHT + "Coming back to the ToDoLists main menu......" + ConsoleColors.RESET);

                    }
                }
            }
            case "3" -> {
                // code for removeAccess
                System.out.println(ConsoleColors.RED_BOLD_BRIGHT + "To remove access to someone, please enter his/her username:" + ConsoleColors.RESET);
                String username = scanner.nextLine().trim();
                String request = url + "/lists/" + username + "/removeAccess/" + currentListId;
                System.out.println(request);
                if (doGet(request) == 200) {
                    if (username.equals(curUsername)) {
                        System.out.println(ConsoleColors.YELLOW_BOLD_BRIGHT + "You removed yourself from current list, routing back to main menu......" + ConsoleColors.RESET);
                        quitSingleListService = true;
                    } else {
                        System.out.println(ConsoleColors.GREEN_BOLD_BRIGHT + "Successfully returned removeAccess, do you want to try something else in this list? (Yes/No)" + ConsoleColors.RESET);
                        String response = scanner.nextLine().toLowerCase().trim();
                        if (response.equals("yes")) {
                            quitSingleListService = false;
                        } else {
                            quitSingleListService = true;
                            System.out.println(ConsoleColors.YELLOW_BOLD_BRIGHT + "Coming back to the ToDoLists main menu......" + ConsoleColors.RESET);

                        }
                    }

                } else {
                    System.out.println(ConsoleColors.RED_BOLD_BRIGHT + "RemoveAccess failed for some reasons, do you want to try something else in this list? (Yes/No)" + ConsoleColors.RESET);
                    String response = scanner.nextLine().toLowerCase().trim();
                    if (response.equals("yes")) {
                        quitSingleListService = false;
                    } else {
                        quitSingleListService = true;
                        System.out.println(ConsoleColors.YELLOW_BOLD_BRIGHT + "Coming back to the ToDoLists main menu......" + ConsoleColors.RESET);
                    }
                }
            }
            case "4" -> {
                // code for getAllUsers
                System.out.println(ConsoleColors.YELLOW_BOLD_BRIGHT + "Returning all users of ToDoList " + currentListId + ConsoleColors.RESET);
                String request = url + "/lists/getAllUsers/"  + currentListId;
                System.out.println(request);
                if (doGet(request) == 200) {
                    System.out.println(ConsoleColors.GREEN_BOLD_BRIGHT + "Successfully returned all users of this ToDoList, do you want to try something else in this list? (Yes/No)" + ConsoleColors.RESET);
                    String response = scanner.nextLine().toLowerCase().trim();
                    if (response.equals("yes")) {
                        quitSingleListService = false;
                    } else {
                        quitSingleListService = true;
                        System.out.println(ConsoleColors.YELLOW_BOLD_BRIGHT + "Coming back to the ToDoLists main menu......" + ConsoleColors.RESET);

                    }

                } else {
                    System.out.println(ConsoleColors.RED_BOLD_BRIGHT + "Get all users failed for some reasons,Do you want to try something else in this list? (Yes/No)" + ConsoleColors.RESET);
                    String response = scanner.nextLine().toLowerCase().trim();
                    if (response.equals("yes")) {
                        quitSingleListService = false;
                    } else {
                        quitSingleListService = true;
                        System.out.println(ConsoleColors.YELLOW_BOLD_BRIGHT + "Coming back to the ToDoLists main menu......" + ConsoleColors.RESET);
                    }
                }
            }
            case "5" -> {
                // code for displayAllTasks
                System.out.println(ConsoleColors.YELLOW_BOLD_BRIGHT + "Returning all tasks of ToDoList " + currentListId + ConsoleColors.RESET) ;
                String query = String.format("username=%s&listid=%s", curUsername, currentListId);
                String request = url + "/tasks/displayAllTasksNames?" + query;
                System.out.println(request);
                if (doGet(request) == 200) {
                    System.out.println(ConsoleColors.GREEN_BOLD_BRIGHT + "Successfully displayed all tasks of this ToDoList, do you want to try something else in this list? (Yes/No)" + ConsoleColors.RESET);
                    String response = scanner.nextLine().toLowerCase().trim();
                    if (response.equals("yes")) {
                        quitSingleListService = false;
                    } else {
                        quitSingleListService = true;
                        System.out.println(ConsoleColors.YELLOW_BOLD_BRIGHT + "Coming back to the ToDoLists main menu......" + ConsoleColors.RESET);

                    }

                } else {
                    System.out.println(ConsoleColors.RED_BOLD_BRIGHT + "Display all tasks failed for some reasons,Do you want to try something else in this list? (Yes/No)" + ConsoleColors.RESET);
                    String response = scanner.nextLine().toLowerCase().trim();
                    if (response.equals("yes")) {
                        quitSingleListService = false;
                    } else {
                        quitSingleListService = true;
                        System.out.println(ConsoleColors.YELLOW_BOLD_BRIGHT + "Coming back to the ToDoLists main menu......" + ConsoleColors.RESET);
                    }
                }
            }
            case "6" -> {
                // code for add a task
                System.out.println(ConsoleColors.RED_BOLD_BRIGHT + "To add a task of ToDoList " + currentListId + ", please enter the task name:" + ConsoleColors.RESET);
                String taskName = scanner.nextLine().trim();
                System.out.println(ConsoleColors.RED_BOLD_BRIGHT + "Please also enter the task description:"+ ConsoleColors.RESET);
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
                    System.out.println(ConsoleColors.GREEN_BOLD_BRIGHT + "Successfully add a task, do you want totry something else in this list? (Yes/No)" + ConsoleColors.RESET);
                    String response = scanner.nextLine().toLowerCase().trim();
                    if (response.equals("yes")) {
                        quitSingleListService = false;
                    } else {
                        quitSingleListService = true;
                        System.out.println(ConsoleColors.YELLOW_BOLD_BRIGHT + "Coming back to the ToDoLists main menu......" + ConsoleColors.RESET);
                    }
                } else {
                    System.out.println(ConsoleColors.RED_BOLD_BRIGHT + "Add this task failed for some reasons, Do you want to try something else in this list? (Yes/No)" + ConsoleColors.RESET);
                    String response = scanner.nextLine().toLowerCase().trim();
                    if (response.equals("yes")) {
                        quitSingleListService = false;
                    } else {
                        quitSingleListService = true;
                        System.out.println(ConsoleColors.YELLOW_BOLD_BRIGHT + "Coming back to the ToDoLists main menu......" + ConsoleColors.RESET);
                    }
                }
            }

            case "7" -> {
                // code for delete a task
                System.out.println(ConsoleColors.RED_BOLD_BRIGHT + "To delete a task from ToDoList " + currentListId + ", please enter the task id:" + ConsoleColors.RESET);
                String taskId = scanner.nextLine().trim();
                String query = String.format("username=%s&listid=%s&taskid=%s", curUsername, currentListId, taskId);
                String request = url + "/tasks/deleteTask?" + query;
                System.out.println(request);
                if (doGet(request) == 200) {
                    System.out.println(ConsoleColors.GREEN_BOLD_BRIGHT + "Successfully delete the task from this ToDoList, do you want to try something else in this list? (Yes/No)" + ConsoleColors.RESET);
                    String response = scanner.nextLine().toLowerCase().trim();
                    if (response.equals("yes")) {
                        quitSingleListService = false;
                    } else {
                        quitSingleListService = true;
                        System.out.println(ConsoleColors.YELLOW_BOLD_BRIGHT + "Coming back to the ToDoLists main menu......" + ConsoleColors.RESET);

                    }

                } else {
                    System.out.println(ConsoleColors.RED_BOLD_BRIGHT + "Deleting this task failed for some reasons,Do you want to try something else in this list? (Yes/No)" + ConsoleColors.RESET);
                    String response = scanner.nextLine().toLowerCase().trim();
                    if (response.equals("yes")) {
                        quitSingleListService = false;
                    } else {
                        quitSingleListService = true;
                        System.out.println(ConsoleColors.YELLOW_BOLD_BRIGHT + "Coming back to the ToDoLists main menu......" + ConsoleColors.RESET);
                    }
                }

            }
            case "8" -> {
                // code for deleteAllTasks
                System.out.println(ConsoleColors.YELLOW_BOLD_BRIGHT + "Deleting all tasks from ToDoList " + currentListId + ConsoleColors.RESET);
                String query = String.format("username=%s&listid=%s", curUsername, currentListId);
                String request = url + "/tasks/deleteAllTasks?" + query;
                System.out.println(request);
                if (doGet(request) == 200) {
                    System.out.println(ConsoleColors.GREEN_BOLD_BRIGHT + "Successfully delete all tasks from this ToDoList, do you want to try something else in this list? (Yes/No)" + ConsoleColors.RESET);
                    String response = scanner.nextLine().toLowerCase().trim();
                    if (response.equals("yes")) {
                        quitSingleListService = false;
                    } else {
                        quitSingleListService = true;
                        System.out.println(ConsoleColors.YELLOW_BOLD_BRIGHT + "Coming back to the ToDoLists main menu......" + ConsoleColors.RESET);

                    }

                } else {
                    System.out.println(ConsoleColors.RED_BOLD_BRIGHT + "Deleting all tasks failed for some reasons,Do you want to try something else in this list? (Yes/No)" + ConsoleColors.RESET);
                    String response = scanner.nextLine().toLowerCase().trim();
                    if (response.equals("yes")) {
                        quitSingleListService = false;
                    } else {
                        quitSingleListService = true;
                        System.out.println(ConsoleColors.YELLOW_BOLD_BRIGHT + "Coming back to the ToDoLists main menu......" + ConsoleColors.RESET);
                    }
                }
            }

            case "9" -> {
                //code to enter a specific task
                System.out.println(ConsoleColors.RED_BOLD_BRIGHT + "Please input the task id you want to enter:" + ConsoleColors.RESET);
                String taskid = scanner.nextLine().trim();
                String query = String.format("username=%s&listid=%s&taskid=%s", curUsername, currentListId, taskid);
                String request = url + "/tasks/getTask?" + query;
                if (doGet(request) == 200) {
                    System.out.println(ConsoleColors.GREEN_BOLD_BRIGHT + "Your input taskId is valid, routing to the task menu" + ConsoleColors.RESET);
                    currentTaskId = taskid;
                } else {
                    System.out.println(ConsoleColors.RED_BOLD_BRIGHT + "Your input taskId is invalid, routing back to ToDoList " + currentListId + " menu" + ConsoleColors.RESET);
                    quitSingleListService = false;
                    currentTaskId = null;
                }

            }

            case "10" -> {
                //code to come back to ToDoList main menu
                System.out.println(ConsoleColors.YELLOW_BOLD_BRIGHT + "Routing back to the ToDoList main menu......" + ConsoleColors.RESET);
                quitSingleListService = true;
            }

            case "11" -> {
                //code to enter a specific task
                System.out.println(ConsoleColors.YELLOW_BOLD_BRIGHT + "Logging out......" + ConsoleColors.RESET);
                quitSingleListService = true;
                loggedIn = false;
            }
        }


    }

    private static void listsService(Scanner scanner) {
        System.out.println("--------------------------------------------------------");
        System.out.println(ConsoleColors.YELLOW_UNDERLINED + "Here in ToDoList main menu, you can:" + ConsoleColors.RESET);
        System.out.println(ConsoleColors.BLUE_BOLD_BRIGHT + "1. Get all your ToDoLists"+ ConsoleColors.RESET);
        System.out.println(ConsoleColors.BLUE_BOLD_BRIGHT +"2. Add a ToDoList"+ ConsoleColors.RESET);
        System.out.println(ConsoleColors.RED_BOLD_BRIGHT +"3. Delete a ToDoList"+ ConsoleColors.RESET);
        System.out.println(ConsoleColors.RED_BOLD_BRIGHT +"4. Delete all ToDoLists"+ ConsoleColors.RESET);
        System.out.println(ConsoleColors.GREEN_BOLD_BRIGHT +"5. Enter into a ToDoList"+ ConsoleColors.RESET);
        System.out.println(ConsoleColors.YELLOW_BOLD_BRIGHT +"6. Logout"+ ConsoleColors.RESET);
        System.out.println("--------------------------------------------------------");
        System.out.println(ConsoleColors.RED_BOLD_BRIGHT + "Please enter the number of the action you want:"+ ConsoleColors.RESET);
        String userRespond;
        userRespond = scanner.nextLine().toLowerCase().trim();

        switch (userRespond) {
            case "1" -> {
                // code for getAllLists
                System.out.println(ConsoleColors.YELLOW_BOLD_BRIGHT + "Displaying all your ToDoLists ......" + ConsoleColors.RESET);
                String query = String.format("username=%s", curUsername);
                String request = url + "/lists/getAllLists?" + query;
                System.out.println(request);
                if (doGet(request) == 200) {
                    System.out.println(ConsoleColors.GREEN_BOLD_BRIGHT + "Successfully display all your ToDoLists, do you want to come back to the main menu? (Yes/No)" + ConsoleColors.RESET);
                    String response = scanner.nextLine().toLowerCase().trim();
                    if (response.equals("yes")) {
                        quitListsService = false;
                    } else {
                        quitListsService = true;
                        System.out.println(ConsoleColors.YELLOW_BOLD_BRIGHT + "Logging out......" + ConsoleColors.RESET);
                    }

                } else {
                    System.out.println(ConsoleColors.RED_BOLD_BRIGHT + "Display all your ToDoLists failed for some reasons,Do you want to come back to the main menu? (Yes/No)" + ConsoleColors.RESET);
                    String response = scanner.nextLine().toLowerCase().trim();
                    if (response.equals("yes")) {
                        quitListsService = false;
                    } else {
                        quitListsService = true;
                        System.out.println(ConsoleColors.YELLOW_BOLD_BRIGHT + "Logging out......" + ConsoleColors.RESET);
                    }
                }
            }

            case "2" -> {
                // code for addList
                System.out.println(ConsoleColors.RED_BOLD_BRIGHT + "To add a ToDoList, please enter the ToDoList name:" + ConsoleColors.RESET);
                String listName = scanner.nextLine().trim();
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("username", curUsername);
                jsonObject.put("listname", listName);
                // String query = String.format("username=%s&listname=%s", curUsername, listName);
                String request = url + "/lists/addList";
                System.out.println(request);
                System.out.println(jsonObject.toString());
                if (doPost2(request, jsonObject.toString()) == 200) {
                    System.out.println(ConsoleColors.GREEN_BOLD_BRIGHT + "Successfully add a ToDoList, do you want to come back to the main menu? (Yes/No)" + ConsoleColors.RESET);
                    String response = scanner.nextLine().toLowerCase().trim();
                    if (response.equals("yes")) {
                        quitListsService = false;
                    } else {
                        quitListsService = true;
                        System.out.println(ConsoleColors.YELLOW_BOLD_BRIGHT + "Logging out......" + ConsoleColors.RESET);
                    }

                } else {
                    System.out.println(ConsoleColors.RED_BOLD_BRIGHT + "Adding a ToDoList failed for some reasons,Do you want to come back to the main menu? (Yes/No)" + ConsoleColors.RESET);
                    String response = scanner.nextLine().toLowerCase().trim();
                    if (response.equals("yes")) {
                        quitListsService = false;
                    } else {
                        quitListsService = true;
                        System.out.println(ConsoleColors.YELLOW_BOLD_BRIGHT + "Logging out......" + ConsoleColors.RESET);
                    }
                }
            }

            case "3" -> {
                // code for deleteList
                System.out.println(ConsoleColors.RED_BOLD_BRIGHT + "To delete a ToDoList, please enter the ToDoList id:" + ConsoleColors.RESET);
                String listId = scanner.nextLine().trim();
                String query = String.format("username=%s&listid=%s", curUsername, listId);
                String request = url + "/lists/deleteList?" + query;
                System.out.println(request);
                if (doGet(request) == 200) {
                    System.out.println(ConsoleColors.GREEN_BOLD_BRIGHT + "Successfully delete a ToDoList, do you want to come back to the main menu? (Yes/No)" + ConsoleColors.RESET);
                    String response = scanner.nextLine().toLowerCase().trim();
                    if (response.equals("yes")) {
                        quitListsService = false;
                    } else {
                        quitListsService = true;
                        System.out.println(ConsoleColors.YELLOW_BOLD_BRIGHT + "Logging out......" + ConsoleColors.RESET);
                    }

                } else {
                    System.out.println(ConsoleColors.RED_BOLD_BRIGHT + "Deleting a ToDoList failed for some reasons,Do you want to come back to the main menu? (Yes/No)" + ConsoleColors.RESET);
                    String response = scanner.nextLine().toLowerCase().trim();
                    if (response.equals("yes")) {
                        quitListsService = false;
                    } else {
                        quitListsService = true;
                        System.out.println(ConsoleColors.YELLOW_BOLD_BRIGHT + "Logging out......" + ConsoleColors.RESET);
                    }
                }

            }
            case "4" -> {
                // code for deleteAllLists
                System.out.println(ConsoleColors.YELLOW_BOLD_BRIGHT + "Deleting all your ToDoLists......" + ConsoleColors.RESET);
                String query = String.format("username=%s", curUsername);
                String request = url + "/lists/deleteAllLists?" + query;
                System.out.println(request);
                if (doGet(request) == 200) {
                    System.out.println(ConsoleColors.GREEN_BOLD_BRIGHT + "Successfully delete all your ToDoLists, do you want to come back to the main menu? (Yes/No)" + ConsoleColors.RESET);
                    String response = scanner.nextLine().toLowerCase().trim();
                    if (response.equals("yes")) {
                        quitListsService = false;
                    } else {
                        quitListsService = true;
                        System.out.println(ConsoleColors.YELLOW_BOLD_BRIGHT + "Logging out......" + ConsoleColors.RESET);
                    }

                } else {
                    System.out.println(ConsoleColors.RED_BOLD_BRIGHT + "Deleting all your ToDoLists failed for some reasons,Do you want to come back to the main menu? (Yes/No" + ConsoleColors.RESET);
                    String response = scanner.nextLine().toLowerCase().trim();
                    if (response.equals("yes")) {
                        quitListsService = false;
                    } else {
                        quitListsService = true;
                        System.out.println(ConsoleColors.YELLOW_BOLD_BRIGHT + "Logging out......" + ConsoleColors.RESET);
                    }
                }
            }
            case "5" -> {
                // code for enterList
                System.out.println(ConsoleColors.RED_BOLD_BRIGHT + "Please enter the listid you want to enter:" + ConsoleColors.RESET);
                String listid = scanner.nextLine().trim();
                String request = url + "/lists/" + curUsername + "/checkAccess/" + listid;
                System.out.println(request);
                if (doGet(request) == 200) {
                    currentListId = listid;
                    System.out.println(ConsoleColors.GREEN_BOLD_BRIGHT + "You entered into list: " + currentListId + ConsoleColors.RESET);
                } else {
                    System.out.println(ConsoleColors.RED_BOLD_BRIGHT + "You don't have the access to " + listid + ", or the listid you input is wrong." + ConsoleColors.RESET);
                    quitListsService = false;
                    System.out.println(ConsoleColors.YELLOW_BOLD_BRIGHT + "Routing back to the main menu......" + ConsoleColors.RESET);
                }
            }
            case "6" -> {
                // code for logout
                System.out.println(ConsoleColors.YELLOW_BOLD_BRIGHT + "Logging out" + ConsoleColors.RESET);
                loggedIn = false;
            }
        }

    }

    public static void userService(Scanner scanner) {
        // read, eval, print, loop
        System.out.println("--------------------------------------------------------");
        System.out.println(ConsoleColors.YELLOW_UNDERLINED + "Here you can:" + ConsoleColors.RESET);
        System.out.println(ConsoleColors.GREEN_BOLD_BRIGHT + "1. Login" + ConsoleColors.RESET);
        System.out.println(ConsoleColors.BLUE_BOLD_BRIGHT + "2. Signup" + ConsoleColors.RESET);
        System.out.println(ConsoleColors.YELLOW_BOLD_BRIGHT + "3. Change Password" + ConsoleColors.RESET);
        System.out.println("--------------------------------------------------------");
        System.out.println(ConsoleColors.RED_BOLD_BRIGHT + "Please enter(the number) which operation do you want: " + ConsoleColors.RESET);

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
                        System.out.println(ConsoleColors.RED_BOLD_BRIGHT + "User and Password are not matching!" + ConsoleColors.RESET);
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
                        System.out.println(ConsoleColors.GREEN_BOLD_BRIGHT +
                                String.format("Successfully registered and logged in as %s!", curUsername) + ConsoleColors.RESET);
                    } else {
                        curUsername = null;
                    }
                }
                case "3" -> {
                    //code for change password
                    String request = url + "/users/changePassword";
                    System.out.println(request);
                    if (doPut(request, changePassword(scanner)) == 200) {
                        System.out.println(ConsoleColors.GREEN_BOLD_BRIGHT + "Successfully change your password, routing back to the login menu......" + ConsoleColors.RESET);
                    } else {
                        System.out.println(ConsoleColors.RED_BOLD_BRIGHT + "Failed to change your password, routing back to the login menu......" + ConsoleColors.RESET);

                    }

                }
            }
        } catch (IOException e) {
            System.out.println(ConsoleColors.RED_BOLD_BRIGHT + "Unable to get commond..." + e.getMessage() + ConsoleColors.RESET);

        }
    }
    private static String loginInput(Scanner inputReader) throws IOException {
        String username;
        String password;

        System.out.println(ConsoleColors.RED_BOLD_BRIGHT + "Enter username: " + ConsoleColors.RESET);
        username = inputReader.nextLine().trim();
        System.out.println(ConsoleColors.RED_BOLD_BRIGHT + "Enter password: " + ConsoleColors.RESET);
        password = inputReader.nextLine().trim();
        curUsername = username;

        return String.format("username=%s&password=%s", username, password);
    }

    private static String signUp (Scanner inputReader) throws IOException {
        String username;
        String password;

        System.out.print(ConsoleColors.RED_BOLD_BRIGHT + "Enter username: " + ConsoleColors.RESET);
        username = inputReader.nextLine().trim();
        System.out.print(ConsoleColors.RED_BOLD_BRIGHT + "Enter password: " + ConsoleColors.RESET);
        password = inputReader.nextLine().trim();
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

        System.out.print(ConsoleColors.RED_BOLD_BRIGHT + "Enter username: " + ConsoleColors.RESET);
        username = inputReader.nextLine().trim();
        System.out.print(ConsoleColors.RED_BOLD_BRIGHT + "Enter the old password: "+ ConsoleColors.RESET);
        oldPassword = inputReader.nextLine().trim();
        System.out.print(ConsoleColors.RED_BOLD_BRIGHT +"Enter the new password: "+ ConsoleColors.RESET);
        newPassword = inputReader.nextLine().trim();
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
            return false;
        }
        System.out.println("Valid JSON object or array");
        return true;
    }

    private static void buildTaskTable(Task[] tasks) {
        int length = tasks.length;
        final String[][] table = new String[length][];
        System.out.println("-------------------------------------------------------------------------------------------------------------------");
        System.out.printf("%20s %20s %20s", "TASK ID", "TASK NAME", "TASK STATUS");
        System.out.println();
        System.out.println("-------------------------------------------------------------------------------------------------------------------");

        for (int i = 0; i < length; i++) {
            table[i] = new String[] {tasks[i].getTaskid(), tasks[i].getTaskname(), tasks[i].getStatus()};
        }

        for (String[] row : table) {
            System.out.format("%20s%20s%20s%n", row);
            System.out.println("-------------------------------------------------------------------------------------------------------------------");
        }
    }

    private static void buildListTable(ToDoList[] toDoLists) {
        int length = toDoLists.length;
        final String[][] table = new String[length][];
        System.out.println("-----------------------------------------------------------------------------");
        System.out.printf("%20s %20s", "LIST ID", "LIST NAME");
        System.out.println();
        System.out.println("-----------------------------------------------------------------------------");

        for (int i = 0; i < length; i++) {
            table[i] = new String[] {toDoLists[i].getListid(), toDoLists[i].getListname()};
        }

        for (String[] row : table) {
            System.out.format("%20s%20s%n", row);
            System.out.println("-----------------------------------------------------------------------------");
        }

    }
}
