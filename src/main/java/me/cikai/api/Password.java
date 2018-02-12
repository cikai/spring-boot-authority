package me.cikai.api;

import com.auth0.jwt.JWT;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.google.gson.Gson;
import me.cikai.common.ResponseCodes;
import me.cikai.common.ResponseMessages;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.exceptions.JedisException;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author cikai
 * Password Operation
 * 密码操作
 */
@RestController
@RequestMapping("/password")
public class Password {

  @Autowired
  JdbcTemplate jdbcTemplate;

  @RequestMapping("/update")
  public String updatePassword(@RequestHeader(value = "token") String token,
                               @RequestHeader(value = "password") String password,
                               @RequestHeader(value = "new_password") String new_password) {
    // token 反解 userId
    String userId = "";
    try {
      DecodedJWT jwt = JWT.decode(token);
      userId = jwt.getIssuer();
    } catch (JWTDecodeException exception) {
      return resultBuilder(false, ResponseCodes.TOKEN_INVALID, 0, ResponseMessages.TOKEN_INVALID, "");
    }
    // 检查 token 是否有效
    JedisPool pool = new JedisPool(new JedisPoolConfig(), "localhost");
    try (Jedis jedis = pool.getResource()) {
      String tokenSaved = jedis.get(userId);
      if (!token.equals(tokenSaved)) {
        return resultBuilder(false, ResponseCodes.TOKEN_TIMEOUT, Integer.parseInt(userId), ResponseMessages.TOKEN_TIMEOUT, "");
      }
      jedis.expire(String.valueOf(userId), 3600);
    } catch (JedisException e) {
      e.printStackTrace();
      return resultBuilder(false, ResponseCodes.TOKEN_TIMEOUT, Integer.parseInt(userId), ResponseMessages.TOKEN_TIMEOUT, "");
    } finally {
      pool.close();
    }
    // 更新密码
    int[] updated = jdbcTemplate.batchUpdate("UPDATE `account`" +
      " SET password = " + new_password +
      " WHERE user_id = " + Integer.parseInt(userId) +
      " AND password = " + password + ";");
    if (updated[0] != 1) {
      return resultBuilder(false, ResponseCodes.PASSWORD_UPDATED_FAILURE, Integer.parseInt(userId), ResponseMessages.PASSWORD_UPDATED_FAILURE, "");
    }
    return resultBuilder(true, ResponseCodes.PASSWORD_UPDATED_SUCCESS, Integer.parseInt(userId), ResponseMessages.PASSWORD_UPDATED_SUCCESS, token);
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
