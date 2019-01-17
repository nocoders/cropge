package com.cropge.service.impl;

import com.cropge.common.ServerReponse;
import com.cropge.dao.UserMapper;
import com.cropge.pojo.User;
import com.cropge.service.IUserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
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
        User user = userMapper.login(username, password);
        if (user == null){
            return ServerReponse.createByErrorMessage("密码错误");
        }
//        将密码制空
        user.setPassword(StringUtils.EMPTY);
        return ServerReponse.createBySuccess("登录成功",user);
    }
}
