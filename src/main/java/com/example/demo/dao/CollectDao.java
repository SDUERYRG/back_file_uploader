package com.example.demo.dao;

import com.example.demo.entity.Collect;
import com.example.demo.entity.File;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
@Mapper
public interface CollectDao {
    /**
     * 通过用户ID获取收藏的文件（过滤掉已删除的文件）
     * @param userId 用户ID
     * @return 收藏的文件列表
     */
    @Select("SELECT f.* FROM file f " +
            "JOIN collect c ON f.id = c.fileid " +
            "WHERE c.userid = #{userId} AND (f.state IS NULL OR f.state != 'deleted')")
    List<File> findFilesByUserId(@Param("userId") Integer userId);

    /**
     * 新增收藏记录
     * @param collect 收藏对象
     * @return 插入的行数
     */
    @Insert("INSERT INTO collect(userid, fileid) VALUES(#{userid}, #{fileid})")
    int insertCollect(Collect collect);

    /**
     * 删除收藏记录
     * @param userId 用户ID
     * @param fileId 文件ID
     * @return 删除的行数
     */
    @Delete("DELETE FROM collect WHERE userid = #{userId} AND fileid = #{fileId}")
    int deleteCollect(@Param("userId") Integer userId, @Param("fileId") Integer fileId);

    /**
     * 检查用户是否已收藏文件（只检查未删除的文件）
     * @param userId 用户ID
     * @param fileId 文件ID
     * @return 收藏记录数量
     */
    @Select("SELECT COUNT(*) FROM collect c " +
            "JOIN file f ON c.fileid = f.id " +
            "WHERE c.userid = #{userId} AND c.fileid = #{fileId} " +
            "AND (f.state IS NULL OR f.state != 'deleted')")
    int checkCollectExists(@Param("userId") Integer userId, @Param("fileId") Integer fileId);
}