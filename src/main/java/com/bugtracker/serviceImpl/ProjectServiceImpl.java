package com.bugtracker.serviceImpl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bugtracker.entity.Project;
import com.bugtracker.repository.ProjectRepository;
import com.bugtracker.service.ProjectService;

@Service
public class ProjectServiceImpl implements ProjectService {

    @Autowired
    private ProjectRepository projectRepository;

    @Override
    public List<Project> getAllProjects() {
        return projectRepository.findAll();
    }

    @Override
    public Project getProjectById(Long id) {
        return projectRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Project not found with id: " + id));
    }

    @Override
    public Project saveProject(Project project) {
        return projectRepository.save(project);
    }

    @Override
    public void deleteProject(Long id) {
        projectRepository.deleteById(id);
    }
}