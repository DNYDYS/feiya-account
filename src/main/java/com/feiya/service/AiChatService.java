package com.feiya.service;

import com.feiya.dto.req.AiChatRequestDTO;
import com.feiya.dto.res.AiChatResponseDTO;

/**
 * AI聊天服务接口
 */
public interface AiChatService {
    /**
     * 多人设智能记账回复
     * @param request 请求参数（用户输入+人设ID+用户ID）
     * @return 结构化响应结果
     */
    AiChatResponseDTO chat(AiChatRequestDTO request);
}