package com.Spring.MindScribe.controllers;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.MediaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.Spring.MindScribe.dto.JournalRequestDTO;
import com.Spring.MindScribe.dto.JournalResponseDTO;
import com.Spring.MindScribe.models.Attachment;
import com.Spring.MindScribe.models.Journal;
import com.Spring.MindScribe.models.User;
import com.Spring.MindScribe.repository.UserRepository;
import com.Spring.MindScribe.services.AttachmentService;
import com.Spring.MindScribe.services.JournalService;
import com.Spring.MindScribe.utils.AuthUtils;
import com.Spring.MindScribe.utils.SlugUtil;
import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
@RequestMapping("/api/v1/journals")
public class JournalController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JournalService journalService;

    @Autowired
    private AttachmentService attachmentService;

    @PostMapping(value = "/create",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<JournalResponseDTO> create(
            @RequestPart("data") String json,
            @RequestPart(value = "files", required = false) MultipartFile[] files,
            Authentication auth) throws Exception {

        ObjectMapper mapper = new ObjectMapper();
        JournalRequestDTO req = mapper.readValue(json, JournalRequestDTO.class);
        String email = auth.getName();
        Journal j = new Journal();
        j.setTitle(req.getTitle());
        j.setContent(req.getContent());
        j.setVisibility(req.getVisibility());
        j.setViewLater(req.isViewLater());
        j.setPublicUrl(SlugUtil.randomSlug());

        Journal saved = journalService.createJournal(email, j);

        if (files != null) {
            for (MultipartFile f : files) {
                attachmentService.addAttachment(email, saved.getId(), f);
            }
        }

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

    @PutMapping(value = "/update/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<JournalResponseDTO> update(
            @PathVariable Long id,
            @RequestPart("data") String json,
            @RequestPart(value = "files", required = false) MultipartFile[] newFiles,
            Authentication auth
    )throws Exception{
        String email = auth.getName();
        JournalRequestDTO req = new ObjectMapper().readValue(json, JournalRequestDTO.class);
        Journal saved = journalService.updateJournal(email, id, req);
        attachmentService.removeAllAttachments(email, id);

        if (newFiles != null) {
            for (MultipartFile f : newFiles) {
                attachmentService.addAttachment(email, id, f);
            }
        }
        return ResponseEntity.ok(toDto(saved));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id, Authentication auth) {
        String email = auth.getName();
        journalService.deleteJournal(email, id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/public/{publicUrl}")
    public ResponseEntity<?> publicGet(@PathVariable String publicUrl) {
        Journal j = journalService.getByPublicUrl(publicUrl);
        if (!"public".equalsIgnoreCase(j.getVisibility())) {
            return ResponseEntity.status(403).body("This journal is not publicly accessible");
        }
        return ResponseEntity.ok(toDto(j));
    }

    @PutMapping("/{id}/view-later")
    public ResponseEntity<JournalResponseDTO> toggleViewLater(@PathVariable Long id, @RequestParam boolean value, Authentication auth) {
        String email = auth.getName();
        Journal j = journalService.getByIdForUser(email, id);
        JournalRequestDTO dto = new JournalRequestDTO();
        dto.setTitle(j.getTitle());
        dto.setContent(j.getContent());
        dto.setVisibility(j.getVisibility());
        dto.setViewLater(value);
        Journal saved = journalService.updateJournal(email, id, dto);
        return ResponseEntity.ok(toDto(saved));
    }

    private JournalResponseDTO toDto(Journal j) {
        var atts = j.getAttachments() == null ? List.<String>of()
                  : j.getAttachments().stream().map(Attachment::getUrl).collect(Collectors.toList());
        return new JournalResponseDTO(j.getId(), j.getTitle(), j.getContent(), j.getVisibility(), j.getPublicUrl(), j.isViewLater(), atts);
    }

    @PostMapping(value = "/{id}/attachments", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadAttachment(
            @PathVariable Long id,
            @RequestPart("file") MultipartFile file,
            Authentication auth) {

        attachmentService.addAttachment(auth.getName(), id, file);
        return ResponseEntity.ok("uploaded");
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
