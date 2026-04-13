package com.bugtracker.controller;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.bugtracker.entity.Bug;
import com.bugtracker.entity.Comment;
import com.bugtracker.entity.User;
import com.bugtracker.repository.UserRepository;
import com.bugtracker.service.BugService;
import com.bugtracker.service.CommentService;

@Controller
@RequestMapping("/developer")
public class DeveloperController {

    @Autowired
    private BugService bugService;

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private CommentService commentService;

    @GetMapping("/dashboard")
    public String developerDashboard(Model model, Authentication auth) {

        String email = auth.getName();
        User developer = userRepository.findByEmail(email);

        List<Bug> bugs = bugService.getBugsByDeveloper(developer);

        long totalAssigned = bugs.size();

        long inProgress = bugs.stream()
                .filter(b -> "IN_PROGRESS".equals(b.getStatus()))
                .count();

        long resolved = bugs.stream()
                .filter(b -> "RESOLVED".equals(b.getStatus()))
                .count();

        long closed = bugs.stream()                     
                .filter(b -> "CLOSED".equals(b.getStatus()))
                .count();

        model.addAttribute("bugs", bugs);
        model.addAttribute("totalAssigned", totalAssigned);
        model.addAttribute("inProgress", inProgress);
        model.addAttribute("resolved", resolved);
        model.addAttribute("closed", closed);          
        model.addAttribute("developerName", developer.getName());

        return "developer/developer-dashboard";
    }
    
    @GetMapping("/update-status/{id}")
    public String updateStatusPage(@PathVariable Long id, Model model) {

        Bug bug = bugService.getBugById(id);

        model.addAttribute("bug", bug);

        return "developer/update-status";
    }

    @PostMapping("/update-status")
    public String updateBugStatus(@RequestParam Long id,
                                  @RequestParam String status) {

        bugService.updateBugStatus(id, status);

        return "redirect:/developer/dashboard";
    }
    
    @GetMapping("/assigned-bugs")
    public String assignedBugs(Model model, Authentication auth) {

        String email = auth.getName();

        User developer = userRepository.findByEmail(email);

        List<Bug> bugs = bugService.getBugsByDeveloper(developer);

        model.addAttribute("bugs", bugs);

        return "developer/assigned-bugs";
    }
    
    @GetMapping("/in-progress")
    public String inProgressBugs(Model model, Authentication auth) {

        String email = auth.getName();

        User developer = userRepository.findByEmail(email);

        List<Bug> bugs = bugService.getBugsByDeveloper(developer)
                .stream()
                .filter(b -> "IN_PROGRESS".equals(b.getStatus()))
                .toList();

        model.addAttribute("bugs", bugs);

        return "developer/in-progress";
    }
    
    @GetMapping("/resolved")
    public String resolvedBugs(Model model, Authentication auth) {

        String email = auth.getName();

        User developer = userRepository.findByEmail(email);

        List<Bug> bugs = bugService.getBugsByDeveloper(developer)
                .stream()
                .filter(b -> "RESOLVED".equals(b.getStatus()))
                .toList();

        model.addAttribute("bugs", bugs);

        return "developer/resolved";
    }
    
    @PostMapping("/add-comment")
    public String addComment(@RequestParam Long bugId,
                             @RequestParam String message,
                             Authentication auth) {

        String email = auth.getName();

        User user = userRepository.findByEmail(email);

        Bug bug = bugService.getBugById(bugId);

        Comment comment = new Comment();
        comment.setMessage(message);
        comment.setBug(bug);
        comment.setUser(user);

        commentService.saveComment(comment);

        return "redirect:/developer/update-status/" + bugId;
    }
    
    @GetMapping("/bug/{id}")
    public String viewBug(@PathVariable Long id, Model model) {

        Bug bug = bugService.getBugById(id);

        model.addAttribute("bug", bug);

        // ✅ FIX HERE
        model.addAttribute("comments", commentService.getCommentsByBug(bug));

        return "developer/bug-details";
    }
    @PostMapping("/bug/{id}/comment")
    public String addComment(@PathVariable Long id,
                             @RequestParam String message,
                             Principal principal) {

        // 1. Get logged-in user
        String email = principal.getName();
        User user = userRepository.findByEmail(email);

        // 2. Get bug
        Bug bug = bugService.getBugById(id);

        // 3. Create comment
        Comment comment = new Comment();
        comment.setMessage(message);
        comment.setBug(bug);
        comment.setUser(user);
        comment.setCreatedDate(LocalDateTime.now());

        // 4. Save
        commentService.saveComment(comment);

        // 5. Redirect back
        return "redirect:/developer/bug/" + id;
    }
    
}