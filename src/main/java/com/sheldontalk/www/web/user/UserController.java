package com.sheldontalk.www.web.user;

import com.sheldontalk.www.model.User;
import com.sheldontalk.www.service.user.UserService;
import com.sheldontalk.www.util.Constants;
import com.sheldontalk.www.web.BaseController;
import com.sheldontalk.www.web.message.login.req.LoginReq;
import com.sheldontalk.www.web.message.login.req.RegisterReq;
import com.sheldontalk.www.web.message.login.resp.LoginResp;
import com.sheldontalk.www.web.message.login.resp.RegisterResp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by Sheldon Chen on 2014/10/15.
 */
@Controller
public class UserController extends BaseController {

    @Autowired
    private UserService userService;

    @RequestMapping(value = "/register", method = RequestMethod.POST)
    @ResponseBody
    public RegisterResp register(@RequestBody RegisterReq req) {
        logger.debug(req.toString());
        RegisterResp resp = new RegisterResp();

        User user = new User(req.account, req.password, req.sex);

        boolean flag = this.userService.register(user);
        logger.debug(user.toString());

        if (flag) {
            resp.code = Constants.SUCCESS;
            resp.user = user;
            resp.desc = "register success";
        } else {
            resp.code = Constants.ERROR;
            resp.desc = "register fail";
        }
        return resp;
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    @ResponseBody
    public LoginResp login(@RequestBody LoginReq req) {
        logger.debug(req.toString());
        LoginResp resp = new LoginResp();
        User user = new User(req.account, req.password);

        User loginUser = this.userService.login(user);
        resp.user = loginUser;
        if (loginUser != null) {
            resp.code = Constants.SUCCESS;
            resp.desc = "user success";
        } else {
            resp.code = Constants.ERROR;
            resp.desc = "user error";
        }
        return resp;
    }
}
