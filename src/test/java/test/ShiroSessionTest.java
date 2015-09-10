package test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.shiro.subject.SimplePrincipalCollection;
import org.apache.shiro.subject.support.DefaultSubjectContext;
import org.apache.shiro.web.util.SavedRequest;
import org.junit.Test;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;

import com.qx.inf.shiro.security.RedisSessionRepository;
import com.qx.inf.shiro.seq.SimpleSessionIdGenerator;
import com.qx.inf.shiro.session.SessionAttributes;
import com.qx.inf.shiro.session.SimpleHttpRequest;
import com.qx.inf.shiro.session.SimpleSession;
import com.qx.inf.shiro.utils.SerializeUtils;

/**
 * @author 刘飞 E-mail:liufei_it@126.com
 *
 * @version 1.0.0
 * @since 2015年8月25日 下午7:57:51
 */
public class ShiroSessionTest {

    @Test
    public void testSessionRepository() throws Exception {
        JedisConnectionFactory connectionFactory = new JedisConnectionFactory();
        connectionFactory.setUsePool(true);
        connectionFactory.setDatabase(1);
        connectionFactory.setHostName("ms01");
        connectionFactory.setPort(6379);
        connectionFactory.setPassword("");
        connectionFactory.afterPropertiesSet();
        StringRedisTemplate redisTemplate = new StringRedisTemplate(connectionFactory);
        redisTemplate.afterPropertiesSet();
        RedisSessionRepository sessionRepository = new RedisSessionRepository();
        sessionRepository.setRedisTemplate(redisTemplate);
        sessionRepository.afterPropertiesSet();
        SimpleSessionIdGenerator sessionIdGenerator = new SimpleSessionIdGenerator();
        sessionIdGenerator.setRedisTemplate(redisTemplate);
        sessionIdGenerator.afterPropertiesSet();
        SimpleSession session = buildSession();
        String sessionId = sessionIdGenerator.generateId(session);
        session.setId(sessionId);
        sessionRepository.add(session);

        SimpleSession loadSession = sessionRepository.get(sessionId);

        System.out.println(((SavedRequest) loadSession.getAttribute(SessionAttributes.SHIRO_SAVED_REQUEST_KEY)).getRequestURI());
        SimplePrincipalCollection principal = (SimplePrincipalCollection) loadSession.getAttribute(DefaultSubjectContext.PRINCIPALS_SESSION_KEY);
        Object primaryPrincipal = principal.getPrimaryPrincipal();
        System.out.println(((User) primaryPrincipal).getName());

        System.out.println(loadSession.getAttribute("etc"));
        System.out.println(loadSession.getAttribute("seq"));

        Object userMap = loadSession.getAttribute("userMap");
        System.out.println(userMap);
        Object userList = loadSession.getAttribute("userList");
        System.out.println(userList);

        Object userArray = loadSession.getAttribute("userArray");

        System.out.println(userArray);
    }

    @Test
    public void testSession() {
        final SimpleSession rebuildSession = buildSession();
        rebuildSession.setId("hahah");
        String serializeJson = SerializeUtils.serialize(rebuildSession);
        System.out.println(serializeJson);
        // SimpleSession rebuildSession = SerializeUtils.deserialize(serializeJson, SimpleSession.class);
        System.out.println(((SavedRequest) rebuildSession.getAttribute(SessionAttributes.SHIRO_SAVED_REQUEST_KEY)).getRequestURI());
        SimplePrincipalCollection principal = (SimplePrincipalCollection) rebuildSession.getAttribute(DefaultSubjectContext.PRINCIPALS_SESSION_KEY);
        Object primaryPrincipal = principal.getPrimaryPrincipal();
        System.out.println(((User) primaryPrincipal).getName());
        System.out.println(rebuildSession.getAttribute("etc"));
        System.out.println(rebuildSession.getAttribute("seq"));
    }

    private SimpleSession buildSession() {
        SimpleSession session = new SimpleSession("127.0.0.1");
        SimpleHttpRequest httpRequest = new SimpleHttpRequest("POST", "id=2&limit=3", "http://127.0.0.1:8080/products");
        SavedRequest request = new SavedRequest(httpRequest);
        session.setAttribute(SessionAttributes.SHIRO_SAVED_REQUEST_KEY, request);

        User user = new User();
        user.setId(1);
        user.setName("fei.liu");
        user.setPassword("123456");
        SimplePrincipalCollection principalCollection = new SimplePrincipalCollection(user, "authc");
        session.setAttribute(DefaultSubjectContext.PRINCIPALS_SESSION_KEY, principalCollection);

        session.setAttribute("etc", "profile.xml");

        session.setAttribute("seq", 890);

        Map<String, User> map = new HashMap<String, User>();

        User user1 = new User();
        user1.setId(123);
        user1.setName("刘飞1");
        user1.setPassword("123456");

        User user2 = new User();
        user2.setId(456);
        user2.setName("刘飞2");
        user2.setPassword("123456");

        map.put("user1", user1);
        map.put("user2", user2);

        session.setAttribute("userMap", map);

        List<User> userList = new ArrayList<User>();

        User user3 = new User();
        user3.setId(789);
        user3.setName("刘飞3");
        user3.setPassword("123456");

        User user4 = new User();
        user4.setId(444);
        user4.setName("刘飞4");
        user4.setPassword("123456");

        userList.add(user3);
        userList.add(user4);

        session.setAttribute("userList", userList);

        User user5 = new User();
        user5.setId(7895);
        user5.setName("刘飞5");
        user5.setPassword("123456");

        User user6 = new User();
        user6.setId(4446);
        user6.setName("刘飞6");
        user6.setPassword("123456");

        session.setAttribute("userArray", new User[] {user5, user6});

        return session;
    }
}
