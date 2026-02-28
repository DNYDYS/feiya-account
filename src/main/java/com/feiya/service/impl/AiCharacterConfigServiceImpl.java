package com.feiya.service.impl;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.feiya.entity.AiCharacterConfig;
import com.feiya.mapper.AiCharacterConfigMapper;
import com.feiya.service.AiCharacterConfigService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * AI人设配置Service实现类
 */
@Slf4j
@Service
public class AiCharacterConfigServiceImpl extends ServiceImpl<AiCharacterConfigMapper, AiCharacterConfig>
        implements AiCharacterConfigService {

    // 默认人设ID（鸭大臣）
    private static final String DEFAULT_CHARACTER_ID = "duck_minister";

    /**
     * 根据人设ID获取配置（自动过滤禁用状态）
     */
    @Override
    public AiCharacterConfig getByCharacterId(String characterId) {
        // 1. 校验参数，为空则用默认人设
        if (characterId == null || characterId.trim().isEmpty()) {
            characterId = DEFAULT_CHARACTER_ID;
        }

        // 2. 查询启用状态的人设配置
        LambdaQueryWrapper<AiCharacterConfig> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AiCharacterConfig::getCharacterId, characterId)
                .eq(AiCharacterConfig::getStatus, 1); // 只查启用的

        AiCharacterConfig config = this.getOne(queryWrapper);

        // 3. 查不到则返回默认人设
        if (config == null) {
            log.warn("未找到人设{}的配置，使用默认人设{}", characterId, DEFAULT_CHARACTER_ID);
            queryWrapper.clear();
            queryWrapper.eq(AiCharacterConfig::getCharacterId, DEFAULT_CHARACTER_ID)
                    .eq(AiCharacterConfig::getStatus, 1);
            config = this.getOne(queryWrapper);
        }

        return config;
    }

    /**
     * 获取人设的情绪-表情包映射
     */
    @Override
    public Map<String, String> getEmotionMapping(String characterId) {
        AiCharacterConfig config = this.getByCharacterId(characterId);
        // JSON对象转Map
        Map map = JSONObject.parseObject(config.getEmotionMapping(), Map.class);
        return map;
    }

    /**
     * 获取人设的Prompt模板
     */
    @Override
    public String getCharacterPrompt(String characterId) {
        AiCharacterConfig config = this.getByCharacterId(characterId);
        return config.getPrompt();
    }
}