package com.qx.inf.shiro.session;

import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;

import com.qx.inf.shiro.security.DistributeSessionDAO;
import com.qx.inf.shiro.security.RedisSessionRepository;
import com.qx.inf.shiro.seq.HttpSessionIdGenerator;

import redis.clients.jedis.Protocol;

/**
 * @author 刘飞 E-mail:liufei_it@126.com
 *
 * @version 1.0.0
 * @since 2015年8月26日 上午9:44:03
 */
public class SimpleSessionManager extends DefaultWebSessionManager implements InitializingBean {

    protected final Logger log = LoggerFactory.getLogger(getClass());

    private String hostName = "localhost";

    private String password = StringUtils.EMPTY;

    private int port = Protocol.DEFAULT_PORT;

    private int timeout = Protocol.DEFAULT_TIMEOUT * 2;

    private int dbIndex = Protocol.DEFAULT_DATABASE;

    private String sessionChunk;

    @Override
    public void afterPropertiesSet() throws Exception {
        RedisSessionRepository sessionRepository = new RedisSessionRepository();
        sessionRepository.setDbIndex(dbIndex);
        sessionRepository.setHostName(hostName);
        sessionRepository.setPassword(password);
        sessionRepository.setPort(port);
        sessionRepository.setTimeout(timeout);
        sessionRepository.setSessionChunk(sessionChunk);
        sessionRepository.afterPropertiesSet();
        DistributeSessionDAO distributeSessionDAO = new DistributeSessionDAO();
        distributeSessionDAO.setSessionIdGenerator(new HttpSessionIdGenerator());
        distributeSessionDAO.setSessionRepository(sessionRepository);
        distributeSessionDAO.afterPropertiesSet();
        setSessionDAO(distributeSessionDAO);
        setSessionFactory(new SimpleSessionFactory());
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
}