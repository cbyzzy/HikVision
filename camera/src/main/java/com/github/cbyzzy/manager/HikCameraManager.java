package com.github.cbyzzy.manager;

import com.github.cbyzzy.hik.HCNetTools;
import com.github.cbyzzy.model.dto.CameraDTO;
import com.github.cbyzzy.service.CameraFileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;

@Service
public class HikCameraManager {

    private final Logger logger = LoggerFactory.getLogger(HikCameraManager.class);

    @Autowired
    private CameraFileService cameraFileService;

    @Value("${app.filepath}")
    private String rootPath;

    @Async
    public void downloadVideoAsync(CameraDTO cameraDTO, String path, String name, int channel) {
        String filePath = rootPath + path + name;

        HCNetTools hcTool = cameraDTO.getHcTool();
        LocalDateTime startTime = cameraDTO.getHistoryDTO().getStartTime();
        LocalDateTime endTime = cameraDTO.getHistoryDTO().getEndTime();

        try {
            boolean success = hcTool.downloadBack(startTime,endTime,filePath,channel);
            if (success) {
                long size = new File(filePath).length();
                if (size > 0) {
                    cameraFileService.saveFileInfo(name,path,size);
                } else {
                    Files.delete(Paths.get(filePath));
                    logger.info("删除文件:" + filePath);
                }
            } else {
                logger.info("下载失败");
            }
        } catch (InterruptedException ex) {
            logger.error("线程异常:", ex);
        } catch (IOException ex2) {
            logger.error("文件删除异常", ex2);
        } finally {
            hcTool.deviceLogout();
        }
    }

    @Async
    public void playControlAsync(CameraDTO cameraDTO, int channel) {
        HCNetTools hcTool = cameraDTO.getHcTool();
        int mills = cameraDTO.getPlayControlDTO().getMill();
        String command = cameraDTO.getPlayControlDTO().getCommand();

        try {
            boolean success = hcTool.playControl(command,mills,channel);
            if (!success) {
                logger.info("控制失败");
            }
        } catch (InterruptedException ex) {
            logger.error("线程异常:", ex);
        } finally {
            hcTool.deviceLogout();
        }
    }

}
