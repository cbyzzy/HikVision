package com.github.cbyzzy.common;

import java.net.*;
import java.util.*;

public class CommonKit {

    /**
     * 根据前端传递的集合参数中取到指定属性的值
     */
    public static List getCols(List vo,String colName) {
        List<String> list = new ArrayList<>();
        for (Object obj : vo) {
            list.add((String) ((LinkedHashMap) obj).get(colName));
        }
        return list;
    }

    public static String join(String[] strs , String separator){
        String result = "";
        for (int i = 0; i < strs.length; i++) {
            if(i == 0){
                result += strs[i];
            }else{
                result += separator+strs[i];
            }
        }
        return result;
    }

    /**
     * 获取项目webapp目录
     * @return
     */
    public static String getLibPath(){
        String path = System.getProperty("user.dir") + "/config/lib/linux/libhcnetsdk.so";
        //String path = CommonKit.class.getClassLoader().getResource("").getPath().substring(1) + "lib\\win\\HCNetSDK.dll";
        return path;
	}

    /**
     * 获取本机ip
     * @return
     */
    public static String getServerIp() {
        // 获取操作系统类型
        String sysType = System.getProperties().getProperty("os.name");
        String ip;
        if (sysType.toLowerCase().startsWith("win")) {  // 如果是Windows系统，获取本地IP地址
            String localIP = null;
            try {
                localIP = InetAddress.getLocalHost().getHostAddress();
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
            if (localIP != null) {
                return localIP;
            }
        } else {
            ip = getIpByEthNum("eth0"); // 兼容Linux
            if (ip != null) {
                return ip;
            }
        }
        return "获取服务器IP错误";
    }

    /**
     * 根据网络接口获取IP地址
     * @param ethNum 网络接口名，Linux下是eth0
     * @return
     */
    private static String getIpByEthNum(String ethNum) {
        try {
            Enumeration allNetInterfaces = NetworkInterface.getNetworkInterfaces();
            InetAddress ip;
            while (allNetInterfaces.hasMoreElements()) {
                NetworkInterface netInterface = (NetworkInterface) allNetInterfaces.nextElement();
                if (ethNum.equals(netInterface.getName())) {
                    Enumeration addresses = netInterface.getInetAddresses();
                    while (addresses.hasMoreElements()) {
                        ip = (InetAddress) addresses.nextElement();
                        if (ip != null && ip instanceof Inet4Address) {
                            return ip.getHostAddress();
                        }
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return "获取服务器IP错误";
    }

    public static void main(String[] args) {
        String ip = "10.192.44.101";
        String[] a = ip.split("\\.");
        String b = CommonKit.join(a,"");
        System.out.println(a+b);
    }

}
