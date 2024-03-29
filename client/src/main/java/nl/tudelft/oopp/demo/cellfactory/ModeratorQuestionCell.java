package nl.tudelft.oopp.demo.cellfactory;

import java.net.URL;

import javafx.collections.ObservableList;
import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.TextArea;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Paint;
import javafx.util.Duration;
import nl.tudelft.oopp.demo.communication.ServerCommunication;
import nl.tudelft.oopp.demo.controllers.ModeratorRoomController;
import nl.tudelft.oopp.demo.controllers.RoomController;
import nl.tudelft.oopp.demo.data.Question;

public class ModeratorQuestionCell extends ListCell<Question> {

    private AnchorPane anchorPane = new AnchorPane();
    private GridPane gridPane = new GridPane();
    private Question question;
    private ObservableList<Question> questions;
    private ObservableList<Question> answered;
    private TextArea editableLabel;
    private boolean editing;
    private RoomController mrc;
    private boolean startTyping;    // used for the 'Someone is answering...'


    /**
     * Constructor for moderator question cell.
     * @param questions ObservableList of current questions
     * @param answered ObservableList of answered questions
     */
    public ModeratorQuestionCell(ObservableList<Question> questions,
                                 ObservableList<Question> answered, RoomController mrc) {

        super();

        this.questions = questions;
        this.answered = answered;
        this.editableLabel = new TextArea();
        editableLabel.setPrefHeight(60);
        editableLabel.setPrefWidth(400);
        editableLabel.setWrapText(true);
        this.editing = false;
        this.mrc = mrc;
        this.startTyping = false;
        // Create visual cell
        createCell();
    }

