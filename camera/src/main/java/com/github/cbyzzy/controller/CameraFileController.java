package com.github.cbyzzy.controller;

import com.github.cbyzzy.model.entity.CameraFile;
import com.github.cbyzzy.entity.ResultEntity;
import com.github.cbyzzy.entity.ResultUtils;
import com.github.cbyzzy.page.PageResult;
import com.github.cbyzzy.utils.HAssert;
import com.github.cbyzzy.service.CameraFileService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;

/**
 * Description:
 * Author:zhangzeyu
 */
@Api(tags = "文件上传下载")
@RestController
@RequestMapping(value = "/camerafile", produces = { "application/json;charset=utf-8" })
public class CameraFileController {

    @Autowired
    private CameraFileService cameraFileService;

    /**
     * 下载文件
     *
     * @param id
     * @param response
     */
    @ApiOperation(value = "下载文件")
    @ApiImplicitParam(name = "id", value = "内容ID", required = true, paramType = "path", dataType = "int")
    @GetMapping("/{id}")
    public void getFile(@PathVariable Integer id, HttpServletResponse response) {
        cameraFileService.downloadFile(id, response);
    }

    /**
     * 下载文件
     *
     * @param fileName
     * @param filePath
     * @param response
     */
    @ApiOperation(value = "下载指定路径文件")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "fileName", value = "下载文件显示名", required = true, paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "filePath", value = "文件路径，由接口提供", required = true, paramType = "query", dataType = "string")
    })
    @GetMapping("/getFile")
    public void getFileByPath(String fileName, String filePath, HttpServletResponse response) {
        HAssert.isBlank(filePath, "文件路径不可为空");
        HAssert.isBlank(fileName, "文件名称不可为空");
        cameraFileService.downloadFromPath(filePath, fileName, response);
    }

    /**
     * 删除文件
     *
     * @param id 文件id
     */
    @ApiOperation("删除文件")
    @ApiImplicitParam(name = "id", value = "文件ID", paramType = "path", dataType = "int")
    @DeleteMapping("/{id}")
    public ResultEntity<String> delete(@PathVariable Integer id) {
        cameraFileService.deleteFile(id);
        return ResultUtils.ok();
    }

    /**
     * 根据ip和类型查找文件
     *
     * @param ipc 网络摄像机ip地址
     * @param type 文件类型 0-所有 1-video 2-picture 3-record
     */
    @ApiOperation("查找文件")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "ipc", value = "网络摄像机ip地址", paramType = "path", dataType = "string"),
            @ApiImplicitParam(name = "type", value = "文件类型", paramType = "path", dataType = "int"),
            @ApiImplicitParam(name = "pageNum", value = "页码", paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "pageSize", value = "数量", paramType = "query", dataType = "string")
    })
    @GetMapping("/findFilePage")
    public ResultEntity<PageResult<CameraFile>> findFilePage(String ipc,
                                                             Integer type,
                                                             @RequestParam(defaultValue = "1") Integer pageNum,
                                                             @RequestParam(defaultValue = "10") Integer pageSize) {
        return ResultUtils.ok(cameraFileService.findFilePage(ipc,type,pageNum,pageSize));
    }
}
