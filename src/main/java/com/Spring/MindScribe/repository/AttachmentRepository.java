package com.Spring.MindScribe.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.Spring.MindScribe.models.Attachment;

public interface AttachmentRepository extends JpaRepository<Attachment, Long> {
}
