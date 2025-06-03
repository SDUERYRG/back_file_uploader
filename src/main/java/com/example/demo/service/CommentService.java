package com.example.demo.service;

import com.example.demo.entity.Comment;
import java.util.List;

public interface CommentService {
    /**
     * 通过文件ID获取评论
     * @param fileId 文件ID
     * @return 评论列表
     */
    List<Comment> getCommentsByFileId(Integer fileId);

    /**
     * 获取评论总数
     * @param fileId 文件ID
     * @return 评论数量
     */
    int countCommentsByFileId(Integer fileId);

    int addComment(Comment comment);
}