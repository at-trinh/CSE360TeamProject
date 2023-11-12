package me.thfour.effortlogger.controllers;

import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.stage.FileChooser;
import me.thfour.effortlogger.models.Database;
import me.thfour.effortlogger.models.Settings;
import me.thfour.effortlogger.models.UserStory;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;

import java.io.*;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class SettingsController implements Initializable {

    @FXML
    private MFXTextField expField;

    @FXML
    private MFXTextField impField;

    @FXML
    private MFXTextField usernameField;

    @FXML
    void expEvent(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Export User Stories");
        fileChooser.setInitialDirectory(
                new File(System.getProperty("user.home") + "/Desktop/")
        );
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("CSV", "*.csv")
        );

        File file = fileChooser.showSaveDialog(usernameField.getScene().getWindow());
        if (file != null) {
            try {
                writeFile(file);
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @FXML
    void impEvent(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Import User Stories");
        fileChooser.setInitialDirectory(
                new File(System.getProperty("user.home") + "/Desktop/")
        );
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("CSV", "*.csv")
        );

        File file = fileChooser.showOpenDialog(usernameField.getScene().getWindow());
        if (file != null) {
            try {
                readFile(file);
            } catch (IOException | SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @FXML
    void processUpdateUsername(ActionEvent event) {
        try {
            EffortLoggerController.getDatabase().setUsername(usernameField.getText());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void writeFile(File file) throws IOException, SQLException {
        PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(file)));
        CSVPrinter printer = new CSVPrinter(writer, CSVFormat.EXCEL);
        ArrayList<UserStory> userStories = EffortLoggerController.getDatabase().getUserStories();
        for (UserStory story : userStories) {
            printer.printRecord(getFormattedString(story));
        }
        printer.flush();
    }

    private void readFile(File file) throws IOException, SQLException {
        Reader in = new FileReader(file);
        Iterable<CSVRecord> records = CSVFormat.EXCEL.withHeader(impField.getText().split(",")).parse(in);
        for (CSVRecord record : records) {
            UserStory story = new UserStory(
                    record.get("project"),
                    record.get("title"),
                    record.get("phase"),
                    record.get("effort_category"),
                    record.get("deliverable"),
                    record.get("status"),
                    record.get("description"),
                    record.get("tags"),
                    Integer.parseInt(record.get("story_points")),
                    record.get("dates"),
                    Boolean.parseBoolean(record.get("is_defect")),
                    record.get("defect_category")
            );
            EffortLoggerController.getDatabase().addExistingUserStory(story);
        }
    }

    private String[] getFormattedString(UserStory story) {
        String format = expField.getText();
        String[] entries = format.split(",");
        for (int i = 0; i < entries.length; i++) {
            entries[i] = entries[i]
                    .replace("project", story.getProject())
                    .replace("title", story.getTitle())
                    .replace("phase", story.getPhase())
                    .replace("effort_category", story.getEffortCategory())
                    .replace("deliverable", story.getDeliverable())
                    .replace("status", story.getStatus())
                    .replace("description", story.getDescription())
                    .replace("tags", story.getTags())
                    .replace("story_points", String.valueOf(story.getStoryPoints()))
                    .replace("dates", story.getDates())
                    .replace("is_defect", String.valueOf(story.isDefect()))
                    .replace("defect_category", story.getDefectCategory());
        }
        return entries;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Settings settings;
        try {
            settings = EffortLoggerController.getDatabase().getSettings();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        usernameField.setText(settings.getUsername());
    }
}
