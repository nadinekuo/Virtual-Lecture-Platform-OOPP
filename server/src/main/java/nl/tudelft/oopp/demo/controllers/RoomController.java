package nl.tudelft.oopp.demo.controllers;

import static nl.tudelft.oopp.demo.config.LoggerConfig.logRequest;

import java.util.List;
import java.util.stream.Collectors;

import nl.tudelft.oopp.demo.entities.Moderator;
import nl.tudelft.oopp.demo.entities.Room;
import nl.tudelft.oopp.demo.entities.Student;
import nl.tudelft.oopp.demo.services.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("rooms")
public class RoomController {

    private final RoomService roomService;

    /**
     * Autowired constructor for the class.
     * @param roomService roomService
     */
    @Autowired
    public RoomController(RoomService roomService) {
        this.roomService = roomService;
    }

    /**
     * GET mapping.
     * @return all rooms from the database
     */
    @GetMapping
    @ResponseBody
    public List<Room> getRooms() {
        logRequest("to get all rooms from the database");
        return roomService.getRooms();
    }

    /**
     * GET mapping with logging.
     * @param roomCode code for a specific room
     * @return the room for the given code
     */
    @GetMapping("/{roomCode}/log")
    @ResponseBody
    public Room getRoomByCodeLog(@PathVariable String roomCode) {
        logRequest("to join room with code '" + roomCode + "'");
        return roomService.getRoomByCode(roomCode);
    }

    /**
     * GET mapping without logging.
     * @param roomCode code for a specific room
     * @return the room for the given code
     */
    @GetMapping("/{roomCode}")
    @ResponseBody
    public Room getRoomByCode(@PathVariable String roomCode) {
        return roomService.getRoomByCode(roomCode);
    }

    /**
     * PUT mapping, deactivates a room.
     * @param roomCode the link connected to the room
     */
    @PutMapping("/update/{roomCode}")
    public void updateRoomStatus(@PathVariable String roomCode) {
        logRequest("to deactivate the room with code '" + roomCode + "'");
        roomService.updateRoomStatusByLink(roomCode);
    }

    /**
     * POST mapping, adds a new room to the DB.
     * @param data the data needed for the creation
     * @return the newly created room
     */
    @PostMapping
    @ResponseBody
    public Room addNewRoom(@RequestBody String data) {
        Room room = roomService.addNewRoom(data);
        if (room != null) {
            logRequest("to create a new room with an id '" + room.getRoomId() + "'");
            return room;
        }
        return null;
    }

    /**
     * PUT mapping, updates the feedback for a specific room.
     * @param roomCode the id of the required room
     * @param feedback the new feedback value
     */
    @PutMapping("/{roomCode}/{feedback}")
    public void updateFeedback(@PathVariable String roomCode, @PathVariable String feedback) {
        roomService.updateRoomSpeed(roomCode, feedback);
    }

    /**
     * GET mapping.
     * @param roomId the id of the required room
     * @return all students in a specific room
     */
    @GetMapping("/students/{roomId}")
    @ResponseBody
    public List<Student> getStudents(@PathVariable String roomId) {
        Room room = roomService.getRoomById((long)Integer.valueOf(roomId));
        return room.getStudents().stream().filter(s -> !s.isBanned()).collect(Collectors.toList());
    }

    /**
     * GET mapping.
     * @param roomId the id of the required room
     * @return all moderators in a specific room
     */
    @GetMapping("/moderators/{roomId}")
    @ResponseBody
    public List<Moderator> getModerators(@PathVariable String roomId) {
        Room room = roomService.getRoomById((long)Integer.valueOf(roomId));
        return room.getModerators();
    }
}
