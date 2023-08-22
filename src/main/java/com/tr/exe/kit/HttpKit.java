package com.tr.exe.kit;

import com.alibaba.fastjson.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Objects;

/**
 * @Author: TR
 * @Date: 2023/8/18
 */
public class HttpKit {

    public static HashMap getRequest(String apiUrl) {
        HashMap hashMap = new HashMap();
        HttpURLConnection connection = null;
        BufferedReader reader = null;
        try {
            // 创建 URL 对象
            URL url = new URL(apiUrl);
            // 打开连接
            connection = (HttpURLConnection) url.openConnection();
            // 设置请求方法类型
            connection.setRequestMethod("GET");
            // 发送请求
            int responseCode = connection.getResponseCode();
            // 检查响应码
            StringBuilder response = null;
            if (responseCode == HttpURLConnection.HTTP_OK) {
                // 读取响应内容
                reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line;
                response = new StringBuilder();
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
            }
            hashMap.put("responseCode", responseCode);
            hashMap.put("responseBody", response);
        } catch (Exception e) {
            e.printStackTrace();
            hashMap.put("responseCode", -1); // 未知异常
        } finally {
            if (Objects.nonNull(reader)) {
                try {
                    reader.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            if (Objects.nonNull(connection)) {
                connection.disconnect();
            }
        }
        return hashMap;
    }

    public static JSONObject postRequest(String apiUrl, JSONObject paramBody) {
        JSONObject resultJson = new JSONObject();
        HttpURLConnection connection = null;
        BufferedReader reader = null;
        OutputStream outputStream = null;
        try {
            // 创建 URL 对象
            URL url = new URL(apiUrl);
            // 创建 HttpURLConnection 对象
            connection = (HttpURLConnection) url.openConnection();
            // 设置请求方法类型
            connection.setRequestMethod("POST");
            // 设置请求头部信息
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Accept", "application/json");
            // 启用输出流
            connection.setDoOutput(true);
            // 创建请求体
            String requestBody = paramBody.toString();
            // 将请求体写入输出流
            outputStream = connection.getOutputStream();
            outputStream.write(requestBody.getBytes());
            outputStream.flush();
            // 获取响应码
            int responseCode = connection.getResponseCode();
            // 读取响应内容
            if (responseCode == HttpURLConnection.HTTP_OK) {
                reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            } else {
                reader = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
            }
            // 将响应内容输出到控制台
            String line;
            StringBuilder response = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            resultJson.put("responseCode", responseCode);
            resultJson.put("responseBody", response);
        } catch (Exception e) {
            e.printStackTrace();
            resultJson.put("responseCode", -1); // 未知异常
        } finally {
            if (Objects.nonNull(outputStream)) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            if (Objects.nonNull(reader)) {
                try {
                    reader.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            if (Objects.nonNull(connection)) {
                connection.disconnect();
            }
        }
        return resultJson;
    }

}
