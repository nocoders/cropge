package com.cropge.service.impl;

import com.cropge.common.Const;
import com.cropge.common.ServerReponse;
import com.cropge.dao.UserMapper;
import com.cropge.pojo.User;
import com.cropge.service.IUserService;
import com.cropge.util.MD5Util;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sun.security.provider.MD5;

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


}
