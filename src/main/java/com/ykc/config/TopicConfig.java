package com.ykc.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * @Description: topic配置
 * @Author: zhutao
 * @Date: 2019/4/23
 */
@Configuration
@Getter
@Setter
public class TopicConfig {

    /**
     * 基础信息缓存TOPIC
     */
    @Value("${ykc.kafka.prop.topics.base.data}")
    private String baseCacheTopic;
}
