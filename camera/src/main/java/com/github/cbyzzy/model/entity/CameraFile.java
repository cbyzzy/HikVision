package com.github.cbyzzy.model.entity;

import java.util.Date;
import javax.persistence.*;

@Table(name = "t_camera_file")
public class CameraFile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "file_name")
    private String fileName;

    @Column(name = "file_path")
    private String filePath;

    @Column(name = "file_size")
    private Long fileSize;

    @Column(name = "file_alias")
    private String fileAlias;

    @Column(name = "create_time")
    private Date createTime;

    /**
     * @return id
     */
    public Integer getId() {
        return id;
    }

    /**
     * @param id
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * @return file_name
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * @param fileName
     */
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    /**
     * @return file_path
     */
    public String getFilePath() {
        return filePath;
    }

    /**
     * @param filePath
     */
    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    /**
     * @return file_size
     */
    public Long getFileSize() {
        return fileSize;
    }

    /**
     * @param fileSize
     */
    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    /**
     * @return file_alias
     */
    public String getFileAlias() {
        return fileAlias;
    }

    /**
     * @param fileAlias
     */
    public void setFileAlias(String fileAlias) {
        this.fileAlias = fileAlias;
    }

    /**
     * @return create_time
     */
    public Date getCreateTime() {
        return createTime;
    }

    /**
     * @param createTime
     */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}