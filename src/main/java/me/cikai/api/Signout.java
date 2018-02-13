package me.cikai.api;

import me.cikai.common.CommonUtils;
import me.cikai.common.ResponseCodes;
import me.cikai.common.ResponseMessages;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.exceptions.JedisException;

/**
 * @author cikai
 * Sign Out
 * 用户登出
 */
@RestController
public class Signout {

  @RequestMapping("/signout")
  public String signout(@RequestHeader("token") String token) {
    String userId = CommonUtils.getUserIdByToken(token);
    if (userId == "" || userId.isEmpty()) {
      return CommonUtils.resultBuilder(false, ResponseCodes.TOKEN_INVALID, 0, ResponseMessages.TOKEN_INVALID, "");
    }
    JedisPool pool = new JedisPool(new JedisPoolConfig(), "localhost");
    try (Jedis jedis = pool.getResource()) {
      if (jedis.get(userId) == null || !jedis.get(userId).equals(token)) {
        return CommonUtils.resultBuilder(false, ResponseCodes.TOKEN_INVALID, 0, ResponseMessages.TOKEN_INVALID, "");
      }
      jedis.del(userId);
      jedis.close();
    } catch (JedisException e) {
      e.printStackTrace();
      return CommonUtils.resultBuilder(false, ResponseCodes.SIGNOUT_FAILURE, Integer.parseInt(userId), ResponseMessages.SIGNOUT_FAILURE, "");
    } finally {
      pool.close();
    }
    return CommonUtils.resultBuilder(true, ResponseCodes.SIGNOUT_SUCCESS, Integer.parseInt(userId), ResponseMessages.SIGNOUT_SUCCESS, "");
  }

}
