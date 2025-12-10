package com.rich.pandabaseserver.controller;

import com.mybatisflex.core.paginate.Page;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.beans.factory.annotation.Autowired;
import com.rich.pandabaseserver.model.entity.RedemptionRecord;
import com.rich.pandabaseserver.service.RedemptionRecordService;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

/**
 * 兑换记录表 控制层。
 *
 * @author @author DuRuiChi
 */
@RestController
@RequestMapping("/redemptionRecord")
public class RedemptionRecordController {

    @Autowired
    private RedemptionRecordService redemptionRecordService;

    /**
     * 保存兑换记录表。
     *
     * @param redemptionRecord 兑换记录表
     * @return {@code true} 保存成功，{@code false} 保存失败
     */
    @PostMapping("save")
    public boolean save(@RequestBody RedemptionRecord redemptionRecord) {
        return redemptionRecordService.save(redemptionRecord);
    }

    /**
     * 根据主键删除兑换记录表。
     *
     * @param id 主键
     * @return {@code true} 删除成功，{@code false} 删除失败
     */
    @DeleteMapping("remove/{id}")
    public boolean remove(@PathVariable Long id) {
        return redemptionRecordService.removeById(id);
    }

    /**
     * 根据主键更新兑换记录表。
     *
     * @param redemptionRecord 兑换记录表
     * @return {@code true} 更新成功，{@code false} 更新失败
     */
    @PutMapping("update")
    public boolean update(@RequestBody RedemptionRecord redemptionRecord) {
        return redemptionRecordService.updateById(redemptionRecord);
    }

    /**
     * 查询所有兑换记录表。
     *
     * @return 所有数据
     */
    @GetMapping("list")
    public List<RedemptionRecord> list() {
        return redemptionRecordService.list();
    }

    /**
     * 根据主键获取兑换记录表。
     *
     * @param id 兑换记录表主键
     * @return 兑换记录表详情
     */
    @GetMapping("getInfo/{id}")
    public RedemptionRecord getInfo(@PathVariable Long id) {
        return redemptionRecordService.getById(id);
    }

    /**
     * 分页查询兑换记录表。
     *
     * @param page 分页对象
     * @return 分页对象
     */
    @GetMapping("page")
    public Page<RedemptionRecord> page(Page<RedemptionRecord> page) {
        return redemptionRecordService.page(page);
    }

}
