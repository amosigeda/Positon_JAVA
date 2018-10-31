package com.hh.api.tools;

import java.io.*;

/**
 * Created by Administrator on 2016/12/5.
 */
public class FileUtils {
    /**
     * @param folderName
     * @param filename
     * @param content
     * @throws IOException
     */
    public static String saveFile(String folderName, String filename, String content) throws IOException {
        return saveFile(folderName, filename, content.getBytes());
    }

    public static String saveFile(String folderName, String filename, byte[] bytes) throws IOException {
        String path = folderName + "/" + filename;
        String savePath = null;//Utils.getAvailableStoragePath() + path;//FIXME
        File file = new File(savePath);//要输出的文件路径
        if (!file.getParentFile().exists()) {//文件不存在
            file.getParentFile().mkdirs();//创建文件夹
        }
        FileOutputStream fos = new FileOutputStream(file);
        fos.write(bytes);
        fos.close();
        return path;
    }

    /**
     * @param folderName
     * @throws IOException
     */
    public static void saveFolder(String folderName) throws IOException {
        String savePath = null;//Utils.getAvailableStoragePath() + folderName;//FIXME
        File file = new File(savePath);//要输出的文件路径
        if (!file.exists()) {
            file.mkdir();
        }
    }

    public static byte[] readFileAsBytes(String path) {
        byte[] bytes;
        try {
            File file = null;//FIXME new File(Utils.getAvailableStoragePath() + path);
            return readFileAsBytes(file);
        } catch (Exception e) {
            e.printStackTrace();
            bytes = new byte[0];
        }
        return bytes;
    }

    public static byte[] readFileAsBytes(File file) {
        byte[] bytes;
        try {
            if (file.isFile() && file.exists()) {
                FileInputStream fin = new FileInputStream(file);
                ByteArrayOutputStream bout = new ByteArrayOutputStream();
                byte[] buffer = new byte[1024];
                int rc;
                while ((rc = fin.read(buffer, 0, buffer.length)) != -1) {
                    bout.write(buffer, 0, rc);
                }
                bytes = bout.toByteArray();
                bout.close();
                fin.close();
            } else {
                bytes = new byte[0];
            }
        } catch (Exception e) {
            e.printStackTrace();
            bytes = new byte[0];
        }
        return bytes;
    }

    public static void closeStream(Closeable... closeables) {
        if (closeables == null) {
            return;
        }
        for (Closeable c : closeables) {
            if (c != null) {
                try {
                    c.close();
                } catch (IOException e) {
                    //
                }
            }
        }
    }

    public static String readFile(File file) {
        return new String(readFileAsBytes(file));
    }

    public static void deleteFile(String path) {
        File file = new File(path);
        file.delete();
    }
}
