package com.rich.pandabaseserver.controller;

import com.mybatisflex.core.paginate.Page;
import com.rich.pandabaseserver.common.ResultUtils;
import com.rich.pandabaseserver.common.response.BaseResponse;
import com.rich.pandabaseserver.exception.ErrorCode;
import com.rich.pandabaseserver.exception.ThrowUtils;
import com.rich.pandabaseserver.model.entity.User;
import com.rich.pandabaseserver.model.vo.RedemptionRecordVO;
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
@Tag(name = "兑换记录管理", description = "兑换记录相关接口")
public class RedemptionRecordController {

    @Autowired
    private RedemptionRecordService redemptionRecordService;

    @Autowired
    private UserService userService;

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

    /**
     * 获取当前用户的兑换记录列表
     *
     * @param request HTTP请求
     * @return 兑换记录列表
     */
    @GetMapping("/list/my")
    @Operation(summary = "我的兑换记录列表", description = "获取当前登录用户的所有兑换记录")
    public BaseResponse<List<RedemptionRecordVO>> getMyRedemptionRecords(HttpServletRequest request) {
        // 获取当前登录用户
        User loginUser = userService.getLoginUser(request);
        ThrowUtils.throwIf(loginUser == null, ErrorCode.NOT_LOGIN_ERROR);

        List<RedemptionRecordVO> records = redemptionRecordService.getUserRedemptionRecords(loginUser.getId());
        return ResultUtils.success(records);
    }

    /**
     * 发货（管理员操作）
     *
     * @param recordId 兑换记录ID
     * @param trackingNumber 物流单号
     * @return 是否成功
     */
    @PostMapping("/ship")
    @Operation(summary = "发货", description = "管理员为兑换记录填写物流单号并发货")
    public BaseResponse<Boolean> shipRecord(@org.springframework.web.bind.annotation.RequestParam Long recordId,
                                            @org.springframework.web.bind.annotation.RequestParam String trackingNumber) {
        ThrowUtils.throwIf(recordId == null || trackingNumber == null || trackingNumber.trim().isEmpty(), 
                ErrorCode.PARAMS_ERROR);

        Boolean result = redemptionRecordService.shipRecord(recordId, trackingNumber);
        return ResultUtils.success(result);
    }

    /**
     * 完成兑换记录（管理员操作）
     *
     * @param recordId 兑换记录ID
     * @return 是否成功
     */
    @PostMapping("/complete/{recordId}")
    @Operation(summary = "完成兑换记录", description = "管理员确认用户已收货，完成兑换记录")
    public BaseResponse<Boolean> completeRecord(@PathVariable Long recordId) {
        ThrowUtils.throwIf(recordId == null, ErrorCode.PARAMS_ERROR);

        Boolean result = redemptionRecordService.completeRecord(recordId);
        return ResultUtils.success(result);
    }

}
