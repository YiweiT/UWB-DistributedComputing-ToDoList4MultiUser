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
import com.fasterxml.jackson.databind.util.JSONPObject;
import org.json.JSONObject;


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

    private static int doPost(String url, String payload) {
        try {
            HttpClient client = HttpClient.newBuilder()
                    .version(HttpClient.Version.HTTP_1_1)
                    .connectTimeout(Duration.ofSeconds(20))
                    .followRedirects(HttpClient.Redirect.NORMAL)
                    .build();
            HttpRequest request = HttpRequest.newBuilder()
                    .POST(HttpRequest.BodyPublishers.ofString(payload))
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
        BufferedReader inputReader = new BufferedReader(new InputStreamReader(System.in));
        Scanner scanner = new Scanner(System.in);
        while(!loggedIn) {
           userService(scanner);
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

    public static String signUp (Scanner inputReader) throws IOException {
        String username;
        String password;

        System.out.print("Enter username: ");
        username = inputReader.nextLine().trim();
        System.out.print("Enter password: ");
        password = inputReader.next().trim();
        curUsername = username;
//        var values = new HashMap<String, String>() {{
//            put("username", username);
//            put ("password", password);
//        }};
//        var objectMapper = new ObjectMapper();
//        String requestBody = objectMapper
//                .writeValueAsString(values);
        JSONObject user = new JSONObject();
        user.put("username", username);
        user.put("password", password);

        return user.toString();
    }
//    public static String resetPassword(Scanner inputReader) {
//        String username;
//        String oldassword;
//
//        System.out.print("Enter username: ");
//        username = inputReader.nextLine().trim();
//        System.out.print("Enter password: ");
//        password = inputReader.next().trim();
//    }
}
