package me.thfour.effortlogger.models;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.sql.*;
import java.util.Date;

public class Database {
    private Connection connection;

    public void init(String url) throws SQLException {
        String jdbcUrl = "jdbc:h2:" + url + "effortloggerdatabase"; // url for where the database should be saved
        this.connection = DriverManager.getConnection(jdbcUrl); // create connection
        System.out.println("Connected to database!");
        createUserStoryTable(); // ensure data can be saved
        createSettingsTable();
    }

    /**
     * Creates the UserStory table where user stories are saved
     * @throws SQLException
     */
    public void createUserStoryTable() throws SQLException {
        PreparedStatement ps = connection.prepareStatement("CREATE TABLE IF NOT EXISTS UserStories(\n" +
                "    StoryId INT NOT NULL AUTO_INCREMENT PRIMARY KEY,\n" +
                "    Project VARCHAR(32) NOT NULL,\n" +
                "    Title VARCHAR(32) NOT NULL,\n" +
                "    Phase VARCHAR(32) NOT NULL,\n" +
                "    EffortCategory VARCHAR(32) NOT NULL,\n" +
                "    Deliverable VARCHAR(32) NOT NULL,\n" +
                "    Status VARCHAR(32) NOT NULL DEFAULT 'Not Started',\n" +
                "    Description VARCHAR(1000) NOT NULL,\n" +
                "    Tags VARCHAR(32) NOT NULL,\n" +
                "    StoryPoints TINYINT NOT NULL,\n" +
                "    IsDefect BIT NOT NULL,\n" +
                "    DefectCategory VARCHAR(32) NOT NULL,\n" +
                "    DateCreated VARCHAR(32) NOT NULL,\n" +
                "    Dates VARCHAR(200)\n" +
                ");");
        ps.executeUpdate();
        ps.close();
    }

    public void createSettingsTable() throws SQLException {
        PreparedStatement ps = connection.prepareStatement("CREATE TABLE IF NOT EXISTS SETTINGS(\n" +
                "    Lock char(1) not null DEFAULT 'X',\n" +
                "    LastUpdatedStoryId INT,\n" +
                "    Username VARCHAR(30) NOT NULL\n" +
                ");");
        ps.executeUpdate();

        ps = connection.prepareStatement("SELECT * FROM SETTINGS");
        ResultSet rs = ps.executeQuery();
        if (!rs.next()) {
            ps = connection.prepareStatement("INSERT INTO SETTINGS ( USERNAME ) VALUES ( 'Default Username' );");
            ps.executeUpdate();
        }
        ps.close();
    }

