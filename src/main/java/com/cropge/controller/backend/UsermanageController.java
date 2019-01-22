package com.cropge.controller.backend;

import com.cropge.common.Const;
import com.cropge.common.ServerResponse;
import com.cropge.pojo.User;
import com.cropge.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

/**
 * 管理员后台管理接口
 */
@Controller
@RequestMapping("/manage/user")
public class UsermanageController {
    @Autowired
    private IUserService iUserService;
    @RequestMapping(value = "login.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> login(String username, String password, HttpSession session){
        ServerResponse<User> reponse = iUserService.login(username, password);
        if (reponse.isSuccess()){
            User user = reponse.getData();
            if (user.getRole() == Const.Role.ROLE_ADMIN){
                session.setAttribute(Const.CURRENT_USER,user);
                return reponse;
            }else{
                return ServerResponse.createByErrorMessage("不是管理员，无法登陆");
            }
        }
        return reponse;
    }
}
