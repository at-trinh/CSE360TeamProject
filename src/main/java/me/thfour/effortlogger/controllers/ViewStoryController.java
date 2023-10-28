package me.thfour.effortlogger.controllers;

import io.github.palexdev.materialfx.controls.MFXPaginatedTableView;
import io.github.palexdev.materialfx.controls.MFXTableColumn;
import io.github.palexdev.materialfx.controls.cell.MFXTableRowCell;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import me.thfour.effortlogger.models.UserStory;

import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.ResourceBundle;

public class ViewStoryController implements Initializable {

    @FXML
    private MFXPaginatedTableView<UserStory> viewStoryTable;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ArrayList<UserStory> userStories = null;
        try {
            userStories = EffortLoggerController.getDatabase().getUserStories();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        MFXTableColumn<UserStory> projectColumn = new MFXTableColumn<>("Project", true, Comparator.comparing(UserStory::getProject));
        MFXTableColumn<UserStory> titleColumn = new MFXTableColumn<>("Title", true, Comparator.comparing(UserStory::getTitle));
        MFXTableColumn<UserStory> phaseColumn = new MFXTableColumn<>("Phase", true, Comparator.comparing(UserStory::getPhase));
        MFXTableColumn<UserStory> effortColumn = new MFXTableColumn<>("Effort Category", true, Comparator.comparing(UserStory::getEffortCategory));
        MFXTableColumn<UserStory> delivColumn = new MFXTableColumn<>("Deliverable", true, Comparator.comparing(UserStory::getDeliverable));
        MFXTableColumn<UserStory> statusColumn = new MFXTableColumn<>("Status", true, Comparator.comparing(UserStory::getStatus));
        MFXTableColumn<UserStory> descriptColumn = new MFXTableColumn<>("Description", true, Comparator.comparing(UserStory::getDescription));
        MFXTableColumn<UserStory> tagsColumn = new MFXTableColumn<>("Tags", true, Comparator.comparing(UserStory::getTags));
        MFXTableColumn<UserStory> storyColumn = new MFXTableColumn<>("Story Points", true, Comparator.comparing(UserStory::getStoryPoints));

        projectColumn.setRowCellFactory(userStory -> new MFXTableRowCell<>(UserStory::getProject));
        titleColumn.setRowCellFactory(userStory -> new MFXTableRowCell<>(UserStory::getTitle));
        phaseColumn.setRowCellFactory(userStory -> new MFXTableRowCell<>(UserStory::getPhase));
        effortColumn.setRowCellFactory(userStory -> new MFXTableRowCell<>(UserStory::getEffortCategory));
        delivColumn.setRowCellFactory(userStory -> new MFXTableRowCell<>(UserStory::getDeliverable));
        statusColumn.setRowCellFactory(userStory -> new MFXTableRowCell<>(UserStory::getStatus));
        descriptColumn.setRowCellFactory(userStory -> new MFXTableRowCell<>(UserStory::getDescription));
        tagsColumn.setRowCellFactory(userStory -> new MFXTableRowCell<>(UserStory::getTags));
        storyColumn.setRowCellFactory(userStory -> new MFXTableRowCell<>(UserStory::getStoryPoints));

        viewStoryTable.getTableColumns().addAll(projectColumn, titleColumn, phaseColumn, effortColumn, delivColumn, statusColumn, descriptColumn, tagsColumn, storyColumn);
        viewStoryTable.setItems(FXCollections.observableArrayList(userStories));
    }

    @FXML
    void refresh(ActionEvent event) {
        ArrayList<UserStory> userStories = null;
        try {
            userStories = EffortLoggerController.getDatabase().getUserStories();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        viewStoryTable.setItems(FXCollections.observableArrayList(userStories));
    }
}
