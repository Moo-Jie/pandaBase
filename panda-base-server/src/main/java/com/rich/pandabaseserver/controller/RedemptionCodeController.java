package com.rich.pandabaseserver.controller;

import com.mybatisflex.core.paginate.Page;
import com.rich.pandabaseserver.common.ResultUtils;
import com.rich.pandabaseserver.common.response.BaseResponse;
import com.rich.pandabaseserver.exception.ErrorCode;
import com.rich.pandabaseserver.exception.ThrowUtils;
import com.rich.pandabaseserver.model.dto.redemption.BatchGenerateRequest;
import com.rich.pandabaseserver.model.dto.redemption.RedeemRequest;
import com.rich.pandabaseserver.model.entity.User;
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
@Tag(name = "兑换码管理", description = "兑换码相关接口")
public class RedemptionCodeController {

    @Autowired
    private RedemptionCodeService redemptionCodeService;

    @Autowired
    private UserService userService;

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

    /**
     * 兑换码兑换
     *
     * @param redeemRequest 兑换请求
     * @param request HTTP请求
     * @return 兑换结果
     */
    @PostMapping("/redeem")
    @Operation(summary = "兑换码兑换", description = "用户输入兑换码进行兑换")
    public BaseResponse<Boolean> redeemCode(@RequestBody RedeemRequest redeemRequest,
                                           HttpServletRequest request) {
        ThrowUtils.throwIf(redeemRequest == null, ErrorCode.PARAMS_ERROR);
        ThrowUtils.throwIf(redeemRequest.getCode() == null || redeemRequest.getCode().trim().isEmpty(),
                ErrorCode.PARAMS_ERROR, "请输入兑换码");

        // 获取当前登录用户
        User loginUser = userService.getLoginUser(request);
        ThrowUtils.throwIf(loginUser == null, ErrorCode.NOT_LOGIN_ERROR);

        redeemRequest.setUserId(loginUser.getId());
        Boolean result = redemptionCodeService.redeemCode(
                redeemRequest.getCode(),
                redeemRequest.getUserId(),
                redeemRequest.getAddressId()
        );

        return ResultUtils.success(result);
    }

    /**
     * 批量生成兑换码（管理员操作）
     *
     * @param request 批量生成请求
     * @return 生成的兑换码列表
     */
    @PostMapping("/batch/generate")
    @Operation(summary = "批量生成兑换码", description = "管理员批量生成指定商品的兑换码")
    public BaseResponse<List<String>> batchGenerateRedemptionCodes(@RequestBody BatchGenerateRequest request) {
        ThrowUtils.throwIf(request == null, ErrorCode.PARAMS_ERROR);
        ThrowUtils.throwIf(request.getProductId() == null, ErrorCode.PARAMS_ERROR, "商品ID不能为空");
        ThrowUtils.throwIf(request.getCount() == null || request.getCount() <= 0, 
                ErrorCode.PARAMS_ERROR, "生成数量必须大于0");
        ThrowUtils.throwIf(request.getCount() > 1000, ErrorCode.PARAMS_ERROR, "单次生成数量不能超过1000");
        ThrowUtils.throwIf(request.getExpireTime() == null, ErrorCode.PARAMS_ERROR, "过期时间不能为空");

        // 如果没有提供批次号，自动生成
        String batchNo = request.getBatchNo();
        if (batchNo == null || batchNo.trim().isEmpty()) {
            batchNo = "BATCH" + System.currentTimeMillis();
        }

        List<RedemptionCode> codes = redemptionCodeService.generateRedemptionCodes(
                request.getProductId(), 
                batchNo, 
                request.getCount(), 
                request.getExpireTime()
        );

        // 提取兑换码字符串列表
        List<String> codeStrings = codes.stream()
                .map(RedemptionCode::getCode)
                .toList();

        return ResultUtils.success(codeStrings);
    }

}
