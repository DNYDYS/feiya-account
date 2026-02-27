package com.feiya.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.feiya.entity.AiCharacterConfig;

import java.util.Map;

/**
 * AI人设配置Service
 */
public interface AiCharacterConfigService extends IService<AiCharacterConfig> {

    /**
     * 根据人设ID获取配置（自动过滤禁用状态）
     * @param characterId 人设唯一标识（如duck_minister）
     * @return 人设配置（默认返回鸭大臣）
     */
    AiCharacterConfig getByCharacterId(String characterId);

    /**
     * 获取人设的情绪-表情包映射
     * @param characterId 人设ID
     * @return 映射Map（如{"angry":"xxx.png"}）
     */
    Map<String, String> getEmotionMapping(String characterId);

    /**
     * 获取人设的Prompt模板
     * @param characterId 人设ID
     * @return Prompt文本
     */
    String getCharacterPrompt(String characterId);
}