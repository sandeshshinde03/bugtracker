package com.bugtracker.serviceImpl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bugtracker.entity.Bug;
import com.bugtracker.entity.Comment;
import com.bugtracker.repository.CommentRepository;
import com.bugtracker.service.CommentService;

@Service
public class CommenttServiceImpl implements CommentService {
	  @Autowired
	    private CommentRepository commentRepository;

	    public void saveComment(Comment comment) {
	        commentRepository.save(comment);
	    }

	    public List<Comment> getCommentsByBug(Bug bug) {
	        return commentRepository.findByBug(bug);
	    }
}
