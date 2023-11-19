package me.thfour.effortlogger;

import io.github.palexdev.materialfx.theming.JavaFXThemes;
import io.github.palexdev.materialfx.theming.MaterialFXStylesheets;
import io.github.palexdev.materialfx.theming.UserAgentBuilder;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

public class MainApplication extends Application {

    private static MainApplication application;
    private Stage stage;

    @Override
    public void start(Stage stage) throws IOException {
        //CSSFX.start(); // ONLY UNCOMMENTED WHEN DEVELOPING. LEAVE COMMENTED FOR RELEASE OR THE PROGRAM WON'T WORK
        application = this;
        this.stage = stage;

        // CSS Themeing
        UserAgentBuilder.builder()
                .themes(JavaFXThemes.MODENA)
                .themes(MaterialFXStylesheets.forAssemble(true))
                .setDeploy(true)
                .setResolveAssets(true)
                .build()
                .setGlobal();

        Parent root = FXMLLoader.load(ResourceLoader.loadURL("fxml/SplashScreen.fxml"));
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.initStyle(StageStyle.UNDECORATED); // gets rid of system top bar
        stage.setTitle("EffortLogger 2.0");
        stage.show();
    }

    public static MainApplication getApplication() {
        return application;
    }

    public void killSplashScreen() {
        this.stage.close();
    }

    public static void main(String[] args) {
        launch();
    }
}