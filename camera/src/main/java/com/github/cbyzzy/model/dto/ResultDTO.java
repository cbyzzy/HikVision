package com.github.cbyzzy.model.dto;

public class ResultDTO {

    private String tasker;
    private Object msg;
    private String rtmpUrl;
    private String flvUrl;

    public ResultDTO() {}

    public ResultDTO(String tasker,String msg,String rtmpUrl) {
        this.tasker = tasker;
        this.msg = msg;
        this.rtmpUrl = rtmpUrl;
    }

    public String getTasker() {
        return tasker;
    }

    public void setTasker(String tasker) {
        this.tasker = tasker;
    }

    public Object getMsg() {
        return msg;
    }

    public void setMsg(Object msg) {
        this.msg = msg;
    }

    public String getRtmpUrl() {
        return rtmpUrl;
    }

    public void setRtmpUrl(String rtmpUrl) {
        this.rtmpUrl = rtmpUrl;
    }

    public String getFlvUrl() { return flvUrl; }

    public void setFlvUrl(String flvUrl) { this.flvUrl = flvUrl; }
}
