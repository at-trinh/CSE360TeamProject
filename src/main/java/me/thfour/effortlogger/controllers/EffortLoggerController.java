package me.thfour.effortlogger.controllers;

import io.github.palexdev.materialfx.controls.MFXIconWrapper;
import io.github.palexdev.materialfx.controls.MFXRectangleToggleNode;
import io.github.palexdev.materialfx.controls.MFXScrollPane;
import io.github.palexdev.materialfx.utils.ScrollUtils;
import io.github.palexdev.materialfx.utils.ToggleButtonsUtil;
import io.github.palexdev.mfxcore.utils.loader.MFXLoader;
import io.github.palexdev.mfxcore.utils.loader.MFXLoaderBean;
import io.github.palexdev.mfxresources.fonts.MFXFontIcon;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import me.thfour.effortlogger.ResourceLoader;
import me.thfour.effortlogger.models.Database;

import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;

public class EffortLoggerController implements Initializable {

    private final Stage stage;
    private double xOffset;
    private double yOffset;

    private final ToggleGroup toggleGroup;

    @FXML
    private HBox windowHeader;

    @FXML
    private MFXFontIcon closeIcon;

    @FXML
    private MFXFontIcon minimizeIcon;

    @FXML
    private MFXFontIcon alwaysOnTopIcon;

    @FXML
    private AnchorPane rootPane;

    @FXML
    private MFXScrollPane scrollPane;

    @FXML
    private VBox navBar;

    @FXML
    private StackPane contentPane;

    @FXML
    private StackPane logoContainer;

    private static Database database;

    public EffortLoggerController(Stage stage) {
        this.stage = stage;
        this.toggleGroup = new ToggleGroup();
        ToggleButtonsUtil.addAlwaysOneSelectedSupport(toggleGroup);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("making database");
        database = new Database();

        // set the correct path for the database depending on system specification
        try {
            String os = System.getProperty("os.name").toLowerCase();
            if (os.startsWith("windows")) {
                database.init(System.getProperty("user.home") + "/AppData/EffortLogger/");
            } else if (os.startsWith("linux")) {
                database.init(System.getProperty("user.home") + ".effortlogger/");
            } else if (os.startsWith("mac")) {
                database.init(System.getProperty("user.home") + "/Desktop/EffortLogger/");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // basic action events for the close and minimize buttons
        closeIcon.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> Platform.exit());
        minimizeIcon.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> ((Stage) rootPane.getScene().getWindow()).setIconified(true));

        initializeLoader();

        // make it so the list is scrollable if the window size is reduced
        ScrollUtils.addSmoothScrolling(scrollPane);
    }

    private void initializeLoader() {
        // create a loader that loads in views with icons
        MFXLoader loader = new MFXLoader();
        loader.addView(MFXLoaderBean.of("PLANNING-POKER", ResourceLoader.loadURL("fxml/PlanningPoker.fxml")).setBeanToNodeMapper(() -> createToggle("fas-circle-dot", "Planning Poker")).setDefaultRoot(true).get());
        loader.addView(MFXLoaderBean.of("VIEW-STORY", ResourceLoader.loadURL("fxml/ViewStory.fxml")).setBeanToNodeMapper(() -> createToggle("fas-circle-dot", "View Stories")).get());
        loader.addView(MFXLoaderBean.of("SETTINGS", ResourceLoader.loadURL("fxml/Settings.fxml")).setBeanToNodeMapper(() -> createToggle("fas-circle-dot", "Settings")).get());

        // create the toggle buttons and add them to the navigation bar on the left side of the screen
        loader.setOnLoadedAction(beans -> {
            List<ToggleButton> nodes = beans.stream()
                    .map(bean -> {
                        ToggleButton toggle = (ToggleButton) bean.getBeanToNodeMapper().get();
                        toggle.setOnAction(event -> contentPane.getChildren().setAll(bean.getRoot()));
                        if (bean.isDefaultView()) {
                            contentPane.getChildren().setAll(bean.getRoot());
                            toggle.setSelected(true);
                        }
                        return toggle;
                    }).toList();
            navBar.getChildren().setAll(nodes);
        });
        loader.start();
    }

    private ToggleButton createToggle(String icon, String text) {
        return createToggle(icon, text, 0);
    }

    // used to make the toggle buttons
    private ToggleButton createToggle(String icon, String text, double rotate) {
        MFXIconWrapper wrapper = new MFXIconWrapper(icon, 24, 32);
        MFXRectangleToggleNode toggleNode = new MFXRectangleToggleNode(text, wrapper);
        toggleNode.setAlignment(Pos.CENTER_LEFT);
        toggleNode.setMaxWidth(Double.MAX_VALUE);
        toggleNode.setToggleGroup(toggleGroup);
        if (rotate != 0)
            wrapper.getIcon().setRotate(rotate);
        return toggleNode;
    }

    public static Database getDatabase() {
        return database;
    }
}
