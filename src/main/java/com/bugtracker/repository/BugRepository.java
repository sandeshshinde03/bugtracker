package com.bugtracker.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.bugtracker.entity.Bug;
import com.bugtracker.entity.User;

public interface BugRepository extends JpaRepository<Bug, Long> {

    // ===============================
    // BASIC METHODS
    // ===============================

    List<Bug> findByAssignedDeveloper(User developer);

    List<Bug> findByAssignedDeveloperIsNull();

    List<Bug> findByReporter(User reporter);

    long countByStatus(String status);

    long countByPriority(String priority);

    long countByReporter(User reporter);

    long countByReporterAndStatus(User reporter, String status);

    // ===============================
    // BUGS PER DEVELOPER (UPDATED)
    // ===============================

    @Query("""
    SELECT 
        b.assignedDeveloper.name,
        COUNT(b),
        SUM(CASE WHEN b.status = 'RESOLVED' THEN 1 ELSE 0 END),
        SUM(CASE WHEN b.status = 'OPEN' OR b.status = 'IN_PROGRESS' THEN 1 ELSE 0 END),
        SUM(CASE WHEN b.status = 'CLOSED' THEN 1 ELSE 0 END)
    FROM Bug b
    WHERE b.assignedDeveloper IS NOT NULL
    GROUP BY b.assignedDeveloper.name
    """)
    List<Object[]> countBugsPerDeveloper();

    // ===============================
    // BUGS PER PROJECT
    // ===============================

    @Query("""
    SELECT b.project.name, COUNT(b)
    FROM Bug b
    GROUP BY b.project.name
    """)
    List<Object[]> countBugsByProject();
}