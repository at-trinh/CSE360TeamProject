package me.thfour.effortlogger.models;

public class UserStory {
    private int storyId;
    private String project;
    private String title;
    private String phase;
    private String effortCategory;
    private String deliverable;
    private String status;
    private String description;
    private String tags;
    private int storyPoints;
    private String dates;
    private boolean isDefect;
    private String defectCategory;

    public UserStory(String project, String title, String phase, String effortCategory, String deliverable, String description, String tags, int storyPoints, boolean isDefect, String defectCategory) {
        this.project = project;
        this.title = title;
        this.phase = phase;
        this.effortCategory = effortCategory;
        this.deliverable = deliverable;
        this.description = description;
        this.tags = tags;
        this.storyPoints = storyPoints;
        this.isDefect = isDefect;
        this.defectCategory = defectCategory;
    }

    public UserStory(int storyId, String project, String title, String phase, String effortCategory, String deliverable, String status, String description, String tags, int storyPoints, String dates) {
        this.storyId = storyId;
        this.project = project;
        this.title = title;
        this.phase = phase;
        this.effortCategory = effortCategory;
        this.deliverable = deliverable;
        this.status = status;
        this.description = description;
        this.tags = tags;
        this.storyPoints = storyPoints;
        this.dates = dates;
    }

    public UserStory(int storyId, String project, String title, String phase, String effortCategory, String deliverable, String status, String description, String tags, int storyPoints, String dates, boolean isDefect, String defectCategory) {
        this.storyId = storyId;
        this.project = project;
        this.title = title;
        this.phase = phase;
        this.effortCategory = effortCategory;
        this.deliverable = deliverable;
        this.status = status;
        this.description = description;
        this.tags = tags;
        this.storyPoints = storyPoints;
        this.dates = dates;
        this.isDefect = isDefect;
        this.defectCategory = defectCategory;
    }

    @Override
    public String toString() {
        return "UserStory{" +
                "storyId=" + storyId +
                ", project='" + project + '\'' +
                ", title='" + title + '\'' +
                ", phase='" + phase + '\'' +
                ", effortCategory='" + effortCategory + '\'' +
                ", deliverable='" + deliverable + '\'' +
                ", status='" + status + '\'' +
                ", description='" + description + '\'' +
                ", tags='" + tags + '\'' +
                ", storyPoints=" + storyPoints +
                ", dates='" + dates + '\'' +
                ", isDefect=" + isDefect +
                ", defectCategory='" + defectCategory + '\'' +
                '}';
    }

    public int getStoryId() {
        return storyId;
    }

    public String getProject() {
        return project;
    }

    public String getTitle() {
        return title;
    }

    public String getPhase() {
        return phase;
    }

    public String getEffortCategory() {
        return effortCategory;
    }

    public UserStory(String project, String title, String phase, String effortCategory, String deliverable, String status, String description, String tags, int storyPoints, String dates, boolean isDefect, String defectCategory) {
        this.project = project;
        this.title = title;
        this.phase = phase;
        this.effortCategory = effortCategory;
        this.deliverable = deliverable;
        this.status = status;
        this.description = description;
        this.tags = tags;
        this.storyPoints = storyPoints;
        this.dates = dates;
        this.isDefect = isDefect;
        this.defectCategory = defectCategory;
    }

    public boolean isDefect() {
        return isDefect;
    }

    public String getDefectCategory() {
        return defectCategory;
    }

    public String getDeliverable() {
        return deliverable;
    }

    public String getStatus() {
        return status;
    }

    public String getDescription() {
        return description;
    }

    public String getTags() {
        return tags;
    }

    public int getStoryPoints() {
        return storyPoints;
    }

    public String getDates() {
        return dates;
    }
}
