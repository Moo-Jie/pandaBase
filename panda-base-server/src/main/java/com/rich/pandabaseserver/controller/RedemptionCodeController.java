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
import com.rich.pandabaseserver.model.entity.RedemptionCode;
import com.rich.pandabaseserver.service.RedemptionCodeService;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

/**
 * 兑换码表 控制层。
 *
 * @author @author DuRuiChi
 */
@RestController
@RequestMapping("/redemptionCode")
public class RedemptionCodeController {

    @Autowired
    private RedemptionCodeService redemptionCodeService;

    /**
     * 保存兑换码表。
     *
     * @param redemptionCode 兑换码表
     * @return {@code true} 保存成功，{@code false} 保存失败
     */
    @PostMapping("save")
    public boolean save(@RequestBody RedemptionCode redemptionCode) {
        return redemptionCodeService.save(redemptionCode);
    }

    /**
     * 根据主键删除兑换码表。
     *
     * @param id 主键
     * @return {@code true} 删除成功，{@code false} 删除失败
     */
    @DeleteMapping("remove/{id}")
    public boolean remove(@PathVariable Long id) {
        return redemptionCodeService.removeById(id);
    }

    /**
     * 根据主键更新兑换码表。
     *
     * @param redemptionCode 兑换码表
     * @return {@code true} 更新成功，{@code false} 更新失败
     */
    @PutMapping("update")
    public boolean update(@RequestBody RedemptionCode redemptionCode) {
        return redemptionCodeService.updateById(redemptionCode);
    }

    /**
     * 查询所有兑换码表。
     *
     * @return 所有数据
     */
    @GetMapping("list")
    public List<RedemptionCode> list() {
        return redemptionCodeService.list();
    }

    /**
     * 根据主键获取兑换码表。
     *
     * @param id 兑换码表主键
     * @return 兑换码表详情
     */
    @GetMapping("getInfo/{id}")
    public RedemptionCode getInfo(@PathVariable Long id) {
        return redemptionCodeService.getById(id);
    }

    /**
     * 分页查询兑换码表。
     *
     * @param page 分页对象
     * @return 分页对象
     */
    @GetMapping("page")
    public Page<RedemptionCode> page(Page<RedemptionCode> page) {
        return redemptionCodeService.page(page);
    }

}