    /**
     * Enters a **NEW** user story into the database
     * @param story user story to saved
     * @throws SQLException
     */
    public void addUserStory(UserStory story) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("INSERT INTO USERSTORIES (Project, Title, Phase, EffortCategory, Deliverable, Description, Tags, StoryPoints, IsDefect, DefectCategory, DateCreated) VALUES ( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? );");
        ps.setString(1, story.getProject());
        ps.setString(2, story.getTitle());
        ps.setString(3, story.getPhase());
        ps.setString(4, story.getEffortCategory());
        ps.setString(5, story.getDeliverable());
        ps.setString(6, story.getDescription());
        ps.setString(7, story.getTags());
        ps.setInt(8, story.getStoryPoints());
        ps.setBoolean(9, story.isDefect());
        ps.setString(10, story.getDefectCategory());
        ps.setString(11, createDate());
        ps.executeUpdate();
        ps.close();
    }

    public void addExistingUserStory(UserStory story) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("INSERT INTO USERSTORIES (Project, Title, Phase, EffortCategory, Deliverable, Description, Tags, StoryPoints, IsDefect, DefectCategory, DateCreated, Dates) VALUES ( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? );");
        ps.setString(1, story.getProject());
        ps.setString(2, story.getTitle());
        ps.setString(3, story.getPhase());
        ps.setString(4, story.getEffortCategory());
        ps.setString(5, story.getDeliverable());
        ps.setString(6, story.getDescription());
        ps.setString(7, story.getTags());
        ps.setInt(8, story.getStoryPoints());
        ps.setBoolean(9, story.isDefect());
        ps.setString(10, story.getDefectCategory());
        ps.setString(11, createDate());
        ps.setString(12, story.getDates());
        ps.executeUpdate();
        ps.close();
    }

    /**
     * Gets a list of all user stories in the database
     * @return all user stories in the database
     * @throws SQLException
     */
    public ArrayList<UserStory> getUserStories() throws SQLException {
        ArrayList<UserStory> stories = new ArrayList<>();

        PreparedStatement ps = connection.prepareStatement("SELECT * FROM USERSTORIES;");
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            stories.add(
                    new UserStory(
                            rs.getInt("StoryId"),
                            rs.getString("Project"),
                            rs.getString("Title"),
                            rs.getString("Phase"),
                            rs.getString("EffortCategory"),
                            rs.getString("Deliverable"),
                            rs.getString("Status"),
                            rs.getString("Description"),
                            rs.getString("Tags"),
                            rs.getInt("StoryPoints"),
                            rs.getString("Dates"),
                            rs.getBoolean("IsDefect"),
                            rs.getString("DefectCategory")
                    )
            );
        }
        rs.close();
        ps.close();
        return stories;
    }

    public void addDateToUserStory(UserStory story, Date date, String status) throws SQLException {
        String dateString = date.toString();
        if (!story.getDates().isBlank())
            dateString = String.join(",", story.getDates(), dateString);

        story.setDates(dateString);
        PreparedStatement ps = connection.prepareStatement("UPDATE USERSTORIES SET DATES=?, STATUS=? WHERE STORYID=?;");
        ps.setString(1, dateString);
        ps.setString(2, status);
        ps.setInt(3, story.getStoryId());
        ps.executeUpdate();
        ps = connection.prepareStatement("UPDATE SETTINGS SET LastUpdatedStoryId=? WHERE Lock = 'X';");
        ps.setInt(1, story.getStoryId());
        ps.executeUpdate();
        ps.close();
    }

    public void setUserStoryStatus(UserStory story, String status) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("UPDATE USERSTORIES SET STATUS=? WHERE STORYID=?;");
        ps.setString(1, status);
        ps.setInt(2, story.getStoryId());
        ps.executeUpdate();
        ps.close();
    }

    public ArrayList<String> getUniqueValues(String column) throws SQLException {
        PreparedStatement ps = connection.prepareStatement(String.format("SELECT DISTINCT %s from USERSTORIES ORDER BY %s ASC;", column, column));
        ResultSet rs = ps.executeQuery();
        ArrayList<String> values = new ArrayList<>();
        String value;
        while (rs.next()) {
            value = rs.getString(column);
            if (!value.isEmpty())
                values.add(value);
        }

        return values;
    }

    public void updateUserStory(UserStory story) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("UPDATE USERSTORIES SET PROJECT=?,TITLE=?, PHASE=?, EFFORTCATEGORY=?, DELIVERABLE=?, STATUS=?, DESCRIPTION=?, TAGS=?, STORYPOINTS=?, ISDEFECT=?, DEFECTCATEGORY=?, DATES=? WHERE STORYID=?;");
        ps.setString(1, story.getProject());
        ps.setString(2, story.getTitle());
        ps.setString(3, story.getPhase());
        ps.setString(4, story.getEffortCategory());
        ps.setString(5, story.getDeliverable());
        ps.setString(6, story.getStatus());
        ps.setString(7, story.getDescription());
        ps.setString(8, story.getTags());
        ps.setInt(9, story.getStoryPoints());
        ps.setBoolean(10, story.isDefect());
        ps.setString(11, story.getDefectCategory());
        ps.setString(12, story.getDates());
        ps.setInt(13, story.getStoryId());
        ps.executeUpdate();
        ps.close();
    }

    public void deleteStoryFromTable(int storyId) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("DELETE FROM USERSTORIES WHERE STORYID=?;");
        ps.setInt(1, storyId);
        ps.executeUpdate();
        ps.close();
    }

    public Settings getSettings() throws SQLException {
        PreparedStatement ps = connection.prepareStatement("SELECT * FROM SETTINGS;");
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            return new Settings(
                    rs.getString("Username"),
                    rs.getInt("LastUpdatedStoryId")
            );
        }
        return new Settings("Default Username", null);
    }

    public void setUsername(String username) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("UPDATE SETTINGS SET USERNAME = ? WHERE Lock = 'X';");
        ps.setString(1, username);
        ps.executeUpdate();
        ps.close();
    }

    private String createDate() {
        Date date = new Date();
        return date.toString();
    }
}
