package com.feiya.entity;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * AI人设配置实体类（对应数据库表ai_character_config）
 */
@Data
@TableName("ai_character_config")
public class AiCharacterConfig implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 人设唯一标识（如duck_minister/lin_daiyu）
     */
    private String characterId;

    /**
     * 人设名称（如鸭大臣/林黛玉）
     */
    private String characterName;

    /**
     * 大模型Prompt模板
     */
    private String prompt;

    /**
     * 情绪-表情包映射（JSON格式）
     * 注意：数据库是JSON类型，这里用JSONObject接收
     */
    private String emotionMapping;

    /**
     * 状态：1-启用，0-禁用
     */
    private Integer status;

    /**
     * 创建人
     */
    private String creator;

    /**
     * 创建时间（自动填充）
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 修改人
     */
    private String updater;

    /**
     * 修改时间（自动填充）
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /**
     * 备注
     */
    private String remark;
}