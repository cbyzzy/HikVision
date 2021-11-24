package com.github.cbyzzy.service;

import com.github.cbyzzy.model.entity.Camera;
import com.github.cbyzzy.model.entity.CameraFile;
import com.github.cbyzzy.model.entity.CameraFlow;
import com.github.cbyzzy.criteria.HTCriteria;
import com.github.cbyzzy.service.BaseService;
import com.github.cbyzzy.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

@Service
@Transactional
public class FlowService extends BaseService<CameraFlow> {

    @Value("${app.filepath}")
    private String rootPath;

    @Autowired
    private DeviceService deviceService;

    @Autowired
    private CameraFileService cameraFileService;

    @Autowired
    private FFmpegService ffmpegService;

    public CameraFlow findByRtmpUrl(String url) {
        HTCriteria<CameraFlow> criteria = HTCriteria.getInstance(CameraFlow.class);
        if (StringUtils.isNotBlank(url)) {
            criteria.andEqualTo(CameraFlow::getRtmp, url);
            List<CameraFlow> list = find(criteria);
            if (list.size() > 0) {
                return list.get(0);
            }
        }
        return null;
    }

    public void stopByTasker(String tasker) {
        if (StringUtils.isNotBlank(tasker)) {
            HTCriteria<CameraFlow> criteria = HTCriteria.getInstance(CameraFlow.class);
            criteria.andEqualTo(CameraFlow::getTasker,tasker);
            CameraFlow cameraFlow = find(criteria).get(0);
            if (cameraFlow.getClient() > 1) {
                cameraFlow.setClient(cameraFlow.getClient() - 1);
            } else {
                stopRecord(tasker);
                ffmpegService.stopTranscoding(tasker);
                delete(cameraFlow.getId());
            }
        }
    }

    public void startRecord(String tasker) {
        if (StringUtils.isNotBlank(tasker)) {
            HTCriteria<CameraFlow> criteria = HTCriteria.getInstance(CameraFlow.class);
            criteria.andEqualTo(CameraFlow::getTasker,tasker);
            List<CameraFlow> list = find(criteria);
            if (list.size() > 0) {
                CameraFlow cameraFlow = list.get(0);
                if (cameraFlow.getFlowRecord() != 1) {
                    Camera camera = deviceService.findOne(cameraFlow.getCameraId());
                    String name = DateTimeFormatter.ofPattern("yyyyMMddHHmmss").format(LocalDateTime.now()) + ".flv";
                    String path = "/record/" + cameraFlow.getCameraIpc().replace(".","")  + "/";
                    cameraFileService.pathCreator(rootPath + path);
                    if (ffmpegService.startRecord(cameraFlow.getTasker(),cameraFlow.getRtmp(),rootPath + path + name) != null) {
                        cameraFileService.saveFileInfo(name,path, 0L);
                        cameraFlow.setFlowRecord(1);
                        cameraFlow.setRecord(name);
                        update(cameraFlow);
                    }
                }
            }
        }
    }

    public void stopRecord(String tasker) {
        if (StringUtils.isNotBlank(tasker)) {
            String recordTasker = "recordTo" + tasker;
            ffmpegService.stopTranscoding(recordTasker);
            //更新文件大小和流状态
            HTCriteria<CameraFlow> criteria = HTCriteria.getInstance(CameraFlow.class);
            criteria.andEqualTo(CameraFlow::getTasker,tasker);
            List<CameraFlow> list = find(criteria);
            if (list.size() > 0) {
                CameraFlow cameraFlow = list.get(0);
                String name = cameraFlow.getRecord();
                String path = "/record/" + cameraFlow.getCameraIpc().replace(".","")  + "/";
                File file = new File(rootPath + path + name);
                if (file.exists() && file.isFile()) {
                    //文件记录查找
                    HTCriteria<CameraFile> criteria2 = HTCriteria.getInstance(CameraFile.class);
                    criteria2.andEqualTo(CameraFile::getFileName,name);
                    criteria2.andEqualTo(CameraFile::getFilePath,path);
                    List<CameraFile> fileList = cameraFileService.find(criteria2);
                    if (fileList.size() > 0) {
                        CameraFile cameraFile = fileList.get(0);
                        cameraFile.setFileSize(file.length());
                        cameraFileService.update(cameraFile);
                    }
                }
                cameraFlow.setFlowRecord(0);
                update(cameraFlow);
            }
        }
    }

    public void notification(String tasker) {
        if (StringUtils.isNotBlank(tasker)) {
            HTCriteria<CameraFlow> criteria = HTCriteria.getInstance(CameraFlow.class);
            criteria.andEqualTo(CameraFlow::getTasker,tasker);
            List<CameraFlow> list = find(criteria);
            if (list.size() > 0) {
                CameraFlow cameraFlow = list.get(0);
                cameraFlow.setUpdateTime(new Date());
                update(cameraFlow);
            }
        }
    }

    /**
     * 每10分钟检查一次流
     */
    @Scheduled(cron = "0 */10 * * * ?")
    public void checkNetwork() {
        List<CameraFlow> list = find();
        for (CameraFlow flow : list) {
            if (flow.getUpdateTime() != null) {
                long update = flow.getUpdateTime().getTime();
                long now = new Date().getTime();
                int millisecond = 10 * 60 * 1000;//10分钟毫秒
                if (update + millisecond < now) {
                    //超过10分钟流未更新,关闭推流
                    stopRecord(flow.getTasker());
                    ffmpegService.stopTranscoding(flow.getTasker());
                    delete(flow.getId());
                }
            }
        }
    }

}
