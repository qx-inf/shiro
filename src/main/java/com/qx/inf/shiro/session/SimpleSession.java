package com.qx.inf.shiro.session;

import java.io.Serializable;
import java.text.DateFormat;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.shiro.session.ExpiredSessionException;
import org.apache.shiro.session.InvalidSessionException;
import org.apache.shiro.session.StoppedSessionException;
import org.apache.shiro.session.mgt.DefaultSessionManager;
import org.apache.shiro.session.mgt.ValidatingSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.qx.inf.shiro.attr.AttrObject;

/**
 * 定义Shiro-Session 方便进行JSON序列号和反序列化.
 * 
 * @author 刘飞 E-mail:liufei_it@126.com
 *
 * @version 1.0.0
 * @since 2015年8月25日 下午2:33:02
 */
public class SimpleSession implements ValidatingSession, Serializable {

    private static final long serialVersionUID = -7125642695178165650L;

    private transient static final Logger log = LoggerFactory.getLogger(SimpleSession.class);

    protected static final long MILLIS_PER_SECOND = 1000;
    protected static final long MILLIS_PER_MINUTE = 60 * MILLIS_PER_SECOND;
    protected static final long MILLIS_PER_HOUR = 60 * MILLIS_PER_MINUTE;

    private transient String id;
    private transient Date startTimestamp;
    private transient Date stopTimestamp;
    private transient Date lastAccessTime;
    private transient long timeout;
    private transient boolean expired;
    private transient String host;
    private transient Map<Object, AttrObject> attributes;

    public SimpleSession() {
        this.timeout = DefaultSessionManager.DEFAULT_GLOBAL_SESSION_TIMEOUT;
        this.startTimestamp = new Date();
        this.lastAccessTime = this.startTimestamp;
    }

