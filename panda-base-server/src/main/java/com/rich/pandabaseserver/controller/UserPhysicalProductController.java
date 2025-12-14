package com.rich.pandabaseserver.controller;

import com.rich.pandabaseserver.common.response.BaseResponse;
import com.rich.pandabaseserver.common.ResultUtils;
import com.rich.pandabaseserver.exception.ErrorCode;
import com.rich.pandabaseserver.exception.ThrowUtils;
import com.rich.pandabaseserver.model.entity.User;
import com.rich.pandabaseserver.model.entity.UserPhysicalProduct;
import com.rich.pandabaseserver.model.vo.MembershipCardVO;
import com.rich.pandabaseserver.model.vo.MyProductVO;
import com.rich.pandabaseserver.service.MembershipCardService;
import com.rich.pandabaseserver.service.UserPhysicalProductService;
import com.rich.pandabaseserver.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * 用户实体商品控制器
 *
 * @author DuRuiChi
 */
@Slf4j
@RestController
@RequestMapping("/userProduct")
@Tag(name = "用户商品")
public class UserPhysicalProductController {

    @Resource
    private UserService userService;

    @Resource
    private MembershipCardService membershipCardService;

    @Resource
    private UserPhysicalProductService userPhysicalProductService;

    /**
     * 获取我的所有商品（会员卡 + 实体商品）
     *
     * @param request HTTP请求
     * @return 商品列表
     */
    @GetMapping("/my/list")
    @Operation(summary = "获取我的所有商品", description = "包含会员卡和实体商品")
    public BaseResponse<List<MyProductVO>> getMyProducts(HttpServletRequest request) {
        // 获取当前登录用户
        User loginUser = userService.getLoginUser(request);
        ThrowUtils.throwIf(loginUser == null, ErrorCode.NOT_LOGIN_ERROR);

        List<MyProductVO> result = new ArrayList<>();

        // 1. 查询会员卡
        List<MembershipCardVO> membershipCardVOs = membershipCardService.getUserMembershipCards(loginUser.getId());
        for (MembershipCardVO cardVO : membershipCardVOs) {
            MyProductVO vo = new MyProductVO();
            vo.setId(cardVO.getId());
            vo.setName(cardVO.getCardTypeText());
            vo.setImageUrl(getCardTypeImage(cardVO.getCardType()));
            vo.setType(cardVO.getCardType());
            vo.setTypeText(cardVO.getCardTypeText());
            vo.setStatus(cardVO.getStatus());
            vo.setStatusText(cardVO.getStatusText());
            vo.setStartTime(cardVO.getStartTime());
            vo.setEndTime(cardVO.getEndTime());
            vo.setCardNumber(cardVO.getCardNumber());
            vo.setRemainCount(cardVO.getRemainCount());
            vo.setCreateTime(cardVO.getCreateTime());
            result.add(vo);
        }

        // 2. 查询实体商品
        List<UserPhysicalProduct> physicalProducts = userPhysicalProductService.getMyPhysicalProducts(loginUser.getId());
        for (UserPhysicalProduct product : physicalProducts) {
            MyProductVO vo = new MyProductVO();
            vo.setId(product.getId());
            vo.setName(product.getProductName());
            vo.setImageUrl(product.getProductImage());
            vo.setType(4); // 实体商品
            vo.setTypeText("实体商品");
            vo.setStatus(product.getStatus());
            vo.setStatusText(product.getStatus() == 0 ? "未核销" : "已核销");
            vo.setQuantity(product.getQuantity());
            vo.setCreateTime(product.getCreateTime());
            result.add(vo);
        }

        return ResultUtils.success(result);
    }

    /**
     * 获取卡类型图片
     */
    private String getCardTypeImage(Integer cardType) {
        if (cardType == null) return "/static/images/年卡VIP3.png";
        return switch (cardType) {
            case 1 -> "/static/images/年卡VIP3.png";
            case 2 -> "/static/images/月卡VIP3.png";
            case 3 -> "/static/logo.png"; // 次票默认图
            default -> "/static/images/年卡VIP3.png";
        };
    }
}

