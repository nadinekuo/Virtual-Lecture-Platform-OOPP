package nl.tudelft.oopp.demo.entities;

import java.time.LocalTime;
import java.util.Objects;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;


@Entity
@Table(name = "question")
public class Question {

    @Id
    @SequenceGenerator(
            name = "question_sequence",
            sequenceName = "question_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "question_sequence"
    )
    private long id;
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "room_room_id", nullable = false)
    private Room room;
    private String text;
    private String answer;
    private String owner;
    private String time;
    private Integer upvotes;
    private Boolean isAnswered = false;



    public Question() {

    }


    /** Constructor with upvotes for testing purposes.
     * @param id - PK of this question.
     * @param room - Room in which this question is asked.
     * @param text - String containing question.
     * @param owner - nickname of person who asked this question.
     * @param upvotes - used to prioritize questions.
     */
    public Question(long id, Room room, String text, String owner, int upvotes) {
        this.id = id;
        this.room = room;
        this.text = text;
        this.answer = "";
        this.owner = owner;
        this.time = LocalTime.now().getHour() + ":" + LocalTime.now().getMinute();
        this.upvotes = upvotes;
        this.isAnswered = false;
    }


    /** Constructor without id (generated by DB sequence generator).
     * @param room - Room in which this question is asked.
     * @param text - String containing question.
     * @param owner - nickname of person who asked this question.
     */
    public Question(Room room, String text, String owner) {
        this.room = room;
        this.text = text;
        this.answer = "";
        this.owner = owner;
        this.time = LocalTime.now().getHour() + ":" + LocalTime.now().getMinute();
        this.upvotes = 0;
        this.isAnswered = false;
    }

    //    public Room getRoom() {
    //        return room;
    //    }

    // Returning the room object does not work properly for GET requests?
    public long getRoom() {
        return room.getRoomId();
    }


    public Long getId() {
        return id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getOwner() {
        return owner;
    }

    public String getTime() {
        return time;
    }

    public int getUpvotes() {
        return upvotes;
    }

    public Boolean getIsAnswered() {
        return isAnswered;
    }

    public void upvote() {
        upvotes++;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Question)) {
            return false;
        }
        Question question1 = (Question) o;
        return getId() == question1.getId()
                // && getRoom() == question1.getRoom()
                && getText().equals(question1.getText())
                && getAnswer().equals(question1.getAnswer())
                && getOwner().equals(question1.getOwner())
                && getTime().equals(question1.getTime())
                && getUpvotes() == question1.getUpvotes();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getRoom(),
                getText(), getAnswer(), getOwner(), getTime(), upvotes);
    }

    @Override
    public String toString() {
        return time + " -- " + text + (!answer.equals("") ? "- " + answer : "");
    }
}
