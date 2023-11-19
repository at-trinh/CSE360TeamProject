package me.thfour.effortlogger.controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import me.thfour.effortlogger.HelloApplication;
import me.thfour.effortlogger.ResourceLoader;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class SplashScreenController implements Initializable {

    @FXML
    private StackPane pane;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Thread thread = new SplashScreen();
        thread.start();
    }

    public class SplashScreen extends Thread {
        @Override
        public void run() {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            Platform.runLater(() -> {
                Stage stage = new Stage();
                FXMLLoader fxmlLoader = new FXMLLoader(ResourceLoader.loadURL("fxml/EffortLogger.fxml")); // load main menu
                fxmlLoader.setControllerFactory(c -> new EffortLoggerController(stage)); // set controller for main menu. this
                // controller dictates the control path
                // for the entire progra
                // init window
                Parent root = null;
                try {
                    root = fxmlLoader.load();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                Scene scene = new Scene(root);
                stage.initStyle(StageStyle.TRANSPARENT); // gets rid of system top bar
                scene.setFill(Color.TRANSPARENT);
                stage.setScene(scene);
                stage.setTitle("EffortLogger 2.0");
                stage.show();
                HelloApplication.getApplication().killSplashScreen();
            });
        }
    }
}
