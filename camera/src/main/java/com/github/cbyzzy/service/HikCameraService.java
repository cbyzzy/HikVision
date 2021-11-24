package com.github.cbyzzy.service;

import com.github.cbyzzy.common.FlowUtils;
import com.github.cbyzzy.common.HikCameraUtils;
import com.github.cbyzzy.hik.HCNetTools;
import com.github.cbyzzy.model.dto.CameraDTO;
import com.github.cbyzzy.model.dto.ResultDTO;
import com.github.cbyzzy.model.entity.CameraFile;
import com.github.cbyzzy.model.entity.CameraFlow;
import com.github.cbyzzy.model.entity.Camera;
import com.github.cbyzzy.manager.HikCameraManager;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

@Service
public class HikCameraService {

    private final Logger logger = LoggerFactory.getLogger(HikCameraService.class);

    @Autowired
    private DeviceService deviceService;

    @Autowired
    private FlowService flowService;

    @Autowired
    private FFmpegService ffmpegService;

    @Autowired
    private CameraFileService cameraFileService;

    @Autowired
    private HikCameraManager hikCameraManager;

    @Value("${app.filepath}")
    private String rootPath;

    @Value("${rtmp.rtmphost}")
    private String rtmpServer;

    @Value("${rtmp.httphost}")
    private String httpServer;

    public ResultDTO getChannel(CameraDTO cameraDTO) {
        ResultDTO resultDTO = new ResultDTO();
        StringBuilder str = new StringBuilder();
        for (int i = 0;i < cameraDTO.getChannelList().size();i++) {
            if (i == cameraDTO.getChannelList().size() - 1) {
                str.append(cameraDTO.getChannelList().get(i));
            } else {
                str.append(cameraDTO.getChannelList().get(i)).append("&");
            }
        }
        resultDTO.setMsg(str.toString());
        return resultDTO;
    }

    /**
     * 开始推流(rtsp->rtmp)
     * @param cameraDTO 相机信息
     * @return ResultDTO
     */
    public ResultDTO startTranscode(CameraDTO cameraDTO) {
        ResultDTO resultDTO = new ResultDTO();

        if (cameraDTO != null) {
            String appName;
            int channelNumber;
            if (cameraDTO.getType() == 0) {
                //摄像机
                appName = cameraDTO.getAccount() + cameraDTO.getIp().replace(".","");
                channelNumber = HikCameraUtils.analyzeChannel(cameraDTO.getChannelList().get(0));
            } else {
                //NVR
                appName = cameraDTO.getAccount() + cameraDTO.getIp().replace(".","") + "to" + cameraDTO.getIpcAddress().replace(".","");
                channelNumber = HikCameraUtils.getIpcChannel(cameraDTO.getChannelList(),cameraDTO.getIpcAddress());
            }
            if (channelNumber == -1) {
                logger.info("实时流-未搜索到ip:" + cameraDTO.getIp() + ", ipc:" + cameraDTO.getIpcAddress() + " 的设备");
            }

            String rtmpUrl = "rtmp://"+ rtmpServer + "/live/" + appName; //rtmp链
            String flvUrl = "http://" + httpServer + "/live/" + appName + ".flv"; //http-flv链

            boolean runFlag = ffmpegService.taskerIsRun(appName);
            CameraFlow cameraFlow = flowService.findByRtmpUrl(rtmpUrl);
            if (cameraFlow != null && !runFlag) {
                flowService.delete(cameraFlow.getId());
            }

            if (runFlag) {
                //已推流
                if (cameraFlow != null) {
                    resultDTO.setTasker(appName);
                    resultDTO.setMsg(HikCameraUtils.resultNames[0]);
                    resultDTO.setRtmpUrl(rtmpUrl);
                    resultDTO.setFlvUrl(flvUrl);
                    cameraFlow.setClient(cameraFlow.getClient() + 1);
                    flowService.update(cameraFlow);
                } else {
                    //停止推流
                    flowService.stopByTasker(appName);
                    resultDTO.setMsg("推流失败");
                    return resultDTO;
                }
            } else {
                //未推流
                //String channelNumberStr = "ch"+channelNumber; //旧通道(2012年之前设备)
                String channelNumberStr = channelNumber + "02"; //新通道(2012年之后设备,02代表子码流)

                //String rtspName = cameraDTO.getAccount() + ":" + cameraDTO.getPassword() + "@" + cameraDTO.getIp() + ":554/Streaming/Channels/" + channelNumberStr + "/sub/av_stream"); //旧码流(子码流)
                String rtspName = cameraDTO.getAccount() + ":" + cameraDTO.getPassword() + "@" + cameraDTO.getIp() + ":554/Streaming/Channels/" + channelNumberStr + "?transportmode=unicast"; //新码流
                String rtmpName = rtmpServer + "/live/"; //rtmp服务器推流入口

                String tasker= ffmpegService.startTranscoding(appName,rtspName,rtmpName);
                if (StringUtils.isNotEmpty(tasker)) {
                    resultDTO.setTasker(tasker);
                    resultDTO.setMsg(HikCameraUtils.resultNames[0]);
                    resultDTO.setRtmpUrl(rtmpUrl);
                    resultDTO.setFlvUrl(flvUrl);

                    //添加流记录
                    cameraFlow = new CameraFlow();
                    cameraFlow.setCameraId(cameraDTO.getId());
                    if (cameraDTO.getType() == 0) {
                        cameraFlow.setCameraIpc(cameraDTO.getIp());
                    } else {
                        cameraFlow.setCameraIpc(cameraDTO.getIpcAddress());
                    }
                    cameraFlow.setTasker(tasker);
                    cameraFlow.setFlowType(FlowUtils.FlowType.HIK.ordinal());
                    cameraFlow.setRtsp("rtsp://" + rtspName);
                    cameraFlow.setRtmp(rtmpUrl);
                    cameraFlow.setFlv(flvUrl);
                    cameraFlow.setClient(1);
                    cameraFlow.setCreateTime(new Date());
                    cameraFlow.setUpdateTime(new Date());
                    flowService.save(cameraFlow);
                } else {
                    resultDTO.setMsg("推流失败");
                    return resultDTO;
                }
            }
            return resultDTO;
        } else {
            resultDTO.setMsg("未找到设备");
            return resultDTO;
        }
    }

