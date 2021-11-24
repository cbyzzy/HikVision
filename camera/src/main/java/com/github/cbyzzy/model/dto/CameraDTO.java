package com.github.cbyzzy.model.dto;

import com.github.cbyzzy.hik.HCNetTools;
import com.github.cbyzzy.model.entity.Camera;

import java.util.List;

public class CameraDTO extends Camera {

    private List<String> channelList;

    private HCNetTools hcTool;

    private HistoryDTO historyDTO;

    private PlayControlDTO playControlDTO;

    //需要录像机查询的摄像头ip地址
    private String ipcAddress;

    public List<String> getChannelList() { return channelList; }
    public void setChannelList(List<String> channelList) { this.channelList = channelList; }

    public HCNetTools getHcTool() { return hcTool; }
    public void setHcTool(HCNetTools hcTool) { this.hcTool = hcTool; }

    public HistoryDTO getHistoryDTO() { return historyDTO; }
    public void setHistoryDTO(HistoryDTO historyDTO) { this.historyDTO = historyDTO; }

    public PlayControlDTO getPlayControlDTO() { return playControlDTO; }
    public void setPlayControlDTO(PlayControlDTO playControlDTO) { this.playControlDTO = playControlDTO; }

    public String getIpcAddress() { return ipcAddress; }
    public void setIpcAddress(String ipcAddress) { this.ipcAddress = ipcAddress; }

}
