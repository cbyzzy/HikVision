package com.github.cbyzzy.model.view;

import com.github.cbyzzy.model.entity.Camera;

import java.util.List;

public class CameraTreeVO {

    private Camera nvr;
    private List<Camera> cameraList;

    public Camera getNvr() {
        return nvr;
    }
    public void setNvr(Camera nvr) {
        this.nvr = nvr;
    }

    public List<Camera> getCameraList() {
        return cameraList;
    }
    public void setCameraList(List<Camera> cameraList) {
        this.cameraList = cameraList;
    }
}
