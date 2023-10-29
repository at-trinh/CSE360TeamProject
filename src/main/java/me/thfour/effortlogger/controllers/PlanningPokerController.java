package me.thfour.effortlogger.controllers;

import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXComboBox;
import io.github.palexdev.materialfx.controls.MFXPaginatedTableView;
import io.github.palexdev.materialfx.controls.MFXTextField;
import io.github.palexdev.materialfx.validation.Constraint;
import io.github.palexdev.materialfx.validation.Severity;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import me.thfour.effortlogger.models.UserStory;

import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.ResourceBundle;

public class PlanningPokerController implements Initializable {

    @FXML
    private MFXComboBox<String> projectField;

    @FXML
    private MFXTextField titleField;

    @FXML
    private MFXComboBox<String> phaseField;

    @FXML
    private MFXComboBox<String> effortCategoryField;

    @FXML
    private MFXComboBox<String> deliverableField;

    @FXML
    private MFXTextField tagsField;

    @FXML
    private MFXTextField descriptionField;

    @FXML
    private MFXButton doneButton;

    @FXML
    private MFXButton nextButton;

    @FXML
    private MFXButton previousButton;

    @FXML
    private MFXPaginatedTableView<UserStory> paginated;

    private ArrayList<MFXTextField> fields;
    private int index = 0;
    private ArrayList<UserStory> userStories;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // list of fields to easily check if any are empty
        fields = new ArrayList<>(Arrays.asList(
            projectField,
            titleField,
            phaseField,
            effortCategoryField,
            deliverableField,
            tagsField,
            descriptionField
        ));

        userStories = new ArrayList<>();
    }

    /**
     * Adds constraints to each of the textfields to make sure they aren't empty
     * @param textField
     */
    private void constraintBuilder(MFXTextField textField) {
        Constraint constraint =Constraint.Builder.build()
                .setSeverity(Severity.ERROR)
                .setMessage(textField.getFloatingText() + " can not be empty")
                .setCondition(textField.textProperty().length().lessThan(0))
                .get();
        textField.getValidator()
                .constraint(constraint);

        // TODO: validation doesn't work because each input box needs a validartion label and im too damn lazy for that shit
//        textField.getValidator().validProperty().addListener((observable, oldValue, newValue) -> {
//            if (newValue) {
//                validationLabel.setVisible(false);
//                passwordField.pseudoClassStateChanged(INVALID_PSEUDO_CLASS, false);
//            }
//        });

//        textField.delegateFocusedProperty().addListener((observable, oldValue, newValue) -> {
//            if (oldValue && !newValue) {
//                List<Constraint> constraints = passwordField.validate();
//                if (!constraints.isEmpty()) {
//                    passwordField.pseudoClassStateChanged(INVALID_PSEUDO_CLASS, true);
//                    validationLabel.setText(constraints.get(0).getMessage());
//                    validationLabel.setVisible(true);
//                }
//            }
//        });
    }

    @FXML
    void doneButtonEvent(ActionEvent event) {
        //  return if any fields are empty
        for (MFXTextField field : fields) {
            if (field.textProperty().length().isEqualTo(0).get()) {
                System.out.println("One or more textfield is empty");
                return;
            }
        }

        // add current user story to array
        add(new UserStory(
                projectField.textProperty().get(),
                titleField.textProperty().get(),
                phaseField.textProperty().get(),
                effortCategoryField.textProperty().get(),
                deliverableField.textProperty().get(),
                descriptionField.textProperty().get(),
                tagsField.textProperty().get(),
                40 // TODO calculate story points2
        ));

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
        for (MFXTextField field : fields) {
            if (field.textProperty().length().isEqualTo(0).get()) {
                System.out.println("One or more textfield is empty");
                return;
            }
        }

        // enable the previous button now that we are on a new page
        previousButton.setDisable(false);
        // add story to array
        add(new UserStory(
                projectField.textProperty().get(),
                titleField.textProperty().get(),
                phaseField.textProperty().get(),
                effortCategoryField.textProperty().get(),
                deliverableField.textProperty().get(),
                descriptionField.textProperty().get(),
                tagsField.textProperty().get(),
                40 // TODO calculate story points2
        ));

        index++;

        // set the values of the fields to the next page
        for (MFXTextField field : fields) {
            if (userStories.size() == index) {
                field.textProperty().set("");
            } else {
                UserStory story = userStories.get(index);
                projectField.textProperty().set(story.getProject());
                titleField.textProperty().set(story.getTitle());
                phaseField.textProperty().set(story.getPhase());
                effortCategoryField.textProperty().set(story.getEffortCategory());
                deliverableField.textProperty().set(story.getDeliverable());
                descriptionField.textProperty().set(story.getDescription());
                tagsField.textProperty().set(story.getTags());
            }
        }
    }

    @FXML
    void previousButtonEvent(ActionEvent event) {
        // save the current fields
        add(new UserStory(
                projectField.textProperty().get(),
                titleField.textProperty().get(),
                phaseField.textProperty().get(),
                effortCategoryField.textProperty().get(),
                deliverableField.textProperty().get(),
                descriptionField.textProperty().get(),
                tagsField.textProperty().get(),
                40 // TODO calculate story points2
        ));
        index--;
        // display the previous item
        UserStory story = userStories.get(index);
        projectField.textProperty().set(story.getProject());
        titleField.textProperty().set(story.getTitle());
        phaseField.textProperty().set(story.getPhase());
        effortCategoryField.textProperty().set(story.getEffortCategory());
        deliverableField.textProperty().set(story.getDeliverable());
        descriptionField.textProperty().set(story.getDescription());
        tagsField.textProperty().set(story.getTags());

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
}
