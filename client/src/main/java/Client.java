import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Locale;
import java.util.Scanner;
import java.util.Stack;

public class Client {

    private static boolean loggedIn;
    private static String curUsername;
    static String url = "http://localhost:8080";
    private String currentListId;
    private String currentTaskId;


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

    private static int doPost(String url) {
        try {
            HttpClient client = HttpClient.newBuilder()
                    .version(HttpClient.Version.HTTP_1_1)
                    .connectTimeout(Duration.ofSeconds(20))
                    .followRedirects(HttpClient.Redirect.NORMAL)
                    .build();
            HttpRequest request = HttpRequest.newBuilder()
                    .POST()
                    .uri(URI.create(url))
                    .build();
            HttpResponse<String>  response = client.send(request, HttpResponse.BodyHandlers.ofString());

            System.out.println(response.body());
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



    public static void main(String[] args) {
        System.out.println("Welcome to to-do list.");
        BufferedReader inputReader = new BufferedReader(new InputStreamReader(System.in));
        Scanner scanner = new Scanner(System.in);
        while(true) {
            // read, eval, print, loop
            if (!loggedIn)
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
                    String request = url + "/users/addUser?" + loginInput(scanner);
                    System.out.println(request);
                } else {
                    System.out.println("Please choose one command from the following:");
                }

            } catch (IOException e) {
                System.out.println("Unable to get commond..." + e.getMessage());

            }
        }
    }

    public static String loginInput(Scanner inputReader) throws IOException {
        String username;
        String password;

        System.out.print("Enter username: ");
        username = inputReader.nextLine().trim();
        System.out.print("Enter password: ");
        password = inputReader.next().trim();
        curUsername = username;
        return String.format("username=%s&password=%s", username, password);


    }
}
