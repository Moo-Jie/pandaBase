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
import com.rich.pandabaseserver.model.entity.MembershipCard;
import com.rich.pandabaseserver.service.MembershipCardService;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

/**
 * 会员卡表 控制层。
 *
 * @author @author DuRuiChi
 */
@RestController
@RequestMapping("/membershipCard")
public class MembershipCardController {

    @Autowired
    private MembershipCardService membershipCardService;

    /**
     * 保存会员卡表。
     *
     * @param membershipCard 会员卡表
     * @return {@code true} 保存成功，{@code false} 保存失败
     */
    @PostMapping("save")
    public boolean save(@RequestBody MembershipCard membershipCard) {
        return membershipCardService.save(membershipCard);
    }

    /**
     * 根据主键删除会员卡表。
     *
     * @param id 主键
     * @return {@code true} 删除成功，{@code false} 删除失败
     */
    @DeleteMapping("remove/{id}")
    public boolean remove(@PathVariable Long id) {
        return membershipCardService.removeById(id);
    }

    /**
     * 根据主键更新会员卡表。
     *
     * @param membershipCard 会员卡表
     * @return {@code true} 更新成功，{@code false} 更新失败
     */
    @PutMapping("update")
    public boolean update(@RequestBody MembershipCard membershipCard) {
        return membershipCardService.updateById(membershipCard);
    }

    /**
     * 查询所有会员卡表。
     *
     * @return 所有数据
     */
    @GetMapping("list")
    public List<MembershipCard> list() {
        return membershipCardService.list();
    }

    /**
     * 根据主键获取会员卡表。
     *
     * @param id 会员卡表主键
     * @return 会员卡表详情
     */
    @GetMapping("getInfo/{id}")
    public MembershipCard getInfo(@PathVariable Long id) {
        return membershipCardService.getById(id);
    }

    /**
     * 分页查询会员卡表。
     *
     * @param page 分页对象
     * @return 分页对象
     */
    @GetMapping("page")
    public Page<MembershipCard> page(Page<MembershipCard> page) {
        return membershipCardService.page(page);
    }

}
