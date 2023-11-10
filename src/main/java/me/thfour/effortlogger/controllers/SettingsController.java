package me.thfour.effortlogger.controllers;

import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.stage.FileChooser;

import java.io.*;

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
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    // TODO
    private static void writeFile(File file) throws IOException {
        PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(file)));
        writer.println("im alive!");
        writer.println("test 2");
        writer.close();
    }

    // TODO
    private static void readFile(File file) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(file));
        String st;
        while ((st = reader.readLine()) != null) {
            System.out.println(st);
        }
        reader.close();
    }


}
