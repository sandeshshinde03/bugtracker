package com.bugtracker.service;

import java.util.List;
import java.util.Map;

import com.bugtracker.entity.Bug;
import com.bugtracker.entity.User;

public interface BugService {

    long countTotalBugs();

    long countOpenBugs();

    long countResolvedBugs();

    long countCriticalBugs();
    
    long countClosedBugs();

    List<Bug> getAllBugs();
    
    void assignBugToDeveloper(Long bugId, Long developerId);

    List<Bug> getUnassignedBugs();

    List<Map<String, Object>> getBugsPerDeveloper();

    Map<String, Long> getBugStatusStats();

    Map<String, Long> getBugProjectStats();
    
    List<Bug> getBugsByDeveloper(User developer);

    void saveBug(Bug bug);
    
    Bug getBugById(Long id);
    
    void updateBugStatus(Long id, String status);
    
    List<Bug> getBugsByReporter(User user);
    
    long countBugsByReporter(User user);

    long countBugsByReporterAndStatus(User user, String status);

}