    /**
     * 回放推流
     * @param cameraDTO 相机信息
     * @return ResultDTO
     */
    public ResultDTO startBackTranscode(CameraDTO cameraDTO) {
        ResultDTO resultDTO = new ResultDTO();

        if (cameraDTO != null) {

            String startTime,endTime;

            DateTimeFormatter sdf = DateTimeFormatter.ofPattern("yyyyMMdd't'HHmmss'z'");
            if (cameraDTO.getHistoryDTO().getEndTime() != null) {
                startTime = sdf.format(cameraDTO.getHistoryDTO().getStartTime());
                endTime = sdf.format(cameraDTO.getHistoryDTO().getEndTime());
            } else {
                startTime = sdf.format(cameraDTO.getHistoryDTO().getStartTime());
                endTime = null;
            }

            String appName;
            int channelNumber;
            if (cameraDTO.getType() == 0) {
                //摄像机
                appName = "history" + cameraDTO.getAccount() + cameraDTO.getIp().replace(".", "") + "start" + startTime + "end" + endTime;
                channelNumber = HikCameraUtils.analyzeChannel(cameraDTO.getChannelList().get(0));
            } else {
                //NVR
                appName = "history" + cameraDTO.getAccount() + cameraDTO.getIp().replace(".", "") + "to" + cameraDTO.getIpcAddress().replace(".", "") + "start" + startTime + "end" + endTime;
                channelNumber = HikCameraUtils.getIpcChannel(cameraDTO.getChannelList(), cameraDTO.getIpcAddress());
            }

            if (channelNumber == -1) {
                logger.info("历史流-未搜索到ip:" + cameraDTO.getIp() + ", ipc:" + cameraDTO.getIpcAddress() + " 的设备");
            }
            String rtmpUrl = "rtmp://"+ rtmpServer + "/live/" + appName; //rtmp链
            String flvUrl = "http://" + httpServer + "/live/" + appName + ".flv"; //http-flv链

            boolean runFlag = ffmpegService.taskerIsRun(appName);
            CameraFlow cameraFlow = flowService.findByRtmpUrl(rtmpUrl);
            if (cameraFlow != null && !runFlag) {
                flowService.delete(cameraFlow.getId());
            }

            if (runFlag) {
                //已推流
                if (cameraFlow != null) {
                    resultDTO.setTasker(appName);
                    resultDTO.setMsg(HikCameraUtils.resultNames[0]);
                    resultDTO.setRtmpUrl(rtmpUrl);
                    resultDTO.setFlvUrl(flvUrl);
                    cameraFlow.setClient(cameraFlow.getClient() + 1);
                    flowService.update(cameraFlow);
                } else {
                    //停止推流
                    flowService.stopByTasker(appName);
                    resultDTO.setMsg("推流失败");
                    return resultDTO;
                }
            } else {
                //未推流
                String channelNumberStr = channelNumber + "01"; //回放流只有主码流
                String rtspName;
                if (endTime != null) {
                    rtspName = cameraDTO.getAccount() + ":" + cameraDTO.getPassword() + "@" + cameraDTO.getIp() + ":554/Streaming/tracks/" + channelNumberStr + "?starttime=" + startTime + "&endtime=" + endTime;
                } else {
                    rtspName = cameraDTO.getAccount() + ":" + cameraDTO.getPassword() + "@" + cameraDTO.getIp() + ":554/Streaming/tracks/" + channelNumberStr + "?starttime=" + startTime;
                }
                String rtmpName = rtmpServer + "/live/"; //rtmp服务器推流入口

                String tasker= ffmpegService.startTranscoding(appName,rtspName,rtmpName);
                if (StringUtils.isNotEmpty(tasker)) {
                    resultDTO.setTasker(tasker);
                    resultDTO.setMsg(HikCameraUtils.resultNames[0]);
                    resultDTO.setRtmpUrl(rtmpUrl);
                    resultDTO.setFlvUrl(flvUrl);

                    //添加流记录
                    cameraFlow = new CameraFlow();
                    cameraFlow.setCameraId(cameraDTO.getId());
                    if (cameraDTO.getType() == 0) {
                        cameraFlow.setCameraIpc(cameraDTO.getIp());
                    } else {
                        cameraFlow.setCameraIpc(cameraDTO.getIpcAddress());
                    }
                    cameraFlow.setTasker(tasker);
                    cameraFlow.setFlowType(FlowUtils.FlowType.HIK.ordinal());
                    cameraFlow.setRtsp("rtsp://" + rtspName);
                    cameraFlow.setRtmp(rtmpUrl);
                    cameraFlow.setFlv(flvUrl);
                    cameraFlow.setClient(1);
                    cameraFlow.setCreateTime(new Date());
                    cameraFlow.setUpdateTime(new Date());
                    flowService.save(cameraFlow);
                } else {
                    resultDTO.setMsg("推流失败");
                    return resultDTO;
                }
            }
            return resultDTO;
        } else {
            resultDTO.setMsg("未找到设备");
            return resultDTO;
        }
    }

