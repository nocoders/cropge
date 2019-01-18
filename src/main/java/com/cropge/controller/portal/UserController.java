package com.cropge.controller.portal;

import com.cropge.common.Const;
import com.cropge.common.ServerReponse;
import com.cropge.pojo.User;
import com.cropge.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/user/")
public class UserController {
    /**
     *  用户登录
     * @param username
     * @param password
     * @param session
     * @return
     */
//    iUserService属性名称同Service中注解的名称相同，就可以调用Service
    @Autowired
    private IUserService iUserService;
    // 数据返回时，自动通过springmvc 的jackson插件将返回值序列化为json
/**
 * 登录接口
 * session中将用户key value存入时为什么将getdata存入，不太懂
 */
    @RequestMapping(value = "login.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerReponse<User> login(String username, String password, HttpSession session){
        ServerReponse<User> reponse = iUserService.login(username, password);
        if (reponse.isSuccess()){
            session.setAttribute(Const.CURRENT_USER,reponse.getData());
        }
        return reponse;
    }
    /**
     * 登出接口
     */
    @RequestMapping(value = "logout.do",method = RequestMethod.GET)
    @ResponseBody
    public ServerReponse<String> logout(HttpSession session){
        session.removeAttribute(Const.CURRENT_USER);
        return ServerReponse.createBySuccess();
    }
    /**
     * 注册接口
     */
    @RequestMapping(value = "register.do",method = RequestMethod.GET)
    @ResponseBody
    public  ServerReponse<String> register(User user){
        return iUserService.register(user);
    }
/**
 *     用户注册时，输入用户名等信息时，直接事实进行判断，该用户名或手机号是否正确，
 *     \是否已经被使用
 *     该接口用于判断用户名和邮箱是否被使用
  */
    @RequestMapping(value = "checkValid.do",method = RequestMethod.GET)
    @ResponseBody
    public ServerReponse<String> checkValid(String str,String type){
        return iUserService.checkValid(str,type);
    }
}
