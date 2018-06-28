package org.cocolian.id.redis;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.exceptions.JedisDataException;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

/**
 * Implementation of the Cocolian Redis interface for Jedis.
 */
public class JedisCocolian implements Redis {
  private JedisPool jedisPool;
  private String host;
  private int port;
  private int localId;
  private GenericObjectPoolConfig genericObjectPoolConfig;
  private int connectionTimeout;
  private int soTimeout;
  private String password;
  private int database;
  public JedisCocolian() { }

  /**
   * Create an instance of JedisCocolian from an existing JedisPool instance.
   *
   * @param jedisPool An existing JedisPool instance you have configured that can be used for the ID generation.
   */
  public JedisCocolian(final JedisPool jedisPool) {
    this.jedisPool = jedisPool;
  }

  public void InitJedisPool(){

    this.jedisPool = new JedisPool(
            this.genericObjectPoolConfig,
            this.host,
            this.port,
            this.connectionTimeout,
            this.soTimeout,
            this.password,
            this.database,
            null);
  }

  /**
   * Getter for the JedisPool instance used for the ID generation.
   *
   * @return The instance of JedisPool that was either passed or created from the host and port given.
   */
  public JedisPool getJedisPool() {
    return jedisPool;
  }

  /**
   * Load the given Lua script into Redis.
   *
   * @param luaScript The Lua script to load into Redis.
   * @return The SHA of the loaded Lua script.
   */
  @Override
  public String loadLuaScript(final String luaScript) {
    return withJedis(jedis -> jedis.scriptLoad(luaScript));
  }

  /**
   * Execute the Lua script with the given SHA, passing the given list of arguments.
   *
   * @param luaScriptSha The SHA of the Lua script to execute.
   * @param arguments The arguments to pass to the Lua script.
   * @return The optional result of executing the Lua script. Absent if the Lua script referenced by the SHA was missing
   * when it was attempted to be executed.
   */
  @Override
  public Optional<CocolianRedisResponse> evalLuaScript(final String luaScriptSha, final List<String> arguments) {
    return withJedis(jedis -> {
      String[] args = arguments.toArray(new String[arguments.size()]);

      try {
        @SuppressWarnings("unchecked")
        List<Long> results = (List<Long>) jedis.evalsha(luaScriptSha, arguments.size(), args);
        return Optional.of(new CocolianRedisResponse(results));
      } catch (JedisDataException e) {
        return Optional.empty();
      }
    });
  }


  /**
   * Request a Jedis resource from the pool, execute the given callback passing the resource, and then ensure the
   * resource is always returned to the pool regardless of success or failure in the callback.
   * @param callback The callback to pass the Jedis resource to so some operation can be done with it.
   * @param <T> The type that will be returned by the callback.
   * @return The value returned by the callback.
   */
  private <T> T withJedis(final Function<Jedis, T> callback) {
    try (Jedis jedis = jedisPool.getResource()) {
      return callback.apply(jedis);
    }
  }


  public Optional<String> get(final String key) {
    return withJedis(jedis -> {
      try {
        return Optional.of(jedis.get(key));
      } catch (JedisDataException e) {
        return Optional.empty();
      }
    });
  }

  public void set(final String key, final String value) {
    jedisPool.getResource().set(key,value);
  }

  public String getHost() {
    return host;
  }
  public int getPort() {
    return port;
  }
  public void setPort(int port) {
    this.port = port;
  }
  public void setHost(String host) {
    this.host = host;
  }
  public int getLocalId() {
    return localId;
  }
  public void setLocalId(int localId) {
    this.localId = localId;
  }
  public GenericObjectPoolConfig getGenericObjectPoolConfig() {
    return genericObjectPoolConfig;
  }
  public void setGenericObjectPoolConfig(GenericObjectPoolConfig genericObjectPoolConfig) {
    this.genericObjectPoolConfig = genericObjectPoolConfig;
  }
  public int getConnectionTimeout() {
    return connectionTimeout;
  }
  public void setConnectionTimeout(int connectionTimeout) {
    this.connectionTimeout = connectionTimeout;
  }
  public int getSoTimeout() {
    return soTimeout;
  }
  public void setSoTimeout(int soTimeout) {
    this.soTimeout = soTimeout;
  }
  public String getPassword() {
    return password;
  }
  public void setPassword(String password) {
    this.password = password;
  }
  public int getDatabase() {
    return database;
  }
  public void setDatabase(int database) {
    this.database = database;
  }
}
