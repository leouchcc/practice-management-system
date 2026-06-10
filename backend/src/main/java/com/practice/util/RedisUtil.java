package com.practice.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Component
public class RedisUtil {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    public void set(String key, Object value) {
        try {
            redisTemplate.opsForValue().set(key, value);
        } catch (Exception e) {
            // Redis连接失败，忽略错误，继续执行
            e.printStackTrace();
        }
    }

    public void set(String key, Object value, long timeout, TimeUnit unit) {
        try {
            redisTemplate.opsForValue().set(key, value, timeout, unit);
        } catch (Exception e) {
            // Redis连接失败，忽略错误，继续执行
            e.printStackTrace();
        }
    }

    public Object get(String key) {
        try {
            return redisTemplate.opsForValue().get(key);
        } catch (Exception e) {
            // Redis连接失败，返回null，继续执行
            e.printStackTrace();
            return null;
        }
    }

    public Boolean delete(String key) {
        try {
            return redisTemplate.delete(key);
        } catch (Exception e) {
            // Redis连接失败，返回false，继续执行
            e.printStackTrace();
            return false;
        }
    }

    public Long delete(Collection<String> keys) {
        try {
            return redisTemplate.delete(keys);
        } catch (Exception e) {
            // Redis连接失败，返回0，继续执行
            e.printStackTrace();
            return 0L;
        }
    }

    public Long deleteByPattern(String pattern) {
        try {
            Set<String> keys = redisTemplate.keys(pattern);
            if (keys != null && !keys.isEmpty()) {
                return redisTemplate.delete(keys);
            }
            return 0L;
        } catch (Exception e) {
            // Redis连接失败，返回0，继续执行
            e.printStackTrace();
            return 0L;
        }
    }

    public Boolean hasKey(String key) {
        try {
            return redisTemplate.hasKey(key);
        } catch (Exception e) {
            // Redis连接失败，返回false，继续执行
            e.printStackTrace();
            return false;
        }
    }

    public Boolean expire(String key, long timeout, TimeUnit unit) {
        try {
            return redisTemplate.expire(key, timeout, unit);
        } catch (Exception e) {
            // Redis连接失败，返回false，继续执行
            e.printStackTrace();
            return false;
        }
    }

    public Long getExpire(String key) {
        try {
            return redisTemplate.getExpire(key);
        } catch (Exception e) {
            // Redis连接失败，返回-1，继续执行
            e.printStackTrace();
            return -1L;
        }
    }

    public Long increment(String key, long delta) {
        try {
            return redisTemplate.opsForValue().increment(key, delta);
        } catch (Exception e) {
            // Redis连接失败，返回0，继续执行
            e.printStackTrace();
            return 0L;
        }
    }

    public Long decrement(String key, long delta) {
        try {
            return redisTemplate.opsForValue().decrement(key, delta);
        } catch (Exception e) {
            // Redis连接失败，返回0，继续执行
            e.printStackTrace();
            return 0L;
        }
    }

    public Object hGet(String key, String hashKey) {
        try {
            return redisTemplate.opsForHash().get(key, hashKey);
        } catch (Exception e) {
            // Redis连接失败，返回null，继续执行
            e.printStackTrace();
            return null;
        }
    }

    public Map<Object, Object> hGetAll(String key) {
        try {
            return redisTemplate.opsForHash().entries(key);
        } catch (Exception e) {
            // Redis连接失败，返回空Map，继续执行
            e.printStackTrace();
            return new java.util.HashMap<>();
        }
    }

    public void hSet(String key, String hashKey, Object value) {
        try {
            redisTemplate.opsForHash().put(key, hashKey, value);
        } catch (Exception e) {
            // Redis连接失败，忽略错误，继续执行
            e.printStackTrace();
        }
    }

    public void hSetAll(String key, Map<String, Object> map) {
        try {
            redisTemplate.opsForHash().putAll(key, map);
        } catch (Exception e) {
            // Redis连接失败，忽略错误，继续执行
            e.printStackTrace();
        }
    }

    public Long hDelete(String key, Object... hashKeys) {
        try {
            return redisTemplate.opsForHash().delete(key, hashKeys);
        } catch (Exception e) {
            // Redis连接失败，返回0，继续执行
            e.printStackTrace();
            return 0L;
        }
    }

    public Boolean hHasKey(String key, String hashKey) {
        try {
            return redisTemplate.opsForHash().hasKey(key, hashKey);
        } catch (Exception e) {
            // Redis连接失败，返回false，继续执行
            e.printStackTrace();
            return false;
        }
    }

    public Set<Object> hKeys(String key) {
        try {
            return redisTemplate.opsForHash().keys(key);
        } catch (Exception e) {
            // Redis连接失败，返回空Set，继续执行
            e.printStackTrace();
            return new java.util.HashSet<>();
        }
    }

    public Long hSize(String key) {
        try {
            return redisTemplate.opsForHash().size(key);
        } catch (Exception e) {
            // Redis连接失败，返回0，继续执行
            e.printStackTrace();
            return 0L;
        }
    }

    public List<Object> lRange(String key, long start, long end) {
        try {
            return redisTemplate.opsForList().range(key, start, end);
        } catch (Exception e) {
            // Redis连接失败，返回空List，继续执行
            e.printStackTrace();
            return new java.util.ArrayList<>();
        }
    }

