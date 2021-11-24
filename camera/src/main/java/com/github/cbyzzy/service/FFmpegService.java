package com.github.cbyzzy.service;

import cc.eguid.FFmpegCommandManager.FFmpegManager;
import cc.eguid.FFmpegCommandManager.FFmpegManagerImpl;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class FFmpegService {

    private FFmpegManager manager;

    /**
     * 开始推流
     * @param appName 进程名称,为相机ip去"."
     * @param rtspName rtsp流
     * @param rtmpName rtmp流
     * @return String
     */
    public String startTranscoding(String appName, String rtspName, String rtmpName)  {
        if(manager == null){
            manager = new FFmpegManagerImpl();
        }
        if(taskerIsRun(appName)) return appName; //如果进程存在,则直接返回进程名
        Map<String,String> map = new HashMap<>();

        map.put("appName", appName); //进程名
        map.put("input", "rtsp://" + rtspName);
        map.put("output", "rtmp://" + rtmpName);
        map.put("codec", "h264");
        map.put("fmt", "flv");
        map.put("fps", "24");
        map.put("rs", "720x480");
        map.put("twoPart", "1");
        // 执行任务，id就是appName，如果执行失败返回为null
        String start = manager.start(appName,"ffmpeg -allowed_media_types video -rtsp_transport tcp -i rtsp://" + rtspName + " -c:v libx264 -f flv -r 24 -g 24 -s 720x480 rtmp://" + rtmpName + appName);
        //String start = com.github.cbyzzy.manager.start(map);
        return start;
    }

    /**
     * 开始记录
     * @param appName 进程名称
     * @param rtmpUrl rtmp流
     */
    public String startRecord(String appName,String rtmpUrl,String filePath) {
        return manager.start("recordTo" + appName,"ffmpeg -y -i " + rtmpUrl + " -c copy -f flv " + filePath);
    }

    /**
     * 关闭进程
     * @param tasker 进程名称
     * @return boolean
     */
    public boolean stopTranscoding(String tasker){
        if(!taskerIsRun(tasker)) return true;
        return manager.stop(tasker);
    }

    /**
     * 判断当前推流进程是否存在
     * @param appName 进程名称
     * @return 进程名称,为"0"时表示进程不存在
     */
    public boolean taskerIsRun(String appName){
        if(manager == null){
            manager = new FFmpegManagerImpl();
        }
        return (manager.queryAll().size()>0 && manager.query(appName) != null);
    }

}  