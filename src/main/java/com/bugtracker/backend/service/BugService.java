package com.bugtracker.backend.service;

import com.bugtracker.backend.model.Bug;
import com.bugtracker.backend.repository.BugRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class BugService {

    @Autowired
    private BugRepository bugRepository;

    public List<Bug> getAllBugs() {
        return bugRepository.findAll();
    }

    public Optional<Bug> getBugById(Long id) {
        return bugRepository.findById(id);
    }

    public Bug createBug(Bug bug) {
        bug.setCreatedAt(LocalDateTime.now());
        return bugRepository.save(bug);
    }

    public Bug updateBug(Long id, Bug updatedBug) {
        return bugRepository.findById(id)
            .map(existing -> {
                existing.setTitle(updatedBug.getTitle());
                existing.setDescription(updatedBug.getDescription());
                existing.setPriority(updatedBug.getPriority());
                existing.setStatus(updatedBug.getStatus());
                existing.setAssignee(updatedBug.getAssignee());
                existing.setDueDate(updatedBug.getDueDate());
                return bugRepository.save(existing);
            })
            .orElseThrow(() -> new RuntimeException("Bug not found"));
    }

    public void deleteBug(Long id) {
        bugRepository.deleteById(id);
    }
}
