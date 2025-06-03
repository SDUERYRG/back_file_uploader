package com.example.demo.dao;

import com.example.demo.entity.Comment;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
@Mapper
public interface CommentDao {
    /**
     * 通过文件ID获取评论
     * @param fileId 文件ID
     * @return 评论列表
     */
    @Select("SELECT * FROM comment WHERE fileid = #{fileId} ORDER BY time DESC")
    @Results({
            @Result(property = "id", column = "id", id = true),
            @Result(property = "userid", column = "userid"),
            @Result(property = "fileid", column = "fileid"),
            @Result(property = "content", column = "content"),
            @Result(property = "time", column = "time")
    })
    List<Comment> findCommentsByFileId(@Param("fileId") Integer fileId);

    /**
     * 获取评论总数
     * @param fileId 文件ID
     * @return 评论数量
     */
    @Select("SELECT COUNT(*) FROM comment WHERE fileid = #{fileId}")
    int countCommentsByFileId(@Param("fileId") Integer fileId);

    // 添加评论
    @Insert("INSERT INTO comment(userid, fileid, content, time) " +
            "VALUES(#{userid}, #{fileid}, #{content}, NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    int insertComment(Comment comment);
}