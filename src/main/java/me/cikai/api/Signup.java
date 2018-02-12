package me.cikai.api;

import me.cikai.common.CommonUtils;
import me.cikai.common.ResponseCodes;
import me.cikai.common.ResponseMessages;
import me.cikai.model.Account;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

/**
 * @author cikai
 * Sign Up
 * 用户注册
 */
@RestController
public class Signup {

  @Autowired
  JdbcTemplate jdbcTemplate;

  @RequestMapping("/signup")
  public String signup(@RequestHeader(value = "email") String email,
                       @RequestHeader(value = "username") String username,
                       @RequestHeader(value = "password") String password) {
    // 邮箱已存在
    if (getUserIdByField("email", email) > 0) {
      return CommonUtils.resultBuilder(false, ResponseCodes.SIGNUP_EMAIL_EXIST, 0, ResponseMessages.SIGNUP_EMAIL_EXIST, username);
    }
    // 用户名已存在
    if (getUserIdByField("username", username) > 0) {
      return CommonUtils.resultBuilder(false, ResponseCodes.SIGNUP_USERNAME_EXIST, 0, ResponseMessages.SIGNUP_USERNAME_EXIST, username);
    }
    // 邮箱为空
    if (email == null || email.isEmpty()) {
      return CommonUtils.resultBuilder(false, ResponseCodes.SIGNUP_EMAIL_BLANK, 0, ResponseMessages.SIGNUP_EMAIL_BLANK, username);
    }
    // 邮箱格式错误
    if (!CommonUtils.checkEmail(email)) {
      return CommonUtils.resultBuilder(false, ResponseCodes.SIGNUP_EMAIL_FORMAT_ERROR, 0, ResponseMessages.SIGNUP_EMAIL_FORMAT_ERROR, username);
    }
    // 用户名为空
    if (username == null || username.isEmpty()) {
      return CommonUtils.resultBuilder(false, ResponseCodes.SIGNUP_USERNAME_BLANK, 0, ResponseMessages.SIGNUP_USERNAME_BLANK, username);
    }
    // 密码为空
    if (password == null || password.isEmpty()) {
      return CommonUtils.resultBuilder(false, ResponseCodes.SIGNUP_PASSWORD_BLANK, 0, ResponseMessages.SIGNUP_PASSWORD_BLANK, username);
    }
    // 用户注册
    Boolean register = registerUser(email, username, password);
    // 注册失败
    if (!register) {
      return CommonUtils.resultBuilder(false, ResponseCodes.SIGNUP_FAILURE, 0, ResponseMessages.SIGNUP_FAILURE, username);
    }
    // 注册成功
    return CommonUtils.resultBuilder(true, ResponseCodes.SIGNUP_SUCCESS, 0, ResponseMessages.SIGNUP_SUCCESS, username);
  }

  public int getUserIdByField(String field, String value) {
    Account account = new Account();
    jdbcTemplate.query("SELECT `user_id` FROM `account` WHERE `" + field + "`=\"" + value + "\" ;",
      (RowMapper) (rs, rowNumber) -> {
        account.setUserId(rs.getInt("user_id"));
        return account;
      });
    return account.getUserId();
  }

  public Boolean registerUser(String email, String username, String password) {
    Map<String, Object> params = new LinkedHashMap<>();
    params.put("email", email);
    params.put("username", username);
    params.put("password", password);
    Object[] object = params.values().toArray();
    List<Object[]> values = new ArrayList<>();
    values.add(object);
    Boolean result;
    try {
      String sql = "INSERT INTO `account` (`email`, `username`, `password`) VALUES (?, ?, ?)";
      int[] ins = jdbcTemplate.batchUpdate(sql, values);
      if (ins[0] == 1) {
        result = true;
      } else {
        result = false;
      }
    } catch (Exception e) {
      result = false;
    }
    return result;
  }

}
