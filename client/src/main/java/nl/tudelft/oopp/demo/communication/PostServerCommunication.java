package nl.tudelft.oopp.demo.communication;

import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import nl.tudelft.oopp.demo.data.Question;
import nl.tudelft.oopp.demo.data.Room;
import nl.tudelft.oopp.demo.data.Student;
import nl.tudelft.oopp.demo.data.User;

public class PostServerCommunication extends ServerCommunication {

    /**
     * Sends room to the server, returns a room with URLs.
     * @param room primitive room
     * @return room with all parameters
     */
    public static Room makeRoom(Room room) {
        if (room == null) {
            return null;
        }

        String postRequestBody = room.getRoomName() + ", "
                + room.getStartingTime() + ", " + room.isActive();

        // send request to the server
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/rooms"))
                .POST(HttpRequest.BodyPublishers.ofString(postRequestBody))
                .build();
        HttpResponse<String> response;
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {
            e.printStackTrace();
            return room;
        }
        if (response.statusCode() != 200) {
            System.out.println("Status: " + response.statusCode());
            return null;
        }
        return gson.fromJson(response.body(), Room.class);
    }

    /** Sends a user to the server, who is saved in the DB ..
     * .. and added to the list of participants in the ..
     * .. corresponding room instance.
     * @param user the user to be saved in the DB
     * @param roomID the id of the room the user is a participant in
     * @return Long - the id of the user
     */
    public static Long sendUser(User user, long roomID) {
        if (user == null) {
            return (long) -1;
        }
        String requestBody = "";
        if (user.getRole().equals("Student")) {
            requestBody = user.getNickname() + ", "
                    + ((Student) user).getIpAddress() + ", " + roomID;
        }
        if (user.getRole().equals("Moderator")) {
            requestBody = user.getNickname() + ", " + roomID;
        }
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create("http://localhost:8080/users/addUser/" + user.getRole()))
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();
        HttpResponse<String> response;
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {
            e.printStackTrace();
            return (long) - 1;
        }
        if (response.statusCode() != 200) {
            System.out.println("Status: " + response.statusCode());
            return (long) - 1;
        }
        return gson.fromJson(String.valueOf(Long.parseLong(response.body())), Long.class);
    }

    /** Makes POST request to server, to store new question in database.
     * Server will attach an id to this question (generated by db)
     * Handled by QuestionController.
     * @param newQuestion - new Question to be posted
     * @return Long - generated id for this question
     */
    public static Long postQuestion(Question newQuestion) {
        if (newQuestion == null) {
            return (long)-1;
        }

        String postRequestBody = newQuestion.getRoom() + "& "
                + newQuestion.getOwner() + "& " + newQuestion.getText();

        // send request to the server
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/questions"))
                .POST(HttpRequest.BodyPublishers.ofString(postRequestBody))
                .build();

        HttpResponse<String> response;

        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {
            e.printStackTrace();
            return (long)-1;
        }

        if (response.statusCode() != 200) {
            System.out.println("Status: " + response.statusCode());
            return (long)-1;
        }

        return gson.fromJson(String.valueOf(Long.parseLong(response.body())), Long.class);
    }
}
