package com.sheldontalk.www.web;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * User: Sheldon
 * Date: 14-4-14
 * Time: 下午4:58
 */
@Controller
public class PageController extends BaseController {

    @RequestMapping(value = {"/", "/index.html"}, method = RequestMethod.GET)
    public String index() {
        return "index";
    }

}
