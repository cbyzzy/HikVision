package com.github.cbyzzy.hik;

import com.sun.jna.NativeLong;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class HCNetTools {
	private final Logger logger = LoggerFactory.getLogger(getClass());

	static HCNetSDK hCNetSDK = HCNetSDK.INSTANCE;

	//海康播放库(SDK里的PlayCtrl不是此处的PlayCtrl)
	//static PlayCtrl playControl = PlayCtrl.INSTANCE;

	private HCNetSDK.NET_DVR_WORKSTATE_V30 m_strWorkState;
	private HCNetSDK.NET_DVR_DEVICEINFO_V30 m_strDeviceInfo;//设备信息
	private HCNetSDK.NET_DVR_IPPARACFG m_strIpparaCfg;//IP参数
	private HCNetSDK.NET_DVR_CLIENTINFO m_strClientInfo;//用户参数

	private boolean bRealPlay;//是否在预览.
	private String m_sDeviceIP;//已登录设备的IP地址

	private int lUserID;//用户句柄
	private int loadHandle;//下载句柄
	private int lPreviewHandle;//预览句柄
	private IntByReference m_lPort;//回调预览时播放库端口指针
	private IntByReference m_err;//错误号

	public HCNetTools() {
		lUserID = -1;
		lPreviewHandle = -1;
		m_lPort = new IntByReference(-1);
		m_err = new IntByReference(-1);
	}

	/**
	 * 初始化资源配置
	 */
	public int initDevices() {
		boolean b = hCNetSDK.NET_DVR_Init();
		if (!b) {
			//初始化失败
			//throw new RuntimeException("初始化失败");
			System.out.println("初始化失败");
			return 1;
		}
		return 0;
	}

	/**
	 * 设备注册
	 *
	 * @param name     设备用户名
	 * @param password 设备登录密码
	 * @param ip       IP地址
	 * @param port     端口
	 * @return 结果
	 */
	public int deviceLogin(String name, String password, String ip, String port) {

		if (bRealPlay) {//判断当前是否在预览
			return 2;//"注册新用户请先停止当前预览";
		}
		if (lUserID > -1) {//先注销,在登录
			hCNetSDK.NET_DVR_Logout_V30(lUserID);
			lUserID = -1;
		}
		//注册(既登录设备)开始
		m_sDeviceIP = ip;
		short iPort = Integer.valueOf(port).shortValue();
		m_strDeviceInfo = new HCNetSDK.NET_DVR_DEVICEINFO_V30();//获取设备参数结构
		lUserID = hCNetSDK.NET_DVR_Login_V30(ip, iPort, name, password, m_strDeviceInfo);//登录设备
		long userID = lUserID;
		if (userID == -1) {
			int iErr = hCNetSDK.NET_DVR_GetLastError();
			System.out.println("：注册失败,错误号：" + iErr);
			System.out.println(hCNetSDK.NET_DVR_GetErrorMsg(m_err));
			m_sDeviceIP = "";//登录未成功,IP置为空
			return 3;//"注册失败";
		}
		return 0;
	}

	/**
	 * 注销登陆
	 */
	public void deviceLogout() {

		//如果在预览,先停止预览, 释放句柄
		if (lPreviewHandle > -1)
		{
			hCNetSDK.NET_DVR_StopRealPlay(lPreviewHandle);
		}

		//如果已经注册,注销
		if (lUserID> -1) {
			hCNetSDK.NET_DVR_Logout_V30(lUserID);
		}
		hCNetSDK.NET_DVR_Cleanup();
	}

	/**
	 * 获取设备通道
	 */
	public List<String> getChannelNumber() {
		List<String> channelList = new ArrayList<>();

		IntByReference ibrBytesReturned = new IntByReference(0);//获取IP接入配置参数
		boolean bRet = false;
		m_strIpparaCfg = new HCNetSDK.NET_DVR_IPPARACFG();
		m_strIpparaCfg.write();
		Pointer lpIpParaConfig = m_strIpparaCfg.getPointer();
		bRet = (m_strDeviceInfo.byIPChanNum != 0);

		String devices = "";
		if (!bRet) {
			//设备不支持,则表示没有IP通道
			for (int iChannum = 0; iChannum < m_strDeviceInfo.byChanNum; iChannum++) {
				devices = "Camera" + (iChannum + m_strDeviceInfo.byStartChan);
				channelList.add(devices);
			}
		} else {
			//设备支持IP通道
			boolean ok = hCNetSDK.NET_DVR_GetDVRConfig(lUserID, HCNetSDK.NET_DVR_GET_IPPARACFG, 0, lpIpParaConfig, m_strIpparaCfg.size(), ibrBytesReturned);
			if (!ok) {
				String err = hCNetSDK.NET_DVR_GetErrorMsg(m_err);
				logger.info("获取配置失败：" + m_err.getValue() + "," + err);
			}
			m_strIpparaCfg.read();

			for (int iChannum = 0; iChannum < m_strDeviceInfo.byChanNum; iChannum++) {
				if(m_strIpparaCfg.byAnalogChanEnable[iChannum] == 1)
				{
					devices = "Camera" + (iChannum + m_strDeviceInfo.byStartChan);
					channelList.add(devices);
				}
			}

			for (int iChannum = 0; iChannum < HCNetSDK.MAX_IP_CHANNEL; iChannum++) {
				if (m_strIpparaCfg.struIPChanInfo[iChannum].byEnable == 1) {
					devices = "IP" + (hCNetSDK.NET_DVR_SDKChannelToISAPI(lUserID,iChannum + m_strDeviceInfo.byStartDChan,true));
					String channelIP = (new String(m_strIpparaCfg.struIPDevInfo[iChannum].struIP.sIpV4));
					channelIP = channelIP.trim();
					devices = devices + "," + channelIP;
					channelList.add(devices);
				}
			}
		}

		return channelList;
	}

	/**
	 * 抓拍图片
	 *
	 * @param channel 通道号
	 */
	public boolean getDVRPic(int channel, String path) {

		HCNetSDK.NET_DVR_WORKSTATE_V30 devwork = new HCNetSDK.NET_DVR_WORKSTATE_V30();
		if (!hCNetSDK.NET_DVR_GetDVRWorkState_V30(lUserID, devwork)) {
			// 返回Boolean值，判断是否获取设备能力
			logger.info("hksdk(抓图)-返回设备状态失败");
		}
		//图片质量
		HCNetSDK.NET_DVR_JPEGPARA jpeg = new HCNetSDK.NET_DVR_JPEGPARA();
		//设置图片分辨率
		jpeg.wPicSize = 4;
		//设置图片质量
		jpeg.wPicQuality = 0;
		IntByReference a = new IntByReference();
		//需要加入通道
		boolean ok = hCNetSDK.NET_DVR_CaptureJPEGPicture(lUserID,channel,jpeg,path);
		if (ok) {
			logger.info("hksdk(抓图)-结果状态值(0表示成功):" + hCNetSDK.NET_DVR_GetLastError());
		} else {
			String err = hCNetSDK.NET_DVR_GetErrorMsg(m_err);
			logger.info("hksdk(抓图)-抓取失败,错误码:" + m_err.getValue() + "," + err);
		}
		return ok;
	}

	/**
	 * 下载录像
	 * @return boolean
	 */
	public boolean downloadBack(LocalDateTime startTime, LocalDateTime endTime, String filePath, int channel) throws InterruptedException {
		loadHandle = -1;
		loadHandle = hCNetSDK.NET_DVR_GetFileByTime(lUserID, channel, getHkTime(startTime), getHkTime(endTime), filePath);
		if (loadHandle >= 0) {
			//开始下载
			boolean downloadFlag = hCNetSDK.NET_DVR_PlayBackControl(loadHandle, hCNetSDK.NET_DVR_PLAYSTART, 0, null);
			int tmp = -1;
			IntByReference pos = new IntByReference(0);
			while (loadHandle >= 0) {
				boolean backFlag = hCNetSDK.NET_DVR_PlayBackControl(loadHandle, hCNetSDK.NET_DVR_PLAYGETPOS, 0, pos);
				if (!backFlag) {//防止单个线程死循环
					return downloadFlag;
				}
				int produce = pos.getValue();
				if ((produce % 10) == 0 && tmp != produce) {//输出进度
					tmp = produce;
					logger.info("视频下载进度:" + "==" + produce + "%");
				}
				if (produce == 100) {//下载成功
					hCNetSDK.NET_DVR_StopGetFile(loadHandle);
					loadHandle = -1;
					String err = hCNetSDK.NET_DVR_GetErrorMsg(m_err);
					logger.info("下载结束,last error: " + m_err.getValue() + "," + err);
					return true;
				}
				if (produce > 100) {//下载失败
					hCNetSDK.NET_DVR_StopGetFile(loadHandle);
					loadHandle = -1;
					String err = hCNetSDK.NET_DVR_GetErrorMsg(m_err);
					logger.warn("下载异常终止!错误原因:" + m_err.getValue() + "," + err);
					return false;
				}
				Thread.sleep(500);
			}
			String err = hCNetSDK.NET_DVR_GetErrorMsg(m_err);
			logger.warn("下载异常终止!错误原因:" + m_err.getValue() + "," + err);
			return false;
		} else {
			String err = hCNetSDK.NET_DVR_GetErrorMsg(m_err);
			logger.warn("获取录像文件错误!错误原因:" + m_err.getValue() + "," + err);
			return false;
		}
	}

	/**
	 * 获取录像文件信息
	 * @Return：
	 */
	public List<HashMap<String,String>> getVideoFileList(LocalDateTime startTime, LocalDateTime endTime, int channel) {
		List<HashMap<String,String>> fileList = new ArrayList<>();

		// 搜索条件
		HCNetSDK.NET_DVR_FILECOND m_strFilecond = new HCNetSDK.NET_DVR_FILECOND();

		m_strFilecond.struStartTime = getHkTime(startTime);
		m_strFilecond.struStopTime = getHkTime(endTime);
		m_strFilecond.lChannel = channel;//通道号

		int lFindFile = hCNetSDK.NET_DVR_FindFile_V30(lUserID, m_strFilecond);
		HCNetSDK.NET_DVR_FINDDATA_V30 strFile = new HCNetSDK.NET_DVR_FINDDATA_V30();
		if (lFindFile < 0) {
			String err = hCNetSDK.NET_DVR_GetErrorMsg(m_err);
			logger.warn("获取录像文件错误!错误原因:" + m_err.getValue() + "," + err);
			return null;
		}
		int lnext;
		strFile = new HCNetSDK.NET_DVR_FINDDATA_V30();
		HashMap<String,String> map = new HashMap<>();
		boolean flag=true;
		while (flag) {
			lnext = hCNetSDK.NET_DVR_FindNextFile_V30(lFindFile, strFile);
			if (lnext == HCNetSDK.NET_DVR_FILE_SUCCESS) {
				//搜索成功
				//添加文件名信息
				String[] s = new String[2];
				s = new String(strFile.sFileName).split("\0", 2);
				map.put("fileName:", new String(s[0]));

				int iTemp;
				String MyString;
				if (strFile.dwFileSize < 1024 * 1024) {
					iTemp = (strFile.dwFileSize) / (1024);
					MyString = iTemp + "K";
				} else {
					iTemp = (strFile.dwFileSize) / (1024 * 1024);
					MyString = iTemp + "M   ";
					iTemp = ((strFile.dwFileSize) % (1024 * 1024)) / (1204);
					MyString = MyString + iTemp + "K";
				}
				map.put("fileSize", MyString);                      //添加文件大小信息
				map.put("struStartTime", strFile.struStartTime.toStringTime());                      //添加开始时间信息
				map.put("struStopTime", strFile.struStopTime.toStringTime());                      //添加结束时间信息
				fileList.add(map);
			} else {
				if (lnext == HCNetSDK.NET_DVR_ISFINDING) {//搜索中
					//System.out.println("搜索中");
					continue;
				} else {
					flag=false;
					if (lnext == HCNetSDK.NET_DVR_FILE_NOFIND) {
						//flag=false;
					} else {
						//flag=false;
						System.out.println("搜索文件结束");
						boolean flag2 = hCNetSDK.NET_DVR_FindClose_V30(lFindFile);
						if (flag2 == false) {
							System.out.println("结束搜索失败");
						}
					}
				}
			}
		}
		return fileList;
	}

	public boolean playControl(String command,int mill,int channel) throws InterruptedException {
		HCPlayControlEnum controlEnum = HCPlayControlEnum.valueOf(command);
		boolean playHandle = false;
		//开始控制
		playHandle = hCNetSDK.NET_DVR_PTZControl_Other(lUserID,channel,controlEnum.getCode(),0);
		if(!playHandle){
			String err = hCNetSDK.NET_DVR_GetErrorMsg(m_err);
			logger.warn("(开始)控制失败!错误原因:" + m_err.getValue() + "," + err);
			return false;
		}
		//控制时间
		Thread.sleep(mill);
		//停止控制
		playHandle = hCNetSDK.NET_DVR_PTZControl_Other(lUserID,channel,controlEnum.getCode(),1);
		if(!playHandle){
			String err = hCNetSDK.NET_DVR_GetErrorMsg(m_err);
			logger.warn("(停止)控制失败!错误原因:" + m_err.getValue() + "," + err);
			return false;
		}
		return true;
	}

	/**
	 * 获取海康录像机格式的时间
	 *
	 * @param time
	 * @return
	 */
	public HCNetSDK.NET_DVR_TIME getHkTime(LocalDateTime time) {
		HCNetSDK.NET_DVR_TIME structTime = new HCNetSDK.NET_DVR_TIME();

		String str = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss").format(time);
		String[] times = str.split("-");
		structTime.dwYear = Integer.parseInt(times[0]);
		structTime.dwMonth = Integer.parseInt(times[1]);
		structTime.dwDay = Integer.parseInt(times[2]);
		structTime.dwHour = Integer.parseInt(times[3]);
		structTime.dwMinute = Integer.parseInt(times[4]);
		structTime.dwSecond = Integer.parseInt(times[5]);
		return structTime;
	}

}
