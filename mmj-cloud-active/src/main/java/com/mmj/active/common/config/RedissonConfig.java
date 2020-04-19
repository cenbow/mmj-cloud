package com.mmj.active.common.config;

import lombok.Data;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@Configuration
@ConfigurationProperties(prefix = "redisson")
@Data
public class RedissonConfig {
    private String value;

    @Bean(destroyMethod = "shutdown", name = "redissonClient")
    public RedissonClient redisson() throws IOException {
        Config config = Config.fromJSON(value);
        return Redisson.create(config);
    }

}
