package nl.tudelft.oopp.demo.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;

import java.util.Objects;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name = "users")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class User {

    @Id
    @SequenceGenerator(
            name = "user_sequence",
            sequenceName = "user_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "user_sequence"
    )
    private long id;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "room_room_id", nullable = false)
    @JsonBackReference
    private Room room;

    private String nickname;


    /** Constructor for User without id (generated by db).
     * @param nickname - nickname entered by User
     * @param room - Room in which user is
     */
    public User(String nickname, Room room) {
        this.nickname = nickname;
        this.room = room;
    }

    /**  Constructor for User with id.
     * @param id - long
     * @param nickname - nickname entered by User
     * @param room - Room in which user is
     */
    public User(Long id, String nickname, Room room) {
        this.id = id;
        this.nickname = nickname;
        this.room = room;
    }


    public User() {

    }

    public String getNickname() {
        return nickname;
    }

    public Long getId() {
        return this.id;
    }

    public Room getRoom() {
        return room;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof User)) {
            return false;
        }
        User user = (User) o;
        return getNickname().equals(user.getNickname())
        && room.equals(user.room);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getNickname());
    }
}
