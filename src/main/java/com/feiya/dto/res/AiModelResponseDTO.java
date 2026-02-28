package com.feiya.dto.res;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 解析大模型原始响应的中间DTO（内部使用，可选加注解）
 */
@Data
@Schema(description = "大模型原始响应解析实体")
public class AiModelResponseDTO {
    /**
     * 操作类型（INSERT-插入账单/QUERY-查询账单）
     */
    @Schema(description = "操作类型", example = "INSERT", allowableValues = {"INSERT", "QUERY"})
    private String type;

    /**
     * 账单金额
     */
    @Schema(description = "账单金额", example = "108.0")
    private Double amount;

    /**
     * 账单分类
     */
    @Schema(description = "账单分类", example = "餐饮")
    private String category;

    /**
     * 人设回复文本
     */
    @Schema(description = "人设回复文案", example = "(拍案怒吼)陛下！这108两又是糟蹋国库！")
    private String reply;

    /**
     * 情绪标签（如：angry/sad/pleased）
     */
    @Schema(description = "情绪标签", example = "angry",
            allowableValues = {"angry", "pleased", "calm", "shocked", "sad", "gentle", "sigh", "happy"})
    private String emotion;

    /**
     * 消费备注
     */
    @Schema(description = "消费备注", example = "蟹黄面")
    private String remark;
}