package com.kevin.common.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * 类名: FileUtil<br/>
 * 包名：org.xinhua.common.util<br/>
 * 作者：kevin[wangqi2017@xinhua.org]<br/>
 * 时间：2018/5/9 15:49<br/>
 * 版本：1.0<br/>
 * 描述：文件工具类<br/>
 */
public class FileUtil {

    private static final Logger log = LoggerFactory.getLogger(FileUtil.class);

    private FileUtil() {
        // no constructor function
    }

    /**
     * 获取文件名的扩展名
     * @param fileName
     * @return
     */
    public static String getExtension(String fileName) {
        String extension = null;
        int index = fileName.lastIndexOf('.');
        if (index > 0) {
            extension = fileName.substring(index + 1);
        }
        return extension == null ? null : extension.toLowerCase();
    }

    /**
     * 写入文件
     * @param is        源字节流
     * @param des       目标文件路径
     * @param append    是否追加
     * @return
     */
    public static void write(InputStream is, String des, boolean append) {
        if (is == null) {
            log.error("源字节流不能为空!");
            return;
        }
        if (StringUtil.isEmpty(des)) {
            log.error("目标文件路径不能为空!");
            return;
        }

        createDirectories(des);
        try (
                BufferedInputStream bis = new BufferedInputStream(is);
                BufferedOutputStream bos = new BufferedOutputStream(
                        new FileOutputStream(des, append))
        ) {
            byte[] buf = new byte[4096];
            int len = 0;
            while ((len = bis.read(buf)) != -1) {
                bos.write(buf, 0, len);
            }
            bos.flush();
        } catch (IOException e) {
            log.error("写入文件异常", e);
        }
    }

    /**
     * 写入文件
     * @param content   内容
     * @param des       目标文件路径
     * @param append    是否追加
     * @return
     */
    public static void write(String content, String des, boolean append) {
        if (content == null) {
            log.error("内容不能为空!");
            return;
        }
        if (StringUtil.isEmpty(des)) {
            log.error("目标文件路径不能为空!");
            return;
        }

        createDirectories(des);
        try (PrintWriter writer =
                     new PrintWriter(new FileWriter(des, append), true)) {
            writer.write(content);
        } catch (IOException e) {
            log.error("写入文件异常", e);
        }
    }

    /**
     * 递归创建文件夹
     * @param path
     */
    private static void createDirectories(String path) {
        Path des = Paths.get(path);
        if (!des.toFile().exists()) {
            try {
                Files.createDirectories(des.getParent());
                Files.createFile(des);
            } catch (IOException e) {
                log.error("创建文件夹失败", e);
            }
        }
    }
}
