package com.example.demo.dao;

import com.example.demo.entity.Looked;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Mapper
public interface LookedDao {
    /**
     * 插入浏览记录
     * @param looked 浏览记录对象
     * @return 插入的行数
     */
    @Insert("INSERT INTO looked(userid, fileid, time) VALUES(#{userid}, #{fileid}, NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    int insertLooked(Looked looked);

    /**
     * 根据用户ID获取浏览记录（只包含未删除的文件）
     * @param userId 用户ID
     * @return 浏览记录列表
     */
    @Select("SELECT l.* FROM looked l " +
            "JOIN file f ON l.fileid = f.id " +
            "WHERE l.userid = #{userId} AND (f.state IS NULL OR f.state != 'deleted') " +
            "ORDER BY l.time DESC")
    List<Looked> findLookedByUserId(@Param("userId") Integer userId);

    /**
     * 检查浏览记录是否存在（只检查未删除的文件）
     * @param userId 用户ID
     * @param fileId 文件ID
     * @return 记录数量
     */
    @Select("SELECT COUNT(*) FROM looked l " +
            "JOIN file f ON l.fileid = f.id " +
            "WHERE l.userid = #{userId} AND l.fileid = #{fileId} " +
            "AND (f.state IS NULL OR f.state != 'deleted')")
    int checkLookedExists(@Param("userId") Integer userId, @Param("fileId") Integer fileId);

    @Select("SELECT l.*, f.filename, f.filetype, u.name AS uploaderName " +
            "FROM looked l " +
            "LEFT JOIN file f ON l.fileid = f.id " +
            "LEFT JOIN user u ON l.userid = u.id " +
            "WHERE l.userid = #{userId} " +
            "AND (f.state IS NULL OR f.state != 'deleted') " +
            "ORDER BY l.time DESC")
    List<Looked> findLookedByUserIdWithDetails(@Param("userId") Integer userId);
}