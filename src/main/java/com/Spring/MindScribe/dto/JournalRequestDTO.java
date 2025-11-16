package com.Spring.MindScribe.dto;

public class JournalRequestDTO {
    private String title;
    private String content;
    private String visibility;
    private boolean viewLater;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getVisibility() {
        return visibility;
    }

    public void setVisibility(String visibility) {
        this.visibility = visibility;
    }

    public boolean isViewLater() {
        return viewLater;
    }

    public void setViewLater(boolean viewLater) {
        this.viewLater = viewLater;
    }

}
