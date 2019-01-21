package com.cropge.controller.portal;

import com.cropge.common.Const;
import com.cropge.common.ResponseCode;
import com.cropge.common.ServerReponse;
import com.cropge.pojo.User;
import com.cropge.service.IUserService;
import org.apache.commons.lang3.StringUtils;
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
    @RequestMapping(value = "logout.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerReponse<String> logout(HttpSession session){
        session.removeAttribute(Const.CURRENT_USER);
        return ServerReponse.createBySuccess();
    }
    /**
     * 注册接口
     */
    @RequestMapping(value = "register.do",method = RequestMethod.POST)
    @ResponseBody
    public  ServerReponse<String> register(User user){
        return iUserService.register(user);
    }
/**
 *     用户注册时，输入用户名等信息时，直接事实进行判断，该用户名或手机号是否正确，
 *     \是否已经被使用
 *     该接口用于判断用户名和邮箱是否被使用
  */
    @RequestMapping(value = "checkValid.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerReponse<String> checkValid(String str,String type){
        return iUserService.checkValid(str,type);
    }
    /**
     * 获取用户登录请求信息
     */
    @RequestMapping(value = "getUserInfo.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerReponse<User> getUserInfo(HttpSession session){
        User user=(User)session.getAttribute(Const.CURRENT_USER);
        if (user != null){
            return ServerReponse.createBySuccess(user);
        }
        return ServerReponse.createByErrorMessage("用户未登录");
    }

//    忘记登录密码,查询找回问题
    @RequestMapping(value = "forget_get_question.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerReponse<String> forgetPwdAndGetQuestion(String username){
        return iUserService.selectQuestion(username);
    }
//    校验问题答案
    @RequestMapping(value = "check_question.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerReponse<String> forgetCheckAnswer(String username,String question,String answer){
        return iUserService.checkAnswer(username,question,answer);
    }
//    重置密码
    @RequestMapping(value = "forget_reset_password.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerReponse<String> forgetResetPassword(String username,String passwordNew,String forgetToken){
        return iUserService.forgetResetPassword(username,passwordNew,forgetToken);
    }
//    用户登录状态下的密码重置
    @RequestMapping(value = "login_reset_password.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerReponse<String> resetPassword(HttpSession session,String passwordold,String passwordnew){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null){
            return ServerReponse.createByErrorMessage("用户尚未登录");
        }
        return iUserService.resetPassword(passwordold,passwordnew,user);
    }

//    更新用户个人信息
    @RequestMapping(value = "update_information.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerReponse<User> update_information(HttpSession session,User user){
        User currentUser = (User) session.getAttribute(Const.CURRENT_USER);
        if (currentUser == null){
            return ServerReponse.createByErrorMessage("用户尚未登录");
        }
        user.setId(currentUser.getId());
        user.setUsername(currentUser.getUsername());
        ServerReponse<User> response = iUserService.updateInformation(user);
        if (response.isSuccess()){
            session.setAttribute(Const.CURRENT_USER,response.getData());
        }
        return response;
    }
//  获取用户详情信息
    @RequestMapping(value = "get_information.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerReponse<User> get_information(HttpSession session){
        User currentUser = (User) session.getAttribute(Const.CURRENT_USER);
        if (currentUser == null){
            return ServerReponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"未登录，需要强制登录");
        }
        return iUserService.getInformation(currentUser.getId());
    }
}