    /**
     * 抓图
    * @param cameraDTO 相机信息
     */
    public ResultDTO catchPic(CameraDTO cameraDTO) {
        ResultDTO resultDTO = new ResultDTO();

        if (cameraDTO != null) {

            String pathName;
            int channelNumber;
            if (cameraDTO.getType() == 0) {
                //摄像机
                pathName = cameraDTO.getIp().replace(".", "");
                channelNumber = HikCameraUtils.analyzeChannel(cameraDTO.getChannelList().get(0));
            } else {
                //NVR
                resultDTO.setMsg("NVR设备不支持抓图");
                return resultDTO;
            }

            HCNetTools hcTool = cameraDTO.getHcTool();
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
            String name = dtf.format(LocalDateTime.now()) + ".jpg";
            String path = "/picture/" + pathName + "/";
            String filePath = rootPath + path + name;
            cameraFileService.pathCreator(rootPath + path);
            boolean success = hcTool.getDVRPic(channelNumber,filePath);

            if (success) {
                long size = new File(filePath).length();
                CameraFile cameraFile = cameraFileService.saveFileInfo(name,path,size);
                resultDTO.setMsg(cameraFile.getId().toString());
            }
            return resultDTO;
        } else {
            resultDTO.setMsg("未找到设备");
            return resultDTO;
        }
    }

