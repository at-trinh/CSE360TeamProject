package me.thfour.effortlogger.controllers;

import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXComboBox;
import io.github.palexdev.materialfx.enums.ButtonType;
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
    private MFXComboBox<UserStory> userStorySelector;

    @FXML
    private VBox buttonVBox;

    @FXML
    private Label clockProgress;

    @FXML
    private Label totalElapsedTimeLabel;

    @FXML
    private Label sessionElapsedTimeLabel;

    @FXML
    private VBox clockStatusVBox;

    private Long taskSessionRunningTimeInSeconds = 0L;
    private Long taskTotalRunningTimeInSeconds = 0L;
    private Timeline taskTimeline;
    private MFXButton startActivityButton;
    private MFXButton pauseResumeActivityButton;
    private GridPane gridPane;

    private EffortControllerState state = EffortControllerState.STOPPED;

    public EffortConsoleController(EffortLoggerController effortLoggerController) {
        effortLoggerController.setEffortConsoleController(this);
    }

    public EffortControllerState getState() {
        return state;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initUI();
    }

    private void initUI() {
        updateClockLabel();
        initSelectUI();
        initSelectedUI();
        initSelector();
    }

    private void initSelectUI() {
        startActivityButton = new MFXButton("Start Activity");
        startActivityButton.setDisable(true);
        startActivityButton.setMaxWidth(Double.MAX_VALUE);
        startActivityButton.setButtonType(ButtonType.RAISED);
        startActivityButton.setOnAction(e -> {
            startActivityAction();
        });
        buttonVBox.getChildren().setAll(startActivityButton);
    }

    private void initSelectedUI() {
        gridPane = new GridPane();
        gridPane.setPadding(new Insets(10));
        gridPane.setHgap(5);
        gridPane.setVgap(5);
        final ColumnConstraints col1 = new ColumnConstraints();
        col1.setHgrow(Priority.ALWAYS);
        final ColumnConstraints col2 = new ColumnConstraints();
        col2.setHgrow(Priority.ALWAYS);
        gridPane.getColumnConstraints().addAll(col1, col2);

        pauseResumeActivityButton = new MFXButton("Pause Activity");
        pauseResumeActivityButton.setMaxWidth(Double.MAX_VALUE);
        pauseResumeActivityButton.prefWidth(Double.MAX_VALUE);
        pauseResumeActivityButton.setButtonType(ButtonType.RAISED);
        pauseResumeActivityButton.setOnAction(e -> {
            if (state.equals(EffortControllerState.RUNNING)) {
                pauseActivityAction();
            } else {
                resumeActivityAction();
            }
        });

        MFXButton finishActivity = new MFXButton("Finish Activity");
        finishActivity.setMaxWidth(Double.MAX_VALUE);
        finishActivity.prefWidth(Double.MAX_VALUE);
        finishActivity.setButtonType(ButtonType.RAISED);
        finishActivity.setOnAction(e -> {
            finishActivityAction();
        });

        gridPane.add(pauseResumeActivityButton, 0, 0);
        gridPane.add(finishActivity, 1, 0);
    }

    private void initSelector() {
        refreshSelector();

        userStorySelector.getSelectionModel().selectedItemProperty().addListener((options, oldValue, newValue) -> {
            if (newValue == null)
                return;

            startActivityButton.setDisable(false);
            taskTotalRunningTimeInSeconds = newValue.getRunningTimeInSeconds();
            taskSessionRunningTimeInSeconds = 0L;
            totalElapsedTimeLabel.setText(getRunningTime(taskTotalRunningTimeInSeconds));
            sessionElapsedTimeLabel.setText(getRunningTime(taskSessionRunningTimeInSeconds));
            state = EffortControllerState.STOPPED;
            updateClockLabel();
        });
    }

    private void startActivityAction() {
        this.state = EffortControllerState.RUNNING;
        taskSessionRunningTimeInSeconds = 0L;

        taskTimeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            taskSessionRunningTimeInSeconds += 1;
            taskTotalRunningTimeInSeconds += 1;
            sessionElapsedTimeLabel.setText(getRunningTime(taskSessionRunningTimeInSeconds));
            totalElapsedTimeLabel.setText(getRunningTime(taskTotalRunningTimeInSeconds));
        }));

        taskTimeline.setCycleCount(Timeline.INDEFINITE);
        taskTimeline.play();

        updateClockLabel();

        try {
            EffortLoggerController.getDatabase().addDateToUserStory(userStorySelector.getSelectedItem(), new Date(), "In Progress");
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }

        buttonVBox.getChildren().setAll(gridPane);
        userStorySelector.setDisable(true);
    }

    public void pauseActivityAction() {
        state = EffortControllerState.PAUSED;
        taskTimeline.pause();
        pauseResumeActivityButton.setText("Resume Activity");
        updateClockLabel();
        try {
            EffortLoggerController.getDatabase().addDateToUserStory(userStorySelector.getSelectedItem(), new Date(), "In Progress");
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
        userStorySelector.setDisable(false);
    }

    private void resumeActivityAction() {
        state = EffortControllerState.RUNNING;
        taskTimeline.play();
        pauseResumeActivityButton.setText("Pause Activity");
        taskSessionRunningTimeInSeconds = 0L;
        updateClockLabel();
        try {
            EffortLoggerController.getDatabase().addDateToUserStory(userStorySelector.getSelectedItem(), new Date(), "In Progress");
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
        userStorySelector.setDisable(true);
    }

    public void finishActivityAction() {
        if (state.equals(EffortControllerState.RUNNING)) {
            try {
                EffortLoggerController.getDatabase().addDateToUserStory(userStorySelector.getSelectedItem(), new Date(), "Finished");
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        } else {
            try {
                EffortLoggerController.getDatabase().setUserStoryStatus(userStorySelector.getSelectedItem(), "Finished");
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        }

        taskTimeline.stop();
        state = EffortControllerState.STOPPED;
        updateClockLabel();
        userStorySelector.getItems().remove(userStorySelector.getSelectedItem());
        userStorySelector.clearSelection();
        startActivityButton.setDisable(true);
        buttonVBox.getChildren().setAll(startActivityButton);

        userStorySelector.setDisable(false);
    }

    private void updateClockLabel() {
        switch (state) {
            case RUNNING:
                clockProgress.setText("CLOCK IS RUNNING");
                clockStatusVBox.setBackground(new Background(new BackgroundFill(Color.GREEN, CornerRadii.EMPTY, Insets.EMPTY)));
                break;
            case PAUSED:
                clockProgress.setText("CLOCK IS PAUSED");
                clockStatusVBox.setBackground(new Background(new BackgroundFill(Color.ORANGE, CornerRadii.EMPTY, Insets.EMPTY)));
                break;
            case STOPPED:
                clockProgress.setText("CLOCK IS STOPPED");
                clockStatusVBox.setBackground(new Background(new BackgroundFill(Color.RED, CornerRadii.EMPTY, Insets.EMPTY)));
                break;
        }
    }

    private String formatTimeNumber(long number) {
        if (number >= 10)
            return String.valueOf(number);
        else
            return "0" + number;
    }

    private String getRunningTime(long time) {
        long seconds = time % 60L;
        long minutes = TimeUnit.SECONDS.toMinutes(time) % 60L;
        long hours = TimeUnit.SECONDS.toHours(time);
        return String.format("%s:%s:%s", formatTimeNumber(hours), formatTimeNumber(minutes), formatTimeNumber(seconds));
    }

    public void refreshSelector() {
        try {
            userStorySelector.setItems(FXCollections.observableList(EffortLoggerController.getDatabase().getUserStories().stream().filter(userStory1 -> !userStory1.getStatus().equals("Finished")).collect(Collectors.toList())));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public enum EffortControllerState {
        RUNNING,
        PAUSED,
        STOPPED
    }
}
