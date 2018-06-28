package org.cocolian.id.redis;

import java.util.List;
import java.util.Optional;

/**
 * This interface defines operations that the ID generator needs in order to be able to work. The most common Redis
 * library, Jedis, has an interface for it already in the cocolian-jedis project, but you can use any library you want
 * simply by implementing this interface around it and passing the instances to the CocolianIdGenerator via a
 * RoundRobinRedisPool instance.
 */
public interface Redis {
  String loadLuaScript(final String luaScript);
  Optional<CocolianRedisResponse> evalLuaScript(final String luaScriptSha, final List<String> arguments);
}