    /**
     * 视频回放下载
     * @param cameraDTO
     */
    public ResultDTO downloadBack(CameraDTO cameraDTO) {
        ResultDTO resultDTO = new ResultDTO();

        if (cameraDTO != null) {

            String pathName;
            int channelNumber;
            if (cameraDTO.getType() == 0) {
                //摄像机
                pathName = cameraDTO.getIp().replace(".", "");
                channelNumber = HikCameraUtils.analyzeChannel(cameraDTO.getChannelList().get(0));
            } else {
                //NVR
                pathName = cameraDTO.getIpcAddress().replace(".", "");
                channelNumber = HikCameraUtils.getIpcChannel(cameraDTO.getChannelList(), cameraDTO.getIpcAddress());
                channelNumber = channelNumber + 32; //远程下载的通道号从32开始
            }

            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
            String name = dtf.format(cameraDTO.getHistoryDTO().getStartTime()) + "TO" + dtf.format(cameraDTO.getHistoryDTO().getEndTime()) + ".mp4";
            String path = "/video/" + pathName + "/";
            cameraFileService.pathCreator(rootPath + path);
            Integer fileId = cameraFileService.findByPathAndName(path,name);
            if (fileId > 0) {
                resultDTO.setMsg(fileId.toString());
                return resultDTO;
            }
            hikCameraManager.downloadVideoAsync(cameraDTO,path,name,channelNumber);
            resultDTO.setMsg("Async");
            return resultDTO;
        } else {
            resultDTO.setMsg("未找到设备");
            return resultDTO;
        }
    }

    /**
     * 获取视频列表
     * @param cameraDTO
     * @return
     */
    public ResultDTO getVideoFileList(CameraDTO cameraDTO) {
        ResultDTO resultDTO = new ResultDTO();

        if (cameraDTO != null) {
            int channelNumber;
            if (cameraDTO.getType() == 0) {
                //摄像机
                channelNumber = HikCameraUtils.analyzeChannel(cameraDTO.getChannelList().get(0));
            } else {
                //NVR
                channelNumber = HikCameraUtils.getIpcChannel(cameraDTO.getChannelList(), cameraDTO.getIpcAddress());
                channelNumber = channelNumber + 32; //远程录像的通道号从33开始
            }
            HCNetTools hcTool = cameraDTO.getHcTool();
            List<HashMap<String,String>> map = hcTool.getVideoFileList(cameraDTO.getHistoryDTO().getStartTime(),cameraDTO.getHistoryDTO().getEndTime(),channelNumber);
            resultDTO.setMsg(map);
            return resultDTO;
        } else {
            resultDTO.setMsg("未找到设备");
            return resultDTO;
        }
    }

    /**
     * 云台控制
     * @param cameraDTO 相机信息
     */
    public ResultDTO playControl(CameraDTO cameraDTO) {
        ResultDTO resultDTO = new ResultDTO();

        if (cameraDTO != null) {

            int channelNumber;
            if (cameraDTO.getType() == 0) {
                //摄像机
                channelNumber = HikCameraUtils.analyzeChannel(cameraDTO.getChannelList().get(0));
            } else {
                //NVR
                resultDTO.setMsg("NVR设备不支持云台控制");
                return resultDTO;
            }
            hikCameraManager.playControlAsync(cameraDTO,channelNumber);
            resultDTO.setMsg("Async");
            return resultDTO;
        } else {
            resultDTO.setMsg("未找到设备");
            return resultDTO;
        }
    }

    /**
     * 停止推流
     * @param id 相机id
     */
    public Boolean stopRtmp(Integer id) {
        if (id == null) {
            return true;
        }

        Camera camera = deviceService.findOne(id);

        if (camera != null) {
            String appName = camera.getAccount() + camera.getIp().replace(".","");
            return ffmpegService.stopTranscoding(appName);
        } else {
            return true;
        }
    }

}
