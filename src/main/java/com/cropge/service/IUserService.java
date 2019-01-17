package com.cropge.service;

import com.cropge.common.ServerReponse;
import com.cropge.pojo.User;

public interface IUserService {

    ServerReponse<User> login(String username, String password);
}
