package me.thfour.effortlogger.models;

import java.sql.*;
import java.util.ArrayList;

public class Database {
    private Connection connection;

    public void init(String url) throws SQLException, ClassNotFoundException {
//        Class.forName("org.h2.Driver");
        String jdbcUrl = "jdbc:h2:" + url + "effortloggerdatabase";
        this.connection = DriverManager.getConnection(jdbcUrl);
        System.out.println("Connected to database!");
        createUserStoryTable();
    }

    public void createUserStoryTable() throws SQLException {
        PreparedStatement ps = connection.prepareStatement("CREATE TABLE IF NOT EXISTS UserStories(\n" +
                "    StoryId INT NOT NULL AUTO_INCREMENT PRIMARY KEY,\n" +
                "    Project VARCHAR(32) NOT NULL,\n" +
                "    Title VARCHAR(32) NOT NULL,\n" +
                "    Phase VARCHAR(32) NOT NULL,\n" +
                "    EffortCategory VARCHAR(32) NOT NULL,\n" +
                "    Deliverable VARCHAR(32) NOT NULL,\n" +
                "    Status VARCHAR(32) NOT NULL DEFAULT 'Not Started',\n" +
                "    Description VARCHAR(200) NOT NULL,\n" +
                "    Tags VARCHAR(32) NOT NULL,\n" +
                "    StoryPoints TINYINT NOT NULL,\n" +
                "    Dates VARCHAR(200)\n" +
                ");");
        ps.executeUpdate();
        ps.close();
    }

    public void addUserStory(UserStory story) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("INSERT INTO USERSTORIES (Project, Title, Phase, EffortCategory, Deliverable, Description, Tags, StoryPoints) VALUES ( ?, ?, ?, ?, ?, ?, ?, ? );");
        ps.setString(1, story.getProject());
        ps.setString(2, story.getTitle());
        ps.setString(3, story.getPhase());
        ps.setString(4, story.getEffortCategory());
        ps.setString(5, story.getDeliverable());
        ps.setString(6, story.getDescription());
        ps.setString(7, story.getTags());
        ps.setInt(8, story.getStoryPoints());
        ps.executeUpdate();
        ps.close();
    }

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
                            rs.getString("Dates")
                    )
            );
        }
        return stories;
    }
}
