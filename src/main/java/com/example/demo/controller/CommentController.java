package com.example.demo.controller;

import com.example.demo.entity.Comment;
import com.example.demo.service.CommentService;
import com.example.demo.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/comment")
public class CommentController {

    private final CommentService commentService;

    @Autowired
    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @GetMapping("/getByFileId/{fileId}")
    public Result getCommentsByFileId(@PathVariable("fileId") Integer fileId) {
        try {
            System.out.println("===== 获取文件评论信息 =====");
            // 通过文件ID获取评论
            List<Comment> comments = commentService.getCommentsByFileId(fileId);

            if (comments == null || comments.isEmpty()) {
                System.out.println("文件暂无评论，文件ID: " + fileId);
                return Result.success(200, "文件暂无评论", new ArrayList<>());
            }

            // 返回评论列表
            System.out.println("返回文件评论信息: " + comments);
            return Result.success(200, "获取评论成功", comments);
        } catch (Exception e) {
            System.err.println("获取评论失败: " + e.getMessage());
            e.printStackTrace();
            return Result.error(500, "获取评论失败: " + e.getMessage());
        }
    }

    @GetMapping("/countByFileId/{fileId}")
    public Result countCommentsByFileId(@PathVariable("fileId") Integer fileId) {
        try {
            System.out.println("===== 获取文件评论数量 =====");
            int count = commentService.countCommentsByFileId(fileId);
            return Result.success(200, "获取评论数量成功", count);
        } catch (Exception e) {
            System.err.println("获取评论数量失败: " + e.getMessage());
            e.printStackTrace();
            return Result.error(500, "获取评论数量失败: " + e.getMessage());
        }
    }

    @PostMapping("/add")
    public Result addComment(@RequestBody Comment comment) {
        try {
            System.out.println("===== 添加新评论 =====");
            System.out.println("收到评论请求: " + comment);

            // 验证必要字段
            if (comment.getUserid() == null) {
                return Result.error(400, "缺少用户ID(userid)");
            }
            if (comment.getFileid() == null) {
                return Result.error(400, "缺少文件ID(fileid)");
            }
            if (comment.getContent() == null || comment.getContent().trim().isEmpty()) {
                return Result.error(400, "评论内容不能为空");
            }

            // 调用服务层插入数据
            int result = commentService.addComment(comment);
            if (result > 0) {
                System.out.println("评论添加成功: " + comment);
                return Result.success(200, "评论添加成功", comment);
            } else {
                System.err.println("评论添加失败，受影响行数: " + result);
                return Result.error(500, "评论添加失败");
            }
        } catch (Exception e) {
            System.err.println("添加评论异常: " + e.getMessage());
            e.printStackTrace();
            return Result.error(500, "服务器内部错误: " + e.getMessage());
        }
    }
}