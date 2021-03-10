package nl.tudelft.oopp.demo.entities;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name = "rooms")
public class Room {

    // transient = no column in DB

    @Id
    @SequenceGenerator(
            name = "room_sequence",
            sequenceName = "room_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "room_sequence"
    )
    private long roomId;
    private URL studentsLink;
    private URL moderatorLink;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startingTime;
    private String roomName;                    // course name e.g.
    @Transient
    private boolean active;
    @Transient
    private List<User> participants;            // List of Users > DB ?
    @Transient
    private Set<Question> questions;          // Or not needed at all because we have the DB


    public Room() {

    }

    /** Constructor for Room without id (generated by DB using sequence generator).
     * @param startingTime - LocalDateTime when meeting starts/room opens.
     * @param roomName - name given to room by creator. (course name e.g.)
     * @param active - whether or not room is active
     * @throws MalformedURLException - Exception to be thrown when URL is incorrect format.
     */
    public Room(LocalDateTime startingTime, String roomName, boolean active) throws MalformedURLException {
        this.startingTime = startingTime;
        this.roomName = roomName;
        this.active = active;
        this.participants = new ArrayList<>();
        this.questions = new HashSet<>();
        linkGenerator();
    }

    /** Constructor for Room with id.
     * @param id - Room ID generated by DB using sequence generator.
     * @param startingTime - LocalDateTime when meeting starts/room opens.
     * @param roomName - name given to room by creator. (course name e.g.)
     * @throws MalformedURLException - Exception to be thrown when URL is incorrect format.
     */
    public Room(long id, LocalDateTime startingTime, String roomName) throws MalformedURLException {
        this.roomId = id;
        this.startingTime = startingTime;
        this.roomName = roomName;
        this.active = false;
        this.participants = new ArrayList<>();
        this.questions = new HashSet<>();
        linkGenerator();
    }

    /** When verifying the links :
     * If the link doesn't contain M and is not linked to a room ..
     * .. then that means the link is invalid students link.
     * If the link contains M and is linked to a room ..
     * .. then it is a valid moderator link.
     */
    private void linkGenerator() throws MalformedURLException {
        this.studentsLink = new URL("http://localhost:8080/room/"
                + UUID.randomUUID().toString().replace("-", "").substring(0,18));
        this.moderatorLink = new URL("http://localhost:8080/room/M"
                + UUID.randomUUID().toString().replace("-", "").substring(0,17));
    }

    public long getRoomId() {
        return roomId;
    }

    public URL getStudentsLink() {
        return studentsLink;
    }

    public URL getModeratorLink() {
        return moderatorLink;
    }

    public LocalDateTime getStartingTime() {
        return startingTime;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public boolean isActive() {
        return active;
    }

    /** A function that closes the window for the students, etc.
     * Room is not active anymore: no more questions can be asked,
     * but Moderators may still answer questions.
     */
    public void hasEnded() {
        this.active = false;
        while (!this.participants.isEmpty()) {
            this.participants.remove(0);
        }
    }

    public List<User> getParticipants() {
        return participants;
    }

    // Useful for exporting the questions ?
    public Set<Question> getQuestions() {
        return questions;
    }


    /** Adds a new User (Student or Moderator) to this Room.
     * @param user - User to be added
     */
    public void addParticipant(User user) {
        this.participants.add(user);
        // Sort by nicknames
        // this.participants.sort();
    }

    public void removeParticipant(User user) {
        this.participants.remove(user);
    }

    public void addQuestion(Question question) {
        this.questions.add(question);
    }


    // Equals method without (@Transient) fields that don't have columns in DB.
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Room)) {
            return false;
        }
        Room room = (Room) o;
        return  getRoomId() == room.getRoomId()
                && getStudentsLink().equals(room.getStudentsLink())
                && getModeratorLink().equals(room.getModeratorLink())
                && getStartingTime().equals(room.getStartingTime())
                && getRoomName().equals(room.getRoomName());
    }

    /** Automatically generated hash method.
     * @return int - the hashCode of the Room
     */
    @Override
    public int hashCode() {
        return Objects.hash(getRoomId(), getStudentsLink(),
                getModeratorLink(), getStartingTime(),
                getRoomName(), isActive(), getParticipants(), getQuestions());
    }

    /** Automatically generated toString method.
     * @return String - same format as the one for the waiting room
     */
    @Override
    public String toString() {
        String result = roomName + "\n(";
        String date = startingTime.toString().substring(0,10).replace("-","/");
        String time = startingTime.toString().substring(11,16);
        result += time + ")\n" + date;
        return result;
    }
}
