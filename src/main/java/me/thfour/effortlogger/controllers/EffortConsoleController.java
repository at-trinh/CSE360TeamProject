package me.thfour.effortlogger.controllers;

import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXComboBox;
import io.github.palexdev.materialfx.enums.ButtonType;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import me.thfour.effortlogger.models.UserStory;

import java.net.URL;
import java.sql.SQLException;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class EffortConsoleController implements Initializable {

    @FXML
    private MFXComboBox<UserStory> userStory;

    @FXML
    private VBox buttonVBox;

    @FXML
    private Label clockProgress;

    @FXML
    private Label elapsedTimeLabel;

    @FXML
    private VBox clockStatusVBox;

    private Long taskRunningTimeInSeconds;
    private Timeline taskTimeline;
    private MFXButton startActivity;
    private GridPane gridPane;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        clockStatusVBox.setBackground(new Background(new BackgroundFill(Color.RED, CornerRadii.EMPTY, Insets.EMPTY)));
        startActivity = new MFXButton("Start Activity");
        startActivity.setDisable(true);
        startActivity.setMaxWidth(Double.MAX_VALUE);
        startActivity.setButtonType(ButtonType.RAISED);
        startActivity.setOnAction(e -> {
            taskRunningTimeInSeconds = 0L;
            taskTimeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
                taskRunningTimeInSeconds += 1;
                long seconds = taskRunningTimeInSeconds % 60L;
                long minutes = TimeUnit.SECONDS.toMinutes(taskRunningTimeInSeconds) % 60L;
                long hours = TimeUnit.SECONDS.toHours(taskRunningTimeInSeconds);
                elapsedTimeLabel.setText(String.format("%s:%s:%s", formatTimeNumber(hours), formatTimeNumber(minutes), formatTimeNumber(seconds)));
            }));
            taskTimeline.setCycleCount(Timeline.INDEFINITE);
            taskTimeline.play();
            clockProgress.setText("CLOCK IS RUNNING");
            clockStatusVBox.setBackground(new Background(new BackgroundFill(Color.GREEN, CornerRadii.EMPTY, Insets.EMPTY)));
            try {
                EffortLoggerController.getDatabase().addDateToUserStory(userStory.getSelectedItem(), new Date(), "In Progress");
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }

            buttonVBox.getChildren().setAll(gridPane);
            userStory.setDisable(true);
        });
        buttonVBox.getChildren().setAll(startActivity);

        gridPane = new GridPane();
        gridPane.setPadding(new Insets(10));
        gridPane.setHgap(5);
        gridPane.setVgap(5);
        final ColumnConstraints col1 = new ColumnConstraints();
        col1.setHgrow(Priority.ALWAYS);
        final ColumnConstraints col2 = new ColumnConstraints();
        col2.setHgrow(Priority.ALWAYS);
        gridPane.getColumnConstraints().addAll(col1, col2);

        MFXButton pauseActivity = new MFXButton("Pause Activity");
        pauseActivity.setMaxWidth(Double.MAX_VALUE);
        pauseActivity.prefWidth(Double.MAX_VALUE);
        pauseActivity.setButtonType(ButtonType.RAISED);
        pauseActivity.setOnAction(e -> {
            if (taskTimeline.getStatus().equals(Animation.Status.RUNNING)) {
                taskTimeline.pause();
                pauseActivity.setText("Resume Activity");
                clockProgress.setText("CLOCK IS PAUSED");
                clockStatusVBox.setBackground(new Background(new BackgroundFill(Color.ORANGE, CornerRadii.EMPTY, Insets.EMPTY)));
                try {
                    EffortLoggerController.getDatabase().addDateToUserStory(userStory.getSelectedItem(), new Date(), "In Progress");
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
                userStory.setDisable(false);
            } else {
                taskTimeline.play();
                pauseActivity.setText("Pause Activity");
                clockProgress.setText("CLOCK IS RUNNING");
                clockStatusVBox.setBackground(new Background(new BackgroundFill(Color.GREEN, CornerRadii.EMPTY, Insets.EMPTY)));
                try {
                    EffortLoggerController.getDatabase().addDateToUserStory(userStory.getSelectedItem(), new Date(), "In Progress");
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
                userStory.setDisable(true);
            }
        });

        MFXButton finishActivity = new MFXButton("Finish Activity");
        finishActivity.setMaxWidth(Double.MAX_VALUE);
        finishActivity.prefWidth(Double.MAX_VALUE);
        finishActivity.setButtonType(ButtonType.RAISED);
        finishActivity.setOnAction(e -> {
            if (taskTimeline.getStatus().equals(Animation.Status.RUNNING)) {
                try {
                    EffortLoggerController.getDatabase().addDateToUserStory(userStory.getSelectedItem(), new Date(), "Finished");
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            } else {
                try {
                    EffortLoggerController.getDatabase().setUserStoryStatus(userStory.getSelectedItem(), "Finished");
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            }
            taskTimeline.stop();
            clockProgress.setText("CLOCK IS STOPPED");
            clockStatusVBox.setBackground(new Background(new BackgroundFill(Color.RED, CornerRadii.EMPTY, Insets.EMPTY)));
            userStory.setDisable(false);
        });

        gridPane.add(pauseActivity, 0, 0);
        gridPane.add(finishActivity, 1, 0);

        try {
            userStory.setItems(FXCollections.observableList(EffortLoggerController.getDatabase().getUserStories().stream().filter(userStory1 -> !userStory1.getStatus().equals("Finished")).collect(Collectors.toList())));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        userStory.getSelectionModel().selectedItemProperty().addListener((options, oldValue, newValue) -> {
            startActivity.setDisable(false);
        });
    }

    private String formatTimeNumber(long number) {
        if (number >= 10)
            return String.valueOf(number);
        else
            return "0" + String.valueOf(number);
    }
}
