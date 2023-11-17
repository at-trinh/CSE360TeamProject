package me.thfour.effortlogger.controllers;

import io.github.palexdev.materialfx.controls.*;
import io.github.palexdev.materialfx.controls.cell.MFXTableRowCell;
import io.github.palexdev.materialfx.dialogs.MFXGenericDialog;
import io.github.palexdev.materialfx.dialogs.MFXGenericDialogBuilder;
import io.github.palexdev.materialfx.dialogs.MFXStageDialog;
import io.github.palexdev.materialfx.enums.ButtonType;
import io.github.palexdev.materialfx.enums.FloatMode;
import io.github.palexdev.materialfx.enums.ScrimPriority;
import io.github.palexdev.materialfx.filter.IntegerFilter;
import io.github.palexdev.materialfx.filter.StringFilter;
import io.github.palexdev.materialfx.validation.Constraint;
import io.github.palexdev.materialfx.validation.Severity;
import io.github.palexdev.mfxresources.fonts.MFXFontIcon;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TextFormatter;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Pair;
import me.thfour.effortlogger.models.UserStory;

import java.net.URL;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class ViewStoryController implements Initializable {

    @FXML
    private MFXPaginatedTableView<UserStory> viewStoryTable;

    private final Stage stage;

    @FXML
    private GridPane pane;

    private final HashMap<MFXTextField, Label> fields = new HashMap<>();
    private int index = 0;
    private VBox defectVBox;
    private MFXToggleButton toggle;

    private GridPane gridPane;

    private final ArrayList<UserStory> registeredUserStories;

    private MFXStageDialog dialog;

    private Pair<Integer, UserStory> currentlyEditedUserStory = null;

    public ViewStoryController(EffortLoggerController effortLoggerController, Stage stage) {
        effortLoggerController.setViewStoryController(this);
        this.stage = stage;
        registeredUserStories = new ArrayList<>();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // initialize data columns
        MFXTableColumn<UserStory> projectColumn = new MFXTableColumn<>("Project", true, Comparator.comparing(UserStory::getProject));
        MFXTableColumn<UserStory> titleColumn = new MFXTableColumn<>("Title", true, Comparator.comparing(UserStory::getTitle));
        MFXTableColumn<UserStory> phaseColumn = new MFXTableColumn<>("Phase", true, Comparator.comparing(UserStory::getPhase));
        MFXTableColumn<UserStory> effortColumn = new MFXTableColumn<>("Effort Category", true, Comparator.comparing(UserStory::getEffortCategory));
        MFXTableColumn<UserStory> delivColumn = new MFXTableColumn<>("Deliverable", true, Comparator.comparing(UserStory::getDeliverable));
        MFXTableColumn<UserStory> statusColumn = new MFXTableColumn<>("Status", true, Comparator.comparing(UserStory::getStatus));
        MFXTableColumn<UserStory> descriptColumn = new MFXTableColumn<>("Description", true, Comparator.comparing(UserStory::getDescription));
        MFXTableColumn<UserStory> tagsColumn = new MFXTableColumn<>("Tags", true, Comparator.comparing(UserStory::getTags));
        MFXTableColumn<UserStory> storyColumn = new MFXTableColumn<>("Story Points", true, Comparator.comparing(UserStory::getStoryPoints));

        // create factorys for each of the columns
        projectColumn.setRowCellFactory(userStory -> new MFXTableRowCell<>(UserStory::getProject));
        titleColumn.setRowCellFactory(userStory -> new MFXTableRowCell<>(UserStory::getTitle));
        phaseColumn.setRowCellFactory(userStory -> new MFXTableRowCell<>(UserStory::getPhase));
        effortColumn.setRowCellFactory(userStory -> new MFXTableRowCell<>(UserStory::getEffortCategory));
        delivColumn.setRowCellFactory(userStory -> new MFXTableRowCell<>(UserStory::getDeliverable));
        statusColumn.setRowCellFactory(userStory -> new MFXTableRowCell<>(UserStory::getStatus));
        descriptColumn.setRowCellFactory(userStory -> new MFXTableRowCell<>(UserStory::getDescription));
        tagsColumn.setRowCellFactory(userStory -> new MFXTableRowCell<>(UserStory::getTags));
        storyColumn.setRowCellFactory(userStory -> new MFXTableRowCell<>(UserStory::getStoryPoints));


        viewStoryTable.getFilters().addAll(
                new StringFilter<>("Project", UserStory::getProject),
                new StringFilter<>("Title", UserStory::getTitle),
                new StringFilter<>("Phase", UserStory::getPhase),
                new StringFilter<>("Effort Category", UserStory::getEffortCategory),
                new StringFilter<>("Deliverable", UserStory::getDeliverable),
                new StringFilter<>("Status", UserStory::getStatus),
                new StringFilter<>("Description", UserStory::getDescription),
                new StringFilter<>("Tags", UserStory::getTags),
                new IntegerFilter<>("Story Points", UserStory::getStoryPoints)
        );

        // add columns to table
        viewStoryTable.getTableColumns().addAll(projectColumn, titleColumn, phaseColumn, effortColumn, delivColumn, statusColumn, descriptColumn, tagsColumn, storyColumn);

        viewStoryTable.setRowsPerPage(14);

        viewStoryTable.getSelectionModel().setAllowsMultipleSelection(false);
        registerTableEvents();
        initDialog();
        refreshTable();
    }

    private void initDialog() {
        createInputPane();
        fields.forEach((this::constraintBuilder));
        populateFields();
        Platform.runLater(this::buildDialog);
    }

    private void createInputPane() {
        gridPane = new GridPane();
        gridPane.setPadding(new Insets(10));
        gridPane.setHgap(5);
        gridPane.setVgap(5);
        final ColumnConstraints col1 = new ColumnConstraints();
        col1.setHgrow(Priority.ALWAYS);
        final ColumnConstraints col2 = new ColumnConstraints();
        col2.setHgrow(Priority.ALWAYS);
        gridPane.getColumnConstraints().addAll(col1, col2);
        Pair<MFXComboBox<String>, Label> projectPair = comboBoxBuilder("Project");
        Pair<MFXTextField, Label> titlePair = textFieldBuilder("Title");
        Pair<MFXComboBox<String>, Label> phasePair = comboBoxBuilder("Phase");
        Pair<MFXComboBox<String>, Label> effortPair = comboBoxBuilder("Effort Category");
        Pair<MFXComboBox<String>, Label> deliverablePair = comboBoxBuilder("Deliverable");
        Pair<MFXTextField, Label> tagsPair = textFieldBuilder("Tags");
        Pair<MFXTextField, Label> descriptionPair = textFieldBuilder("Description");
        Pair<MFXComboBox<String>, Label> statusPair = comboBoxBuilder("Status");
        Pair<MFXTextField, Label> datesPair = textFieldBuilder("Dates");

        gridPane.add(new VBox(projectPair.getKey(), projectPair.getValue()), 0, 0);
        gridPane.add(new VBox(titlePair.getKey(), titlePair.getValue()), 1, 0);
        gridPane.add(new VBox(phasePair.getKey(), phasePair.getValue()), 0, 1);
        gridPane.add(new VBox(effortPair.getKey(), effortPair.getValue()), 1, 1);
        gridPane.add(new VBox(deliverablePair.getKey(), deliverablePair.getValue()), 0, 2);
        gridPane.add(new VBox(tagsPair.getKey(), tagsPair.getValue()), 1, 2);
        gridPane.add(new VBox(descriptionPair.getKey(), descriptionPair.getValue()), 0, 3, 2, 1);
        gridPane.add(new VBox(statusPair.getKey(), statusPair.getValue()), 1, 5);
        gridPane.add(new VBox(datesPair.getKey(), datesPair.getValue()), 0, 6, 2, 1);

        toggle = new MFXToggleButton("Defect?");
        toggle.setOnAction(e -> {
            defectVBox.setVisible(toggle.isSelected());
        });
        Pair<MFXComboBox<String>, Label> defectPair = comboBoxBuilder("Defect Category");
        defectVBox = new VBox(defectPair.getKey(), defectPair.getValue());
        gridPane.add(toggle, 0, 4);
        gridPane.add(defectVBox, 1, 4);

        MFXButton saveButton = new MFXButton("Save");
        saveButton.setId("custom");
        saveButton.setButtonType(ButtonType.RAISED);
        saveButton.setStyle("-fx-background-color: -mfx-purple; -fx-text-fill: white;");
        saveButton.setMaxWidth(60);
        saveButton.setTextAlignment(TextAlignment.CENTER);
        saveButton.setOnAction(e -> {
            if (isEmptyField())
                return;

            try {
                UserStory newStory = buildUserStoryFromDialog();
                EffortLoggerController.getDatabase().updateUserStory(newStory);
                viewStoryTable.getItems().set(currentlyEditedUserStory.getKey(), newStory);
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            } catch (IllegalArgumentException ex) { /* noop */ }
            dialog.close();
        });

        VBox saveButtonVBox = new VBox(saveButton);
        saveButtonVBox.setAlignment(Pos.CENTER_RIGHT);
        gridPane.add(saveButtonVBox, 1, 7);

        Pair<MFXTextField, Label> storyPointsPair = textFieldBuilder("Story Points");
        storyPointsPair.getKey().setTextFormatter(new TextFormatter<>(change -> {
            String text = change.getControlNewText();

            if (text.matches("[0-9]*")) {
                return change;
            }

            return null;
        }));
        gridPane.add(new VBox(storyPointsPair.getKey(), storyPointsPair.getValue()), 0, 5);

        MFXTooltip.of(
                projectPair.getKey(),
                """
                     Enter the name of your project here.
                     If you have already entered a user story with your project name, you can also select the project by clicking the drop down arrow on the right of the text field.
                     """
        ).install();

        MFXTooltip.of(
                titlePair.getKey(),
                "Enter the title of your user story here."
        ).install();

        MFXTooltip.of(
                phasePair.getKey(),
                """
                     Enter the phase that this user story is part of.
                     If you have already entered a user story with your phase, you can also select the project by clicking the drop down arrow on the right of the text field.
                     Some phases are Requirements, Conceptual Design, Test Case Generation, Reflection, Planning, Drafting, and Finalizing.
                     """
        ).install();

        MFXTooltip.of(
                effortPair.getKey(),
                """
                     Enter the effort category that this user story is part of.
                     If you have already entered a user story with your effort category, you can also select the project by clicking the drop down arrow on the right of the text field.
                     Some effort categories are Plans, Deliverables, Interruptions, and Defects. 
                     """
        ).install();

        MFXTooltip.of(
                deliverablePair.getKey(),
                """
                     Enter the deliverable that this user story is part of.
                     If you have already entered a user story with your deliverable category, you can also select the project by clicking the drop down arrow on the right of the text field.
                     Some deliverable categories are Conceptual Design, Reflection, Outline, Solution, Report, and Test Cases.
                     """
        ).install();

        MFXTooltip.of(
                tagsPair.getKey(),
                """
                     Enter some tags that help define your user story.
                     Some examples include the programming language, feature, bug, and libraries.
                     """
        ).install();

        MFXTooltip.of(
                descriptionPair.getKey(),
                """
                     Enter a description so that you can search for your user story later or just jog your memory.
                     """
        ).install();

        MFXTooltip.of(
                defectPair.getKey(),
                """
                     Enter the defect category that this user story is part of.
                     If you have already entered a user story with your defect category, you can also select the project by clicking the drop down arrow on the right of the text field.
                     Some defect categories are Documentation, Syntax, Package, Function, Data, System, and Interface.
                     """
        ).install();


        VBox vbox = new VBox();
        vbox.setFillWidth(true);
        vbox.getChildren().setAll(gridPane);
//        content.setContent(vbox);
//        content.setFitToWidth(true);

        fields.put(projectPair.getKey(), projectPair.getValue());
        fields.put(titlePair.getKey(), titlePair.getValue());
        fields.put(phasePair.getKey(), phasePair.getValue());
        fields.put(effortPair.getKey(), effortPair.getValue());
        fields.put(deliverablePair.getKey(), deliverablePair.getValue());
        fields.put(tagsPair.getKey(), tagsPair.getValue());
        fields.put(descriptionPair.getKey(), descriptionPair.getValue());
        fields.put(defectPair.getKey(), defectPair.getValue());
        fields.put(storyPointsPair.getKey(), storyPointsPair.getValue());
        fields.put(statusPair.getKey(), statusPair.getValue());
        fields.put(datesPair.getKey(), datesPair.getValue());
    }

    /**
     * Adds constraints to each of the textfields to make sure they aren't empty
     * @param field text field
     * @param label label for text field
     */
    private void constraintBuilder(MFXTextField field, Label label) {
        if (field.getFloatingText().equals("Dates"))
            return;

        Constraint constraint = Constraint.Builder.build()
                .setSeverity(Severity.ERROR)
                .setMessage(field.getFloatingText() + " can not be empty")
                .setCondition(field.textProperty().length().greaterThan(0))
                .get();

        field.getValidator()
                .constraint(constraint);

        field.getValidator().validProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                label.setVisible(false);
                //field.pseudoClassStateChanged(INVALID_PSEUDO_CLASS, false);
            }
        });

        field.delegateFocusedProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue && !newValue) {
                List<Constraint> constraints = field.validate();
                if (!constraints.isEmpty()) {
                    //field.pseudoClassStateChanged(INVALID_PSEUDO_CLASS, true);
                    label.setVisible(true);
                }
            }
        });

        //label.setVisible(true);
    }

    private void populateFields() {
        for (MFXTextField field : fields.keySet()) {
            switch (field.getFloatingText()) {
                case "Project":
                    try {
                        ((MFXComboBox<String>) field).setItems(FXCollections.observableList(EffortLoggerController.getDatabase().getUniqueValues("PROJECT")));
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                    break;
                case "Phase":
                    try {
                        ((MFXComboBox<String>) field).setItems(FXCollections.observableList(EffortLoggerController.getDatabase().getUniqueValues("PHASE")));
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                    break;
                case "Effort Category":
                    try {
                        ((MFXComboBox<String>) field).setItems(FXCollections.observableList(EffortLoggerController.getDatabase().getUniqueValues("EffortCategory")));
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                    break;
                case "Deliverable":
                    try {
                        ((MFXComboBox<String>) field).setItems(FXCollections.observableList(EffortLoggerController.getDatabase().getUniqueValues("Deliverable")));
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                    break;
                case "Defect Category":
                    try {
                        ((MFXComboBox<String>) field).setItems(FXCollections.observableList(EffortLoggerController.getDatabase().getUniqueValues("DefectCategory")));
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                    break;
                case "Status":
                    try {
                        ((MFXComboBox<String>) field).setItems(FXCollections.observableList(EffortLoggerController.getDatabase().getUniqueValues("Status")));
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                    break;
            }
        }
    }

    private void buildDialog() {
        VBox vbox = new VBox();
        vbox.setFillWidth(true);
        vbox.getChildren().setAll(gridPane);

        MFXGenericDialog dialogContent = MFXGenericDialogBuilder.build()
                .setHeaderText("Edit User Story")
                .setContent(vbox)
                .get();

        dialog = MFXGenericDialogBuilder.build(dialogContent)
                .toStageDialogBuilder()
                .initOwner(stage)
                .initModality(Modality.APPLICATION_MODAL)
                .setDraggable(true)
                .setTitle("View User Story")
                .setOwnerNode(pane)
                .setScrimPriority(ScrimPriority.WINDOW)
                .setScrimOwner(true)
                .get();
    }

    private Pair<MFXComboBox<String>, Label> comboBoxBuilder(String fieldName) {
        MFXComboBox<String> mfxComboBox = new MFXComboBox<>();
        mfxComboBox.setFloatMode(FloatMode.BORDER);
        mfxComboBox.setFloatingText(fieldName);
        mfxComboBox.setAllowEdit(true);
        mfxComboBox.setMinWidth(200);
        mfxComboBox.setMaxWidth(Double.MAX_VALUE);
        Label label = new Label(String.format("%s must not be empty", fieldName));
        label.setVisible(false);
        label.setTextFill(Color.web("cc0000"));
        label.setPadding(new Insets(0, 0, 0, 5));
        return new Pair<>(mfxComboBox, label);
    }

    private Pair<MFXTextField, Label> textFieldBuilder(String fieldName) {
        MFXTextField textField = new MFXTextField();
        textField.setFloatMode(FloatMode.BORDER);
        textField.setFloatingText(fieldName);
        textField.setAllowEdit(true);
        textField.setMinWidth(200);
        textField.setMaxWidth(Double.MAX_VALUE);
        Label label = new Label(String.format("%s must not be empty", fieldName));
        label.setVisible(false);
        label.setTextFill(Color.web("#cc0000"));
        label.setPadding(new Insets(0, 0, 0, 5));
        return new Pair<>(textField, label);
    }

    private void restoreStory(UserStory story) {
        toggle.setSelected(story.isDefect());
        for (MFXTextField field : fields.keySet()) {
            switch (field.getFloatingText()) {
                case "Project":
                    field.setText(story.getProject());
                    break;
                case "Title":
                    field.setText(story.getTitle());
                    break;
                case "Phase":
                    field.setText(story.getPhase());
                    break;
                case "Effort Category":
                    field.setText(story.getEffortCategory());
                    break;
                case "Deliverable":
                    field.setText(story.getDeliverable());
                    break;
                case "Description":
                    field.setText(story.getDescription());
                    break;
                case "Tags":
                    field.setText(story.getTags());
                    break;
                case "Story Points":
                    field.setText(String.valueOf(story.getStoryPoints()));
                    break;
                case "Defect Category":
                    field.setText(story.getDefectCategory());
                    field.getParent().setVisible(story.isDefect());
                    break;
                case "Dates":
                    field.setText((story.getDates() == null) ? "" : story.getDates());
                    break;
                case "Status":
                    field.setText(story.getStatus());
                    break;
            }
        }
    }

    public void refreshTable() {
        // populate table once again
        refreshUserStoryRows();

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> registerRowEvents());
            }
        }, 1000);
    }

    private void deleteUserStory(UserStory userStory) throws SQLException {
        viewStoryTable.getItems().remove(userStory);
        EffortLoggerController.getDatabase().deleteStoryFromTable(userStory.getStoryId());
        registeredUserStories.remove(userStory);
    }

    private void editUserStory(Integer index, UserStory userStory) {
        restoreStory(userStory);
        dialog.showDialog();
        currentlyEditedUserStory = new Pair<>(index, userStory);
    }

    private void refreshUserStoryRows() {
        ArrayList<UserStory> userStories = null;
        try {
            userStories = EffortLoggerController.getDatabase().getUserStories();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        viewStoryTable.setItems(FXCollections.observableArrayList(userStories));
    }

    private void registerRowEvents() {
        MFXContextMenuItem deleteItem = MFXContextMenuItem.Builder.build().setIcon(new MFXFontIcon("fas-delete-left", 16)).setText("Delete entry").setOnAction(e -> {
            try {
                deleteUserStory(viewStoryTable.getSelectionModel().getSelectedValue());
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        }).setAccelerator("Del").get();

        MFXContextMenuItem editItem = MFXContextMenuItem.Builder.build().setIcon(new MFXFontIcon("fas-pencil", 16)).setText("Edit entry").setOnAction(e -> {
            viewStoryTable.getSelectionModel().getSelection().forEach(this::editUserStory);
        }).setAccelerator("E").get();

        viewStoryTable.getCells().forEach((index, mfxTableRow) -> {
            if (registeredUserStories.contains(mfxTableRow.getData()))
                return;

            MFXContextMenu contextMenu = MFXContextMenu.Builder.build(mfxTableRow)
                    .addItem(editItem)
                    .addItem(deleteItem)
                    .setPopupStyleableParent(viewStoryTable)
                    .installAndGet();

            mfxTableRow.addEventFilter(MouseEvent.MOUSE_CLICKED, e -> {
                if (e.getButton() == MouseButton.PRIMARY && e.getClickCount() == 2) {
                    editUserStory(index, mfxTableRow.getData());
                }
            });
        });
    }

    private UserStory buildUserStoryFromDialog() {
        String project = null;
        String title = null;
        String phase = null;
        String effortCategory = null;
        String deliverable = null;
        String description  = null;
        String tags  = null;
        String dates = null;
        String status = null;
        int storyPoints = -1;
        boolean isDefect = toggle.isSelected();
        String defectCategory = null;
        for (MFXTextField field : fields.keySet()) {
            switch (field.getFloatingText()) {
                case "Project":
                    project = field.getText();
                    break;
                case "Title":
                    title = field.getText();
                    break;
                case "Phase":
                    phase = field.getText();
                    break;
                case "Effort Category":
                    effortCategory = field.getText();
                    break;
                case "Deliverable":
                    deliverable = field.getText();
                    break;
                case "Description":
                    description = field.getText();
                    break;
                case "Tags":
                    tags = field.getText();
                    break;
                case "Story Points":
                    try {
                        storyPoints = Integer.parseInt(field.getText());
                    } catch (NumberFormatException e) {
                        storyPoints = 0;
                    }
                    break;
                case "Defect Category":
                    defectCategory = field.getText();
                    break;
                case "Dates":
                    dates = field.getText();
                    break;
                case "Status":
                    status = field.getText();
                    break;
            }
        }

        return new UserStory(
                currentlyEditedUserStory.getValue().getStoryId(),
                project,
                title,
                phase,
                effortCategory,
                deliverable,
                status,
                description,
                tags,
                storyPoints,
                dates,
                isDefect,
                defectCategory
        );
    }

    private void registerTableEvents() {
        viewStoryTable.addEventHandler(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode() == KeyCode.DELETE) {
                try {
                    deleteUserStory(viewStoryTable.getSelectionModel().getSelectedValue());
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }

            if (event.getCode() == KeyCode.E) {
                viewStoryTable.getSelectionModel().getSelection().forEach(this::editUserStory);
            }
        });
    }

    private boolean isEmptyField() {
        AtomicBoolean shouldReturn = new AtomicBoolean(false);
        fields.forEach((field, label) -> {
            if (field.getText().isEmpty() && !field.getFloatingText().equals("Defect Category") && !field.getFloatingText().equals("Dates")) {
                label.setVisible(true);
                shouldReturn.set(true);
            } else if (field.getFloatingText().equals("Defect Category") && toggle.isSelected() && field.getText().isEmpty()) {
                label.setVisible(true);
                shouldReturn.set(true);
            }
        });

        return shouldReturn.get();
    }
}