    public SimpleSession(String host) {
        this();
        this.host = host;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getStartTimestamp() {
        return startTimestamp;
    }

    public void setStartTimestamp(Date startTimestamp) {
        this.startTimestamp = startTimestamp;
    }

    public Date getStopTimestamp() {
        return stopTimestamp;
    }

    public void setStopTimestamp(Date stopTimestamp) {
        this.stopTimestamp = stopTimestamp;
    }

    public Date getLastAccessTime() {
        return lastAccessTime;
    }

    public void setLastAccessTime(Date lastAccessTime) {
        this.lastAccessTime = lastAccessTime;
    }

    public boolean isExpired() {
        return expired;
    }

    public void setExpired(boolean expired) {
        this.expired = expired;
    }

    public long getTimeout() {
        return timeout;
    }

    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    @JsonSerialize(using = SessionAttributesSerializer.class)
    public Map<Object, AttrObject> getAttributes() {
        return attributes;
    }

    @JsonDeserialize(using = SessionAttributesDeserializer.class)
    public void setAttributes(Map<Object, AttrObject> attributes) {
        this.attributes = attributes;
    }

    public void touch() {
        this.lastAccessTime = new Date();
    }

    public void stop() {
        if (this.stopTimestamp == null) {
            this.stopTimestamp = new Date();
        }
    }

    protected boolean isStopped() {
        return getStopTimestamp() != null;
    }

    protected void expire() {
        stop();
        this.expired = true;
    }

    public boolean isValid() {
        return !isStopped() && !isExpired();
    }

    protected boolean isTimedOut() {
        if (isExpired()) {
            return true;
        }
        long timeout = getTimeout();
        if (timeout >= 0l) {
            Date lastAccessTime = getLastAccessTime();
            if (lastAccessTime == null) {
                String msg =
                        "session.lastAccessTime for session with id [" + getId() + "] is null.  This value must be set at " + "least once, preferably at least upon instantiation.  Please check the "
                                + getClass().getName() + " implementation and ensure " + "this value will be set (perhaps in the constructor?)";
                throw new IllegalStateException(msg);
            }
            long expireTimeMillis = System.currentTimeMillis() - timeout;
            Date expireTime = new Date(expireTimeMillis);
            return lastAccessTime.before(expireTime);
        } else {
            if (log.isTraceEnabled()) {
                log.trace("No timeout for session with id [" + getId() + "].  Session is not considered expired.");
            }
        }
        return false;
    }

    public void validate() throws InvalidSessionException {
        if (isStopped()) {
            String msg = "Session with id [" + getId() + "] has been " + "explicitly stopped.  No further interaction under this session is " + "allowed.";
            throw new StoppedSessionException(msg);
        }
        if (isTimedOut()) {
            expire();
            Date lastAccessTime = getLastAccessTime();
            long timeout = getTimeout();
            Serializable sessionId = getId();
            DateFormat df = DateFormat.getInstance();
            String msg = "Session with id [" + sessionId + "] has expired. " + "Last access time: " + df.format(lastAccessTime) + ".  Current time: " + df.format(new Date())
                    + ".  Session timeout is set to " + timeout / MILLIS_PER_SECOND + " seconds (" + timeout / MILLIS_PER_MINUTE + " minutes)";
            if (log.isTraceEnabled()) {
                log.trace(msg);
            }
            throw new ExpiredSessionException(msg);
        }
    }

    private Map<Object, AttrObject> getAttributesLazy() {
        Map<Object, AttrObject> attributes = getAttributes();
        if (attributes == null) {
            attributes = new HashMap<Object, AttrObject>();
            setAttributes(attributes);
        }
        return attributes;
    }

    @JsonIgnore
    public Collection<Object> getAttributeKeys() throws InvalidSessionException {
        Map<Object, AttrObject> attributes = getAttributes();
        if (attributes == null) {
            return Collections.emptySet();
        }
        return attributes.keySet();
    }

    public Object getAttribute(Object key) {
        Map<Object, AttrObject> attributes = getAttributes();
        if (attributes == null) {
            return null;
        }
        AttrObject attribute = attributes.get(key);
        if (attribute == null) {
            return null;
        }
        return attribute.readValueAsType();
    }

    public void setAttribute(Object key, Object value) {
        if (value == null) {
            removeAttribute(key);
        } else {
            getAttributesLazy().put(key, new AttrObject(value));
        }
    }

    public Object removeAttribute(Object key) {
        Map<Object, AttrObject> attributes = getAttributes();
        if (attributes == null) {
            return null;
        }
        AttrObject attribute = attributes.remove(key);
        if (attribute == null) {
            return null;
        }
        return attribute.readValueAsType();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof SimpleSession) {
            SimpleSession other = (SimpleSession) obj;
            Serializable thisId = getId();
            Serializable otherId = other.getId();
            if (thisId != null && otherId != null) {
                return thisId.equals(otherId);
            } else {
                return onEquals(other);
            }
        }
        return false;
    }

    protected boolean onEquals(SimpleSession ss) {
        return (getStartTimestamp() != null ? getStartTimestamp().equals(ss.getStartTimestamp()) : ss.getStartTimestamp() == null)
                && (getStopTimestamp() != null ? getStopTimestamp().equals(ss.getStopTimestamp()) : ss.getStopTimestamp() == null)
                && (getLastAccessTime() != null ? getLastAccessTime().equals(ss.getLastAccessTime()) : ss.getLastAccessTime() == null) && (getTimeout() == ss.getTimeout())
                && (isExpired() == ss.isExpired()) && (getHost() != null ? getHost().equals(ss.getHost()) : ss.getHost() == null)
                && (getAttributes() != null ? getAttributes().equals(ss.getAttributes()) : ss.getAttributes() == null);
    }

    @Override
    public int hashCode() {
        Serializable id = getId();
        if (id != null) {
            return id.hashCode();
        }
        int hashCode = getStartTimestamp() != null ? getStartTimestamp().hashCode() : 0;
        hashCode = 31 * hashCode + (getStopTimestamp() != null ? getStopTimestamp().hashCode() : 0);
        hashCode = 31 * hashCode + (getLastAccessTime() != null ? getLastAccessTime().hashCode() : 0);
        hashCode = 31 * hashCode + Long.valueOf(Math.max(getTimeout(), 0)).hashCode();
        hashCode = 31 * hashCode + Boolean.valueOf(isExpired()).hashCode();
        hashCode = 31 * hashCode + (getHost() != null ? getHost().hashCode() : 0);
        hashCode = 31 * hashCode + (getAttributes() != null ? getAttributes().hashCode() : 0);
        return hashCode;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getName()).append(",id=").append(getId());
        return sb.toString();
    }
}