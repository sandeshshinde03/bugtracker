package com.bugtracker.service;

import java.util.List;

import com.bugtracker.entity.Bug;
import com.bugtracker.entity.Comment;

public interface CommentService {
	void saveComment(Comment comment);
	List<Comment> getCommentsByBug(Bug bug);
}
