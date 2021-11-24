package com.github.cbyzzy.aop;

import com.github.cbyzzy.common.HikCameraUtils;
import com.github.cbyzzy.hik.HCNetTools;
import com.github.cbyzzy.model.dto.ResultDTO;
import com.github.cbyzzy.model.entity.Camera;
import com.github.cbyzzy.model.dto.CameraDTO;
import com.github.cbyzzy.service.DeviceService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 海康设备登陆AOP切面
 */
@Aspect
@Component
public class HikCameraAspect {

    @Autowired
    private DeviceService deviceService;

    private final Logger logger = LoggerFactory.getLogger(HikCameraAspect.class);

    @Around(value = "execution(* com.github.cbyzzy.service.HikCameraService.*(..))")
    public Object aroundCamera(ProceedingJoinPoint joinPoint) throws RuntimeException {

        Object result = null;

        try {
            Object[] args = joinPoint.getArgs();
            if (args.length < 1) {
                throw new Throwable("无传入参数");
            }

            if (args[0] != null & args[0] instanceof CameraDTO) {
                CameraDTO cameraDTO = (CameraDTO) args[0];
                Camera camera = deviceService.findOne(cameraDTO.getId());

                if (camera == null) { throw new Throwable("未查询到指定设备"); }

                cameraDTO.setAccount(camera.getAccount());
                cameraDTO.setPassword(camera.getPassword());
                cameraDTO.setIp(camera.getIp());
                cameraDTO.setPort(camera.getPort());
                cameraDTO.setType(camera.getType());

                //设备登录
                int code = login(cameraDTO);
                args[0] = cameraDTO;

                switch (code) {
                    case 0:
                        //成功登录
                        result = joinPoint.proceed(args);
                        ResultDTO resultDTO = (ResultDTO)result;
                        //针对异步方法,交给异步方法去注销
                        if (!resultDTO.getMsg().equals("Async")) {
                            logout(cameraDTO);
                        }
                        break;
                    case 4:
                        //获取通道失败
                        logout(cameraDTO);
                        result = new ResultDTO("",HikCameraUtils.resultNames[code],"");
                        break;
                    default:
                        //注册失败
                        result = new ResultDTO("",HikCameraUtils.resultNames[code],"");
                }
                return result;
            } else {
                throw new Throwable("传入参数异常");
            }

        } catch (Throwable e) {
            logger.error("目标方法执行异常,目标类:" + joinPoint.getTarget() + "方法：" + joinPoint.getSignature().getName(), e);
            throw new RuntimeException("系统繁忙，请稍后再试!");
        }
    }

    public int login(CameraDTO cameraDTO) {
        cameraDTO.setHcTool(new HCNetTools());
        HCNetTools hcTool = cameraDTO.getHcTool();

        int code = 0;
        if(hcTool.initDevices() == 1) {
            code = 1;//初始化失败
            return code;
        }

        int regSuc = hcTool.deviceLogin(cameraDTO.getAccount(),cameraDTO.getPassword(),cameraDTO.getIp(),cameraDTO.getPort());
        if(regSuc != 0) {
            code = regSuc;//注册失败
            return code;
        }

        List<String> channelList;
        channelList = hcTool.getChannelNumber();
        cameraDTO.setChannelList(channelList);
        if (channelList.size() == 0) code = 4;

        return code;
    }

    public void logout(CameraDTO cameraDTO) {
        cameraDTO.getHcTool().deviceLogout();
    }

}
