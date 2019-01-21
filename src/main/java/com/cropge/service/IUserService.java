package com.cropge.service;

import com.cropge.common.ServerReponse;
import com.cropge.pojo.User;

public interface IUserService {

    ServerReponse<User> login(String username, String password);

    ServerReponse<String> register(User user);

    ServerReponse<String>checkValid(String str,String type);

    ServerReponse selectQuestion(String username);

    ServerReponse<String> checkAnswer(String username,String question,String answer);

    ServerReponse<String> forgetResetPassword(String username,String passwordNew,String forgetToken);

    ServerReponse<String> resetPassword(String passwordold,String passwordnew,User user);

    ServerReponse<User> updateInformation(User user);

    ServerReponse<User> getInformation(Integer userId);
    }
