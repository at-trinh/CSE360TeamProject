package me.thfour.effortlogger.models;

public class Settings {

    private String Username;
    private Integer LastUpdatedStory;

    public Settings(String username, Integer lastUpdatedStory) {
        Username = username;
        LastUpdatedStory = lastUpdatedStory;
    }

    public String getUsername() {
        return Username;
    }

    @Override
    public String toString() {
        return "Settings{" +
                "Username='" + Username + '\'' +
                ", LastUpdatedStory=" + LastUpdatedStory +
                '}';
    }

    public Integer getLastUpdatedStory() {
        return LastUpdatedStory;
    }
}
