package com.github.cbyzzy.utils;

import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

public class FileUtils {

    private static char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6',
            '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };

    /**
     * 计算文件sha1
     * @param in 文件流
     * @return
     */
    public static String getSha1(FileInputStream in) {
        try {
            MessageDigest messagedigest = MessageDigest.getInstance("SHA-1");
            FileChannel ch = in.getChannel();
            MappedByteBuffer byteBuffer = ch.map(FileChannel.MapMode.READ_ONLY, 0, ch.size());
            messagedigest.update(byteBuffer);
            return bufferToHex(messagedigest.digest());
        } catch (NoSuchAlgorithmException | IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @Description 计算二进制数据
     * @return String
     * */
    private static String bufferToHex(byte bytes[]) {
        return bufferToHex(bytes, 0, bytes.length);
    }

    private static String bufferToHex(byte bytes[], int m, int n) {
        StringBuffer stringbuffer = new StringBuffer(2 * n);
        int k = m + n;
        for (int l = m; l < k; l++) {
            appendHexPair(bytes[l], stringbuffer);
        }
        return stringbuffer.toString();
    }

    private static void appendHexPair(byte bt, StringBuffer stringbuffer) {
        char c0 = hexDigits[(bt & 0xf0) >> 4];
        char c1 = hexDigits[bt & 0xf];
        stringbuffer.append(c0);
        stringbuffer.append(c1);
    }

    /**
     * 读取txt文件的内容
     *
     * @param file 想要读取的文件对象
     * @return 返回文件内容
     * @throws IOException
     * @throws IllegalStateException
     */
    public static List<String> txt2String(MultipartFile file, String tempPath,
                                    String judge) throws IllegalStateException, IOException {
        /*tempPath += "/temp.txt";*/
        File tempFile = new File(tempPath);
        if ("0".equalsIgnoreCase(judge)) {
            if (!tempFile.getParentFile().exists()) {
                tempFile.getParentFile().mkdirs();
            }
            if (null != file){
                file.transferTo(tempFile);
            }
        }
        /*StringBuilder result = new StringBuilder();*/
        List<String> list = new ArrayList<>();
        try {
            BufferedReader br = new BufferedReader(new FileReader(tempFile));// 构造一个BufferedReader类来读取文件
            String s = null;
            while ((s = br.readLine()) != null) {// 使用readLine方法，一次读一行
                /*result.append(System.lineSeparator() + s);*/
                list.add(s);
            }
            br.close();
            if ("1".equalsIgnoreCase(judge)) {
                tempFile.delete();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        /*return result.toString();*/
        return list;
    }
}
