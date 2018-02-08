package me.cikai.api;

import com.google.gson.Gson;
import me.cikai.common.CommonUtils;
import me.cikai.common.Messages;
import me.cikai.model.Account;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.regex.Pattern.*;

/**
 * @author cikai
 * Sign In
 * 用户登录
 */
@RestController
public class Signin {

  @Autowired
  JdbcTemplate jdbcTemplate;

  @RequestMapping("/signin")
  public String signup(@RequestParam(value = "user") String user,
                       @RequestParam(value = "password") String password) {
    // 获取 user_id
    int userId = CommonUtils.checkEmail(user) ? getUserIdByField("email", user) : getUserIdByField("username", user);
    // 检测用户是否注册
    if (userId == 0) {
      return resultBuilder("107", Messages.SIGNIN_USER_NOT_EXIST, userId);
    }
    // 登录，检测 user_id password 是否匹配
    if (!userSignin(userId, password)) {
      return resultBuilder("108", Messages.SIGNIN_PASSWORD_ERROR, userId);
    }
    // 生成 token


    // 存入 redis
    return "sign in";
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

  public Boolean userSignin(int userId, String password) {
    Account account = new Account();
    jdbcTemplate.query("SELECT `password` FROM `account` WHERE `user_id`=" + userId + " ;",
      (RowMapper) (rs, rowNumber) -> {
        account.setPassword(rs.getString("password"));
        return account;
      });
    return account.getPassword().equals(password);
  }

  public String resultBuilder(String code, String message, int userId) {
    Gson gson = new Gson();
    Map<String, String> result = new HashMap<>(4);
    result.put("user_id", String.valueOf(userId));
    result.put("result", "false");
    result.put("code", code);
    result.put("message", message);
    return gson.toJson(result);
  }
}
