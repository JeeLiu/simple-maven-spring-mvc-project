/**
 *
 */
package com.sheldontalk.www.web.message.login.req;


import com.sheldontalk.www.web.message.BaseReq;

/**
 * @author Sheldon
 * @Date 2014年4月23日
 * @Time 上午9:57:40
 */
public class LoginReq extends BaseReq {
    public String account;
    public String password;

    @Override
    public String toString() {
        return "LoginReq{" +
                "account='" + account + '\'' +
                "} " + super.toString();
    }
}
