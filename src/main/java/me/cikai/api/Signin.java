package me.cikai.api;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.google.gson.Gson;
import me.cikai.common.CommonUtils;
import me.cikai.common.ResponseCodes;
import me.cikai.common.ResponseMessages;
import me.cikai.model.Account;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.exceptions.JedisException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.Map;
import java.io.UnsupportedEncodingException;

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
      return resultBuilder(false, ResponseCodes.SIGNIN_USER_NOT_EXIST, userId, ResponseMessages.SIGNIN_USER_NOT_EXIST, "");
    }
    // 登录，检测 user_id password 是否匹配
    if (!userSignin(userId, password)) {
      return resultBuilder(false, ResponseCodes.SIGNIN_PASSWORD_ERROR, userId, ResponseMessages.SIGNIN_PASSWORD_ERROR, "");
    }
    // 生成 JWT
    String secret = "";
    String token = "";
    try {
      secret = new String(Files.readAllBytes(Paths.get("C:\\Users\\lenovo\\.ssh\\id_rsa")));
    } catch (IOException e) {
      e.printStackTrace();
    }
    if (secret == null || secret.isEmpty()) {
      // 服务端密钥获取失败
      return resultBuilder(false, ResponseCodes.SERVER_PRIVATE_KEY_READ_ERROR, userId, ResponseMessages.SERVER_PRIVATE_KEY_READ_ERROR, "");
    }
    try {
      Algorithm algorithm = Algorithm.HMAC256(secret);
      token = JWT.create()
        .withIssuer(String.valueOf(userId))
        .sign(algorithm);
    } catch (UnsupportedEncodingException exception) {
      return resultBuilder(false, ResponseCodes.SERVER_ERROR, userId, ResponseMessages.SERVER_ERROR, "");
    } catch (JWTCreationException exception) {
      return resultBuilder(false, ResponseCodes.SERVER_ERROR, userId, ResponseMessages.SERVER_ERROR, "");
    }
    if (token == null || token.isEmpty()) {
      // 服务端token生成错误
      return resultBuilder(false, ResponseCodes.SERVER_ERROR, userId, ResponseMessages.SERVER_ERROR, "");
    }
    // 存入 redis，设置生存时间，单位：秒
    JedisPool pool = new JedisPool(new JedisPoolConfig(), "localhost");
    try (Jedis jedis = pool.getResource()) {
      if (jedis.get(String.valueOf(userId)) != null) {
        // 检测用户是否已经登录
        return resultBuilder(false, ResponseCodes.SIGNIN_ALREADY_WARNING, userId, ResponseMessages.SIGNIN_ALREADY_WARNING, token);
      }
      jedis.set(String.valueOf(userId), token);
      jedis.expire(String.valueOf(userId), 3600);
    } catch (JedisException e) {
      e.printStackTrace();
    } finally {
      pool.close();
    }
    return resultBuilder(true, ResponseCodes.SIGNIN_SUCCESS, userId, ResponseMessages.SIGNIN_SUCCESS, token);
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

  public String resultBuilder(Boolean flag, String code, int userId, String message, String token) {
    Gson gson = new Gson();
    Map<String, String> result = new LinkedHashMap<>(5);
    if (flag) {
      result.put("result", "true");
      result.put("code", code);
      result.put("user_id", String.valueOf(userId));
      result.put("token", token);
      result.put("message", message);
      return gson.toJson(result);
    }
    result.put("result", "false");
    result.put("code", code);
    result.put("user_id", String.valueOf(userId));
    result.put("message", message);
    return gson.toJson(result);
  }
}
