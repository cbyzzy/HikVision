package com.github.cbyzzy.model.entity;

import java.util.Date;
import javax.persistence.*;

@Table(name = "t_camera_flow")
public class CameraFlow {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "camera_id")
    private Integer cameraId;

    @Column(name = "camera_ipc")
    private String cameraIpc;

    /**
     * ffmpeg推流任务Name
     */
    private String tasker;

    /**
     * 0-SDK 1-GB28182
     */
    @Column(name = "flow_type")
    private Integer flowType;

    private String rtsp;

    private String rtmp;

    private String flv;

    @Column(name = "ws_flv")
    private String wsFlv;

    private String hls;

    private Integer client;

    @Column(name = "flow_record")
    private Integer flowRecord;

    private String record;

    @Column(name = "update_time")
    private Date updateTime;

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
     * @return camera_id
     */
    public Integer getCameraId() {
        return cameraId;
    }

    /**
     * @param cameraId
     */
    public void setCameraId(Integer cameraId) {
        this.cameraId = cameraId;
    }

    /**
     * @return camera_ipc
     */
    public String getCameraIpc() {
        return cameraIpc;
    }

    /**
     * @param cameraIpc
     */
    public void setCameraIpc(String cameraIpc) {
        this.cameraIpc = cameraIpc;
    }

    /**
     * 获取ffmpeg推流任务Name
     *
     * @return tasker - ffmpeg推流任务Name
     */
    public String getTasker() {
        return tasker;
    }

    /**
     * 设置ffmpeg推流任务Name
     *
     * @param tasker ffmpeg推流任务Name
     */
    public void setTasker(String tasker) {
        this.tasker = tasker;
    }

    /**
     * 获取0-SDK 1-GB28182
     *
     * @return flow_type - 0-SDK 1-GB28182
     */
    public Integer getFlowType() {
        return flowType;
    }

    /**
     * 设置0-SDK 1-GB28182
     *
     * @param flowType 0-SDK 1-GB28182
     */
    public void setFlowType(Integer flowType) {
        this.flowType = flowType;
    }

    /**
     * @return rtsp
     */
    public String getRtsp() {
        return rtsp;
    }

    /**
     * @param rtsp
     */
    public void setRtsp(String rtsp) {
        this.rtsp = rtsp;
    }

    /**
     * @return rtmp
     */
    public String getRtmp() {
        return rtmp;
    }

    /**
     * @param rtmp
     */
    public void setRtmp(String rtmp) {
        this.rtmp = rtmp;
    }

    /**
     * @return flv
     */
    public String getFlv() {
        return flv;
    }

    /**
     * @param flv
     */
    public void setFlv(String flv) {
        this.flv = flv;
    }

    /**
     * @return ws_flv
     */
    public String getWsFlv() {
        return wsFlv;
    }

    /**
     * @param wsFlv
     */
    public void setWsFlv(String wsFlv) {
        this.wsFlv = wsFlv;
    }

    /**
     * @return hls
     */
    public String getHls() {
        return hls;
    }

    /**
     * @param hls
     */
    public void setHls(String hls) {
        this.hls = hls;
    }

    /**
     * @return client
     */
    public Integer getClient() {
        return client;
    }

    /**
     * @param client
     */
    public void setClient(Integer client) {
        this.client = client;
    }

    /**
     * @return flow_record
     */
    public Integer getFlowRecord() {
        return flowRecord;
    }

    /**
     * @param flowRecord
     */
    public void setFlowRecord(Integer flowRecord) {
        this.flowRecord = flowRecord;
    }

    /**
     * @return record
     */
    public String getRecord() {
        return record;
    }

    /**
     * @param record
     */
    public void setRecord(String record) {
        this.record = record;
    }

    /**
     * @return update_time
     */
    public Date getUpdateTime() {
        return updateTime;
    }

    /**
     * @param updateTime
     */
    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
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