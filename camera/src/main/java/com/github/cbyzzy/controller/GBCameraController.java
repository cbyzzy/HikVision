package com.github.cbyzzy.controller;

import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = "GB28182相机控制")
@RestController
@RequestMapping(value = "/gbcamera", produces = {"application/json;charset=utf-8"})
public class GBCameraController {
}
