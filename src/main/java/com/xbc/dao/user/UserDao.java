package com.xbc.dao.user;

import com.xbc.entity.Role;
import com.xbc.entity.User;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public interface UserDao {
    //得到登录用户
    public User getLoginUser(Connection connection, String userCode, String passWord) throws SQLException;

    //修改当前用户密码
    public int updatePassWord(Connection connection, int id, String passWord) throws Exception;

    //查询用户总数
    public int getUserCount(Connection connection, String userName, int userRole) throws Exception;

    //通过条件查询userList
    public List<User> getUserList(Connection connection, String userName, int userRole, int currentPageNo, int pageSize) throws Exception;

    //删除用户
    public int deleteUserById(Connection connection,Integer id) throws Exception;

    //通过userCode获取对应的User
    public User getUserNameByUserCode(Connection connection,String userCode) throws Exception;

    //添加用户
    public int addUser(Connection connection,User user) throws Exception;
    //通过userId获取user
    public User getUserById(Connection connection, String id)throws Exception;

    //修改用户
    public int modify(Connection connection,User user) throws Exception;

}
