package com.github.cbyzzy.hik;

public enum HCPlayControlEnum {
    ZOOM_IN(HCNetSDK.ZOOM_IN, "焦距变大"),
    ZOOM_OUT(HCNetSDK.ZOOM_OUT, "焦距变小"),
    FOCUS_NEAR(HCNetSDK.FOCUS_NEAR, "焦点前调"),
    FOCUS_FAR(HCNetSDK.FOCUS_FAR, "焦点后调"),
    IRIS_OPEN(HCNetSDK.IRIS_OPEN, "光圈扩大"),
    IRIS_CLOSE(HCNetSDK.IRIS_CLOSE, "光圈缩小"),
    TILT_UP(HCNetSDK.TILT_UP, "云台上仰"),
    TILT_DOWN(HCNetSDK.TILT_DOWN, "云台下俯"),
    PAN_LEFT(HCNetSDK.PAN_LEFT, "云台左转"),
    PAN_RIGHT(HCNetSDK.PAN_RIGHT, "云台右转"),
    UP_LEFT(HCNetSDK.UP_LEFT, "云台上仰和左转"),
    UP_RIGHT(HCNetSDK.UP_RIGHT, "云台上仰和右转"),
    DOWN_LEFT(HCNetSDK.DOWN_LEFT, "云台下俯和左转"),
    DOWN_RIGHT(HCNetSDK.DOWN_RIGHT, "云台下俯和右转");

    private Integer code;
    private String msg;

    private  Integer value;

    private HCPlayControlEnum(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
