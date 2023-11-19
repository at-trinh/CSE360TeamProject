package me.thfour.effortlogger.controllers;

import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXIconWrapper;
import io.github.palexdev.materialfx.controls.MFXRectangleToggleNode;
import io.github.palexdev.materialfx.controls.MFXScrollPane;
import io.github.palexdev.materialfx.dialogs.MFXGenericDialog;
import io.github.palexdev.materialfx.dialogs.MFXGenericDialogBuilder;
import io.github.palexdev.materialfx.dialogs.MFXStageDialog;
import io.github.palexdev.materialfx.enums.ButtonType;
import io.github.palexdev.materialfx.enums.ScrimPriority;
import io.github.palexdev.materialfx.utils.ScrollUtils;
import io.github.palexdev.materialfx.utils.ToggleButtonsUtil;
import io.github.palexdev.mfxcore.utils.loader.MFXLoader;
import io.github.palexdev.mfxcore.utils.loader.MFXLoaderBean;
import io.github.palexdev.mfxresources.fonts.MFXFontIcon;
import javafx.application.Platform;
import javafx.css.PseudoClass;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import me.thfour.effortlogger.ResourceLoader;
import me.thfour.effortlogger.models.Database;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;

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

    private ViewStoryController viewStoryController;
    private EffortConsoleController effortConsoleController;

    private MFXStageDialog closeDialog;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("making database");
        database = new Database();

        // set the correct path for the database depending on system specification
        try {
            String os = System.getProperty("os.name").toLowerCase();
            if (os.startsWith("windows")) {
                database.init(System.getProperty("user.home") + "/AppData/Roaming/EffortLogger/");
            } else if (os.startsWith("linux")) {
                database.init(System.getProperty("user.home") + ".effortlogger/");
            } else if (os.startsWith("mac")) {
                database.init(System.getProperty("user.home") + "/Desktop/EffortLogger/");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // basic action events for the close and minimize buttons
        closeIcon.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            if (effortConsoleController.getState().equals(EffortConsoleController.EffortControllerState.RUNNING))
                closeDialog.showDialog();
            else
                Platform.exit();
        });
        minimizeIcon.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> ((Stage) rootPane.getScene().getWindow()).setIconified(true));
        alwaysOnTopIcon.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            boolean newVal = !stage.isAlwaysOnTop();
            alwaysOnTopIcon.pseudoClassStateChanged(PseudoClass.getPseudoClass("always-on-top"), newVal);
            stage.setAlwaysOnTop(newVal);
        });

        windowHeader.setOnMousePressed(event -> {
            xOffset = stage.getX() - event.getScreenX();
            yOffset = stage.getY() - event.getScreenY();
        });
        windowHeader.setOnMouseDragged(event -> {
            stage.setX(event.getScreenX() + xOffset);
            stage.setY(event.getScreenY() + yOffset);
        });

        initializeLoader();
        initCloseDialog();

        // make it so the list is scrollable if the window size is reduced
        ScrollUtils.addSmoothScrolling(scrollPane);
    }

    private void initCloseDialog() {
        GridPane gridPane = new GridPane();
        gridPane.setPadding(new Insets(10));
        gridPane.setHgap(5);
        gridPane.setVgap(5);
        final ColumnConstraints col1 = new ColumnConstraints();
        col1.setHgrow(Priority.ALWAYS);
        final ColumnConstraints col2 = new ColumnConstraints();
        col2.setHgrow(Priority.ALWAYS);
        gridPane.getColumnConstraints().addAll(col1, col2);
        gridPane.setVgap(30);

        MFXButton pauseButton = new MFXButton("Pause Activity");
        pauseButton.setStyle("-fx-background-color: -mfx-orange; -fx-text-fill: white;");
        pauseButton.setButtonType(ButtonType.RAISED);
        pauseButton.setOnAction(e -> {
            effortConsoleController.pauseActivityAction();
            Platform.exit();
        });
        VBox pauseButtonBox = new VBox(pauseButton);
        pauseButtonBox.setAlignment(Pos.CENTER);

        MFXButton finishButton = new MFXButton("Finish Activity");
        finishButton.setStyle("-fx-background-color: -mfx-green; -fx-text-fill: white;");
        finishButton.setButtonType(ButtonType.RAISED);
        finishButton.setOnAction(e -> {
            effortConsoleController.finishActivityAction();
            Platform.exit();
        });
        VBox finishButtonBox = new VBox(finishButton);
        finishButtonBox.setAlignment(Pos.CENTER);

        String closeMessage = """
                You currently have an activity being recorded. Before you can
                close the program, you must choose whether you want to pause
                the activity or finish the activity otherwise your data can
                be lost or corrupted. What would you like to do?
                """;

        gridPane.add(new VBox(new Label(closeMessage)), 0, 0, 2, 1);
        gridPane.add(pauseButtonBox, 0, 1);
        gridPane.add(finishButtonBox, 1, 1);

        VBox vbox = new VBox();
        vbox.setFillWidth(true);
        vbox.getChildren().setAll(gridPane);

        MFXGenericDialog dialogContent = MFXGenericDialogBuilder.build()
                .setHeaderText("Confirm Close")
                .setContent(vbox)
                .get();

        closeDialog = MFXGenericDialogBuilder.build(dialogContent)
                .toStageDialogBuilder()
                .initOwner(this.stage)
                .initModality(Modality.APPLICATION_MODAL)
                .setDraggable(true)
                .setTitle("Confirm Close")
                .setOwnerNode(rootPane)
                .setScrimPriority(ScrimPriority.WINDOW)
                .setScrimOwner(true)
                .get();
    }

    private void initializeLoader() {
        // create a loader that loads in views with icons
        MFXLoader loader = new MFXLoader();
        loader.addView(MFXLoaderBean.of("PLANNING-POKER", ResourceLoader.loadURL("fxml/PlanningPoker.fxml")).setBeanToNodeMapper(() -> createToggle("fas-bullseye", "Planning Poker")).setDefaultRoot(true).get());
        loader.addView(MFXLoaderBean.of("EFFORT-CONSOLE", ResourceLoader.loadURL("fxml/EffortConsole.fxml")).setBeanToNodeMapper(() -> createToggle("fas-terminal", "Effort Console")).setControllerFactory(c -> new EffortConsoleController(this)).get());
        loader.addView(MFXLoaderBean.of("VIEW-STORY", ResourceLoader.loadURL("fxml/ViewStory.fxml")).setBeanToNodeMapper(() -> createToggle("fas-eye", "View Stories")).setControllerFactory(c -> new ViewStoryController(this, stage)).get());
        loader.addView(MFXLoaderBean.of("SETTINGS", ResourceLoader.loadURL("fxml/Settings.fxml")).setBeanToNodeMapper(() -> createToggle("fas-gear", "Settings")).get());

        // create the toggle buttons and add them to the navigation bar on the left side of the screen
        loader.setOnLoadedAction(beans -> {
            List<ToggleButton> nodes = beans.stream()
                    .map(bean -> {
                        ToggleButton toggle = (ToggleButton) bean.getBeanToNodeMapper().get();
                        if (bean.getViewName().equals("VIEW-STORY")) {
                            toggle.setOnAction(event -> {
                                contentPane.getChildren().setAll(bean.getRoot());
                                viewStoryController.refreshTable();
                            });
                        } else if (bean.getViewName().equals("EFFORT-CONSOLE")) {
                            toggle.setOnAction(event -> {
                                contentPane.getChildren().setAll(bean.getRoot());
                                effortConsoleController.refreshSelector();
                            });
                        } else {
                            toggle.setOnAction(event -> contentPane.getChildren().setAll(bean.getRoot()));
                        }
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

    public ViewStoryController getViewStoryController() {
        return viewStoryController;
    }

    public void setViewStoryController(ViewStoryController viewStoryController) {
        this.viewStoryController = viewStoryController;
    }

    public EffortConsoleController getEffortConsoleController() {
        return effortConsoleController;
    }

    public void setEffortConsoleController(EffortConsoleController effortConsoleController) {
        this.effortConsoleController = effortConsoleController;
    }
}
