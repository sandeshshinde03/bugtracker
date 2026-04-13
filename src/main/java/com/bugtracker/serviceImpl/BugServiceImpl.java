package com.bugtracker.serviceImpl;

import com.bugtracker.entity.Bug;
import com.bugtracker.entity.User;
import com.bugtracker.repository.BugRepository;
import com.bugtracker.repository.UserRepository;
import com.bugtracker.service.BugService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class BugServiceImpl implements BugService {

    @Autowired
    private BugRepository bugRepository;

    @Autowired
    private UserRepository userRepository;

    // ===============================
    // COUNT METHODS
    // ===============================

    @Override
    public long countTotalBugs() {
        return bugRepository.count();
    }

    @Override
    public long countOpenBugs() {
        return bugRepository.countByStatus("OPEN");
    }

    @Override
    public long countResolvedBugs() {
        return bugRepository.countByStatus("RESOLVED");
    }

    @Override
    public long countClosedBugs() {   // ✅ NEW
        return bugRepository.countByStatus("CLOSED");
    }

    @Override
    public long countCriticalBugs() {
        return bugRepository.countByPriority("CRITICAL");
    }

    // ===============================
    // BUG FETCHING
    // ===============================

    @Override
    public List<Bug> getAllBugs() {
        return bugRepository.findAll();
    }

    @Override
    public List<Bug> getUnassignedBugs() {
        return bugRepository.findByAssignedDeveloperIsNull();
    }

    @Override
    public List<Bug> getBugsByDeveloper(User developer) {
        return bugRepository.findByAssignedDeveloper(developer);
    }

    @Override
    public List<Bug> getBugsByReporter(User user) {
        return bugRepository.findByReporter(user);
    }

    // ===============================
    // DEVELOPER STATS (UPDATED)
    // ===============================

    @Override
    public List<Map<String, Object>> getBugsPerDeveloper() {

        List<Object[]> results = bugRepository.countBugsPerDeveloper();
        List<Map<String, Object>> list = new ArrayList<>();

        for (Object[] row : results) {

            Map<String, Object> map = new HashMap<>();

            map.put("name", row[0]);
            map.put("total", row[1]);
            map.put("resolved", row[2]);
            map.put("pending", row[3]);
            map.put("closed", row[4]);   

            list.add(map);
        }

        return list;
    }

    // ===============================
    // STATUS STATS
    // ===============================

    @Override
    public Map<String, Long> getBugStatusStats() {

        Map<String, Long> stats = new HashMap<>();

        stats.put("OPEN", bugRepository.countByStatus("OPEN"));
        stats.put("IN_PROGRESS", bugRepository.countByStatus("IN_PROGRESS"));
        stats.put("RESOLVED", bugRepository.countByStatus("RESOLVED"));
        stats.put("CLOSED", bugRepository.countByStatus("CLOSED"));  // ✅ NEW

        return stats;
    }

    // ===============================
    // PROJECT STATS
    // ===============================

    @Override
    public Map<String, Long> getBugProjectStats() {

        List<Object[]> results = bugRepository.countBugsByProject();
        Map<String, Long> map = new HashMap<>();

        for (Object[] row : results) {
            map.put((String) row[0], (Long) row[1]);
        }

        return map;
    }

    // ===============================
    // SAVE / UPDATE
    // ===============================

    @Override
    public void saveBug(Bug bug) {
        bugRepository.save(bug);
    }

    @Override
    public Bug getBugById(Long id) {
        return bugRepository.findById(id).orElse(null);
    }

    @Override
    public void updateBugStatus(Long id, String status) {

        Bug bug = bugRepository.findById(id).orElseThrow();

        bug.setStatus(status);

        bugRepository.save(bug);
    }

    // ===============================
    // ASSIGN BUG
    // ===============================

    @Override
    public void assignBugToDeveloper(Long bugId, Long developerId) {

        Bug bug = bugRepository.findById(bugId).orElseThrow();
        User developer = userRepository.findById(developerId).orElseThrow();

        bug.setAssignedDeveloper(developer);
        bug.setStatus("OPEN");

        bugRepository.save(bug);
    }

    // ===============================
    // REPORTER COUNT
    // ===============================

    @Override
    public long countBugsByReporter(User user) {
        return bugRepository.countByReporter(user);
    }

    @Override
    public long countBugsByReporterAndStatus(User user, String status) {
        return bugRepository.countByReporterAndStatus(user, status);
    }
}