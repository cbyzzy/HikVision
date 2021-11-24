package com.github.cbyzzy.common;

import org.apache.commons.lang3.StringUtils;

import java.util.List;

public class HikCameraUtils {

    /**
     * 设备注册信息
     */
    public static String[] resultNames = {"成功","初始化失败","注册前请关闭预览","注册失败","通道获取失败"};

    /**
     * 获取通道号
     */
    public static int analyzeChannel(String devices) {
        int iChannelNum = -1;

        if (StringUtils.isNotEmpty(devices)) {
            if (devices.contains("Camera")) {//Camara开头表示模拟通道
                //子字符串中获取通道号
                iChannelNum = Integer.parseInt(devices.substring(6));
            } else if (devices.contains("IP")) {
                //IP开头表示IP通道
                iChannelNum = Integer.parseInt(devices.substring(2));
            }
        }
        return iChannelNum;
    }

    public static int getIpcChannel(List<String> channelList,String ipcAddress) {
        int iChannelNum = -1;
        for(String channel:channelList) {
            if (channel.split(",")[1].equals(ipcAddress)) {
                iChannelNum = Integer.parseInt(channel.split(",")[0].substring(2));
                break;
            }
        }
        return iChannelNum;
    }

}
