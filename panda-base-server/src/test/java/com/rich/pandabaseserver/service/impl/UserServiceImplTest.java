package com.rich.pandabaseserver.service.impl;

import com.rich.pandabaseserver.service.UserService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@Slf4j
@SpringBootTest
class UserServiceImplTest {
    @Resource
    private UserService userService;

    private static String PASSWORD = "123456";

    @Test
    void getEncryptPassword() {
        String encryptPassword = userService.getEncryptPassword(PASSWORD);
        log.info("“ {} ”加密后为：“{}”", PASSWORD, encryptPassword);
    }
}