package com.Spring.MindScribe.services;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.Spring.MindScribe.dto.JournalRequestDTO;
import com.Spring.MindScribe.models.Journal;
import com.Spring.MindScribe.models.User;
import com.Spring.MindScribe.repository.JournalPermissionRepository;
import com.Spring.MindScribe.repository.JournalRepository;
import com.Spring.MindScribe.repository.UserRepository;
import com.Spring.MindScribe.utils.SlugUtil;
import org.springframework.lang.NonNull;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class JournalService {
    private final JournalRepository journalRepo;
    private final JournalPermissionRepository permissionRepo;
    private final UserRepository userRepo;
    private final AttachmentService attachmentService;

    public JournalService(JournalRepository journalRepo,
                          JournalPermissionRepository permissionRepo,
                          UserRepository userRepo,
                          AttachmentService attachmentService) {
        this.journalRepo = journalRepo;
        this.permissionRepo = permissionRepo;
        this.userRepo = userRepo;
        this.attachmentService=attachmentService;
    }

    public Journal createJournal(String ownerEmail, Journal j) {
        User owner = userRepo.findByEmail(ownerEmail).orElseThrow(() -> new NoSuchElementException("User not found"));
        j.setOwner(owner);
        if ("public".equalsIgnoreCase(j.getVisibility()) && (j.getPublicUrl() == null || j.getPublicUrl().isBlank())) {
            String slug;
            do {
                slug = SlugUtil.randomSlug();
            } while (journalRepo.findByPublicUrl(slug).isPresent());
            j.setPublicUrl(slug);
        }
        return journalRepo.save(j);
    }

    public Journal updateJournal(String actorEmail,@NonNull Long journalId, JournalRequestDTO req) {
        Journal j = journalRepo.findById(journalId).orElseThrow();
        if (!isOwner(actorEmail, j) && !hasWritePermission(actorEmail, j)) {
            throw new SecurityException("Not allowed to update");
        }
        j.setTitle(req.getTitle());
        j.setContent(req.getContent());
        j.setVisibility(req.getVisibility());
        j.setViewLater(req.isViewLater());
        return journalRepo.save(j);
    }

    public void deleteJournal(String actorEmail,@NonNull Long journalId) {
        Journal j = journalRepo.findById(journalId)
        .orElseThrow(() -> new NoSuchElementException("Journal not found with ID: " + journalId));
        
        if (!isOwner(actorEmail, j)) throw new SecurityException("Not allowed to delete");
        attachmentService.removeAllAttachments(actorEmail, journalId);
        journalRepo.delete(j);
    }

    public Journal getByIdForUser(String actorEmail,@NonNull Long journalId) {
        Journal j = journalRepo.findById(journalId).orElseThrow();
        if (isOwner(actorEmail, j) || hasReadPermission(actorEmail, j) || "public".equalsIgnoreCase(j.getVisibility())) {
            return j;
        }
        throw new SecurityException("Access denied");
    }

    public Journal getByPublicUrl(String publicUrl) {
        return journalRepo.findByPublicUrl(publicUrl).orElseThrow();
    }

    public List<Journal> listForUser(String actorEmail) {
        User u = userRepo.findByEmail(actorEmail).orElseThrow();
        return journalRepo.findByOwner(u);
    }

    public boolean isOwner(String email, Journal j) {
        return j.getOwner() != null && j.getOwner().getEmail().equalsIgnoreCase(email);
    }

    public boolean hasReadPermission(String email, Journal j) {
        if (isOwner(email, j)) return true;
        Optional<User> userOpt = userRepo.findByEmail(email);
        if (userOpt.isEmpty()) return false;
        return permissionRepo.findByJournalAndUser(j, userOpt.get())
                .map(p -> "read".equalsIgnoreCase(p.getPermission()) || "write".equalsIgnoreCase(p.getPermission()))
                .orElse(false);
    }

    public boolean hasWritePermission(String email, Journal j) {
        if (isOwner(email, j)) return true;
        Optional<User> userOpt = userRepo.findByEmail(email);
        if (userOpt.isEmpty()) return false;
        return permissionRepo.findByJournalAndUser(j, userOpt.get())
                .map(p -> "write".equalsIgnoreCase(p.getPermission()))
                .orElse(false);
    }
}
