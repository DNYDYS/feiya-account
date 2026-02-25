package com.feiya.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.parameters.HeaderParameter;
import io.swagger.v3.oas.models.parameters.Parameter;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.HandlerMethod;

/**
 * Knife4j全局配置（添加默认token请求头）
 */
@Configuration
public class Knife4jConfig {

    /**
     * 文档基本信息配置
     */
    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("飞鸭AI记账框架接口文档")
                        .description("飞鸭AI记账 - 包含用户认证、账单管理等核心接口")
                        .version("1.0.0")
                        .contact(new Contact().name("开发团队").email("dev@feiya.com")));
    }

    /**
     * 全局添加token请求头（所有接口自动带这个请求头）
     */
    @Bean
    public OperationCustomizer globalHeaderCustomizer() {
        return (Operation operation, HandlerMethod handlerMethod) -> {
            Parameter tokenHeader = new HeaderParameter()
                    .name("token")
                    .description("JWT令牌（默认测试值）")
                    .required(false)
                    .example("eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxIiwiaWF0IjoxNzcyNjE1MDAxLCJleHAiOjE3NzI3MDE0MDF9.xxxx");
//                    .schema(new StringSchema().defaultValue(""));

            operation.addParametersItem(tokenHeader);
            return operation;
        };
    }
}