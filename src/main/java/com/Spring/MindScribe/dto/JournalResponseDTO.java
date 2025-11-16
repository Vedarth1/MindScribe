package com.Spring.MindScribe.dto;

import java.util.List;

public class JournalResponseDTO {
    Long id;
    String title;
    String content;
    String visibility;
    String publicUrl;
    boolean viewLater;
    List<String> attachments;

    public JournalResponseDTO(Long id, String title,String content,String visibility,String publicUrl,boolean viewLater,List<String> attachments)
    {
        this.id=id;
        this.title=title;
        this.content=content;
        this.visibility=visibility;
        this.publicUrl=publicUrl;
        this.viewLater=viewLater;
        this.attachments=attachments;
    }

    public Long getId() {
        return id;
    }
    public String getTitle() {
        return title;
    }
    public String getContent() {
        return content;
    }
    public String getVisibility() {
        return visibility;
    }
    public String getPublicUrl() {
        return publicUrl;
    }
    public boolean isViewLater() {
        return viewLater;
    }
    public List<String> getAttachments() {
        return attachments;
    }
}