    /**
     * Creates a visual moderator cell for questions.
     */
    private void createCell() {

        // Add grid pane to anchor pane
        anchorPane.getChildren().add(gridPane);
        ColumnConstraints columnZeroConstraints = new ColumnConstraints();
        columnZeroConstraints.setPrefWidth(250);
        columnZeroConstraints.setPercentWidth(70);
        ColumnConstraints columnOneConstraints = new ColumnConstraints();
        columnOneConstraints.setPrefWidth(100);
        columnOneConstraints.setPercentWidth(30);
        gridPane.getColumnConstraints().add(columnZeroConstraints);
        gridPane.getColumnConstraints().add(columnOneConstraints);

        // Create all labels
        Label questionLabel = new Label();
        questionLabel.setId("questionLabel");
        questionLabel.setPrefWidth(460);
        questionLabel.wrapTextProperty().setValue(true);

        Label upVotesLabel = new Label();
        upVotesLabel.setId("upVotesLabel");

        Label ownerLabel = new Label();
        ownerLabel.setId("ownerLabel");
        ownerLabel.wrapTextProperty().setValue(true);
        ownerLabel.setTextFill(Paint.valueOf("#00A6D6"));

        // Create buttons
        Button replyButton = new Button();
        replyButton.setId("replyButton");
        replyButton.setTooltip(new Tooltip("Answer question"));
        replyButton.setPrefWidth(26);
        URL path = StudentQuestionCell.class.getResource("/images/replyBlue.png");
        setButtonStyle(replyButton, path);
        replyButton.setCursor(Cursor.HAND);

        Button answerButton = new Button();
        answerButton.setId("answeredButton");
        answerButton.setTooltip(new Tooltip("Mark as answered"));
        answerButton.setPrefWidth(28);
        path = StudentQuestionCell.class.getResource("/images/checkmark.png");
        setButtonStyle(answerButton, path);
        answerButton.setCursor(Cursor.HAND);

        Button editButton = new Button();
        editButton.setId("editButton");
        editButton.setTooltip(new Tooltip("Edit Question"));
        editButton.setPrefWidth(25);
        path = StudentQuestionCell.class.getResource("/images/colouredPencil.png");
        setButtonStyle(editButton, path);
        editButton.setCursor(Cursor.HAND);

        Button deleteButton = new Button();
        deleteButton.setId("deleteButton");
        deleteButton.setPrefWidth(28);
        path = StudentQuestionCell.class.getResource("/images/redTrash.png");
        setButtonStyle(deleteButton, path);
        deleteButton.setCursor(Cursor.HAND);

        // Create text area
        TextArea answerBox = new TextArea("");
        answerBox.setId("answerBox");
        answerBox.setWrapText(true);
        answerBox.setPrefHeight(125);
        answerBox.setPrefWidth(200);
        answerBox.setCursor(Cursor.TEXT);

        // Wrap edit and delete button
        HBox editDeleteWrapper = new HBox(editButton, deleteButton);
        editDeleteWrapper.setSpacing(5);

        // Wrap answer button and text area
        HBox answerWrapper = new HBox(answerBox, replyButton, answerButton);
        answerWrapper.setId("answerWrapper");
        answerWrapper.setSpacing(5);

        // Add elements to grid pane
        gridPane.add(ownerLabel, 0, 0);
        gridPane.add(questionLabel, 0,1);
        gridPane.add(answerWrapper, 0,2);
        gridPane.add(editDeleteWrapper, 1,2);
        gridPane.add(upVotesLabel, 1,0);

        // Give background colours
        gridPane.styleProperty().setValue("-fx-background-color: white");
        anchorPane.styleProperty().setValue("-fx-background-color: #E5E5E5");

        // Align grid pane
        gridPane.setAlignment(Pos.CENTER);
        gridPane.setHgap(5);
        gridPane.setVgap(5);

        // Align grid pane in anchor pane
        AnchorPane.setTopAnchor(gridPane, 10.0);
        AnchorPane.setLeftAnchor(gridPane, 10.0);
        AnchorPane.setRightAnchor(gridPane, 10.0);
        AnchorPane.setBottomAnchor(gridPane, 10.0);

        // creates a service that will be used to know when
        // to mark a question as not being answered
        ScheduledService<Boolean> service = new ScheduledService<>() {
            @Override
            protected Task<Boolean> createTask() {
                return new Task<>() {
                    @Override
                    protected Boolean call() {
                        updateMessage("Checking for updates..");
                        return true;
                    }
                };
            }
        };

        service.setPeriod(Duration.seconds(1));

        // When the service is done it sends a request
        // to mark question as no longer being answered
        service.setOnSucceeded(e -> {
            ServerCommunication.markQuestionAsIsNotBeingAnswered(question.getId());
            startTyping = false;
        });

        // Trigger event for every time something is entered in the answerBox
        answerBox.setOnKeyTyped(event -> {
            if (startTyping == false) {
                ServerCommunication.markQuestionAsIsBeingAnswered(question.getId());
                startTyping = true;
                service.restart();
            }

            // 2 seconds delay after the person has stopped writing
            service.setDelay(Duration.seconds(2));
        });


        // Click event for the 'Edit' button
        editButton.setOnAction(event -> {

            if (this.question == null) {
                return;
            }

            // User saves changes
            if (editing) {

                // Send changes to server
                mrc.editQuestion(
                        this.question, editableLabel.getText());

                gridPane.getChildren().remove(editableLabel);
                gridPane.add(questionLabel, 0, 1);
                question.setText(editableLabel.getText());
                editButton.setTooltip(new Tooltip("Edit question"));
                editButton.setPrefWidth(25);
                URL url = StudentQuestionCell.class.getResource("/images/colouredPencil.png");
                setButtonStyle(editButton, url);
                questionLabel.setText(editableLabel.getText());
                editing = false;

            } else { // User wants to make changes

                gridPane.getChildren().remove(questionLabel);
                gridPane.add(editableLabel, 0,1);
                editButton.setTooltip(new Tooltip("Save Changes"));
                editButton.setPrefWidth(27);
                URL url = StudentQuestionCell.class.getResource("/images/checkGreen.png");
                setButtonStyle(editButton, url);
                editableLabel.setText(question.getText());
                editing = true;

            }
        });

        // Click event for the 'Mark answered' button
        answerButton.setOnAction(event -> {

            // Next line marks the question as answered in the DB
            ServerCommunication.markQuestionAsAnswered(question.getId());

            // The if is to submit the already written text before marking
            if (!answerBox.getText().equals("")) {

                String answer = answerBox.getText() + " -" + mrc.getUser().getNickname();

                ((ModeratorRoomController) mrc).setAnswer(this.question, answer);
            }

            answerBox.clear();
        });

        // Click event for the 'Reply' button
        replyButton.setOnAction(event -> {

            String answer = answerBox.getText() + " -" + mrc.getUser().getNickname();

            // Send answer to server to store in db
            ((ModeratorRoomController) mrc).setAnswer(this.question, answer);

            question.setAnswer(answer);
            answerBox.setPromptText(answer);
            answerBox.clear();
            answerBox.deselect();
        });

        // Click event for the 'Delete' button
        deleteButton.setOnAction(event -> {

            // Send to server to delete from DB
            mrc.deleteQuestion(this.question);

            // Remove question from list
            questions.remove(question);
        });
    }

    /**
     * Updates the item in the ListView.
     * @param item updated item
     * @param empty true if empty, false if not
     */
    @Override
    protected void updateItem(Question item, boolean empty) {

        // Update listview
        super.updateItem(item, empty);

        // Empty list item
        if (empty || item == null) {

            setGraphic(null);
            setText("");

        } else { // Non-empty list item

            // Update question object
            this.question = item;

            // Look for question and owner label
            Label questionLabel = (Label) gridPane.lookup("#questionLabel");
            Label ownerLabel = (Label) gridPane.lookup("#ownerLabel");
            Label upVotesLabel = (Label) gridPane.lookup("#upVotesLabel");

            // Update question
            if (questionLabel != null) {
                questionLabel.setText(item.getText());
            }

            ownerLabel.setText(item.getOwner());
            upVotesLabel.setText(item.getUpvotes() + " Votes");

            // Next few lines are for showing the current answer to the question as prompt
            // or showing 'Someone is answering...' in case some is... answering
            TextArea answerBox = (TextArea) gridPane.lookup("#answerBox");

            if (item.isBeingAnswered()) {
                answerBox.setPromptText("Someone is answering...");
            } else {
                answerBox.setPromptText(this.question.getAnswer());
            }

            // Show graphic representation
            setGraphic(anchorPane);
        }
    }

    private void setButtonStyle(Button button, URL path) {
        button.setStyle("-fx-background-image: url('" + path + "');"
                + " -fx-background-repeat: no-repeat;"
                + " -fx-background-size: 100% 100%;");
    }
}
