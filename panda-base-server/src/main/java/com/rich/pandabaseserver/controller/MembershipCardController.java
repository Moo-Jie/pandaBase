package com.rich.pandabaseserver.controller;

import com.mybatisflex.core.paginate.Page;
import com.rich.pandabaseserver.common.ResultUtils;
import com.rich.pandabaseserver.common.response.BaseResponse;
import com.rich.pandabaseserver.exception.ErrorCode;
import com.rich.pandabaseserver.exception.ThrowUtils;
import com.rich.pandabaseserver.model.entity.User;
import com.rich.pandabaseserver.model.vo.MembershipCardVO;
import com.rich.pandabaseserver.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
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
@Tag(name = "会员卡管理", description = "会员卡相关接口")
public class MembershipCardController {

    @Autowired
    private MembershipCardService membershipCardService;

    @Autowired
    private UserService userService;

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

    /**
     * 获取当前用户的会员卡列表
     *
     * @param request HTTP请求
     * @return 会员卡列表
     */
    @GetMapping("/list/my")
    @Operation(summary = "我的会员卡列表", description = "获取当前登录用户的所有会员卡")
    public BaseResponse<List<MembershipCardVO>> getMyMembershipCards(HttpServletRequest request) {
        // 获取当前登录用户
        User loginUser = userService.getLoginUser(request);
        ThrowUtils.throwIf(loginUser == null, ErrorCode.NOT_LOGIN_ERROR);

        List<MembershipCardVO> cards = membershipCardService.getUserMembershipCards(loginUser.getId());
        return ResultUtils.success(cards);
    }

    /**
     * 根据ID获取会员卡详情
     *
     * @param id 会员卡ID
     * @param request HTTP请求
     * @return 会员卡详情
     */
    @GetMapping("/get/vo/{id}")
    @Operation(summary = "获取会员卡详情", description = "查看会员卡详细信息")
    public BaseResponse<MembershipCardVO> getMembershipCardVOById(@PathVariable Long id,
                                                                   HttpServletRequest request) {
        ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMS_ERROR);

        // 获取当前登录用户
        User loginUser = userService.getLoginUser(request);
        ThrowUtils.throwIf(loginUser == null, ErrorCode.NOT_LOGIN_ERROR);

        MembershipCard card = membershipCardService.getById(id);
        ThrowUtils.throwIf(card == null, ErrorCode.NOT_FOUND_ERROR, "会员卡不存在");
        ThrowUtils.throwIf(!card.getUserId().equals(loginUser.getId()), ErrorCode.NO_AUTH_ERROR, "无权限查看此会员卡");

        MembershipCardVO cardVO = membershipCardService.getMembershipCardVO(card);
        return ResultUtils.success(cardVO);
    }

    /**
     * 核销会员卡
     *
     * @param id 会员卡ID
     * @param request HTTP请求
     * @return 核销结果
     */
    @PostMapping("/verify/{id}")
    @Operation(summary = "核销会员卡", description = "使用会员卡入园，次票会减少次数")
    public BaseResponse<Boolean> verifyCard(@PathVariable Long id,
                                            HttpServletRequest request) {
        ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMS_ERROR);

        // 获取当前登录用户
        User loginUser = userService.getLoginUser(request);
        ThrowUtils.throwIf(loginUser == null, ErrorCode.NOT_LOGIN_ERROR);

        Boolean result = membershipCardService.verifyCard(id, loginUser.getId());
        return ResultUtils.success(result);
    }

    /**
     * 检查会员卡是否可用
     *
     * @param id 会员卡ID
     * @param request HTTP请求
     * @return 是否可用
     */
    @GetMapping("/check/{id}")
    @Operation(summary = "检查会员卡可用性", description = "检查会员卡是否有效且可使用")
    public BaseResponse<Boolean> checkCardAvailable(@PathVariable Long id,
                                                    HttpServletRequest request) {
        ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMS_ERROR);

        // 获取当前登录用户
        User loginUser = userService.getLoginUser(request);
        ThrowUtils.throwIf(loginUser == null, ErrorCode.NOT_LOGIN_ERROR);

        Boolean result = membershipCardService.checkCardAvailable(id, loginUser.getId());
        return ResultUtils.success(result);
    }
}
