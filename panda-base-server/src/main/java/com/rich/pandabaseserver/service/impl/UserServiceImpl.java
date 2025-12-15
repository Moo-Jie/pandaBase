package com.rich.pandabaseserver.service.impl;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.bean.WxMaJscode2SessionResult;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.rich.pandabaseserver.exception.BusinessException;
import com.rich.pandabaseserver.exception.ErrorCode;
import com.rich.pandabaseserver.mapper.UserMapper;
import com.rich.pandabaseserver.model.dto.user.UserQueryRequest;
import com.rich.pandabaseserver.model.entity.User;
import com.rich.pandabaseserver.model.enums.UserRoleEnum;
import com.rich.pandabaseserver.model.vo.LoginUserVO;
import com.rich.pandabaseserver.model.vo.UserVO;
import com.rich.pandabaseserver.service.UserService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.error.WxErrorException;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.rich.pandabaseserver.constant.UserConstant.USER_LOGIN_STATE;

/**
 * 用户表 服务层实现。
 *
 * @author @author DuRuiChi
 */
@Slf4j
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Resource
    private WxMaService wxMaService;
    // 加密盐值
    private static final String PASSWORD_SALT = "rich";

    /**
     * 构造登录用户视图对象
     *
     * @param user 用户实体对象
     * @return 登录用户VO对象，输入为空时返回null
     * @author DuRuiChi
     * @create 2025/8/5
     **/
    @Override
    public LoginUserVO getLoginUserVO(User user) {
        if (user == null) {
            return null;
        }
        LoginUserVO loginUserVO = new LoginUserVO();
        BeanUtil.copyProperties(user, loginUserVO);
        return loginUserVO;
    }


    /**
     * 获取当前登录用户
     *
     * @param request HTTP请求对象
     * @return 当前登录用户实体
     * @throws BusinessException 未登录或登录态无效
     * @author DuRuiChi
     * @create 2025/8/5
     **/
    @Override
    public User getLoginUser(HttpServletRequest request) {
        // 1. 从session获取登录态
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        if (userObj == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }

        // 2. 基础类型校验
        if (!(userObj instanceof User)) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR, "登录状态异常");
        }

        // 3. 查询最新用户信息
        User currentUser = (User) userObj;
        return this.getByIdOptional(currentUser.getId())
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_LOGIN_ERROR, "用户不存在"));
    }

    /**
     * 获取用户视图对象
     *
     * @param user 用户实体
     * @return 脱敏后的用户VO，输入为空时返回null
     * @author DuRuiChi
     * @create 2025/8/5
     **/
    @Override
    public UserVO getUserVO(User user) {
        if (user == null) {
            return null;
        }
        UserVO userVO = new UserVO();
        BeanUtil.copyProperties(user, userVO);
        return userVO;
    }

    /**
     * 用户列表转换为视图对象列表
     *
     * @param userList 用户实体列表
     * @return 脱敏后的用户VO列表（永远非null）
     * @author DuRuiChi
     * @create 2025/8/5
     **/
    @Override
    public List<UserVO> getUserVOList(List<User> userList) {
        if (CollUtil.isEmpty(userList)) {
            return new ArrayList<>();
        }
        return userList.stream()
                .map(this::getUserVO)
                .filter(vo -> vo != null) // 过滤掉转换失败的空对象
                .collect(Collectors.toList());
    }

    /**
     * 用户退出登录
     *
     * @param request HTTP请求对象
     * @return 总是返回true
     * @throws BusinessException 用户未登录
     * @author DuRuiChi
     * @create 2025/8/5
     **/
    @Override
    public boolean userLogout(HttpServletRequest request) {
        // 检查登录状态
        if (request.getSession().getAttribute(USER_LOGIN_STATE) == null) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "用户未登录");
        }
        // 清除登录态
        request.getSession().removeAttribute(USER_LOGIN_STATE);
        return true;
    }

    /**
     * 构建查询条件包装器
     *
     * @param userQueryRequest 查询参数对象
     * @return 查询条件包装器
     * @throws BusinessException 参数为空
     * @author DuRuiChi
     * @create 2025/8/5
     **/
    @Override
    public QueryWrapper getQueryWrapper(UserQueryRequest userQueryRequest) {
        if (userQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "查询参数不能为空");
        }

        // 查询条件
        Long id = userQueryRequest.getId();
        String account = StrUtil.trimToNull(userQueryRequest.getAccount());
        String nickname = StrUtil.trimToNull(userQueryRequest.getNickname());
        String userProfile = StrUtil.trimToNull(userQueryRequest.getUserProfile());
        Integer userRole = userQueryRequest.getUserRole();
        String sortField = StrUtil.trimToNull(userQueryRequest.getSortField());
        String sortOrder = StrUtil.trimToNull(userQueryRequest.getSortOrder());

        QueryWrapper queryWrapper = QueryWrapper.create();
        // 精确匹配
        if (id != null) {
            queryWrapper.eq("id", id);
        }
        if (userRole != null) {
            queryWrapper.eq("role", userRole);
        }
        // 模糊查询
        if (account != null) {
            queryWrapper.like("account", account);
        }
        if (nickname != null) {
            queryWrapper.like("nickname", nickname);
        }
        if (userProfile != null) {
            queryWrapper.like("userProfile", userProfile);
        }
        // 排序条件
        if (sortField != null && sortOrder != null) {
            boolean isAsc = "ascend".equalsIgnoreCase(sortOrder);
            queryWrapper.orderBy(sortField, isAsc);
        }
        return queryWrapper;
    }

    /**
     * 密码加密
     *
     * @param password 明文密码
     * @return MD5加密后的密码
     * @throws BusinessException 密码为空
     * @author DuRuiChi
     * @create 2025/8/5
     **/
    @Override
    public String getEncryptPassword(String password) {
        if (StrUtil.isBlank(password)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码不能为空");
        }
        String input = password + PASSWORD_SALT;
        return DigestUtils.md5DigestAsHex(input.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 安全获取用户信息（Optional包装）
     *
     * @param id
     * @return java.util.Optional<com.rich.pandabaseserver.model.entity.User>
     * @author DuRuiChi
     * @create 2025/8/5
     **/
    private Optional<User> getByIdOptional(Long id) {
        return Optional.ofNullable(id)
                .map(this::getById);
    }

    /**
     * 微信小程序登录
     *
     * @param code      微信登录code
     * @param nickname  用户昵称
     * @param avatarUrl 用户头像URL
     * @param request   HTTP请求对象
     * @return 登录用户信息
     */
    @Override
    public LoginUserVO wxLogin(String code, String nickname, String avatarUrl, HttpServletRequest request) {
        // 1. 参数校验
        if (StrUtil.isBlank(code)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "登录code不能为空");
        }

        try {
            log.info("开始微信登录，code: {}", code);

            // 2. 通过code获取微信用户的openid和session_key
            WxMaJscode2SessionResult session;
            try {
                session = wxMaService.getUserService().getSessionInfo(code);
            } catch (WxErrorException e) {
                log.error("微信接口调用失败，错误代码：{}，错误信息：{}，微信原始报文：{}",
                        e.getError().getErrorCode(),
                        e.getError().getErrorMsg(),
                        e.getError().getJson());

                // 针对不同的错误码给出不同的提示
                if (e.getError().getErrorCode() == 40029) {
                    throw new BusinessException(ErrorCode.OPERATION_ERROR,
                            "登录凭证已失效，请重新登录。可能原因：登录凭证已被使用或已过期");
                } else if (e.getError().getErrorCode() == 40163) {
                    throw new BusinessException(ErrorCode.OPERATION_ERROR,
                            "登录凭证校验失败，请检查小程序配置");
                } else {
                    throw new BusinessException(ErrorCode.OPERATION_ERROR,
                            "微信登录失败：" + e.getError().getErrorMsg());
                }
            }

            String openid = session.getOpenid();
            String unionid = session.getUnionid();

            log.info("微信登录成功，openid: {}, unionid: {}", openid, unionid);

            // 3. 查询用户是否已存在
            QueryWrapper queryWrapper = QueryWrapper.create()
                    .eq("openid", openid);
            User user = this.getOne(queryWrapper);

            // 4. 如果用户不存在，则创建新用户（自动注册）
            if (user == null) {
                user = new User();
                user.setOpenid(openid);
                user.setUnionid(unionid);

                // 设置昵称：优先使用用户输入的昵称，否则生成随机昵称
                if (StrUtil.isNotBlank(nickname)) {
                    user.setNickname(nickname);
                } else {
                    // 生成随机昵称：小熊猫_xxxxx
                    String randomNickname = "小熊猫_" + IdUtil.simpleUUID().substring(0, 5);
                    user.setNickname(randomNickname);
                }

                // 保存头像URL（前端已上传到OSS）
                if (StrUtil.isNotBlank(avatarUrl)) {
                    user.setAvatarUrl(avatarUrl);
                    log.info("保存头像URL: {}", avatarUrl);
                }

                // 生成账号：XMJD_xxxxx
                String account = "XMJD_" + IdUtil.simpleUUID().substring(0, 5);
                user.setAccount(account);

                user.setRole(UserRoleEnum.USER.getValue());
                user.setIsAnonymous(false);
                user.setStatus(true);

                if (!this.save(user)) {
                    throw new BusinessException(ErrorCode.OPERATION_ERROR, "创建用户失败");
                }
                log.info("创建新用户成功，userId: {}, openid: {}, account: {}, nickname: {}",
                        user.getId(), openid, user.getAccount(), user.getNickname());
            } else {
                // 更新用户信息
                boolean needUpdate = false;

                // 更新unionid
                if (StrUtil.isNotBlank(unionid) && !unionid.equals(user.getUnionid())) {
                    user.setUnionid(unionid);
                    needUpdate = true;
                }

                // 更新昵称（如果用户提供了新的昵称）
                if (StrUtil.isNotBlank(nickname) && !nickname.equals(user.getNickname())) {
                    user.setNickname(nickname);
                    needUpdate = true;
                }

                // 更新头像URL（如果用户提供了新的头像）
                if (StrUtil.isNotBlank(avatarUrl) && !avatarUrl.equals(user.getAvatarUrl())) {
                    user.setAvatarUrl(avatarUrl);
                    needUpdate = true;
                    log.info("头像URL更新成功: {}", avatarUrl);
                }

                if (needUpdate) {
                    this.updateById(user);
                }

                log.info("用户登录成功，userId: {}, openid: {}, nickname: {}",
                        user.getId(), openid, user.getNickname());
            }

            // 5. 存储登录状态
            request.getSession().setAttribute(USER_LOGIN_STATE, user);

            // 6. 返回脱敏后的用户信息
            return getLoginUserVO(user);

        } catch (Exception e) {
            log.error("微信登录失败", e);
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "微信登录失败: " + e.getMessage());
        }
    }
}
