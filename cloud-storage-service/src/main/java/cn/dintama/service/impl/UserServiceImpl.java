package cn.dintama.service.impl;

import cn.dintama.dao.UserDao;
import cn.dintama.entity.User;
import cn.dintama.service.UserService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * Created by Dintama on 2017/5/25.
 */
@Service("userService")
public class UserServiceImpl implements UserService {

    @Resource
    private UserDao userDao;

    @Override
    public User getUserByEmail(String email) {
        return userDao.selectUserByEmail(email);
    }

    @Override
    public Boolean validateLogin(User user) {
        User u = userDao.selectUserByEmail(user.getEmail());
        if(u == null){
            return false;
        }
        if(u.getPassword().equals(user.getPassword())){
            return true;
        }
        return false;
    }

    @Override
    public Boolean validateSignUp(User user) {
        User u = userDao.selectUserByEmail(user.getEmail());
        if(u == null){
            return true;
        }
        return false;
    }
}
