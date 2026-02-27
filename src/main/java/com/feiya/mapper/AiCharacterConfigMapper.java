package com.feiya.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.feiya.entity.AiCharacterConfig;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
 * AI人设配置Mapper（数据库操作）
 */
@Mapper
public interface AiCharacterConfigMapper extends BaseMapper<AiCharacterConfig> {

}