package me.cikai.api;

import me.cikai.common.CommonUtils;
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
    String userId = CommonUtils.getUserIdByToken(token);
    if (userId == "" || userId.isEmpty()) {
      return CommonUtils.resultBuilder(false, ResponseCodes.TOKEN_INVALID, 0, ResponseMessages.TOKEN_INVALID, "");
    }
    // 检查 token 是否有效
    JedisPool pool = new JedisPool(new JedisPoolConfig(), "localhost");
    try (Jedis jedis = pool.getResource()) {
      String tokenSaved = jedis.get(userId);
      if (!token.equals(tokenSaved)) {
        return CommonUtils.resultBuilder(false, ResponseCodes.TOKEN_TIMEOUT, Integer.parseInt(userId), ResponseMessages.TOKEN_TIMEOUT, "");
      }
      jedis.expire(String.valueOf(userId), 3600);
      jedis.close();
    } catch (JedisException e) {
      e.printStackTrace();
      return CommonUtils.resultBuilder(false, ResponseCodes.TOKEN_TIMEOUT, Integer.parseInt(userId), ResponseMessages.TOKEN_TIMEOUT, "");
    } finally {
      pool.close();
    }
    // 更新密码
    int[] updated = jdbcTemplate.batchUpdate("UPDATE `account`" +
      " SET password = \"" + new_password +
      "\" WHERE user_id = " + Integer.parseInt(userId) +
      " AND password = \"" + password + "\";");
    if (updated[0] != 1) {
      return CommonUtils.resultBuilder(false, ResponseCodes.PASSWORD_UPDATED_FAILURE, Integer.parseInt(userId), ResponseMessages.PASSWORD_UPDATED_FAILURE, "");
    }
    return CommonUtils.resultBuilder(true, ResponseCodes.PASSWORD_UPDATED_SUCCESS, Integer.parseInt(userId), ResponseMessages.PASSWORD_UPDATED_SUCCESS, token);
  }

}
