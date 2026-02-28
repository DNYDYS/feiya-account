package com.feiya.dto.res;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

/**
 * AI聊天接口返回给前端的响应DTO
 */
@Data
@Builder
@Schema(description = "AI智能记账响应结果")
public class AiChatResponseDTO {
    /**
     * 账单金额
     */
    @Schema(description = "账单金额", example = "108.0")
    private Double billAmount;

    /**
     * 账单分类（如：餐饮/交通/购物）
     */
    @Schema(description = "账单分类", example = "餐饮")
    private String billCategory;

    /**
     * 人设回复文本（如：陛下！这108两又是糟蹋国库！）
     */
    @Schema(description = "人设回复文案", example = "(拍案怒吼)陛下！这108两又是糟蹋国库！")
    private String characterReply;

    /**
     * 表情包URL
     */
    @Schema(description = "情绪对应的表情包URL", example = "https://xxx/duck_angry.png")
    private String emojiUrl;

    /**
     * 消费备注（如：蟹黄面）
     */
    @Schema(description = "消费备注（自动提取）", example = "蟹黄面")
    private String remark;
}