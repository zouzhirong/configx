/**
 * $Id$
 * Copyright(C) 2012-2016 configx.com. All rights reserved.
 */
package com.configx.web.web.controller;

import com.configx.web.model.App;
import com.configx.web.service.app.AppService;
import com.configx.web.service.user.UserContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Index Controller
 */
@Controller
public class IndexController {

    @Autowired
    private AppService appService;

    @RequestMapping("/")
    public String index() {
        App app = appService.getDefaultApp(UserContext.email());
        if (app == null) {
            return "redirect:/apps";
        } else {
            return "redirect:/apps/" + app.getId() + "/config";
        }
    }

}
