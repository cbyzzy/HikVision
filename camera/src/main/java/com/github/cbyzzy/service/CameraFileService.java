package com.github.cbyzzy.service;

import com.github.cbyzzy.model.entity.CameraFile;
import com.github.cbyzzy.criteria.HTCriteria;
import com.github.cbyzzy.exception.WebException;
import com.github.cbyzzy.page.PageResult;
import com.github.cbyzzy.service.BaseService;
import com.github.cbyzzy.utils.GuidUtils;
import com.github.cbyzzy.utils.HAssert;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class CameraFileService extends BaseService<CameraFile> {

    private Logger logger = LoggerFactory.getLogger(CameraFileService.class);

    @Value("${app.filepath}")
    private String rootPath;

    public String getRootPath() {
        //if (!rootPath.endsWith(File.separator)) {
            //rootPath += File.separator;
        //}
        return rootPath;
    }

    //根据路径文件名查找文件
    public Integer findByPathAndName(String path,String name) {
        HTCriteria<CameraFile> criteria = HTCriteria.getInstance(CameraFile.class);
        criteria.andEqualTo(CameraFile::getFilePath,path);
        criteria.andEqualTo(CameraFile::getFileName,name);
        List<CameraFile> list = find(criteria);
        if (list.size() > 0) {
            return list.get(0).getId();
        } else {
            return -1;
        }
    }

    public CameraFile writeFile(byte[] bytes, String path, String name) {
        //存储到本地
        File file = new File(rootPath + path + name);
        int size = bytes.length;
        BufferedOutputStream outputStream = null;
        try {
            outputStream = new BufferedOutputStream(new FileOutputStream(file));
            outputStream.write(bytes, 0, size);
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return saveFileInfo(name, path, (long)size);
    }

    /**
     * 保存文件信息
     *
     * @param name
     * @param path
     * @param size
     * @return
     */
    public CameraFile saveFileInfo(String name, String path, Long size) {
        CameraFile fu = new CameraFile();
        fu.setFileName(name);
        fu.setFileSize(size);
        fu.setFilePath(path);
        fu.setCreateTime(new Date());
        super.save(fu);
        return fu;
    }

    /**
     * 重命名文件
     *
     * @param name
     * @param rename
     * @return
     */
    private String rename(String name, String rename) {
        String filename = name;
        if (StringUtils.isBlank(rename)) {
            rename = GuidUtils.getId();
        }
        int last = name.lastIndexOf(".");
        if (last > 0) {
            String ext = name.substring(last, name.length());
            filename = rename + ext;
        }
        return filename;
    }

    /**
     * 相对路径
     *
     * @param dir
     * @return
     */
    private String relPath(String... dir) {
        if (dir == null || dir.length == 0) {
            dir = new String[]{"default"};
        }
        StringBuilder relPath = new StringBuilder();
        for (String p : dir) {
            if (StringUtils.isNotBlank(p)) {
                relPath.append(p).append(File.separator);
            }
        }
        return relPath.toString();
    }

    /**
     * 下载文件
     *
     * @param id
     * @param response
     */
    public void downloadFile(Integer id, HttpServletResponse response) {
        CameraFile fileUpload = findOne(id);
        if (fileUpload == null) {
            throw new WebException("文件信息不存在");
        }
        this.downloadFromPath(fileUpload.getFilePath(), fileUpload.getFileName(), response);
    }

    /**
     * 下载指定目录文件
     *
     * @param filePath
     * @param fileName
     * @param response
     */
    public void downloadFromPath(String filePath, String fileName, HttpServletResponse response) {
        try (ServletOutputStream out = response.getOutputStream()) {
            String path = getRootPath() + filePath + fileName;
            if (!Files.exists(Paths.get(path))) {
                throw new Exception("文件不存在");
            }
            if (fileName.lastIndexOf(".") > 0) {
                String ext = fileName.substring(fileName.lastIndexOf("."), fileName.length());
                String showName = fileName.substring(0, fileName.lastIndexOf("."));

                if (".mp4".equals(ext)) {
                    response.setHeader("Content-Type", "video/mp4");
                    response.setHeader("Accept-Ranges", "bytes");
                    response.setHeader("Connection", "keep-alive");
                }
            }
            response.setHeader("Content-Disposition", "attachment;filename="
                    + URLEncoder.encode(fileName, "utf-8"));
            IOUtils.write(Files.readAllBytes(Paths.get(path)), out);
            out.flush();
        } catch (Exception e) {
            throw new WebException(e.getMessage());
        }
    }

    public void downloadVideo(String filePath, String fileName, HttpServletRequest request, HttpServletResponse response) {
        BufferedInputStream bis = null;
        try {
            String path = getRootPath() + filePath;
            File file = new File(path);
            if (file.exists()) {
                long p = 0L;
                long toLength = 0L;
                long contentLength = 0L;
                int rangeSwitch = 0; // 0,从头开始的全文下载；1,从某字节开始的下载（bytes=27000-）；2,从某字节开始到某字节结束的下载（bytes=27000-39000）
                long fileLength;
                String rangBytes = "";
                fileLength = file.length();

                // get file content
                InputStream ins = new FileInputStream(file);
                bis = new BufferedInputStream(ins);

                // tell the client to allow accept-ranges
                response.reset();
                response.setHeader("Accept-Ranges", "bytes");

                // client requests a file block download start byte
                String range = request.getHeader("Range");
                if (range != null && range.trim().length() > 0 && !"null".equals(range)) {
                    response.setStatus(javax.servlet.http.HttpServletResponse.SC_PARTIAL_CONTENT);
                    rangBytes = range.replaceAll("bytes=", "");
                    if (rangBytes.endsWith("-")) { // bytes=270000-
                        rangeSwitch = 1;
                        p = Long.parseLong(rangBytes.substring(0, rangBytes.indexOf("-")));
                        contentLength = fileLength - p; // 客户端请求的是270000之后的字节（包括bytes下标索引为270000的字节）
                    } else { // bytes=270000-320000
                        rangeSwitch = 2;
                        String temp1 = rangBytes.substring(0, rangBytes.indexOf("-"));
                        String temp2 = rangBytes.substring(rangBytes.indexOf("-") + 1, rangBytes.length());
                        p = Long.parseLong(temp1);
                        toLength = Long.parseLong(temp2);
                        contentLength = toLength - p + 1; // 客户端请求的是 270000-320000 之间的字节
                    }
                } else {
                    contentLength = fileLength;
                }

                // 如果设设置了Content-Length，则客户端会自动进行多线程下载。如果不希望支持多线程，则不要设置这个参数。
                // Content-Length: [文件的总大小] - [客户端请求的下载的文件块的开始字节]
//                response.setHeader("Content-Length", new Long(contentLength).toString());

                // 断点开始
                // 响应的格式是:
                // Content-Range: bytes [文件块的开始字节]-[文件的总大小 - 1]/[文件的总大小]
                if (rangeSwitch == 1) {
                    String contentRange = new StringBuffer("bytes ").append(new Long(p).toString()).append("-")
                            .append(new Long(fileLength - 1).toString()).append("/")
                            .append(new Long(fileLength).toString()).toString();
                    response.setHeader("Content-Range", contentRange);
                    bis.skip(p);
                } else if (rangeSwitch == 2) {
                    String contentRange = range.replace("=", " ") + "/" + new Long(fileLength).toString();
                    response.setHeader("Content-Range", contentRange);
                    bis.skip(p);
                } else {
                    String contentRange = new StringBuffer("bytes ").append("0-").append(fileLength - 1).append("/")
                            .append(fileLength).toString();
                    response.setHeader("Content-Range", contentRange);
                }

                response.setContentType("application/octet-stream");
                response.addHeader("Content-Disposition", "attachment;filename=" + fileName);

                OutputStream out = response.getOutputStream();
                int n = 0;
                long readLength = 0;
                int bsize = 1024;
                byte[] bytes = new byte[bsize];
                if (rangeSwitch == 2) {
                    // 针对 bytes=27000-39000 的请求，从27000开始写数据
                    while (readLength <= contentLength - bsize) {
                        n = bis.read(bytes);
                        readLength += n;
                        out.write(bytes, 0, n);
                    }
                    if (readLength <= contentLength) {
                        n = bis.read(bytes, 0, (int) (contentLength - readLength));
                        out.write(bytes, 0, n);
                    }
                } else {
                    while ((n = bis.read(bytes)) != -1) {
                        out.write(bytes, 0, n);
                    }
                }
                out.flush();
                out.close();
                bis.close();
            }
        } catch (IOException ie) {
            // 忽略 ClientAbortException 之类的异常
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 删除文件
     *
     * @param id
     * @throws IOException
     */
    public void deleteFile(Integer id) {
        CameraFile fileInfo = findOne(id);
        if (fileInfo == null) {
            HAssert.error("文件不存在");
        }

        try {
            Path path = Paths.get(rootPath + fileInfo.getFilePath() + fileInfo.getFileName());
            Files.delete(path);
        } catch (IOException e) {
            logger.warn("删除失败：" + e.getMessage());
        }
        delete(id);
    }

    public PageResult<CameraFile> findFilePage(String ipc, Integer type, Integer pageNum, Integer pageSize) {
        HTCriteria<CameraFile> criteria = HTCriteria.getInstance(CameraFile.class);
        List<String> pathList = new ArrayList<>();
        String address = ipc.replace(".","");
        pathList.add("/video/" + address + "/");
        pathList.add("/picture/" + address + "/");
        pathList.add("/record/" + address + "/");
        if (StringUtils.isNotBlank((address))) {
            switch (type) {
                case 0:
                    criteria.andIn(CameraFile::getFilePath,pathList);
                    break;
                case 1:
                    criteria.andEqualTo(CameraFile::getFilePath,pathList.get(0));
                    break;
                case 2:
                    criteria.andEqualTo(CameraFile::getFilePath,pathList.get(1));
                    break;
                case 3:
                    criteria.andEqualTo(CameraFile::getFilePath,pathList.get(2));
                    break;
            }
        }
        criteria.orderDesc(CameraFile::getCreateTime);
        return findPage(criteria,pageNum,pageSize);
    }

    /**
     * 判断路径是否存在,若不存在,则创建
     *
     * @param path 文件路径
     */
    public void pathCreator(String path) {
        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }
    }

}
