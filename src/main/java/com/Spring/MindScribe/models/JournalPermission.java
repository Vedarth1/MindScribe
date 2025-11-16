package com.Spring.MindScribe.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "journal_permissions")
public class JournalPermission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "journal_id")
    private Journal journal;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false)
    private String permission;

    @Column(nullable = false)
    private String sharedBy; 

    @Column(nullable = false)
    private String sharedTo;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getPermission() { return permission; }
    public void setPermission(String permission) { this.permission = permission; }

    public String getSharedBy() { return sharedBy; }
    public void setSharedBy(String sharedBy) { this.sharedBy = sharedBy; }

    public String getSharedTo() { return sharedTo; }
    public void setSharedTo(String sharedTo) { this.sharedTo = sharedTo; }

    public Journal getJournal() { return journal; }
    public void setJournal(Journal journal) { this.journal = journal; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
}
