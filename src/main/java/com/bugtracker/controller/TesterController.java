package com.bugtracker.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.bugtracker.entity.Bug;
import com.bugtracker.entity.Comment;
import com.bugtracker.entity.User;
import com.bugtracker.repository.CommentRepository;
import com.bugtracker.repository.UserRepository;
import com.bugtracker.service.BugService;
import com.bugtracker.service.ProjectService;

@Controller
@RequestMapping("/tester")
public class TesterController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BugService bugService;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private ProjectService projectService;

    // ==============================
    // COMMON METHOD (Reuse)
    // ==============================
    private User getLoggedInUser(Principal principal) {
        return userRepository.findByEmail(principal.getName());
    }

    // ==============================
    // 1. DASHBOARD
    // ==============================
    @GetMapping("/dashboard")
    public String testerDashboard(Model model, Principal principal) {

        User user = getLoggedInUser(principal);

        model.addAttribute("loggedInUser", user);

        model.addAttribute("totalBugs", bugService.countBugsByReporter(user));
        model.addAttribute("inProgressBugs", bugService.countBugsByReporterAndStatus(user, "IN_PROGRESS"));
        model.addAttribute("resolvedBugs", bugService.countBugsByReporterAndStatus(user, "RESOLVED"));
        model.addAttribute("closedBugs", bugService.countBugsByReporterAndStatus(user, "CLOSED"));

        model.addAttribute("bugs", bugService.getBugsByReporter(user));

        return "tester/tester-dashboard";
    }

    // ==============================
    // 2. REPORT BUG (FORM)
    // ==============================
    @GetMapping("/report-bug")
    public String showReportBugPage(Model model, Principal principal) {

        User user = getLoggedInUser(principal);

        model.addAttribute("loggedInUser", user);
        model.addAttribute("bug", new Bug());
        model.addAttribute("projects", projectService.getAllProjects());

        return "tester/create-bug";
    }

    // ==============================
    // 3. SAVE BUG
    // ==============================
    @PostMapping("/save-bug")
    public String saveBug(@ModelAttribute Bug bug, Principal principal) {

        User reporter = getLoggedInUser(principal);

        bug.setReporter(reporter);
        bug.setStatus("OPEN");

        bugService.saveBug(bug);

        return "redirect:/tester/dashboard";
    }

    // ==============================
    // 4. MY BUGS LIST
    // ==============================
    @GetMapping("/my-bugs")
    public String myBugs(Model model, Principal principal) {

        User user = getLoggedInUser(principal);

        List<Bug> bugs = bugService.getBugsByReporter(user);

        model.addAttribute("loggedInUser", user);
        model.addAttribute("bugs", bugs);

        return "tester/my-bugs";
    }

    // ==============================
    // 5. BUG DETAILS PAGE
    // ==============================
    @GetMapping("/bug/{id}")
    public String viewBug(@PathVariable Long id, Model model, Principal principal) {

        User user = getLoggedInUser(principal);

        Bug bug = bugService.getBugById(id);

        if (bug == null) {
            return "redirect:/tester/dashboard";
        }

        List<Comment> comments = bug.getComments();

        model.addAttribute("loggedInUser", user);
        model.addAttribute("bug", bug);
        model.addAttribute("comments", comments);

        return "tester/bug-details";
    }

    // ==============================
    // 6. ADD COMMENT
    // ==============================
    @PostMapping("/bug/{id}/comment")
    public String addComment(@PathVariable Long id,
                             @RequestParam("message") String message,
                             Principal principal) {

        User user = getLoggedInUser(principal);
        Bug bug = bugService.getBugById(id);

        Comment comment = new Comment();
        comment.setMessage(message);
        comment.setUser(user);
        comment.setBug(bug);

        commentRepository.save(comment);

        return "redirect:/tester/bug/" + id;
    }

    // ==============================
    // 7. CLOSE BUG (Tester only after RESOLVED)
    // ==============================
    @PostMapping("/bug/{id}/status")
    public String updateStatus(@PathVariable Long id,
                               @RequestParam String status,
                               Principal principal) {

        User user = getLoggedInUser(principal);
        Bug bug = bugService.getBugById(id);

        // Only reporter can update
        if (!bug.getReporter().getId().equals(user.getId())) {
            return "redirect:/tester/bug/" + id;
        }

        // ✅ CLOSE: only if RESOLVED → CLOSED
        if (status.equals("CLOSED")) {

            if (!bug.getStatus().equals("RESOLVED")) {
                return "redirect:/tester/bug/" + id;
            }

            bugService.updateBugStatus(id, "CLOSED");
        }

        // ✅ REOPEN: only if CLOSED → OPEN
        else if (status.equals("OPEN")) {

            if (!bug.getStatus().equals("CLOSED")) {
                return "redirect:/tester/bug/" + id;
            }

            bugService.updateBugStatus(id, "OPEN");
        }

        return "redirect:/tester/bug/" + id;
    }
}