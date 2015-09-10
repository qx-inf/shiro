package com.qx.inf.shiro.seq;

import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.mgt.eis.SessionIdGenerator;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;

import redis.clients.jedis.Protocol;

/**
 * @author 刘飞 E-mail:liufei_it@126.com
 *
 * @version 1.0.0
 * @since 2015年8月25日 下午5:16:59
 */
public class SimpleSessionIdGenerator implements SessionIdGenerator, InitializingBean {

    private static final String QX_SESSION_ID_HOLDER_KEY = "qx:session:id:h-jx";

    private static final String KEY_DOT = "--";

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
    public String generateId(Session session) {
        String val = redisTemplate.opsForValue().get(QX_SESSION_ID_HOLDER_KEY);
        if (val == null) {
            redisTemplate.opsForValue().set(QX_SESSION_ID_HOLDER_KEY, "2");
            return UUID.randomUUID() + KEY_DOT + "1";
        }
        long seq = NumberUtils.toLong(val, 2L);
        redisTemplate.opsForValue().set(QX_SESSION_ID_HOLDER_KEY, String.valueOf(seq + 1));
        return UUID.randomUUID() + KEY_DOT + val;
    }

    public void setRedisTemplate(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
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
}
