package io.github.tesla.filter.utils;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class AntMatchUtil {

    private static final String pathSeparator = "/";

    private static final PathMatcher pathMatcher = new AntPathMatcher();
    private static final Pattern replacePattern = Pattern.compile("#\\{(\\d+)\\}");

    private static Pattern HTTP_PREFIX = Pattern.compile("^https?://.*", Pattern.CASE_INSENSITIVE);

    public static String concatPath(String... paths) {
        StringBuilder pathBuild = new StringBuilder();
        for (String path : paths) {
            path = path(path);
            if (path.startsWith(pathSeparator) && pathBuild.toString().endsWith(pathSeparator)) {
                pathBuild.append(path.substring(1));
            } else {
                pathBuild.append(path);
            }
        }
        return pathBuild.toString();
    }

    // 计算字符串在给定字符串出现的次数
    public static int findCount(String src, String des) {
        int index = 0;
        int count = 0;
        while ((index = src.indexOf(des, index)) != -1) {
            count++;
            index = index + des.length();
        }
        return count;
    }

    public static boolean match(String pattern, String path) {
        String remotePath = path;
        if (remotePath.indexOf("?") > 0) {
            remotePath = remotePath.substring(0, remotePath.indexOf("?"));
        }
        return pathMatcher.match(path(pattern), path(remotePath));
    }

    public static boolean matchPrefix(String servicePrefix, String path) {
        String remotePath = path;
        if (remotePath.indexOf("?") > 0) {
            remotePath = remotePath.substring(0, remotePath.indexOf("?"));
        }
        return concatPath(remotePath, pathSeparator).startsWith(concatPath(servicePrefix, pathSeparator));
    }

    public static Map<String, String> parseQueryString(String queryString) {
        if (StringUtils.isBlank(queryString)) {
            return null;
        }
        int index = queryString.indexOf("?");
        if (index >= 0) {
            queryString = queryString.substring(index + 1);
        }

        Map<String, String> argMap = Maps.newHashMap();
        String[] queryArr = queryString.split("&");
        for (int i = 0; i < queryArr.length; i++) {
            String string = queryArr[i];
            String[] keyAndValue = string.split("=", 2);
            if (keyAndValue.length != 2) {
                argMap.put(keyAndValue[0], StringUtils.EMPTY);
            } else {
                argMap.put(keyAndValue[0], keyAndValue[1]);
            }
        }
        return argMap;
    }

    /**
     * @author: zhipingzhang
     * @date: 2018/11/16 14:55
     * @description: 统一处理path
     */
    public static String path(String path) {
        if (StringUtils.isBlank(path)) {
            path = pathSeparator;
        } else if (path.equalsIgnoreCase(pathSeparator)) {

        } else if (path.startsWith(pathSeparator)) {

        } else if (path.startsWith("?")) {

        } else if (HTTP_PREFIX.matcher(path).matches()) {

        } else {
            path = pathSeparator + path;
        }
        if (!path.equalsIgnoreCase(pathSeparator) && path.endsWith(pathSeparator)) {
            path = StringUtils.substringBeforeLast(path, pathSeparator);
        }
        return path;
    }

    /**
     * @author: zhipingzhang
     * @date: 2018/11/16 14:20
     * @description:返回null证明不匹配 最复杂的情况下支持的匹配串请看AntMatchTestTest的junit case
     */
    public static String replacePathWithinPattern(String patternPath, String remotePath, String targetPath) {
        patternPath = path(patternPath);
        remotePath = path(remotePath);
        String remoteParamStr = null;
        if (remotePath.indexOf("?") > 0) {
            remoteParamStr = remotePath.substring(remotePath.indexOf("?"));
            remotePath = remotePath.substring(0, remotePath.indexOf("?"));
        }
        String targetParamStr = null;
        if (targetPath.indexOf("?") > 0) {
            targetParamStr = targetPath.substring(targetPath.indexOf("?"));
            targetPath = targetPath.substring(0, targetPath.indexOf("?"));
        }
        if (!pathMatcher.match(patternPath, remotePath)) {
            return null;
        }
        // 未自定义转发路径或
        if (StringUtils.isBlank(targetPath)) {
            return remotePath;
        }
        targetPath = path(targetPath);
        // 包含替换占位符 仅替换Url参数即可
        if (targetPath.contains("#{")) {
            Matcher matcher = replacePattern.matcher(targetPath);
            String extractPath = pathMatcher.extractPathWithinPattern(patternPath, remotePath);
            extractPath = path(extractPath);
            List<String> extractPathList = Lists.newArrayList();
            int maxCount = 0;
            while (matcher.find()) {
                int curr = Integer.parseInt(matcher.group(1));
                if (curr > maxCount) {
                    maxCount = curr;
                }
            }
            for (int i = 1; i <= maxCount; i++) {
                String mathPath = extractPath.split(pathSeparator)[1];
                extractPath = extractPath.substring(mathPath.length() + 1);
                if (i == maxCount && patternPath.endsWith("**")) {
                    mathPath = concatPath(mathPath, extractPath);
                }
                if (mathPath.startsWith(pathSeparator)) {
                    mathPath = mathPath.substring(1);
                }
                extractPathList.add(mathPath);
            }
            for (int i = 1; i <= maxCount; i++) {
                targetPath = targetPath.replace("#{" + i + "}", extractPathList.get(i - 1));
            }
        }
        if (StringUtils.isNotBlank(remoteParamStr)) {
            if (StringUtils.isNotBlank(targetParamStr)) {
                if (targetParamStr.contains("#{")) {
                    Map<String, String> paramMap = parseQueryString(remoteParamStr);
                    for (String key : paramMap.keySet()) {
                        targetParamStr = targetParamStr.replace("#{" + key + "}", paramMap.get(key));
                    }
                }
                targetPath = concatPath(targetPath, targetParamStr);
            } else if (!targetPath.contains(remoteParamStr)) {
                targetPath = concatPath(targetPath, remoteParamStr);
            }
        }
        return targetPath;
    }

    public static String replacePrefix(String uri, String originalPrefix, String targetPrefix) {
        String changedPath = path(uri).substring(path(originalPrefix).length());
        changedPath = concatPath(targetPrefix, changedPath);
        return changedPath;
    }

    /**
     * @author: zhipingzhang
     * @date: 2018/11/16 15:20
     * @description: 校验是否满足path规则
     */
    public static boolean validatePattern(String patternPath) {
        if (StringUtils.isBlank(patternPath)) {
            return false;
        }
        patternPath = path(patternPath);
        /*if (!pathMatcher.isPattern(patternPath)) {
            return false;
        }*/
        if (!patternPath.contains("**")) {
            return true;
        }
        // 不支持url参数
        if (patternPath.contains("{") && patternPath.contains("}")) {
            return false;
            // patternPath = patternPath.substring(0, patternPath.indexOf("{")-1);
        }
        if (patternPath.split("\\*\\*").length > 1) {
            return false;
        }
        return patternPath.endsWith("**");
    }
}
