package com.feiya.client;

import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 * 大模型客户端（适配豆包API，可替换为其他大模型）
 */
@Slf4j
@Component
public class AiClient {

    @Value("${ai.doubao.api-key:your-api-key}")
    private String apiKey;

    @Value("${ai.doubao.url:https://api.doubao.com/chat/completions}")
    private String apiUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    /**
     * 调用大模型接口
     * @param prompt 拼接后的Prompt（人设规则+用户输入）
     * @return 大模型返回的JSON字符串
     */
    public String chat(String prompt) {
        try {
            // 1. 构建请求头
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + apiKey);

            // 2. 构建请求体
            JSONObject requestBody = new JSONObject();
            requestBody.put("model", "doubao-pro");
            requestBody.put("temperature", 0.8);
            requestBody.put("max_tokens", 500);

            JSONObject message = new JSONObject();
            message.put("role", "user");
            message.put("content", prompt);
            requestBody.put("messages", new JSONObject[]{message});

            // 3. 发送请求
            HttpEntity<String> request = new HttpEntity<>(requestBody.toString(), headers);
            ResponseEntity<String> response = restTemplate.postForEntity(apiUrl, request, String.class);

            // 4. 解析响应
            if (response.getStatusCode().is2xxSuccessful()) {
                JSONObject responseJson = JSONObject.parseObject(response.getBody());
                String content = responseJson.getJSONArray("choices")
                        .getJSONObject(0)
                        .getJSONObject("message")
                        .getString("content");
                log.info("大模型回复内容：{}", content);
                return content;
            } else {
                log.error("调用大模型失败，状态码：{}，响应：{}", response.getStatusCode(), response.getBody());
                throw new RuntimeException("AI接口调用失败");
            }
        } catch (Exception e) {
            log.error("AI大模型调用异常", e);
            throw new RuntimeException("系统调用异常");
        }
    }
}