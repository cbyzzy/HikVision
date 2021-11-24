package com.github.cbyzzy.controller;

import com.github.cbyzzy.entity.ResultEntity;
import com.github.cbyzzy.entity.ResultUtils;
import com.github.cbyzzy.service.FlowService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = "视频流")
@RestController
@RequestMapping(value = "/flow", produces = {"application/json;charset=utf-8"})
public class FlowController {

    @Autowired
    private FlowService flowService;

    /**
     * 停止并删除推流
     * @param tasker 推流名
     */
    @ApiOperation("关闭进程")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "tasker", value = "推流名", paramType = "query", dataType = "String")
    })
    @GetMapping("/stopByTasker")
    public ResultEntity stopByTasker(String tasker){
        flowService.stopByTasker(tasker);
        return  ResultUtils.ok();
    }

    /**
     * 开始录制
     * @param tasker 推流名
     */
    @ApiOperation("开始录制")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "tasker", value = "推流名", paramType = "query", dataType = "String")
    })
    @GetMapping("/startRecord")
    public ResultEntity startRecord(String tasker){
        flowService.startRecord(tasker);
        return  ResultUtils.ok();
    }

    /**
     * 停止录制
     * @param tasker 推流名
     */
    @ApiOperation("停止录制")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "tasker", value = "推流名", paramType = "query", dataType = "String")
    })
    @GetMapping("/stopRecord")
    public ResultEntity stopRecord(String tasker){
        flowService.stopRecord(tasker);
        return  ResultUtils.ok();
    }

    /**
     * 流状态更新
     * @param tasker 推流名
     */
    @ApiOperation("流状态更新")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "tasker", value = "推流名", paramType = "query", dataType = "String")
    })
    @GetMapping("/notification")
    public ResultEntity notification(String tasker){
        flowService.notification(tasker);
        return  ResultUtils.ok();
    }

}
