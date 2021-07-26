package com.lzp.mapper;

import com.lzp.entity.User;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface UserMapper {

    /*查询所有的账户*/
    @Select("select * from user")
    public List<User> selectallUser();

    /*验证账号*/
    @Select("select * from user where username = #{username} and password = #{password}")
    public User checkUser(@Param("username") String username,@Param("password") String password);

    /*寻找某一个用户*/
    @Select("select * from user where userid = #{userid}")
    public User finduserByid(@Param("userid") int userid);

    /*寻找某一个用户*/
    @Select("select * from user where username = #{username}")
    public User finduserByusername(@Param("username") String username);

    /*用户存款操作*/
    @Update("UPDATE user SET account = account + #{account} where userid = #{userid}")
    public int updateUserAccount(@Param("userid")int userid,@Param("account")int account);

    /*用户转账操作*/
    @Update("UPDATE user SET account = account - #{account} where username = #{username}")
    public int updateUserAccount1(@Param("username")String username,@Param("account")int account);

}
