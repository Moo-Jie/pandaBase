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
import com.rich.pandabaseserver.model.entity.ExportRecord;
import com.rich.pandabaseserver.service.ExportRecordService;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

/**
 * 数据导出记录表 控制层。
 *
 * @author @author DuRuiChi
 */
@RestController
@RequestMapping("/exportRecord")
public class ExportRecordController {

    @Autowired
    private ExportRecordService exportRecordService;

    /**
     * 保存数据导出记录表。
     *
     * @param exportRecord 数据导出记录表
     * @return {@code true} 保存成功，{@code false} 保存失败
     */
    @PostMapping("save")
    public boolean save(@RequestBody ExportRecord exportRecord) {
        return exportRecordService.save(exportRecord);
    }

    /**
     * 根据主键删除数据导出记录表。
     *
     * @param id 主键
     * @return {@code true} 删除成功，{@code false} 删除失败
     */
    @DeleteMapping("remove/{id}")
    public boolean remove(@PathVariable Long id) {
        return exportRecordService.removeById(id);
    }

    /**
     * 根据主键更新数据导出记录表。
     *
     * @param exportRecord 数据导出记录表
     * @return {@code true} 更新成功，{@code false} 更新失败
     */
    @PutMapping("update")
    public boolean update(@RequestBody ExportRecord exportRecord) {
        return exportRecordService.updateById(exportRecord);
    }

    /**
     * 查询所有数据导出记录表。
     *
     * @return 所有数据
     */
    @GetMapping("list")
    public List<ExportRecord> list() {
        return exportRecordService.list();
    }

    /**
     * 根据主键获取数据导出记录表。
     *
     * @param id 数据导出记录表主键
     * @return 数据导出记录表详情
     */
    @GetMapping("getInfo/{id}")
    public ExportRecord getInfo(@PathVariable Long id) {
        return exportRecordService.getById(id);
    }

    /**
     * 分页查询数据导出记录表。
     *
     * @param page 分页对象
     * @return 分页对象
     */
    @GetMapping("page")
    public Page<ExportRecord> page(Page<ExportRecord> page) {
        return exportRecordService.page(page);
    }

}
