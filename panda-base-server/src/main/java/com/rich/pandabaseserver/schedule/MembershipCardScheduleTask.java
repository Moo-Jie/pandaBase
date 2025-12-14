package com.rich.pandabaseserver.schedule;

import com.rich.pandabaseserver.service.MembershipCardService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 会员卡相关定时任务
 *
 * @author DuRuiChi
 */
@Slf4j
@Component
public class MembershipCardScheduleTask {

    @Autowired
    private MembershipCardService membershipCardService;

    /**
     * 自动处理过期会员卡
     * 每 10 分钟执行一次
     */
    @Scheduled(cron = "0 */10 * * * ?")
    public void autoExpireCards() {
        log.info("开始执行会员卡过期自动处理定时任务");
        try {
            membershipCardService.autoExpireCards();
            log.info("会员卡过期自动处理定时任务执行完成");
        } catch (Exception e) {
            log.error("会员卡过期自动处理定时任务执行失败", e);
        }
    }
}

