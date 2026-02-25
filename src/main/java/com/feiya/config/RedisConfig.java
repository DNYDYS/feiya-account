package com.feiya.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * Redis核心配置（终极版：无任何废弃API，适配Spring Boot 3.x+）
 */
@Configuration
public class RedisConfig {

    // 从配置文件读取Redis连接信息
    @Value("${spring.data.redis.host:localhost}")
    private String redisHost;

    @Value("${spring.data.redis.port:6379}")
    private int redisPort;

    @Value("${spring.data.redis.password:}")
    private String redisPassword;

    @Value("${spring.data.redis.database:0}")
    private int redisDatabase;

    /**
     * 配置RedisTemplate（彻底解决序列化乱码 + 无废弃API）
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);

        // 1. 构建安全的ObjectMapper（核心：无任何废弃写法）
        ObjectMapper om = new ObjectMapper();
        // 允许访问所有字段
        om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        // 安全的多态类型验证（官方标准写法，无废弃）
        om.activateDefaultTyping(
                BasicPolymorphicTypeValidator.builder()
                        .allowIfBaseType(Object.class)
                        .build(),
                ObjectMapper.DefaultTyping.NON_FINAL
        );

        // 2. 核心修复：通过构造器传入ObjectMapper，不再用setObjectMapper（已废弃）
        Jackson2JsonRedisSerializer<Object> jacksonSerializer = new Jackson2JsonRedisSerializer<>(om, Object.class);

        // 3. String序列化器（Key专用）
        StringRedisSerializer stringSerializer = new StringRedisSerializer();

        // 4. 序列化规则配置
        template.setKeySerializer(stringSerializer);       // Key：String
        template.setValueSerializer(jacksonSerializer);    // Value：JSON
        template.setHashKeySerializer(stringSerializer);   // HashKey：String
        template.setHashValueSerializer(jacksonSerializer);// HashValue：JSON

        template.afterPropertiesSet();
        return template;
    }

    /**
     * 配置RedissonClient（分布式限流核心，无废弃API）
     */
    @Bean
    public RedissonClient redissonClient() {
        Config config = new Config();
        // 单机模式配置（生产可改为集群）
        config.useSingleServer()
                .setAddress("redis://" + redisHost + ":" + redisPort)
                .setPassword(redisPassword.isEmpty() ? null : redisPassword)
                .setDatabase(redisDatabase)
                .setTimeout(5000) // 5秒超时（与你之前的设置一致）
                .setConnectionPoolSize(10)
                .setConnectionMinimumIdleSize(5);

        return Redisson.create(config);
    }
}