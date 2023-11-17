package me.thfour.effortlogger.controllers;

import io.github.palexdev.materialfx.controls.*;
import io.github.palexdev.materialfx.controls.cell.MFXTableRowCell;
import io.github.palexdev.materialfx.enums.ButtonType;
import io.github.palexdev.materialfx.enums.FloatMode;
import io.github.palexdev.materialfx.filter.StringFilter;
import io.github.palexdev.materialfx.validation.Constraint;
import io.github.palexdev.materialfx.validation.Severity;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.util.Pair;
import me.thfour.effortlogger.models.UserStory;

import java.net.URL;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class PlanningPokerController implements Initializable {

    @FXML
    private MFXButton doneButton;

    @FXML
    private MFXButton nextButton;

    @FXML
    private MFXButton previousButton;

    @FXML
    private MFXPaginatedTableView<UserStory> paginated;

    @FXML
    private ScrollPane content;

    private final HashMap<MFXTextField, Label> fields = new HashMap<>();
    private int index = 0;
    private final ArrayList<UserStory> userStories = new ArrayList<>();
    private VBox defectVBox;
    private MFXToggleButton toggle;

    private ArrayList<StringFilter<UserStory>> filters;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        createInputPane();
        fields.forEach((this::constraintBuilder));
        populateTable();
        populateFields();
//        doReflection();
    }

    private void populateTable() {
        // get user stories from database
        ArrayList<UserStory> userStories = null;
        try {
            userStories = EffortLoggerController.getDatabase().getUserStories();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        // initialize data columns
        MFXTableColumn<UserStory> projectColumn = new MFXTableColumn<>("Project", true, Comparator.comparing(UserStory::getProject));
        MFXTableColumn<UserStory> titleColumn = new MFXTableColumn<>("Title", true, Comparator.comparing(UserStory::getTitle));
        MFXTableColumn<UserStory> phaseColumn = new MFXTableColumn<>("Phase", true, Comparator.comparing(UserStory::getPhase));
        MFXTableColumn<UserStory> effortColumn = new MFXTableColumn<>("Effort Category", true, Comparator.comparing(UserStory::getEffortCategory));
        MFXTableColumn<UserStory> delivColumn = new MFXTableColumn<>("Deliverable", true, Comparator.comparing(UserStory::getDeliverable));
        MFXTableColumn<UserStory> descriptColumn = new MFXTableColumn<>("Description", true, Comparator.comparing(UserStory::getDescription));
        MFXTableColumn<UserStory> tagsColumn = new MFXTableColumn<>("Tags", true, Comparator.comparing(UserStory::getTags));
        MFXTableColumn<UserStory> storyColumn = new MFXTableColumn<>("Story Points", true, Comparator.comparing(UserStory::getStoryPoints));

        // create factorys for each of the columns
        projectColumn.setRowCellFactory(userStory -> new MFXTableRowCell<>(UserStory::getProject));
        titleColumn.setRowCellFactory(userStory -> new MFXTableRowCell<>(UserStory::getTitle));
        phaseColumn.setRowCellFactory(userStory -> new MFXTableRowCell<>(UserStory::getPhase));
        effortColumn.setRowCellFactory(userStory -> new MFXTableRowCell<>(UserStory::getEffortCategory));
        delivColumn.setRowCellFactory(userStory -> new MFXTableRowCell<>(UserStory::getDeliverable));
        descriptColumn.setRowCellFactory(userStory -> new MFXTableRowCell<>(UserStory::getDescription));
        tagsColumn.setRowCellFactory(userStory -> new MFXTableRowCell<>(UserStory::getTags));
        storyColumn.setRowCellFactory(userStory -> new MFXTableRowCell<>(UserStory::getStoryPoints));

        // add columns to table
        paginated.getTableColumns().addAll(projectColumn, titleColumn, phaseColumn, effortColumn, delivColumn, descriptColumn, tagsColumn, storyColumn);

        filters = new ArrayList<>();
        filters.add(new StringFilter<>("Project", UserStory::getProject));
        filters.add(new StringFilter<>("Phase", UserStory::getPhase));
        filters.add(new StringFilter<>("Effort Category", UserStory::getEffortCategory));
        filters.add(new StringFilter<>("Deliverable", UserStory::getDeliverable));
        filters.add(new StringFilter<>("Defect Category", UserStory::getDefectCategory));
        paginated.getFilters().addAll(filters);

        // populate table
        paginated.setItems(FXCollections.observableArrayList(userStories));
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
            }
        }
    }

    private void createInputPane() {
        GridPane gridPane = new GridPane();
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

        gridPane.add(new VBox(projectPair.getKey(), projectPair.getValue()), 0, 0);
        gridPane.add(new VBox(titlePair.getKey(), titlePair.getValue()), 1, 0);
        gridPane.add(new VBox(phasePair.getKey(), phasePair.getValue()), 0, 1);
        gridPane.add(new VBox(effortPair.getKey(), effortPair.getValue()), 1, 1);
        gridPane.add(new VBox(deliverablePair.getKey(), deliverablePair.getValue()), 0, 2);
        gridPane.add(new VBox(tagsPair.getKey(), tagsPair.getValue()), 1, 2);
        gridPane.add(new VBox(descriptionPair.getKey(), descriptionPair.getValue()), 0, 3, 2, 1);

        this.toggle = new MFXToggleButton("Defect?");
        toggle.setOnAction(e -> {
            defectVBox.setVisible(toggle.isSelected());
        });
        Pair<MFXComboBox<String>, Label> defectPair = comboBoxBuilder("Defect Category");
        defectVBox = new VBox(defectPair.getKey(), defectPair.getValue());
        defectVBox.setVisible(false);
        gridPane.add(toggle, 0, 4);
        gridPane.add(defectVBox, 1, 4);

        Pair<MFXTextField, Label> storyPointsPair = textFieldBuilder("Story Points");
        storyPointsPair.getKey().setTextFormatter(new TextFormatter<>(change -> {
            String text = change.getControlNewText();

            if (text.matches("[0-9]*")) {
                return change;
            }

            return null;
        }));
        gridPane.add(new VBox(storyPointsPair.getKey(), storyPointsPair.getValue()), 0, 5);
        MFXButton button = new MFXButton("Calculate");
        button.setButtonType(ButtonType.RAISED);
        button.setOnAction(e -> {
            OptionalDouble average = paginated.getTransformableList()
                    .stream()
                    .mapToDouble(UserStory::getStoryPoints)
                    .average();
            for (MFXTextField field : fields.keySet()) {
                if (field.getFloatingText().equals("Story Points")) {
                    if (average.isPresent())
                        field.setText(String.valueOf((int) average.getAsDouble()));
                    else
                        field.setText("0");
                }
            }

        });
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

        VBox buttonVBox = new VBox(button);
        buttonVBox.setAlignment(Pos.BASELINE_LEFT);
        gridPane.add(buttonVBox, 1, 5);


        VBox vbox = new VBox();
        vbox.setFillWidth(true);
        vbox.getChildren().setAll(gridPane);
        content.setContent(vbox);
        content.setFitToWidth(true);

        fields.put(projectPair.getKey(), projectPair.getValue());
        fields.put(titlePair.getKey(), titlePair.getValue());
        fields.put(phasePair.getKey(), phasePair.getValue());
        fields.put(effortPair.getKey(), effortPair.getValue());
        fields.put(deliverablePair.getKey(), deliverablePair.getValue());
        fields.put(tagsPair.getKey(), tagsPair.getValue());
        fields.put(descriptionPair.getKey(), descriptionPair.getValue());
        fields.put(defectPair.getKey(), defectPair.getValue());
        fields.put(storyPointsPair.getKey(), storyPointsPair.getValue());
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

    /**
     * Adds constraints to each of the textfields to make sure they aren't empty
     * @param field text field
     * @param label label for text field
     */
    private void constraintBuilder(MFXTextField field, Label label) {
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

    @FXML
    void doneButtonEvent(ActionEvent event) {
        // add current user story to array
        add(tempSave());

        //  return if any fields are empty
        if (checkStories())
            return;

        // add user stories to database
        for (UserStory story : userStories) {
            try {
                EffortLoggerController.getDatabase().addUserStory(story);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @FXML
    void nextButtonEvent(ActionEvent event) {

        // return if any fields are empty
        if (isEmptyField())
            return;

        // enable the previous button now that we are on a new page
        previousButton.setDisable(false);
        // add story to array
        add(tempSave());
        index++;

        // set the values of the fields to the next page
        if (userStories.size() == index) {
            resetState();
        } else {
            restoreStory(userStories.get(index));
        }
    }

    @FXML
    void previousButtonEvent(ActionEvent event) {
        // save the current fields
        add(tempSave());
        index--;
        // display the previous item
        UserStory story = userStories.get(index);
        restoreStory(story);

        // if this is the first item, disable the previous button
        if (index == 0)
            previousButton.setDisable(true);
    }

    private void add(UserStory story) {
        // use add if it doesnt exist and use set when it does exist.
        if (userStories.size() == index) {
            userStories.add(
                    index,
                    story
            );
        } else {
            userStories.set(
                    index,
                    story
            );
        }
    }

    private boolean isEmptyField() {
        AtomicBoolean shouldReturn = new AtomicBoolean(false);
        fields.forEach((field, label) -> {
            if (field.getText().isEmpty() && !field.getFloatingText().equals("Defect Category")) {
                label.setVisible(true);
                shouldReturn.set(true);
            } else if (field.getFloatingText().equals("Defect Category") && toggle.isSelected() && field.getText().isEmpty()) {
                label.setVisible(true);
                shouldReturn.set(true);
            }
        });

        return shouldReturn.get();
    }

    private void resetState() {
        fields.forEach((field, label) -> {
            field.clear();
            label.setVisible(false);
        });
    }

    private UserStory tempSave() {
        String project = null;
        String title = null;
        String phase = null;
        String effortCategory = null;
        String deliverable = null;
        String description  = null;
        String tags  = null;
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
            }
        }

        return new UserStory(
                project,
                title,
                phase,
                effortCategory,
                deliverable,
                description,
                tags,
                storyPoints,
                isDefect,
                defectCategory
        );
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
                    break;
            }
        }
    }

    private boolean checkStories() {
        for (int i = 0; i < userStories.size(); i++) {
            UserStory story = userStories.get(i);
            if (story.getProject().isBlank() || story.getTitle().isBlank() || story.getPhase().isBlank() ||
                    story.getEffortCategory().isBlank() || story.getDeliverable().isBlank() ||
                    story.getDescription().isBlank() || story.getTags().isBlank()) {
                index = i;
                if (index != 0) {
                    previousButton.setDisable(false);
                }
                restoreStory(story);
                isEmptyField();
                return true;
            }
        }

        return false;
    }
}
