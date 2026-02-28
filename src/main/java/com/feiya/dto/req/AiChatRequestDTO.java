package com.feiya.dto.req;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * AI聊天接口请求DTO
 */
@Data
@Schema(description = "AI智能记账请求参数")
public class AiChatRequestDTO {
    /**
     * 用户输入内容（如：中午蟹面花了108元）
     */
    @Schema(description = "用户输入的记账内容", example = "中午蟹面花了108元", requiredMode = Schema.RequiredMode.REQUIRED)
    private String content;

    /**
     * 人设ID（如：duck_minister/lin_daiyu）
     */
    @Schema(description = "人设唯一标识", example = "duck_minister",
            allowableValues = {"duck_minister", "lin_daiyu"}, requiredMode = Schema.RequiredMode.REQUIRED)
    private String characterId;

    /**
     * 用户ID（前端可传，或从登录态获取）
     */
    @Schema(description = "用户ID（登录后自动填充）", example = "1001", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Long userId;
}