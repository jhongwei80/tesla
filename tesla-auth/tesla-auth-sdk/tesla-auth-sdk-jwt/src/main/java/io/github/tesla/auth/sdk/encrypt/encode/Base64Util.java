/*
 * Copyright 2017 GcsSloop
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 *
 * Last modified 2017-09-07 17:28:13
 *
 * GitHub: https://github.com/GcsSloop WeiBo: http://weibo.com/GcsSloop WebSite: http://www.gcssloop.com
 */
package io.github.tesla.auth.sdk.encrypt.encode;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.commons.codec.binary.Base64;

import io.github.tesla.auth.common.utils.CloseUtils;
import io.github.tesla.auth.common.utils.TextUtils;

/**
 * Base64 工具类
 */
public class Base64Util {
    /**
     * 解密
     * 
     * @param key
     * @return
     * @throws Exception
     */
    public static byte[] decryptBASE64(String key) throws Exception {
        return Base64.decodeBase64(key);
    }

    /**
     * 加密
     * 
     * @param key
     * @return
     * @throws Exception
     */
    public static String encryptBASE64(byte[] key) throws Exception {
        return Base64.encodeBase64String(key);
    }

    /**
     * Base64加密
     *
     * @param file
     *            加密文件
     * @return 加密结果字符串
     */
    public static String encryptFileBASE64(File file) throws Exception {
        if (null == file)
            return "";

        FileInputStream inputFile = null;
        try {
            inputFile = new FileInputStream(file);
            byte[] buffer = new byte[(int)file.length()];
            inputFile.read(buffer);
            return encryptBASE64(buffer);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            CloseUtils.close(inputFile);
        }
        return "";
    }

    /**
     * Base64解密
     *
     * @param filePath
     *            解密文件路径
     * @param code
     *            解密文件编码
     * @return 解密结果文件
     */
    public static File decryptFileBASE64(String filePath, String code) {
        if (TextUtils.isEmpty(filePath) || TextUtils.isEmpty(code)) {
            return null;
        }

        FileOutputStream fos = null;
        File desFile = new File(filePath);
        try {
            byte[] decodeBytes = decryptBASE64(code);
            fos = new FileOutputStream(desFile);
            fos.write(decodeBytes);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            CloseUtils.close(fos);
        }
        return desFile;
    }
}