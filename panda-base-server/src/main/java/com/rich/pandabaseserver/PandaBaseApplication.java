package com.rich.pandabaseserver;

import dev.langchain4j.community.store.embedding.redis.spring.RedisEmbeddingStoreAutoConfiguration;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(exclude = {RedisEmbeddingStoreAutoConfiguration.class})
@MapperScan("com.rich.pandabaseserver.mapper")
@EnableScheduling
public class PandaBaseApplication {

    public static void main(String[] args) {
        SpringApplication.run(PandaBaseApplication.class, args);
    }
}
