package com.Spring.MindScribe.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.Spring.MindScribe.models.Journal;
import com.Spring.MindScribe.models.User;

public interface JournalRepository extends JpaRepository<Journal, Long>{
    List<Journal> findByOwner(User owner);
    Optional<Journal> findByPublicUrl(String publicUrl);
}
