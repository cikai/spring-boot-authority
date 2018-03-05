package me.cikai.common;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * @author cikai
 */
public class RedisHelper {

  private JedisPool jedisPool;

  public RedisHelper() {
  }

  private static class RedisHolder {
    public final static RedisHelper instance = new RedisHelper();
  }

  public RedisHelper getInstance() {
    return RedisHolder.instance;
  }

  private void initJedisPool() {
    jedisPool = new JedisPool(new JedisPoolConfig(), "localhost");
  }

  public Jedis getJedis() {
    if (jedisPool == null) {
      initJedisPool();
    }
    return jedisPool.getResource();
  }

}
