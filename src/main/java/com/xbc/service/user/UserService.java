package com.xbc.service.user;

import com.xbc.entity.User;

import java.sql.Connection;
import java.util.List;


public interface UserService {
    /**
     * 用户登录
     */
    public User login(String userCode, String passWord);

    /**
     * 根据用户id修改密码
     */
    public boolean updatePassWord(int id,String passWord);

    /**
     * 查询记录数
     * @param userName
     * @param userRole
     * @return
     */
    public int getUserCount(String userName, int userRole);

    /**
     * 根据条件查询用户列表
     * @param queryUserName
     * @param queryUserRole
     * @param currentPageNo
     * @param pageSize
     * @return
     */
    public List<User> getUserList(String queryUserName, int queryUserRole, int currentPageNo, int pageSize);

    /**
     * 通过用户id删除用户
     * @param id
     * @return
     */
    public Boolean deleteUserById(Integer id);

    /**
     * 根据userCode查询对应的用户，判断是否存在此用户
     * @param userCode
     * @return
     */
    public User getUserNameByUserCode(String userCode);

    /**
     * 添加用户
     * @param user
     * @return
     */
    public Boolean addUser(User user);

    /**
     * 修改用户信息
     * @param user
     * @return
     */
    public Boolean modifyUser(User  user);

    /**
     * 通过id获得用户信息
     * @param id
     * @return
     */
    //根据ID查找user
    public User getUserById(String id);
}
