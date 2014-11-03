package com.sheldontalk.www.dao.impl;


import com.sheldontalk.www.dao.common.GenericHibernateDao;
import com.sheldontalk.www.model.User;
import org.springframework.stereotype.Repository;

/**
 * User: Sheldon
 * Date: 14-4-14
 * Time: 下午5:31
 */
@Repository
public class UserDao extends GenericHibernateDao<User, String> {
}