    public Long lSize(String key) {
        try {
            return redisTemplate.opsForList().size(key);
        } catch (Exception e) {
            // Redis连接失败，返回0，继续执行
            e.printStackTrace();
            return 0L;
        }
    }

    public Object lIndex(String key, long index) {
        try {
            return redisTemplate.opsForList().index(key, index);
        } catch (Exception e) {
            // Redis连接失败，返回null，继续执行
            e.printStackTrace();
            return null;
        }
    }

    public Long lPush(String key, Object value) {
        try {
            return redisTemplate.opsForList().leftPush(key, value);
        } catch (Exception e) {
            // Redis连接失败，返回0，继续执行
            e.printStackTrace();
            return 0L;
        }
    }

    public Long lPushAll(String key, Collection<Object> values) {
        try {
            return redisTemplate.opsForList().leftPushAll(key, values);
        } catch (Exception e) {
            // Redis连接失败，返回0，继续执行
            e.printStackTrace();
            return 0L;
        }
    }

    public Long rPush(String key, Object value) {
        try {
            return redisTemplate.opsForList().rightPush(key, value);
        } catch (Exception e) {
            // Redis连接失败，返回0，继续执行
            e.printStackTrace();
            return 0L;
        }
    }

    public Long rPushAll(String key, Collection<Object> values) {
        try {
            return redisTemplate.opsForList().rightPushAll(key, values);
        } catch (Exception e) {
            // Redis连接失败，返回0，继续执行
            e.printStackTrace();
            return 0L;
        }
    }

    public void lSet(String key, long index, Object value) {
        try {
            redisTemplate.opsForList().set(key, index, value);
        } catch (Exception e) {
            // Redis连接失败，忽略错误，继续执行
            e.printStackTrace();
        }
    }

    public Long lRemove(String key, long count, Object value) {
        try {
            return redisTemplate.opsForList().remove(key, count, value);
        } catch (Exception e) {
            // Redis连接失败，返回0，继续执行
            e.printStackTrace();
            return 0L;
        }
    }

    public Set<Object> sMembers(String key) {
        try {
            return redisTemplate.opsForSet().members(key);
        } catch (Exception e) {
            // Redis连接失败，返回空Set，继续执行
            e.printStackTrace();
            return new java.util.HashSet<>();
        }
    }

    public Long sAdd(String key, Object... values) {
        try {
            return redisTemplate.opsForSet().add(key, values);
        } catch (Exception e) {
            // Redis连接失败，返回0，继续执行
            e.printStackTrace();
            return 0L;
        }
    }

    public Long sSize(String key) {
        try {
            return redisTemplate.opsForSet().size(key);
        } catch (Exception e) {
            // Redis连接失败，返回0，继续执行
            e.printStackTrace();
            return 0L;
        }
    }

    public Boolean sIsMember(String key, Object value) {
        try {
            return redisTemplate.opsForSet().isMember(key, value);
        } catch (Exception e) {
            // Redis连接失败，返回false，继续执行
            e.printStackTrace();
            return false;
        }
    }

    public Long sRemove(String key, Object... values) {
        try {
            return redisTemplate.opsForSet().remove(key, values);
        } catch (Exception e) {
            // Redis连接失败，返回0，继续执行
            e.printStackTrace();
            return 0L;
        }
    }

    public Set<Object> zRange(String key, long start, long end) {
        try {
            return redisTemplate.opsForZSet().range(key, start, end);
        } catch (Exception e) {
            // Redis连接失败，返回空Set，继续执行
            e.printStackTrace();
            return new java.util.HashSet<>();
        }
    }

    public Set<Object> zReverseRange(String key, long start, long end) {
        try {
            return redisTemplate.opsForZSet().reverseRange(key, start, end);
        } catch (Exception e) {
            // Redis连接失败，返回空Set，继续执行
            e.printStackTrace();
            return new java.util.HashSet<>();
        }
    }

    public Boolean zAdd(String key, Object value, double score) {
        try {
            return redisTemplate.opsForZSet().add(key, value, score);
        } catch (Exception e) {
            // Redis连接失败，返回false，继续执行
            e.printStackTrace();
            return false;
        }
    }

    public Long zRemove(String key, Object... values) {
        try {
            return redisTemplate.opsForZSet().remove(key, values);
        } catch (Exception e) {
            // Redis连接失败，返回0，继续执行
            e.printStackTrace();
            return 0L;
        }
    }

    public Long zSize(String key) {
        try {
            return redisTemplate.opsForZSet().size(key);
        } catch (Exception e) {
            // Redis连接失败，返回0，继续执行
            e.printStackTrace();
            return 0L;
        }
    }

    public Long zRank(String key, Object value) {
        try {
            return redisTemplate.opsForZSet().rank(key, value);
        } catch (Exception e) {
            // Redis连接失败，返回-1，继续执行
            e.printStackTrace();
            return -1L;
        }
    }

    public Long zReverseRank(String key, Object value) {
        try {
            return redisTemplate.opsForZSet().reverseRank(key, value);
        } catch (Exception e) {
            // Redis连接失败，返回-1，继续执行
            e.printStackTrace();
            return -1L;
        }
    }
}
