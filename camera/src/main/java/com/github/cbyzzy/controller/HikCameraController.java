package com.github.cbyzzy.controller;

import com.github.cbyzzy.model.dto.CameraDTO;
import com.github.cbyzzy.model.dto.HistoryDTO;
import com.github.cbyzzy.model.dto.PlayControlDTO;
import com.github.cbyzzy.model.dto.ResultDTO;
import com.github.cbyzzy.entity.ResultEntity;
import com.github.cbyzzy.entity.ResultUtils;
import com.github.cbyzzy.utils.LocalDateUtils;
import com.github.cbyzzy.utils.StringUtils;
import com.github.cbyzzy.service.HikCameraService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@Api(tags = "海康相机控制")
@RestController
@RequestMapping(value = "/hikcamera", produces = {"application/json;charset=utf-8"})
public class HikCameraController {

	@Autowired
	private HikCameraService hikCameraService;

	@ApiOperation("获取设备通道信息")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "id", value = "设备id", paramType = "query", dataType = "Integer")
	})
	@GetMapping("/getChannel")
	public ResultEntity<ResultDTO> getChannel(Integer id) {
		CameraDTO cameraDTO = new CameraDTO();
		cameraDTO.setId(id);
		ResultDTO result = hikCameraService.getChannel(cameraDTO);
		return ResultUtils.ok(result);
	}

	@ApiOperation("开始推流")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "id", value = "设备id", paramType = "query", dataType = "Integer"),
			@ApiImplicitParam(name = "ipc", value = "nvr的ipc摄像机ip地址", paramType = "query", dataType = "String")
	})
	@GetMapping("/startTranscode")
	public ResultEntity<ResultDTO> startTranscode(Integer id, @RequestParam(required = false) String ipc) {
		CameraDTO cameraDTO = new CameraDTO();
		cameraDTO.setId(id);
		if (StringUtils.isNotBlank(ipc)) {
			cameraDTO.setIpcAddress(ipc);
		}
		ResultDTO result = hikCameraService.startTranscode(cameraDTO);
	 	return ResultUtils.ok(result);
	}

	/**
	 * 回放推流
	 * @param id 相机id
	 * @return ResultDTO
	 */
	@ApiOperation("回放推流")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "id", value = "设备id", paramType = "query", dataType = "Integer"),
			@ApiImplicitParam(name = "ipc", value = "nvr的ipc摄像机ip地址", paramType = "query", dataType = "String"),
			@ApiImplicitParam(name = "startTime", value = "起始时间", paramType = "query", dataType = "Date"),
			@ApiImplicitParam(name = "endTime", value = "结束时间", paramType = "query", dataType = "Date")
	})
	@GetMapping("/startBackTranscode")
	public ResultEntity<ResultDTO> startBackTranscode(Integer id,
													  @RequestParam(required = false) String ipc,
													  @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date startTime,
													  @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date endTime) {
		CameraDTO cameraDTO = new CameraDTO();
		cameraDTO.setId(id);
		if (StringUtils.isNotBlank(ipc)) {
			cameraDTO.setIpcAddress(ipc);
		}
		cameraDTO.setHistoryDTO(new HistoryDTO(LocalDateUtils.dateToLocalDateTime(startTime), LocalDateUtils.dateToLocalDateTime(endTime)));
		ResultDTO result = hikCameraService.startBackTranscode(cameraDTO);
		return ResultUtils.ok(result);
	}

	/**
	 * 抓图
	 * @param id 相机id
	 * @param ipc nvr的ipc摄像机ip地址
	 * @return ResultDTO
	 */
	@ApiOperation("抓图")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "id", value = "设备id", paramType = "query", dataType = "Integer"),
			@ApiImplicitParam(name = "ipc", value = "nvr的ipc摄像机ip地址", paramType = "query", dataType = "String")
	})
	@GetMapping("/catchPic")
	public ResultEntity<ResultDTO> catchPic(Integer id, String ipc){

		CameraDTO cameraDTO = new CameraDTO();
		cameraDTO.setId(id);
		if (StringUtils.isNotBlank(ipc)) {
			cameraDTO.setIpcAddress(ipc);
		}
		ResultDTO result = hikCameraService.catchPic(cameraDTO);
		return ResultUtils.ok(result);
	}

	/**
	 * 视频下载
	 * @return ResultDTO
	 */
	@ApiOperation("视频下载")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "id", value = "设备id", paramType = "query", dataType = "Integer"),
			@ApiImplicitParam(name = "ipc", value = "nvr的ipc摄像机ip地址", paramType = "query", dataType = "String"),
			@ApiImplicitParam(name = "startTime", value = "起始时间", paramType = "query", dataType = "Date"),
			@ApiImplicitParam(name = "endTime", value = "结束时间", paramType = "query", dataType = "Date")
	})
	@GetMapping("/downloadVideo")
	public ResultEntity<ResultDTO> downloadVideo(Integer id,
									  @RequestParam(required = false) String ipc,
									  @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date startTime,
									  @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date endTime) {
		CameraDTO cameraDTO = new CameraDTO();
		cameraDTO.setId(id);
		if (StringUtils.isNotBlank(ipc)) {
			cameraDTO.setIpcAddress(ipc);
		}
		cameraDTO.setHistoryDTO(new HistoryDTO(LocalDateUtils.dateToLocalDateTime(startTime), LocalDateUtils.dateToLocalDateTime(endTime)));
		ResultDTO result = hikCameraService.downloadBack(cameraDTO);
		return ResultUtils.ok(result);
	}

	/**
	 * 获取录像文件信息
	 * @return ResultDTO
	 */
	@ApiOperation("获取录像文件信息")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "id", value = "设备id", paramType = "query", dataType = "Integer"),
			@ApiImplicitParam(name = "ipc", value = "nvr的ipc摄像机ip地址", paramType = "query", dataType = "String"),
			@ApiImplicitParam(name = "startTime", value = "起始时间", paramType = "query", dataType = "Date"),
			@ApiImplicitParam(name = "endTime", value = "结束时间", paramType = "query", dataType = "Date")
	})
	@GetMapping("/getVideoFileList")
	public ResultEntity<ResultDTO> getVideoFileList(Integer id,
												   @RequestParam(required = false) String ipc,
												   @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date startTime,
												   @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date endTime) {
		CameraDTO cameraDTO = new CameraDTO();
		cameraDTO.setId(id);
		if (StringUtils.isNotBlank(ipc)) {
			cameraDTO.setIpcAddress(ipc);
		}
		cameraDTO.setHistoryDTO(new HistoryDTO(LocalDateUtils.dateToLocalDateTime(startTime), LocalDateUtils.dateToLocalDateTime(endTime)));
		ResultDTO result = hikCameraService.getVideoFileList(cameraDTO);
		return ResultUtils.ok(result);
	}

	/**
	 * 云台控制
	 * @param id 相机id
	 * @param ipc nvr的ipc摄像机ip地址
	 * @return ResultDTO
	 */
	@ApiOperation("云台控制")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "id", value = "设备id", paramType = "query", dataType = "Integer"),
			@ApiImplicitParam(name = "ipc", value = "nvr的ipc摄像机ip地址", paramType = "query", dataType = "String"),
			@ApiImplicitParam(name = "mill", value = "云台动作时间(ms)", paramType = "query", dataType = "Integer"),
			@ApiImplicitParam(name = "command", value = "云台动指令", paramType = "query", dataType = "String"),
	})
	@GetMapping("/playControl")
	public ResultEntity<ResultDTO> playControl(Integer id, String ipc,Integer mill,String command){

		CameraDTO cameraDTO = new CameraDTO();
		cameraDTO.setId(id);
		if (StringUtils.isNotBlank(ipc)) {
			cameraDTO.setIpcAddress(ipc);
		}
		if (mill != null && StringUtils.isNotBlank(command)) {
			if (mill > 0) {
				cameraDTO.setPlayControlDTO(new PlayControlDTO(mill,command));
			}
		} else {
			ResultDTO result = new ResultDTO();
			result.setMsg("错误:云台控制传入参数错误");
		}
		ResultDTO result = hikCameraService.playControl(cameraDTO);
		return ResultUtils.ok(result);
	}

	/**
	 * 关闭ffmpeg进程
	 * @param id 相机id
	 * @return Boolean
	 */
	@ApiOperation("关闭进程")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "id", value = "相机id", paramType = "query", dataType = "Integer")
	})
	@GetMapping("/stopRtmp")
	public ResultEntity<Boolean> stopRtmp(Integer id){
		Boolean result = hikCameraService.stopRtmp(id);
		return ResultUtils.ok(result);
	}

}
