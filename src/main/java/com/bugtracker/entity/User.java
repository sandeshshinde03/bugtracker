package com.bugtracker.entity;

import java.util.List;

import jakarta.persistence.*;

@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(unique = true)
    private String email;

    private String password;

    private String role;

    @OneToMany(mappedBy = "reporter")
    private List<Bug> reportedBugs;

    @OneToMany(mappedBy = "assignedDeveloper")
    private List<Bug> assignedBugs;

    @OneToMany(mappedBy = "user")
    private List<Comment> comments;

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

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public List<Bug> getReportedBugs() {
		return reportedBugs;
	}

	public void setReportedBugs(List<Bug> reportedBugs) {
		this.reportedBugs = reportedBugs;
	}

	public List<Bug> getAssignedBugs() {
		return assignedBugs;
	}

	public void setAssignedBugs(List<Bug> assignedBugs) {
		this.assignedBugs = assignedBugs;
	}

	public List<Comment> getComments() {
		return comments;
	}

	public void setComments(List<Comment> comments) {
		this.comments = comments;
	}

    // Getters and Setters
    
}