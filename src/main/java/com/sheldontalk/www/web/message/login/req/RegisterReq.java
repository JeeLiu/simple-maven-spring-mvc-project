package com.sheldontalk.www.web.message.login.req;

import com.sheldontalk.www.web.message.BaseReq;

/**
 * Created by Sheldon Chen on 2014/10/15.
 */
public class RegisterReq extends BaseReq {

    public String account;
    public String password;
    public Integer sex;

    @Override
    public String toString() {
        return "RegisterReq{" +
                "account='" + account + '\'' +
                ", password='" + password + '\'' +
                ", sex=" + sex +
                '}';
    }
}
