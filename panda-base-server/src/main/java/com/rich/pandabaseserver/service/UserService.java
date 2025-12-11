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

    long userRegister(String account, String password, String checkPassword);

    long userRegisterWithPhone(String phone, String nickname, String password, String checkPassword);

    LoginUserVO getLoginUserVO(User user);

    LoginUserVO userLogin(String account, String password, HttpServletRequest request);

    User getLoginUser(HttpServletRequest request);

    UserVO getUserVO(User user);

    List<UserVO> getUserVOList(List<User> userList);

    boolean userLogout(HttpServletRequest request);

    QueryWrapper getQueryWrapper(UserQueryRequest userQueryRequest);

    String getEncryptPassword(String password);
}
