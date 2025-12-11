package com.rich.pandabaseserver.schedule;

import com.rich.pandabaseserver.service.PurchaseOrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 订单相关定时任务
 *
 * @author DuRuiChi
 */
@Slf4j
@Component
public class OrderScheduleTask {

    @Autowired
    private PurchaseOrderService purchaseOrderService;

    /**
     * 自动取消过期订单
     * 每5分钟执行一次
     */
    @Scheduled(cron = "0 */5 * * * ?")
    public void autoExpireOrders() {
        log.info("开始执行订单过期自动取消定时任务");
        try {
            purchaseOrderService.autoExpireOrders();
            log.info("订单过期自动取消定时任务执行完成");
        } catch (Exception e) {
            log.error("订单过期自动取消定时任务执行失败", e);
        }
    }
}

