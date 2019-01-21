package com.cropge.service.impl;

import com.cropge.common.Const;
import com.cropge.common.ServerReponse;
import com.cropge.common.TokenCatch;
import com.cropge.dao.UserMapper;
import com.cropge.pojo.User;
import com.cropge.service.IUserService;
import com.cropge.util.MD5Util;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sun.security.provider.MD5;

import java.util.UUID;

//  将service注入到controller上供controller调用
@Service("iUserService")
public class UserServiceImpl implements IUserService {
    @Autowired
    private UserMapper userMapper;
    @Override
    public ServerReponse<User> login(String username, String password) {
        int resultCount = userMapper.checkUsername(username);
        if (resultCount==0){
            return ServerReponse.createByErrorMessage("用户名不存在");
        }
//     todo   密码登录md5
        String md5Password=MD5Util.MD5EncodeUtf8(password);
        User user = userMapper.login(username, md5Password);
        if (user == null){
            return ServerReponse.createByErrorMessage("密码错误");
        }
//        将密码制空
        user.setPassword(StringUtils.EMPTY);
        return ServerReponse.createBySuccess("登录成功",user);
    }

    @Override
    public ServerReponse<String> register(User user) {
        ServerReponse<String> valid = this.checkValid(user.getUsername(), Const.USERNAME);
        if (!valid.isSuccess()){
            return valid;
        }
        valid=this.checkValid(user.getEmail(),Const.EMAIL);
        if (!valid.isSuccess()){
            return valid;
        }
        user.setRole(Const.Role.ROLE_CUSTOMER);
        user.setPassword(MD5Util.MD5EncodeUtf8(user.getPassword()));
        int insert = userMapper.insert(user);
        if (insert==0){
            return ServerReponse.createByErrorMessage("注册失败");
        }

        return ServerReponse.createBySuccessMessage("注册失败");
    }
    @Override
    public ServerReponse<String>checkValid(String str,String type){
        if (StringUtils.isNoneBlank(type)){
//            校验
            if (Const.USERNAME.equals(type)){
                int resultCount = userMapper.checkUsername(str);
                if (resultCount>0){
                    return ServerReponse.createByErrorMessage("用户名已存在");
                }
            }else if (Const.EMAIL.equals(type)){
               int resultCount = userMapper.checkEmail(str);
                if (resultCount>0){
                    return ServerReponse.createByErrorMessage("email已存在");
                }
            }
        }else{
            return ServerReponse.createByErrorMessage("参数错误");
        }
        return ServerReponse.createBySuccessMessage("校验成功");
    }

    public ServerReponse selectQuestion(String username){
        ServerReponse<String> reponse = this.checkValid(username, Const.USERNAME);
        if (reponse.isSuccess()){
            return ServerReponse.createByErrorMessage("用户名不存在");
        }
        String question = userMapper.selectQuestion(username);
        if (StringUtils.isNotBlank(question)){
            return ServerReponse.createBySuccess(question);
        }
        return ServerReponse.createByErrorMessage("无找回密码问题");
    }

    public ServerReponse<String> checkAnswer(String username,String question,String answer){
        int result = userMapper.checkAnswer(username, question, answer);
        if (result>0){
//            问题及问题答案正确
            String forgetToken = UUID.randomUUID().toString();
            TokenCatch.setKey(TokenCatch.TOKEN_PREFIX+username,forgetToken);
            return ServerReponse.createBySuccess(forgetToken);
        }
        return ServerReponse.createByErrorMessage("问题答案错误");
    }

    public ServerReponse<String> forgetResetPassword(String username,String passwordNew,String forgetToken){
//       检查传递过来的token是否有效
        if (StringUtils.isBlank(forgetToken)){
            return ServerReponse.createByErrorMessage("参数错误，token未传递");
        }
//        检查用户是否存在
        ServerReponse<String> reponse = this.checkValid(username, Const.USERNAME);
        if (reponse.isSuccess()){
            return ServerReponse.createByErrorMessage("用户名不存在");
        }
        String token = TokenCatch.getKey(TokenCatch.TOKEN_PREFIX + username);
//        检查缓存token是否存在
        if (StringUtils.isBlank(token)){
            return ServerReponse.createByErrorMessage("token无效或者过期");
        }
        if (StringUtils.equals(token,forgetToken)){
            String md5Password = MD5Util.MD5EncodeUtf8(passwordNew);
            int rowCount=userMapper.updatePasswordByUsername(username,md5Password);
            if (rowCount>0){
                return ServerReponse.createBySuccessMessage("修改密码成功");
            }
        }else{
            return ServerReponse.createByErrorMessage("token错误，请重新获取重置密码的token");
        }
        return  ServerReponse.createByErrorMessage("密码修改失败");
    }

    public ServerReponse<String> resetPassword(String passwordold,String passwordnew,User user){
//        防止横向越权，检查下该用户的旧密码是否正确
        int result = userMapper.checkPassword(MD5Util.MD5EncodeUtf8(passwordold), user.getId());
        if (result == 0){
            return ServerReponse.createByErrorMessage("旧密码错误");
        }
        user.setPassword(MD5Util.MD5EncodeUtf8(passwordnew));
        result=userMapper.updateByPrimaryKeySelective(user);
        if (result>0){
            return ServerReponse.createBySuccessMessage("密码更新成功");
        }
        return ServerReponse.createByErrorMessage("密码更新失败");
    }

    public ServerReponse<User> updateInformation(User user){
//        username 不能被更新
//        email也要进行校验，检查新的email是否已经存在，并且如果存在的email如果相同的话，不能是我们当前这个用户的
        int result = userMapper.checkEmailByUserId(user.getEmail(),user.getId());
        if (result >0){
            ServerReponse.createByErrorMessage("email 已经被占用，请更换email再尝试");
        }
        User updateUser = new User();
        updateUser.setId(user.getId());
        updateUser.setEmail(user.getEmail());
        updateUser.setPhone(user.getPhone());
        updateUser.setAnswer(user.getAnswer());
        updateUser.setQuestion(user.getQuestion());

        result = userMapper.updateByPrimaryKeySelective(updateUser);
        if (result>0){
            return ServerReponse.createBySuccess("更新个人信息成功",updateUser);
        }
        return ServerReponse.createByErrorMessage("更新个人信息失败");
    }

    public ServerReponse<User> getInformation(Integer userId){
        User user = userMapper.selectByPrimaryKey(userId);
        if (user ==null){
            return ServerReponse.createByErrorMessage("找不到当前用户");
        }
        user.setPassword(StringUtils.EMPTY);
        return ServerReponse.createBySuccess(user);
    }
}
