package com.github.cbyzzy.model.entity;

import java.util.Date;
import javax.persistence.*;

@Table(name = "t_camera")
public class Camera {
    //主键id
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    //设备编号（或者序列号之类的）
    private String no;

    //设备类型（0-摄像头 1-NVR录像机）
    private Integer type;

    //摄像头设备所属NVR录像机设备的id
    @Column(name = "belong_nvr_id")
    private Integer belongNvrId;

    //登录用户名
    private String account;

    //登录密码
    private String password;

    //ip地址
    private String ip;

    //端口
    private String port;

    //设备名称
    private String name;

    //设备是否支持云台控制
    @Column(name = "ptz_control")
    private Byte ptzControl;

    //设备启用标志
    private Byte enable;

    //这个是我用来标识这个摄像头是属于哪个房间的
    @Column(name = "tester_id")
    private String testerId;

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
     * @return no
     */
    public String getNo() {
        return no;
    }

    /**
     * @param no
     */
    public void setNo(String no) {
        this.no = no;
    }

    /**
     * @return type
     */
    public Integer getType() {
        return type;
    }

    /**
     * @param type
     */
    public void setType(Integer type) {
        this.type = type;
    }

    /**
     * @return belong_nvr_id
     */
    public Integer getBelongNvrId() {
        return belongNvrId;
    }

    /**
     * @param belongNvrId
     */
    public void setBelongNvrId(Integer belongNvrId) {
        this.belongNvrId = belongNvrId;
    }

    /**
     * @return account
     */
    public String getAccount() {
        return account;
    }

    /**
     * @param account
     */
    public void setAccount(String account) {
        this.account = account;
    }

    /**
     * @return password
     */
    public String getPassword() {
        return password;
    }

    /**
     * @param password
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * @return ip
     */
    public String getIp() {
        return ip;
    }

    /**
     * @param ip
     */
    public void setIp(String ip) {
        this.ip = ip;
    }

    /**
     * @return port
     */
    public String getPort() {
        return port;
    }

    /**
     * @param port
     */
    public void setPort(String port) {
        this.port = port;
    }

    /**
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return ptz_control
     */
    public Byte getPtzControl() {
        return ptzControl;
    }

    /**
     * @param ptzControl
     */
    public void setPtzControl(Byte ptzControl) {
        this.ptzControl = ptzControl;
    }

    /**
     * @return enable
     */
    public Byte getEnable() {
        return enable;
    }

    /**
     * @param enable
     */
    public void setEnable(Byte enable) {
        this.enable = enable;
    }

    /**
     * @return tester_id
     */
    public String getTesterId() {
        return testerId;
    }

    /**
     * @param testerId
     */
    public void setTesterId(String testerId) {
        this.testerId = testerId;
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