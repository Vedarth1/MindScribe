package com.Spring.MindScribe.services;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.Spring.MindScribe.models.Attachment;
import com.Spring.MindScribe.models.Journal;
import com.Spring.MindScribe.repository.AttachmentRepository;
import com.Spring.MindScribe.repository.JournalRepository;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class AttachmentService {

    @Autowired
    private CloudinaryService cloudinaryService;

    @Autowired
    private JournalRepository journalRepo;

    @Autowired 
    private AttachmentRepository attachmentRepo;

    public void addAttachment(String email, Long journalId, MultipartFile file) {
        Journal journal = journalRepo.findById(journalId).orElseThrow();
        String url = cloudinaryService.uploadFile(file);
        Attachment att = new Attachment();
        att.setUrl(url);
        att.setJournal(journal);

        journal.getAttachments().add(att);
        journalRepo.save(journal);
    }

    public void removeAttachment(String email, Long journalId, Long attachmentId) {
        Journal journal = journalRepo.findById(journalId).orElseThrow();

        Attachment att = attachmentRepo.findById(attachmentId)
                .orElseThrow(() -> new RuntimeException("Attachment not found"));

        if (!att.getJournal().getId().equals(journalId)) {
            throw new RuntimeException("You are not allowed to delete this file");
        }

        try {
            cloudinaryService.deleteFile(att.getUrl());
        } catch (Exception e) {
            System.err.println("Cloud delete failed for URL: " + att.getUrl());
        }

        journal.getAttachments().remove(att);
        attachmentRepo.delete(att);
    }

    public void removeAllAttachments(String email, Long journalId) {
        Journal journal = journalRepo.findById(journalId).orElseThrow();

        for (Attachment att : journal.getAttachments()) {
            try {
                cloudinaryService.deleteFile(att.getUrl());
            } catch (Exception e) {
                System.err.println("Cloud delete failed for URL: " + att.getUrl());
            }
            attachmentRepo.delete(att);
        }

        journal.getAttachments().clear();
    }
}
