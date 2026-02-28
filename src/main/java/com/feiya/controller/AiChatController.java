package com.feiya.controller;

import com.feiya.dto.req.AiChatRequestDTO;
import com.feiya.dto.res.AiChatResponseDTO;
import com.feiya.service.AiChatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * AI智能记账接口控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/ai")
@Tag(name = "AI智能记账接口", description = "多人设（鸭大臣/林黛玉）智能记账回复")
public class AiChatController {

    @Autowired
    private AiChatService aiChatService;

    /**
     * 多人设智能记账回复
     */
    @PostMapping("/chat")
    @Operation(summary = "智能记账回复", description = "根据用户输入和选择的人设，返回结构化记账结果+人设回复")
    public AiChatResponseDTO chat(@Valid @RequestBody AiChatRequestDTO request) {
        return aiChatService.chat(request);
    }
}