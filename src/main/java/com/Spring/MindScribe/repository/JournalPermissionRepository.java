package com.Spring.MindScribe.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.Spring.MindScribe.models.Journal;
import com.Spring.MindScribe.models.JournalPermission;
import com.Spring.MindScribe.models.User;

public interface JournalPermissionRepository extends JpaRepository<JournalPermission, Long>{
    List<JournalPermission> findByJournal(Journal journal);
    Optional<JournalPermission> findByJournalAndUser(Journal journal, User user);
    List<JournalPermission> findByUser(User user);
}
