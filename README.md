# HikVision Camera/NVR Secondary Development
## 海康威视SDK二次开发

### 项目介绍
    本项目为海康威视网络摄像机和NVR录像机的SDK二次开发，实现了对网络摄像机/NVR的实时流、历史流的推流功能以及抓图、录像下载以及云台控制功能 
##### 项目技术栈
    JDK：1.8
    SpringBoot: 2.2.9
    Dao: MyBatis + tkMyBatis
    转码： FFmpeg + FFmpegCommandHandler
    流媒体： ossrs/srs:4.0
##### 技术实现
    - 通过Jna调用海康SDK的lib库（注意windows部署环境下使用dll，linux部署环境下使用so），lib库可以直接从海康官网下载，注意下载库的32bit和64bit的区分
    
    - 对于不同的部署环境（win和linux），其HCNetSDK.java这个文件实际上是有区别的，这里只提供了linux下的文件，windows环境下实际上只要将接口中extends  
      的Library和Callback改为StdLibrary和StdCallCallBack就行，实测下来是能用的。
      
    - 关于上一点，海康对java的调用接口定义（即HCNetSDK.java文件）并不经常维护，建议看看海康的SDK开发帮助文档，根据需要可自己修改HCNetSDK文件的接口定义
    
    - 视频流推流过程：海康的实时流和历史流可以直接由rtsp流组装的方式直接获取，为了能够在网页上进行播放，需要采用FFmpeg对rtsp流转码为rtmp流，本项目采用了  
      FFmpegCommandHandler组件对FFmpeg进行命令调用，而后需要将转码后的rtmp流推送到流媒体服务器中，这里推荐使用ossrs/srs4.0流媒体服务器，与nginx-r  
      tmp相比，更加稳定和高效
      
    - 流媒体服务器：srs流媒体服务器可以使用docker的方式直接部署（通过编译的方式部署较为麻烦），同时可以将推送来的rtmp流转码为http-flv流。注意html5不支  
      持rtmp流的播放，推荐播放http-flv流
      
    - 前端视频流播放：通过流媒体服务器，前端可以获取到rtmp流和http-flv流，推荐使用flv流进行播放，可以使用flv.js播放组件或LivePlayer播放组件播放
    
    - 项目运行需要数据库支持，定义了三个数据库（Camera、CameraFlow、CameraFile），具体的字段定义可以通过model.entity包查看
    
    - 项目引用了三个外部jar包，包括examples(海康相关)、Jna(dll/so库调用)、FFmpegCommandHandler(FFmpeg命令执行器),三个jar包放在了resources/jar  
      下面，推荐将这三个jar包通过maven安装到仓库中调用以避免产生部署时依赖找不到的问题
##### 交流
    - 有具体疑问和建议可以通过邮件联系：cbyzzy@sina.com

