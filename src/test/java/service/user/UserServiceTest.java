package service.user;

import com.sheldontalk.www.dao.impl.UserDao;
import com.sheldontalk.www.model.User;
import com.sheldontalk.www.service.user.UserService;
import com.sheldontalk.www.util.Constants;
import com.sheldontalk.www.util.SecurityUtils;
import junit.framework.Assert;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Created by Sheldon Chen on 2014/10/15.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:servlet-context.xml")
public class UserServiceTest {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private UserDao userDao;

    @Autowired
    private UserService userService;

    @Before
    public void setUp() {
        logger.debug("set up user service test data");
        User user = new User();
        user.setUsername("admin");
        user.setPassword(SecurityUtils.getHashPassword("admin", Constants.SALT));
        user.setSex(1);
        this.userDao.saveOrUpdate(user);
    }

    @Test
    public void registerTest() {
        logger.debug("test register success");
        User sheldon = new User();
        sheldon.setUsername("sheldon");
        sheldon.setPassword("sheldon");
        sheldon.setSex(1);
        boolean registerSuccessFlag = this.userService.register(sheldon);
        Assert.assertEquals(true, registerSuccessFlag);

        User admin = new User();
        admin.setUsername("admin");
        admin.setPassword("admin");
        admin.setSex(1);
        boolean registerErrorFlag = this.userService.register(admin);
        Assert.assertEquals(false, registerErrorFlag);
    }

    @After
    public void tearDown() {
        this.userDao.deleteAll();
    }
}
