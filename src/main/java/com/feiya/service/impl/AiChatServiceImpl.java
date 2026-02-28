package com.feiya.service.impl;

import com.alibaba.fastjson2.JSONObject;
import com.feiya.client.AiClient;
import com.feiya.dto.req.AiChatRequestDTO;
import com.feiya.dto.res.AiChatResponseDTO;
import com.feiya.dto.res.AiModelResponseDTO;
import com.feiya.entity.AiCharacterConfig;
import com.feiya.service.AiCharacterConfigService;
import com.feiya.service.AiChatService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * AI聊天服务实现类（整合数据库人设+大模型调用）
 */
@Slf4j
@Service
public class AiChatServiceImpl implements AiChatService {

    @Autowired
    private AiCharacterConfigService characterConfigService;

    @Autowired
    private AiClient aiClient;

    @Override
    public AiChatResponseDTO chat(AiChatRequestDTO request) {
        try {
            // 1. 参数校验
            String userInput = request.getContent();
            String characterId = request.getCharacterId();
            Long userId = request.getUserId() == null ? 1001L : request.getUserId(); // 兜底用户ID

            // 2. 从数据库读取人设配置
            AiCharacterConfig config = characterConfigService.getByCharacterId(characterId);
            String promptTemplate = config.getPrompt();

            // 3. 拼接最终Prompt
            String finalPrompt = promptTemplate + "\n用户输入：" + userInput;
            log.info("用户{}调用人设{}，拼接后的Prompt：{}", userId, characterId, finalPrompt.substring(0, 100) + "...");

            // 4. 调用大模型接口
            String aiRawResponse = aiClient.chat(finalPrompt);

            // 5. 解析大模型响应
            AiModelResponseDTO aiModelResp = JSONObject.parseObject(aiRawResponse, AiModelResponseDTO.class);

            // 6. 插入账单（实际业务中调用账单Service）
            if ("INSERT".equals(aiModelResp.getType())) {
                log.info("用户{}插入账单：金额{}元，分类{}，备注{}",
                        userId, aiModelResp.getAmount(), aiModelResp.getCategory(), aiModelResp.getRemark());
                // billService.saveBill(userId, aiModelResp.getAmount(), aiModelResp.getCategory(), aiModelResp.getRemark());
            }

            // 7. 获取表情包URL
            Map<String, String> emotionMapping = characterConfigService.getEmotionMapping(characterId);
            String emojiUrl = "";
            if (emotionMapping != null) {
                emojiUrl = emotionMapping.getOrDefault(aiModelResp.getEmotion(), "https://xxx/default.png");
            }


            // 8. 组装返回结果
            return AiChatResponseDTO.builder()
                    .billAmount(aiModelResp.getAmount())
                    .billCategory(aiModelResp.getCategory())
                    .characterReply(aiModelResp.getReply())
                    .emojiUrl(emojiUrl)
                    .remark(aiModelResp.getRemark())
                    .build();
        } catch (Exception e) {
            log.error("AI回复处理失败", e);
            // 兜底返回
            return AiChatResponseDTO.builder()
                    .characterReply("公子/陛下，我今日有些不舒服，暂不能记账啦~")
                    .emojiUrl("https://xxx/default.png")
                    .remark("")
                    .build();
        }
    }
}