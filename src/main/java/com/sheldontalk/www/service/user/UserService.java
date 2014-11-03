/**
 *
 */
package com.sheldontalk.www.service.user;

import com.sheldontalk.www.dao.common.ValidateUtil;
import com.sheldontalk.www.dao.impl.UserDao;
import com.sheldontalk.www.model.User;
import com.sheldontalk.www.service.BaseService;
import com.sheldontalk.www.util.Constants;
import com.sheldontalk.www.util.SecurityUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


/**
 * @author Sheldon
 * @Date: 2014-4-23
 * @Time: 上午9:53
 */
@Service
public class UserService extends BaseService {

    @Autowired
    private UserDao userDao;

    public boolean register(User user) {
        boolean flag = true;

        DetachedCriteria detachedCriteria = DetachedCriteria.forClass(User.class);
        detachedCriteria.add(Restrictions.eq("username", user.getUsername()));

        List<User> users = this.userDao.findByDetachedCriteria(detachedCriteria);
        if (users != null && users.size() > 0) {
            flag = false;
        } else {
            try {
                user.setPassword(SecurityUtils.getHashPassword(user.getPassword(), Constants.SALT));
                ValidateUtil.validate(user);
                this.userDao.saveOrUpdate(user);
            } catch (Exception e) {
                flag = false;
            }
        }

        return flag;
    }

    public User login(User user) {
        DetachedCriteria detachedCriteria = DetachedCriteria.forClass(User.class);
        detachedCriteria.add(Restrictions.eq("username", user.getUsername()));

        List<User> users = this.userDao.findByDetachedCriteria(detachedCriteria);

        User loginUser = null;
        if (users != null && users.size() > 0) {
            User duser = users.get(0);
            if (SecurityUtils.getHashPassword(user.getPassword(), Constants.SALT).equals(duser.getPassword())) {
                loginUser = duser;
            }
        }
        return loginUser;
    }
}
