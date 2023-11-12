package me.thfour.effortlogger.models;

import org.h2.engine.User;

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

    private String createDate() {
        Date date = new Date();
        return date.toString();
    }
}
