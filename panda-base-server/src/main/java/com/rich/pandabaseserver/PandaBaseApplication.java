package com.rich.pandabaseserver;

import org.dromara.x.file.storage.spring.EnableFileStorage;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@MapperScan("com.rich.pandabaseserver.mapper")
@EnableScheduling
@EnableFileStorage
public class PandaBaseApplication {

    public static void main(String[] args) {
        SpringApplication.run(PandaBaseApplication.class, args);
    }
}
