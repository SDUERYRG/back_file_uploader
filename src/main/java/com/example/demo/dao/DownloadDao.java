package com.example.demo.dao;

import com.example.demo.entity.Download;
import com.example.demo.entity.File;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
@Mapper
public interface DownloadDao {
    /**
     * 通过用户ID获取下载的文件
     * @param userId 用户ID
     * @return 下载的文件列表
     */
    @Select("SELECT f.* FROM file f " +
            "JOIN download d ON f.id = d.fileid " +
            "WHERE d.userid = #{userId} AND (f.state IS NULL OR f.state != 'deleted') " +
            "ORDER BY d.time DESC")
    List<File> findFilesByUserId(@Param("userId") Integer userId);

    /**
     * 新增下载记录
     * @param download 下载对象
     * @return 插入的行数
     */
    @Insert("INSERT INTO download(userid, fileid, time) VALUES(#{userid}, #{fileid}, #{time})")
    int insertDownload(Download download);

    /**
     * 检查用户是否已下载文件
     * @param userId 用户ID
     * @param fileId 文件ID
     * @return 下载记录数量
     */
    @Select("SELECT COUNT(*) FROM download WHERE userid = #{userId} AND fileid = #{fileId}")
    int checkDownloadExists(@Param("userId") Integer userId, @Param("fileId") Integer fileId);
} 