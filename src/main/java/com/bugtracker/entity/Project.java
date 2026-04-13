package com.bugtracker.entity;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.*;

@Entity
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String description;

    private LocalDateTime createdDate = LocalDateTime.now();

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL)
    private List<Bug> bugs;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public LocalDateTime getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(LocalDateTime createdDate) {
		this.createdDate = createdDate;
	}

	public List<Bug> getBugs() {
		return bugs;
	}

	public void setBugs(List<Bug> bugs) {
		this.bugs = bugs;
	}

    
}