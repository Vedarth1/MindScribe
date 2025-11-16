package com.Spring.MindScribe.controllers;

import java.util.List;
import java.util.stream.Collectors;

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
// import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.Spring.MindScribe.dto.JournalRequestDTO;
import com.Spring.MindScribe.dto.JournalResponseDTO;
import com.Spring.MindScribe.dto.ShareRequestDTO;
import com.Spring.MindScribe.models.Attachment;
import com.Spring.MindScribe.models.Journal;
import com.Spring.MindScribe.models.User;
import com.Spring.MindScribe.repository.UserRepository;
import com.Spring.MindScribe.services.JournalService;
import com.Spring.MindScribe.utils.AuthUtils;
import com.Spring.MindScribe.utils.SlugUtil;

@RestController
@RequestMapping("/api/v1/journals")
public class JournalController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JournalService journalService;

    @PostMapping("/create")
    public ResponseEntity<JournalResponseDTO> create(@RequestBody JournalRequestDTO req, Authentication auth) {
        String email = auth.getName();
        Journal j = new Journal();
        j.setTitle(req.getTitle());
        j.setContent(req.getContent());
        j.setVisibility(req.getVisibility());
        j.setViewLater(req.isViewLater());
        j.setPublicUrl(SlugUtil.randomSlug());
        Journal saved = journalService.createJournal(email, j);
        return ResponseEntity.ok(toDto(saved));
    }

    @GetMapping("/viewAll")
    public ResponseEntity<List<JournalResponseDTO>> list(Authentication auth) {
        String email = auth.getName();
        List<Journal> list = journalService.listForUser(email);
        var dtos = list.stream().map(this::toDto).collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/view/{id}")
    public ResponseEntity<JournalResponseDTO> get(@PathVariable Long id, Authentication auth) {
        String email = auth.getName();
        Journal j = journalService.getByIdForUser(email, id);
        return ResponseEntity.ok(toDto(j));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<JournalResponseDTO> update(@PathVariable Long id, @RequestBody JournalRequestDTO req, Authentication auth) {
        String email = auth.getName();
        Journal upd = new Journal();
        upd.setTitle(req.getTitle());
        upd.setContent(req.getContent());
        upd.setVisibility(req.getVisibility());
        upd.setViewLater(req.isViewLater());
        Journal saved = journalService.updateJournal(email, id, upd);
        return ResponseEntity.ok(toDto(saved));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id, Authentication auth) {
        String email = auth.getName();
        journalService.deleteJournal(email, id);
        return ResponseEntity.noContent().build();
    }

    // --

    @PostMapping("/{id}/share")
    public ResponseEntity<?> share(@PathVariable Long id, @RequestBody ShareRequestDTO req, Authentication auth) {
        String email = auth.getName();
        if (!"read".equalsIgnoreCase(req.getPermission()) && !"write".equalsIgnoreCase(req.getPermission())) {
            return ResponseEntity.badRequest().body("permission must be 'read' or 'write'");
        }
        journalService.shareJournal(email, id, req.getTargetEmail(), req.getPermission());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/public/{publicUrl}")
    public ResponseEntity<JournalResponseDTO> publicGet(@PathVariable String publicUrl) {
        Journal j = journalService.getByPublicUrl(publicUrl);
        return ResponseEntity.ok(toDto(j));
    }

    // @PutMapping("/{id}/view-later")
    // public ResponseEntity<JournalResponseDTO> toggleViewLater(@PathVariable Long id, @RequestParam boolean value, Authentication auth) {
    //     String email = auth.getName();
    //     Journal j = journalService.getByIdForUser(email, id);
    //     j.setViewLater(value);
    //     Journal saved = journalService.updateJournal(email, id, j);
    //     return ResponseEntity.ok(toDto(saved));
    // }

    private JournalResponseDTO toDto(Journal j) {
        var atts = j.getAttachments() == null ? List.<String>of()
                  : j.getAttachments().stream().map(Attachment::getUrl).collect(Collectors.toList());
        return new JournalResponseDTO(j.getId(), j.getTitle(), j.getContent(), j.getVisibility(), j.getPublicUrl(), j.isViewLater(), atts);
    }

    @GetMapping("/hello")
    public ResponseEntity<String> sayHello() {

        Authentication auth = AuthUtils.getAuthentication();
        if (auth == null) {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        String email = auth.getName();
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        return ResponseEntity.ok("Hello, " + user.getName() + "! Your email is: " + user.getEmail());
    }
}
