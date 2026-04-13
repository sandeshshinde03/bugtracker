package com.bugtracker.controller;

import com.bugtracker.entity.Bug;
import com.bugtracker.entity.Comment;
import com.bugtracker.entity.Project;
import com.bugtracker.entity.User;
import com.bugtracker.repository.BugRepository;
import com.bugtracker.repository.ProjectRepository;
import com.bugtracker.repository.UserRepository;
import com.bugtracker.service.BugService;
import com.bugtracker.service.CommentService;
import com.bugtracker.service.ProjectService;
import com.bugtracker.service.UserService;

import java.security.Principal;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private BugService bugService;

    @Autowired
    private UserService userService;

    @Autowired
    private ProjectService projectService;
    
    @Autowired
	private UserRepository userRepository;
    
    @Autowired
    private ProjectRepository projectRepository;
    
    @Autowired
    private BugRepository bugRepository;
    
    @Autowired
    private CommentService commentService;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

    // ===============================
    // ADMIN DASHBOARD
    // ===============================

    @GetMapping("/dashboard")
    public String adminDashboard(Model model) {

        model.addAttribute("totalBugs", bugService.countTotalBugs());
        //model.addAttribute("openBugs", bugService.countOpenBugs());
        model.addAttribute("resolvedBugs", bugService.countResolvedBugs());
        model.addAttribute("closedBugs", bugService.countClosedBugs());
        model.addAttribute("criticalBugs", bugService.countCriticalBugs());

        model.addAttribute("developerStats", bugService.getBugsPerDeveloper());

        return "admin/admin-dashboard";
    }

    // ===============================
    // USER MANAGEMENT
    // ===============================

    @GetMapping("/users")
    public String userList(Model model) {

        model.addAttribute("users", userService.getAllUsers());

        return "admin/user-list";
    }

    @GetMapping("/users/add")
    public String addUserForm() {
        return "admin/add-user";
    }

    @PostMapping("/add-user")
    public String registerUser(User user, Model model) {

        if (userRepository.existsByEmail(user.getEmail())) {
            model.addAttribute("error", "Email already exists!");
            return "admin/add-user";
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
        return "redirect:/admin/users";
    }
    
    @GetMapping("/users/delete/{id}")
    public String deleteUser(@PathVariable Long id) {

        userRepository.deleteById(id);

        return "redirect:/admin/users";
    }

    // ===============================
    // PROJECT MANAGEMENT
    // ===============================

    @GetMapping("/projects")
    public String projectList(Model model) {

        model.addAttribute("projects", projectService.getAllProjects());

        return "admin/project-list";
    }

    @GetMapping("/projects/add")
    public String addProjectForm() {

        return "admin/add-project";
    }
    
    @PostMapping("/projects/add")
    public String saveProject(Project project) {

        projectRepository.save(project);

        return "redirect:/admin/projects";
    }

 @GetMapping("/projects/edit/{id}")
 public String editProjectForm(@PathVariable Long id, Model model) {

     Project project = projectRepository.findById(id).orElseThrow();

     model.addAttribute("project", project);

     return "admin/edit-project";
 }


 @PostMapping("/projects/update/{id}")
 public String updateProject(@PathVariable Long id, Project project) {

     Project existingProject = projectRepository.findById(id).orElseThrow();

     existingProject.setName(project.getName());
     existingProject.setDescription(project.getDescription());

     projectRepository.save(existingProject);

     return "redirect:/admin/projects";
 }


 @GetMapping("/projects/delete/{id}")
 public String deleteProject(@PathVariable Long id) {

     projectRepository.deleteById(id);

     return "redirect:/admin/projects";
 }

    // ===============================
    // BUG MANAGEMENT
    // ===============================

    @GetMapping("/bugs")
    public String bugList(Model model) {

        model.addAttribute("bugs", bugService.getAllBugs());

        return "admin/bug-list";
    }

 @GetMapping("/bugs/view/{id}")
 public String viewBug(@PathVariable Long id, Model model) {

     Bug bug = bugRepository.findById(id).orElseThrow();

     model.addAttribute("bug", bug);

     return "admin/bug-view";
 }

 @GetMapping("/bugs/edit/{id}")
 public String editBug(@PathVariable Long id, Model model) {

     Bug bug = bugService.getBugById(id);

     model.addAttribute("bug", bug);
     model.addAttribute("projects", projectService.getAllProjects());
     model.addAttribute("developers", userService.getDevelopers());

     return "admin/bug-edit";
 }

 @PostMapping("/bugs/update")
 public String updateBug(
         @ModelAttribute Bug updatedBug,
         @RequestParam Long projectId,
         @RequestParam(required = false) Long developerId
 ) {

     // 1. Fetch existing bug from DB
     Bug existingBug = bugService.getBugById(updatedBug.getId());

     // 2. Update only editable fields
     existingBug.setTitle(updatedBug.getTitle());
     existingBug.setPriority(updatedBug.getPriority());

     // 3. Set Project
     Project project = projectService.getProjectById(projectId);
     existingBug.setProject(project);

     // 4. Set Developer
     if (developerId != null) {
         User dev = userService.getUserById(developerId);
         existingBug.setAssignedDeveloper(dev);
     } else {
         existingBug.setAssignedDeveloper(null);
     }

     // 5. Save updated entity
     bugService.saveBug(existingBug);

     return "redirect:/admin/bugs";
 }

 @GetMapping("/bugs/delete/{id}")
 public String deleteBug(@PathVariable Long id) {

     bugRepository.deleteById(id);

     return "redirect:/admin/bugs";
 }
    

    // ===============================
    // ASSIGN BUGS TO DEVELOPERS
    // ===============================

    @GetMapping("/assign")
    public String assignBugPage(Model model) {

        model.addAttribute("bugs", bugService.getUnassignedBugs());
        model.addAttribute("developers", userService.getDevelopers());

        return "admin/assign-bug";
    }

    @PostMapping("/assign")
    public String assignBugToDeveloper(
            @RequestParam Long bugId,
            @RequestParam Long developerId) {

        bugService.assignBugToDeveloper(bugId, developerId);

        return "redirect:/admin/bugs";
    }

    // ===============================
    // REPORTS / STATISTICS
    // ===============================

    @GetMapping("/reports")
    public String reports(Model model) {

        model.addAttribute("bugsByStatus", bugService.getBugStatusStats());
        model.addAttribute("bugsByProject", bugService.getBugProjectStats());

        return "admin/reports";
    }
    
    @GetMapping("/bug/{id}")
    public String viewBugs(@PathVariable Long id, Model model) {

        Bug bug = bugService.getBugById(id);

        model.addAttribute("bug", bug);
        model.addAttribute("comments", commentService.getCommentsByBug(bug));

        return "admin/bug-details";
    }
    
    @PostMapping("/bug/{id}/comment")
    public String addComment(@PathVariable Long id,
                             @RequestParam String message,
                             Principal principal) {

        User user = userRepository.findByEmail(principal.getName());
        Bug bug = bugService.getBugById(id);

        Comment comment = new Comment();
        comment.setBug(bug);
        comment.setUser(user);
        comment.setMessage(message);
        comment.setCreatedDate(LocalDateTime.now());

        commentService.saveComment(comment);

        return "redirect:/admin/bug/" + id;
    }

}