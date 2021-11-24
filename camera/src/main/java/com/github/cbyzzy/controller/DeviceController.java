package com.github.cbyzzy.controller;

import com.github.cbyzzy.model.entity.Camera;
import com.github.cbyzzy.model.view.CameraTreeVO;
import com.github.cbyzzy.entity.ResultUtils;
import com.github.cbyzzy.page.PageResult;
import com.github.cbyzzy.entity.ResultEntity;
import com.github.cbyzzy.service.DeviceService;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "相机设备")
@RestController
@RequestMapping(value = "/dev", produces = {"application/json;charset=utf-8"})
public class DeviceController {

    @Autowired
    private DeviceService deviceService;

    @ApiOperation("相机设备分页信息(by试验台ID)")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "testerId", value = "试验台id", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "pageNum", value = "页码", paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "pageSize", value = "数量", paramType = "query", dataType = "string")
    })
    @GetMapping("/findPage")
    public ResultEntity<PageResult<Camera>> findPage(@RequestParam(required = false) String testerId,
                                           @RequestParam(defaultValue = "1") Integer pageNum,
                                           @RequestParam(defaultValue = "10") Integer pageSize) {
        return ResultUtils.ok(deviceService.findPage(testerId,pageNum,pageSize));
    }

    @ApiOperation("NVR分页信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", value = "页码", paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "pageSize", value = "数量", paramType = "query", dataType = "string")
    })
    @GetMapping("/findNvrPage")
    public ResultEntity<PageResult<Camera>> findPage(@RequestParam(defaultValue = "1") Integer pageNum,
                                                     @RequestParam(defaultValue = "10") Integer pageSize) {
        return ResultUtils.ok(deviceService.findNvrPage(pageNum,pageSize));
    }

    @ApiOperation("NVR列表")
    @GetMapping("/findNvr")
    public ResultEntity<List<Camera>> findPage() {
        return ResultUtils.ok(deviceService.findNvrList());
    }

    @ApiOperation("查询相机设备")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "cameraId", value = "相机设备Id", paramType = "query", dataType = "Integer"),
    })
    @GetMapping("/findCamera")
    public ResultEntity<Camera> findCamera(Integer cameraId) {
        return ResultUtils.ok(deviceService.findOne(cameraId));
    }

    @ApiOperation("更新相机设备信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "camera", value = "相机设备信息", paramType = "body", dataType = "Camera"),
    })
    @PostMapping("/updateCamera")
    public ResultEntity updateCamera(@RequestBody Camera camera) {
        deviceService.updateCamera(camera);
        return ResultUtils.ok();
    }

    @ApiOperation("删除相机设备信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "cameraId", value = "相机设备Id", paramType = "query", dataType = "Integer"),
    })
    @GetMapping("/deleteCamera")
    public ResultEntity deleteCamera(Integer cameraId) {
        deviceService.delete(cameraId);
        return ResultUtils.ok();
    }

    @ApiOperation("相机设备树")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "testerList", value = "有权限试验室id号", paramType = "body", dataType = "List<String>"),
    })
    @PostMapping("/getCameraTree")
    public ResultEntity<List<CameraTreeVO>> getCameraTree(@RequestBody List<String> testerList) {
        return ResultUtils.ok(deviceService.getCameraTree(testerList));
    }

}
