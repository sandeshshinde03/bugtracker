package com.bugtracker.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bugtracker.entity.Project;

public interface ProjectRepository extends JpaRepository<Project, Long> {
	
}