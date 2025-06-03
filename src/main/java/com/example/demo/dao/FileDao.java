package com.example.demo.dao;

import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.demo.entity.File;

@Repository
@Mapper
public interface FileDao extends BaseMapper<File>{
    @Insert("INSERT INTO file (filename, filepath, userid, filetype, filesize) VALUES (#{filename}, #{filepath}, #{userid}, #{filetype}, #{filesize})")
    public boolean incrementFileCount(File file);

    @Select("SELECT COUNT(*) FROM file")
    public int getFileCount();

    @Select("SELECT COUNT(*) FROM file WHERE filetype IN (${types}) AND (state IS NULL OR state != 'deleted')")
    Long countByTypes(@Param("types") String types); // 参数类型为String

    @Select("SELECT DISTINCT filetype FROM file WHERE (state IS NULL OR state != 'deleted')")
    List<String> findDistinctFileTypes();

    @Select("SELECT * FROM file WHERE (state IS NULL OR state != 'deleted') ORDER BY lookednum DESC LIMIT 10")
    List<File> findTop10ByOrderByLookednumDesc();

    @Select("SELECT * FROM file WHERE filetype NOT IN (${types}) AND (state IS NULL OR state != 'deleted')")
    List<File> findByFileTypeNotIn(@Param("types") String types);

    @Select("SELECT * FROM file WHERE filetype IN (${types}) AND (state IS NULL OR state != 'deleted')")
    List<File> findByFileTypeIn(@Param("types") String types);

    /**
     * 增加文件收藏量
     * @param fileId 文件ID
     * @return 更新的行数
     */
    @Update("UPDATE file SET collectnum = collectnum + 1 WHERE id = #{fileId}")
    int increaseCollectNum(@Param("fileId") Integer fileId);

    @Select("SELECT collectnum FROM file WHERE id = #{fileId}")
    int getCollectNum(@Param("fileId") Integer fileId);

    /**
     * 减少文件收藏量
     * @param fileId 文件ID
     * @return 更新的行数
     */
    @Update("UPDATE file SET collectnum = collectnum - 1 WHERE id = #{fileId}")
    int decreaseCollectNum(@Param("fileId") Integer fileId);

    /**
     * 查询未删除的文件列表（根据用户ID）
     * @param userId 用户ID
     * @return 文件列表
     */
    @Select("SELECT * FROM file WHERE userid = #{userId} AND (state IS NULL OR state != 'deleted')")
    List<File> findActiveFilesByUserId(@Param("userId") Integer userId);

    /**
     * 增加文件下载量
     * @param fileId 文件ID
     * @return 更新的行数
     */
    @Update("UPDATE file SET downloadnum = downloadnum + 1 WHERE id = #{fileId}")
    int increaseDownloadNum(@Param("fileId") Integer fileId);

    /**
     * 增加文件浏览量
     * @param fileId 文件ID
     * @return 更新的行数
     */
    @Update("UPDATE file SET lookednum = lookednum + 1 WHERE id = #{fileId}")
    int increaseLookedNum(@Param("fileId") Integer fileId);

    /**
     * 逻辑删除文件（更新state为deleted）
     * @param fileId 文件ID
     * @return 更新的行数
     */
    @Update("UPDATE file SET state = 'deleted' WHERE id = #{fileId}")
    int logicDeleteById(@Param("fileId") Integer fileId);
}
