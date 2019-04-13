package io.github.tesla.ops.utils;

import java.io.*;
import java.util.Map;
import java.util.UUID;
import java.util.WeakHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileUtil {

    private static Logger logger = LoggerFactory.getLogger(FileUtil.class);
    private static Map<String, String> textCache = new WeakHashMap<>();

    public static boolean deleteFile(String fileName) {
        File file = new File(fileName);
        // 如果文件路径所对应的文件存在，并且是一个文件，则直接删除
        if (file.exists() && file.isFile()) {
            return file.delete();
        } else {
            return false;
        }
    }

    public static String renameToUUID(String fileName) {
        return UUID.randomUUID() + "." + fileName.substring(fileName.lastIndexOf(".") + 1);
    }

    public static void uploadFile(byte[] file, String filePath, String fileName) throws Exception {
        File targetFile = new File(filePath);
        if (!targetFile.exists()) {
            targetFile.mkdirs();
        }
        FileOutputStream out = new FileOutputStream(filePath + fileName);
        out.write(file);
        out.flush();
        out.close();
    }

    public static String readTextFromFile(String filePath) {
        if (textCache.get(filePath) == null) {
            InputStream is = FileUtil.class.getResourceAsStream(filePath);
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            StringBuilder sb = new StringBuilder();
            String line = null;
            try {
                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }
            } catch (IOException e) {
                logger.error("读取模板异常", e);
            } finally {
                try {
                    is.close();
                } catch (IOException e) {
                }
            }
            textCache.put(filePath, sb.toString());
            return sb.toString();
        }
        return textCache.get(filePath);
    }
}
