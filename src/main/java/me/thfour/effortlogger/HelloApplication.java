package me.thfour.effortlogger;

import io.github.palexdev.materialfx.css.themes.MFXThemeManager;
import io.github.palexdev.materialfx.css.themes.Themes;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import me.thfour.effortlogger.controllers.EffortLoggerController;

import java.io.IOException;

public class HelloApplication extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        //CSSFX.start(); // ONLY UNCOMMENTED WHEN DEVELOPING. LEAVE COMMENTED FOR RELEASE OR THE PROGRAM WON'T WORK

        FXMLLoader fxmlLoader = new FXMLLoader(ResourceLoader.loadURL("fxml/EffortLogger.fxml")); // load main menu
        fxmlLoader.setControllerFactory(c -> new EffortLoggerController(stage)); // set controller for main menu. this
                                                                                // controller dictates the control path
                                                                                // for the entire program
        // init window
        Parent root = fxmlLoader.load();
        Scene scene = new Scene(root);
        MFXThemeManager.addOn(scene, Themes.DEFAULT, Themes.LEGACY); // CAUSES MAJOR PERFORMANCE ISSUES
        //stage.initStyle(StageStyle.TRANSPARENT); // gets rid of system top bar
        scene.setFill(Color.TRANSPARENT);
        stage.setScene(scene);
        stage.setTitle("EffortLogger 2.0");
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}