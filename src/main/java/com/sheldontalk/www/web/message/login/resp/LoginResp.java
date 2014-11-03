/**
 * 
 */
package com.sheldontalk.www.web.message.login.resp;


import com.sheldontalk.www.model.User;
import com.sheldontalk.www.web.message.BaseResp;

/**
 * @author Sheldon
 * @Date 2014年4月23日
 * @Time 上午9:54:54
 */
public class LoginResp extends BaseResp {
    public User user;

    @Override
    public String toString() {
        return "LoginResp{" +
                "user=" + user +
                "} " + super.toString();
    }
}
