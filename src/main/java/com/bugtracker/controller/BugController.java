package com.bugtracker.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import com.bugtracker.entity.Bug;
import com.bugtracker.service.BugService;

@Controller
@RequestMapping("/bugs")
public class BugController {

    @Autowired
    private BugService bugService;

    @GetMapping
    public String viewBugs(Model model) {

        model.addAttribute("bugs", bugService.getAllBugs());

        return "bug-list";
    }

    @GetMapping("/create")
    public String createBugForm(Model model) {

        model.addAttribute("bug", new Bug());

        return "create-bug";
    }

    @PostMapping("/save")
    public String saveBug(@ModelAttribute Bug bug) {

        bugService.saveBug(bug);

        return "redirect:/bugs";
    }

}
