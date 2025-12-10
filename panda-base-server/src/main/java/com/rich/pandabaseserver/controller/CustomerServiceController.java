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
import com.rich.pandabaseserver.model.entity.CustomerService;
import com.rich.pandabaseserver.service.CustomerServiceService;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

/**
 * 客服会话表 控制层。
 *
 * @author @author DuRuiChi
 */
@RestController
@RequestMapping("/customerService")
public class CustomerServiceController {

    @Autowired
    private CustomerServiceService customerServiceService;

    /**
     * 保存客服会话表。
     *
     * @param customerService 客服会话表
     * @return {@code true} 保存成功，{@code false} 保存失败
     */
    @PostMapping("save")
    public boolean save(@RequestBody CustomerService customerService) {
        return customerServiceService.save(customerService);
    }

    /**
     * 根据主键删除客服会话表。
     *
     * @param id 主键
     * @return {@code true} 删除成功，{@code false} 删除失败
     */
    @DeleteMapping("remove/{id}")
    public boolean remove(@PathVariable Long id) {
        return customerServiceService.removeById(id);
    }

    /**
     * 根据主键更新客服会话表。
     *
     * @param customerService 客服会话表
     * @return {@code true} 更新成功，{@code false} 更新失败
     */
    @PutMapping("update")
    public boolean update(@RequestBody CustomerService customerService) {
        return customerServiceService.updateById(customerService);
    }

    /**
     * 查询所有客服会话表。
     *
     * @return 所有数据
     */
    @GetMapping("list")
    public List<CustomerService> list() {
        return customerServiceService.list();
    }

    /**
     * 根据主键获取客服会话表。
     *
     * @param id 客服会话表主键
     * @return 客服会话表详情
     */
    @GetMapping("getInfo/{id}")
    public CustomerService getInfo(@PathVariable Long id) {
        return customerServiceService.getById(id);
    }

    /**
     * 分页查询客服会话表。
     *
     * @param page 分页对象
     * @return 分页对象
     */
    @GetMapping("page")
    public Page<CustomerService> page(Page<CustomerService> page) {
        return customerServiceService.page(page);
    }

}
