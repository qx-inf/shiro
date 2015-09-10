package com.qx.inf.shiro.security;

import java.io.Serializable;
import java.util.Collection;
import java.util.concurrent.TimeUnit;

import org.apache.shiro.session.Session;
import org.apache.shiro.session.UnknownSessionException;
import org.apache.shiro.session.mgt.eis.SessionDAO;
import org.apache.shiro.session.mgt.eis.SessionIdGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.qx.inf.shiro.session.SimpleSession;

/**
 * @author 刘飞 E-mail:liufei_it@126.com
 *
 * @version 1.0.0
 * @since 2015年8月25日 下午5:05:14
 */
public class DistributeSessionDAO implements SessionDAO, InitializingBean {

    private static final Logger log = LoggerFactory.getLogger(DistributeSessionDAO.class);

    private SessionRepository sessionRepository;

    private SessionIdGenerator sessionIdGenerator;
    
    private static final int LOCAL_CACHE_TIMEOUT = 30;// 本地cache默认保存30秒

    //本地二级缓存
    private LoadingCache<String, SimpleSession> localCache;

    @Override
    public void afterPropertiesSet() throws Exception {
        localCache = CacheBuilder.newBuilder().expireAfterAccess(LOCAL_CACHE_TIMEOUT, TimeUnit.SECONDS)
                .initialCapacity(200).maximumSize(2000).build(new CacheLoader<String, SimpleSession>() {
                    @Override
                    public SimpleSession load(String sessionId) throws Exception {
                        try {
                            return sessionRepository.get(sessionId);
                        } catch (Exception e) {
                            log.error("Can't load Session from SessionRepository.", e);
                        }
                        return null;
                    }
                });
    }

    @Override
    public Serializable create(final Session session) {
        final SimpleSession simpleSession = (SimpleSession) session;
        String sessionId = generateSessionId(simpleSession);
        simpleSession.setId(sessionId);
        log.debug("Create Session[{}].", sessionId);
        localCache.put(sessionId, simpleSession);
        sessionRepository.add(simpleSession);
        return sessionId;
    }

    @Override
    public void delete(final Session session) {
        final SimpleSession simpleSession = (SimpleSession) session;
        String sessionId = simpleSession.getId();
        log.debug("Delete Session[{}].", sessionId);
        localCache.invalidate(sessionId);
        sessionRepository.remove(simpleSession);
    }

    @Override
    public Collection<Session> getActiveSessions() {
        return sessionRepository.getActiveSessions();
    }

    @Override
    public SimpleSession readSession(Serializable sessionId) throws UnknownSessionException {
        SimpleSession session = null;
        try {
            session = localCache.get((String) sessionId);
            if (session != null) return session;
        } catch (Throwable e) {}
        log.info("Loading Session[{}] from SessionRepository.",sessionId);
        session = sessionRepository.get((String) sessionId);
        if (session == null) {
            throw new UnknownSessionException("None Session[" + sessionId + "].");
        }
        localCache.put(session.getId(), session);
        return session;
    }

    @Override
    public void update(final Session session) throws UnknownSessionException {
        final SimpleSession simpleSession = (SimpleSession) session;
        String sessionId = simpleSession.getId();
        log.debug("Update Session[{}].", sessionId);
        localCache.put(sessionId, simpleSession);
        sessionRepository.add(simpleSession);
    }

    protected String generateSessionId(final SimpleSession session) {
        if (this.sessionIdGenerator == null) {
            throw new IllegalStateException("'SessionIdGenerator' attribute required.");
        }
        return (String) sessionIdGenerator.generateId(session);
    }

    public void setSessionRepository(SessionRepository sessionRepository) {
        this.sessionRepository = sessionRepository;
    }

    public void setSessionIdGenerator(SessionIdGenerator sessionIdGenerator) {
        this.sessionIdGenerator = sessionIdGenerator;
    }
}