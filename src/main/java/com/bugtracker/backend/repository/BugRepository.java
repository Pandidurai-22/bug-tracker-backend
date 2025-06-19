package com.bugtracker.backend.repository;

import com.bugtracker.backend.model.Bug;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BugRepository extends JpaRepository<Bug, Long>{
    List<Bug> findByStatus(String status);
    List<Bug> findByPriority(String priority);
    List<Bug> findByAssignee(String assignee);

}