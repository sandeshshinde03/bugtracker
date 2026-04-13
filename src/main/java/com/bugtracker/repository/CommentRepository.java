package com.bugtracker.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bugtracker.entity.Comment;
import com.bugtracker.entity.Bug;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findByBug(Bug bug);

}