package com.Spring.MindScribe.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.Spring.MindScribe.dto.JournalRequestDTO;
import com.Spring.MindScribe.services.ShareJournalService;

@RestController
@RequestMapping("/api/v1/journals/share")
public class JournalSharingController {

    @Autowired
    private ShareJournalService shareJournalService;

    @PostMapping("/{id}/{username}/{permission}")
    public ResponseEntity<?> shareJournal(@PathVariable Long id,@PathVariable String username,@PathVariable String permission,Authentication auth) {
        String email = auth.getName();
        shareJournalService.shareJournal(email, id, username, permission);
        return ResponseEntity.ok("Shared successfully");
    }

    @GetMapping("/sharedjournals/get")
    public ResponseEntity<?> getSharedJournals(Authentication auth) {
        String email = auth.getName();
        return ResponseEntity.ok(shareJournalService.getSharedJournalsDTO(email));
    }

    @DeleteMapping("/delete/{id}/{username}")
    public ResponseEntity<?> deleteSharedUser(@PathVariable Long id,@PathVariable String username,Authentication auth) 
    {
        String email = auth.getName();
        shareJournalService.removeShare(email, id, username);
        return ResponseEntity.ok("Share deleted");
    }

    @PutMapping("/{id}/sharedupdate/{username}/{permission}")
    public ResponseEntity<?> updateSharePermission(@PathVariable Long id,@PathVariable String username,@PathVariable String permission,Authentication auth) 
    {
        String email = auth.getName();
        shareJournalService.updateSharePermission(email, id, username, permission);
        return ResponseEntity.ok("Permission updated");
    }

    @PutMapping("/update/sharedjournal/{id}")
    public ResponseEntity<?> updateSharedJournal(@PathVariable Long id,@RequestBody JournalRequestDTO req,Authentication auth) 
    {
        String email = auth.getName();
        return ResponseEntity.ok(shareJournalService.updateSharedJournal(email, id, req));
    }
}
