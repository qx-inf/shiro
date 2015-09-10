package com.qx.inf.shiro.security;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.session.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;

import com.qx.inf.shiro.session.SimpleSession;
import com.qx.inf.shiro.utils.SerializeUtils;

import redis.clients.jedis.Protocol;

/**
 * @author 刘飞 E-mail:liufei_it@126.com
 *
 * @version 1.0.0
 * @since 2015年8月25日 下午4:39:45
 */
public class RedisSessionRepository implements SessionRepository, InitializingBean {

    private static final Logger log = LoggerFactory.getLogger(RedisSessionRepository.class);

    private static final String SESSION_PRIFIX = "qx-session-jx:";

    private static final int SESSION_TIME_OUT = 1800;// 秒

    private String sessionChunk;

    private StringRedisTemplate redisTemplate;

    private String hostName = "localhost";

    private String password = StringUtils.EMPTY;

    private int port = Protocol.DEFAULT_PORT;

    private int timeout = Protocol.DEFAULT_TIMEOUT * 2;

    private int dbIndex = Protocol.DEFAULT_DATABASE;

    @Override
    public void afterPropertiesSet() throws Exception {
        if (redisTemplate == null) {
            JedisConnectionFactory connectionFactory = new JedisConnectionFactory();
            connectionFactory.setUsePool(true);
            if (dbIndex > 0) {
                connectionFactory.setDatabase(dbIndex);
            }
            connectionFactory.setHostName(hostName);
            connectionFactory.setPort(port);
            connectionFactory.setPassword(password);
            connectionFactory.setTimeout(timeout);
            connectionFactory.afterPropertiesSet();
            redisTemplate = new StringRedisTemplate(connectionFactory);
            redisTemplate.afterPropertiesSet();
        }
    }

    @Override
    public Collection<Session> getActiveSessions() {
        Set<String> sessionIds = redisTemplate.keys(SESSION_PRIFIX + sessionChunk + ":*");
        Set<Session> sessions = new HashSet<Session>();
        if (sessionIds != null && sessionIds.size() > 0) {
            for (String key : sessionIds) {
                try {
                    SimpleSession session = SerializeUtils.deserialize(redisTemplate.opsForValue().get(key), SimpleSession.class);
                    sessions.add(session);
                } catch (Throwable e) {
                    continue;
                }
            }
        }
        return sessions;
    }

    @Override
    public String add(final SimpleSession session) {
        String key = getKey(session);
        try {
            redisTemplate.opsForValue().set(key, SerializeUtils.serialize(session), SESSION_TIME_OUT, TimeUnit.MINUTES);
        } catch (Throwable e) {
            log.error("Add Session[" + key + "] Error.", e);
        }
        return session.getId();
    }

    @Override
    public SimpleSession get(String sessionId) {
        String key = getKey(sessionId);
        try {
            String value = redisTemplate.opsForValue().get(key);
            if (value == null) {
                return null;
            }
            return SerializeUtils.deserialize(value, SimpleSession.class);
        } catch (Throwable e) {
            log.error("Get Session[" + key + "] Error.", e);
        }
        return null;
    }

    @Override
    public void remove(final SimpleSession session) {
        String key = getKey(session);
        redisTemplate.delete(key);
    }

    @Override
    public void touch(final SimpleSession session) {
        String key = getKey(session);
        redisTemplate.expire(key, SESSION_TIME_OUT, TimeUnit.MINUTES);
    }

    public void setRedisTemplate(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void setSessionChunk(String sessionChunk) {
        this.sessionChunk = sessionChunk;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public void setDbIndex(int dbIndex) {
        this.dbIndex = dbIndex;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    private String getKey(String sessionId) {
        return SESSION_PRIFIX + sessionChunk + ":" + sessionId;
    }

    private String getKey(final SimpleSession session) {
        return SESSION_PRIFIX + sessionChunk + ":" + session.getId();
    }
}
