package com.rich.pandabaseserver.service;

import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.service.IService;
import com.rich.pandabaseserver.model.dto.user.UserQueryRequest;
import com.rich.pandabaseserver.model.entity.User;
import com.rich.pandabaseserver.model.vo.LoginUserVO;
import com.rich.pandabaseserver.model.vo.UserVO;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

/**
 * 用户表 服务层。
 *
 * @author @author DuRuiChi
 */
public interface UserService extends IService<User> {

    LoginUserVO getLoginUserVO(User user);

    User getLoginUser(HttpServletRequest request);

    UserVO getUserVO(User user);

    List<UserVO> getUserVOList(List<User> userList);

    boolean userLogout(HttpServletRequest request);

    QueryWrapper getQueryWrapper(UserQueryRequest userQueryRequest);

    String getEncryptPassword(String password);

    LoginUserVO wxLogin(String code, String nickname, String avatarUrl, HttpServletRequest request);
}
