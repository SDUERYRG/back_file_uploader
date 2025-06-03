package com.example.demo.service.imp;

import com.example.demo.dao.CommentDao;
import com.example.demo.entity.Comment;
import com.example.demo.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentServiceImp implements CommentService {

    private final CommentDao commentDao;

    @Autowired
    public CommentServiceImp(CommentDao commentDao) {
        this.commentDao = commentDao;
    }

    @Override
    public List<Comment> getCommentsByFileId(Integer fileId) {
        return commentDao.findCommentsByFileId(fileId);
    }

    @Override
    public int countCommentsByFileId(Integer fileId) {
        return commentDao.countCommentsByFileId(fileId);
    }

    @Override
    public int addComment(Comment comment) {
        return commentDao.insertComment(comment);
    }
}