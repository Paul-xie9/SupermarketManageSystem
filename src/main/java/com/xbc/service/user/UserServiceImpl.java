package com.xbc.service.user;

import com.xbc.dao.BaseDao;
import com.xbc.dao.user.UserDao;
import com.xbc.dao.user.UserDaoImpl;
import com.xbc.entity.User;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class UserServiceImpl implements UserService {
    /**
     * 业务层都会调用dao层
     */
    private UserDao userDao;

    public UserServiceImpl() {
        userDao = new UserDaoImpl();
    }

    /**
     * 用户登录
     *
     * @param userCode
     * @param passWord
     * @return
     */
    @Override
    public User login(String userCode, String passWord) {
        Connection connection = null;
        User user = null;

        try {
            connection = BaseDao.getConnection();
            user = userDao.getLoginUser(connection, userCode, passWord);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            BaseDao.closeResource(connection, null, null);
        }
        return user;
    }

    /**
     * 根据用户id修改密码
     *
     * @param id
     * @param passWord
     * @return
     */
    @Override
    public boolean updatePassWord(int id, String passWord) {
        Connection connection = null;
        boolean flag = false;

        try {
            connection = BaseDao.getConnection();   //获取数据库连接
            if (userDao.updatePassWord(connection, id, passWord) > 0) {
                flag = true;    //说明修改成功
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            BaseDao.closeResource(connection, null, null);
        }
        return flag;
    }


    /**
     * 查询记录数
     *
     * @param queryUserName
     * @param queryUserRole
     * @return
     */
    @Override
    public int getUserCount(String queryUserName, int queryUserRole) {
        Connection connection = null;
        int count = 0;

        System.out.println("queryUserName ---- > " + queryUserName);
        System.out.println("queryUserRole ---- > " + queryUserRole);
        try {
            connection = BaseDao.getConnection();
            count = userDao.getUserCount(connection, queryUserName, queryUserRole);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            BaseDao.closeResource(connection, null, null);
        }
        return count;
    }

    /**
     * 根据条件查询用户列表
     *
     * @param queryUserName
     * @param queryUserRole
     * @param currentPageNo
     * @param pageSize
     * @return
     */
    @Override
    public List<User> getUserList(String queryUserName, int queryUserRole, int currentPageNo, int pageSize) {
        Connection connection = null;
        List<User> userList = null;
        System.out.println("queryUserName ---- > " + queryUserName);
        System.out.println("queryUserRole ---- > " + queryUserRole);
        System.out.println("currentPageNo ---- > " + currentPageNo);
        System.out.println("pageSize ---- > " + pageSize);
        try {
            connection = BaseDao.getConnection();
            userList = userDao.getUserList(connection, queryUserName, queryUserRole, currentPageNo, pageSize);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            BaseDao.closeResource(connection, null, null);
        }
        return userList;
    }

    /**
     * 通过id删除用户
     *
     * @param id
     * @return
     */
    @Override
    public Boolean deleteUserById(Integer id) {
        boolean flag = false;
        Connection connection = null;

        try {
            connection = BaseDao.getConnection();
            int i = userDao.deleteUserById(connection, id);

            if (i > 0) {
                flag = true;    //说明删除成功
            }
        } catch (Exception e) {
            e.printStackTrace();
            flag = false;
        } finally {
            BaseDao.closeResource(connection, null, null);
        }
        return flag;
    }

    /**
     * 根据userCode查询对应的用户，判断是否存在此用户
     *
     * @param userCode
     * @return
     */
    @Override
    public User getUserNameByUserCode(String userCode) {
        Connection connection = null;
        User user = null;
        try {
            connection = BaseDao.getConnection();
            user = userDao.getUserNameByUserCode(connection, userCode);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            BaseDao.closeResource(connection, null, null);
        }
        return user;
    }

    /**
     * 添加用户信息
     *
     * @param user
     * @return
     */
    @Override
    public Boolean addUser(User user) {
        boolean flag = false;   //记录插入情况
        int addUserRows = 0;    //记录插入用户的条数
        Connection connection = null;

        try {
            connection = BaseDao.getConnection();   //获取数据库连接
            addUserRows = userDao.addUser(connection, user);

            if (addUserRows > 0) {
                flag = true;
            }
        } catch (Exception e) {
            flag = false;
            System.out.println("service层发现 => 用户删除失败！");
            e.printStackTrace();
        }
        return flag;
    }

    /**
     * 修改用户信息
     *
     * @param user
     * @return
     */
    @Override
    public Boolean modifyUser(User user) {
        boolean flag = false;
        int modifyRows = 0;
        Connection connection = null;

        try {
            connection = BaseDao.getConnection();
            modifyRows = userDao.modify(connection, user);

            if (modifyRows > 0) {
                flag = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            flag = false;
        }
        return flag;
    }

    /**
     * 根据id查詢用戶信息
     * @param id
     * @return
     */
    @Override
    public User getUserById(String id) {
        Connection connection = null;
        User user = null;
        try {
            connection = BaseDao.getConnection();
            user = userDao.getUserById(connection, id);
        } catch (Exception e) {
            e.printStackTrace();
            user = null;
        }finally {
            BaseDao.closeResource(connection,null,null);
        }
        return user;
    }

    /**
     * 测试
     */
    @Test
    public void test() {
        UserServiceImpl userService = new UserServiceImpl();
        User admin = userService.login("admin", "admin");
        System.out.println(admin.getAddress());

        List<User> userList = userService.getUserList("系统管理员", 1, 1, 5);
        for (User user : userList
        ) {
            System.out.println(user);
        }

    }

    @Test
    public void test_deleteUserById() {
        UserServiceImpl userService = new UserServiceImpl();
        Boolean byId = userService.deleteUserById(2);
        System.out.println("删除情况:" + byId);
    }
}
