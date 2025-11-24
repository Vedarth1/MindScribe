package com.Spring.MindScribe.services;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.Spring.MindScribe.dto.JournalRequestDTO;
import com.Spring.MindScribe.dto.JournalResponseDTO;
import com.Spring.MindScribe.models.Attachment;
import com.Spring.MindScribe.models.Journal;
import com.Spring.MindScribe.models.JournalPermission;
import com.Spring.MindScribe.models.User;
import com.Spring.MindScribe.repository.JournalPermissionRepository;
import com.Spring.MindScribe.repository.JournalRepository;
import com.Spring.MindScribe.repository.UserRepository;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class ShareJournalService {

    @Autowired
    private JournalRepository journalRepo;

    @Autowired
    private JournalPermissionRepository permissionRepo;

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private JournalService journalService;
    
    public void shareJournal(String sharerEmail, Long journalId, String username, String permission) {

        if (!permission.equalsIgnoreCase("read") && !permission.equalsIgnoreCase("write")) {
            throw new RuntimeException("Permission must be 'read' or 'write'");
        }

        Journal journal = journalRepo.findById(journalId)
                .orElseThrow(() -> new NoSuchElementException("Journal not found"));

        // Only owner can share
        if (!journal.getOwner().getEmail().equalsIgnoreCase(sharerEmail)) {
            throw new SecurityException("Only owner can share this journal");
        }

        User targetUser = userRepo.findByEmail(username)
                .orElseThrow(() -> new NoSuchElementException("Target user not found"));

        // Check if already shared → update instead of duplicate
        JournalPermission existing =permissionRepo.findByJournalAndUser(journal, targetUser).orElse(null);

        if (existing != null) {
            existing.setPermission(permission);
            permissionRepo.save(existing);
            return;
        }

        JournalPermission jp = new JournalPermission();
        jp.setJournal(journal);
        jp.setUser(targetUser);
        jp.setSharedBy(sharerEmail);
        jp.setSharedTo(username);
        jp.setPermission(permission);

        permissionRepo.save(jp);
    }

    public List<Journal> getJournalsSharedWith(String email) {

        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new NoSuchElementException("User not found"));

        List<JournalPermission> permissions = permissionRepo.findByUser(user);

        return permissions.stream()
                .map(JournalPermission::getJournal)
                .collect(Collectors.toList());
    }

    public void removeShare(String sharerEmail, Long journalId, String username) {

        Journal journal = journalRepo.findById(journalId)
                .orElseThrow(() -> new NoSuchElementException("Journal not found"));

        if (!journal.getOwner().getEmail().equalsIgnoreCase(sharerEmail)) {
            throw new SecurityException("Only owner can remove share");
        }

        User targetUser = userRepo.findByEmail(username)
                .orElseThrow(() -> new NoSuchElementException("Target user not found"));

        JournalPermission permission =
                permissionRepo.findByJournalAndUser(journal, targetUser)
                        .orElseThrow(() -> new NoSuchElementException("Share not found"));

        permissionRepo.delete(permission);
    }

    public void updateSharePermission(String sharerEmail, Long journalId, String username, String permission) {

        if (!permission.equalsIgnoreCase("read") && !permission.equalsIgnoreCase("write")) {
            throw new RuntimeException("Permission must be 'read' or 'write'");
        }

        Journal journal = journalRepo.findById(journalId)
                .orElseThrow(() -> new NoSuchElementException("Journal not found"));

        if (!journal.getOwner().getEmail().equalsIgnoreCase(sharerEmail)) {
            throw new SecurityException("Only owner can update permission");
        }

        User targetUser = userRepo.findByEmail(username)
                .orElseThrow(() -> new NoSuchElementException("Target user not found"));

        JournalPermission jp = permissionRepo.findByJournalAndUser(journal, targetUser)
                .orElseThrow(() -> new NoSuchElementException("Share entry not found"));

        jp.setPermission(permission);
        permissionRepo.save(jp);
    }

    public JournalResponseDTO updateSharedJournal(String actorEmail, Long journalId, JournalRequestDTO req) {

        Journal journal = journalRepo.findById(journalId)
                .orElseThrow(() -> new NoSuchElementException("Journal not found"));

        User user = userRepo.findByEmail(actorEmail)
                .orElseThrow(() -> new NoSuchElementException("User not found"));

        JournalPermission permission =
                permissionRepo.findByJournalAndUser(journal, user)
                        .orElseThrow(() -> new SecurityException("No access"));

        if (!permission.getPermission().equalsIgnoreCase("write")) {
            throw new SecurityException("You only have READ access. Cannot update journal.");
        }

        journal.setTitle(req.getTitle());
        journal.setContent(req.getContent());
        journalRepo.save(journal);

        return toDto(journal);
    }

    public List<JournalResponseDTO> getSharedJournalsDTO(String email) {
        return getJournalsSharedWith(email)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
        }

        private JournalResponseDTO toDto(Journal j) {
        return new JournalResponseDTO(
                j.getId(),
                j.getTitle(),
                j.getContent(),
                j.getVisibility(),
                j.getPublicUrl(),
                j.isViewLater(),
                j.getAttachments().stream().map(Attachment::getUrl).toList()
        );
        }

}

