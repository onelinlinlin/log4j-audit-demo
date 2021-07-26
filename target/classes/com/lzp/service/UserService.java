package com.lzp.service;

import com.lzp.entity.User;
import com.lzp.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;

    /*查找所有的用户*/
    public List<User> selectallUser(){
        return userMapper.selectallUser();
    }

    /*验证用户*/
    public  User checkUser(String username,String password){
        return userMapper.checkUser(username,password);
    }

    /*寻找某一用户*/
    public User finduserByid(int userid){
        return userMapper.finduserByid(userid);
    }

    /*通过用户账号寻找用户*/
    public User findUserByName(String username){
        return userMapper.finduserByusername(username);
    }

    /*用户存款操作*/
    public int updateUserAccount(int userid,int account){
        return userMapper.updateUserAccount(userid,account);
    }

    /*用户转账操作*/
    public int updateUserAccount1(String username,int account){
        return userMapper.updateUserAccount1(username,account);
    }
}
