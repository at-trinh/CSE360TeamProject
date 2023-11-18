package me.thfour.effortlogger.models;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.concurrent.TimeUnit;

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
        this.status = "Not Started";
        this.dates = "";
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
        return String.format("%s, %s, %s, %s, %s, %s, %s, %d, %s, %s", project, title, phase, effortCategory, deliverable, status, tags, storyPoints, isDefect ? "Defect" : "Not a Defect", defectCategory);
    }

    public String toFormattedString() {
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
        if (dates == null)
            return "";
        return dates;
    }

    public void setProject(String project) {
        this.project = project;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setPhase(String phase) {
        this.phase = phase;
    }

    public void setEffortCategory(String effortCategory) {
        this.effortCategory = effortCategory;
    }

    public void setDeliverable(String deliverable) {
        this.deliverable = deliverable;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public void setStoryPoints(int storyPoints) {
        this.storyPoints = storyPoints;
    }

    public void setDates(String dates) {
        this.dates = dates;
    }

    public void setDefect(boolean defect) {
        isDefect = defect;
    }

    public void setDefectCategory(String defectCategory) {
        this.defectCategory = defectCategory;
    }

    public long getRunningTimeInSeconds() {
        long diffInMillies = 0L;
        if (dates == null)
            return diffInMillies;

        ArrayList<Date> datesArray = getDatesArray();

        for (int i = 0; i < datesArray.size(); i += 2) {
            System.out.println(datesArray.get(i));
            diffInMillies += datesArray.get(i+1).getTime() - datesArray.get(i).getTime();
        }

        return TimeUnit.SECONDS.convert(diffInMillies, TimeUnit.MILLISECONDS);
    }

    public ArrayList<Date> getDatesArray() {

        ArrayList<Date> datesArrayList = new ArrayList<>();
        if (dates == null)
            return datesArrayList;

        String[] datesAsString = dates.split(",");

        for (String date : datesAsString) {
            if (date.isBlank())
                continue;

            datesArrayList.add(new Date(date));
        }

        return datesArrayList;
    }
}
