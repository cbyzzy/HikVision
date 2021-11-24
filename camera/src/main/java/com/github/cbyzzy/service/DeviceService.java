package com.github.cbyzzy.service;

import com.github.cbyzzy.model.entity.Camera;
import com.github.cbyzzy.model.view.CameraTreeVO;
import com.github.cbyzzy.criteria.HTCriteria;
import com.github.cbyzzy.page.PageResult;
import com.github.cbyzzy.service.BaseService;
import com.github.cbyzzy.utils.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
public class DeviceService extends BaseService<Camera> {

    public PageResult<Camera> findPage(String testerId, Integer pageNum, Integer pageSize) {
        HTCriteria<Camera> criteria = HTCriteria.getInstance(Camera.class);
        criteria.andEqualTo(Camera::getType,0);
        if (StringUtils.isNotBlank((testerId))) {
            criteria.andEqualTo(Camera::getTesterId,testerId);
        }
        return findPage(criteria,pageNum,pageSize);
    }

    public PageResult<Camera> findNvrPage(Integer pageNum, Integer pageSize) {
        HTCriteria<Camera> criteria = HTCriteria.getInstance(Camera.class);
        criteria.andEqualTo(Camera::getType,1);
        return findPage(criteria,pageNum,pageSize);
    }

    public List<Camera> findNvrList() {
        HTCriteria<Camera> criteria = HTCriteria.getInstance(Camera.class);
        criteria.andEqualTo(Camera::getType,1);
        return find(criteria);
    }

    public void updateCamera(Camera camera) {
        if (camera.getId() != null) {
            //更新相机设备信息
            update(camera);
        } else {
            //添加相机设备信息
            camera.setCreateTime(new Date());
            save(camera);
        }
    }

    public List<CameraTreeVO> getCameraTree(List<String> testerList) {

        List<CameraTreeVO> treeList = new ArrayList<>();
        List<Camera> cameraList;

        if (testerList.size() > 0) {
            HTCriteria<Camera> criteria = HTCriteria.getInstance(Camera.class);
            criteria.andIn(Camera::getTesterId,testerList);
            criteria.orEqualTo(Camera::getType,"1");
            cameraList = find(criteria);
        } else {
            cameraList = find();
        }

        Map<Integer,List<Camera>> cameraMap = cameraList.stream().collect(Collectors.groupingBy(Camera::getBelongNvrId));
        //nvr或无nvr管理的摄像机
        List<Camera> nvrList = cameraMap.get(0);
        //生成树
        for (Camera nvr : nvrList) {
            CameraTreeVO cameraTreeVO = new CameraTreeVO();
            cameraTreeVO.setNvr(nvr);
            cameraTreeVO.setCameraList(cameraMap.get(nvr.getId()));
            treeList.add(cameraTreeVO);
        }

        return treeList;
    }

}
