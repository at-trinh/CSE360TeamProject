package me.thfour.effortlogger.controllers;

import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.stage.FileChooser;
import me.thfour.effortlogger.models.UserStory;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import java.io.*;
import java.sql.SQLException;

public class SettingsController {

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

    // TODO
    private void writeFile(File file) throws IOException {
        PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(file)));
        writer.println("im alive!");
        writer.println("test 2");
        writer.close();
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
}
