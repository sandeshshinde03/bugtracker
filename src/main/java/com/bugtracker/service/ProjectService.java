package com.bugtracker.service;

import java.util.List;
import com.bugtracker.entity.Project;

public interface ProjectService {

    List<Project> getAllProjects();

    Project getProjectById(Long id);   

    Project saveProject(Project project); 

    void deleteProject(Long id); 
}