package nl.tudelft.oopp.demo.data;

import java.net.URL;
import java.time.LocalDateTime;

public class Room {
    // these match the properties in Room entity on server
    // but we only include what we need on client (there will be more added when needed)
    // No setter necessary, since we don't update on client.

    private String roomName;
    private URL studentsLink;
    private URL moderatorLink;
    private LocalDateTime startingTime;
    private boolean active;
    private long roomId;

    /**
     * Room constructor.
     * @param roomName roomName
     * @param studentsLink studentsLink
     * @param moderatorLink moderatorLink
     * @param startingTime startingTime
     */
    public Room(String roomName, URL studentsLink, URL moderatorLink,
                LocalDateTime startingTime, long id) {
        this.roomName = roomName;
        this.studentsLink = studentsLink;
        this.moderatorLink = moderatorLink;
        this.startingTime = startingTime;
        this.active = true;
        this.roomId = id;
    }

    /**
     * Room constructor.
     * @param roomName roomName
     * @param startingTime startingTime
     * @param active active
     */
    public Room(String roomName, LocalDateTime startingTime, boolean active) {
        this.roomName = roomName;
        this.startingTime = startingTime;
        this.active = active;
    }

    /**
     * Room constructor.
     * @param roomName roomName
     * @param startingTime startingTime
     * @param active active
     */
    public Room(long roomId, String roomName, LocalDateTime startingTime, boolean active) {
        this.roomId = roomId;
        this.roomName = roomName;
        this.startingTime = startingTime;
        this.active = active;
    }



    public String getRoomName() {
        return roomName;
    }

    public URL getStudentsLink() {
        return studentsLink;
    }

    public URL getModeratorLink() {
        return moderatorLink;
    }

    public long getRoomId() {
        return roomId;
    }

    public LocalDateTime getStartingTime() {
        return startingTime;
    }

    public boolean isActive() {
        return active;
    }

    public void end() {
        this.active = false;
    }
}
