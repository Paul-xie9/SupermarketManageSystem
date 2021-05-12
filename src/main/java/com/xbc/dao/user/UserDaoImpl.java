package com.xbc.dao.user;

import com.mysql.cj.util.StringUtils;
import com.xbc.dao.BaseDao;
import com.xbc.entity.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserDaoImpl implements UserDao {

    /**
     * 用户登录
     *
     * @param connection
     * @param userCode
     * @param passWord
     * @return
     * @throws SQLException
     */
    @Override
    public User getLoginUser(Connection connection, String userCode, String passWord) throws SQLException {
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        User user = null;

        if (connection != null) {
            String sql = "select *from smbms_user where userCode=? and userPassWord=?";
            Object[] params = {userCode, passWord};


            resultSet = BaseDao.executeQuery(connection, sql, preparedStatement, resultSet, params);
            if (resultSet.next()) {
                user = new User();
                user.setId(resultSet.getInt("id"));
                user.setUserCode(resultSet.getString("userCode"));
                user.setUserName(resultSet.getString("userName"));
                user.setUserPassword(resultSet.getString("userPassword"));
                user.setGender(resultSet.getInt("gender"));
                user.setBirthday(resultSet.getDate("birthday"));
                user.setPhone(resultSet.getString("phone"));
                user.setAddress(resultSet.getString("address"));
                user.setUserRole(resultSet.getInt("userRole"));
                user.setCreatedBy(resultSet.getInt("createdBy"));
                user.setCreatedDate(resultSet.getDate("createdDate"));
                user.setModifiedBy(resultSet.getInt("modifiedBy"));
                user.setModifiedDate(resultSet.getDate("modifiedDate"));
            }
            BaseDao.closeResource(null, preparedStatement, resultSet);
        }

        return user;
    }

    /**
     * 修改用户密码
     *
     * @param connection
     * @param id
     * @param passWord
     * @return
     * @throws SQLException
     */
    @Override
    public int updatePassWord(Connection connection, int id, String passWord) throws Exception {
        PreparedStatement preparedStatement = null;
        int update = 0;

        if (connection != null) {
            String sql = "update smbms_user set userPassword = ? where id = ?";
            Object params[] = {passWord, id};    //顺序不能颠倒,要和后面的参数一致
            update = BaseDao.executeUpdate(connection, preparedStatement, sql, params);
            BaseDao.closeResource(null, preparedStatement, null);
        }
        return update;
    }


    /**
     * 查询用户总数，根据角色或者姓名查询总数
     *
     * @param connection
     * @param userName
     * @param userRole
     * @return
     * @throws Exception
     */
    @Override
    public int getUserCount(Connection connection, String userName, int userRole) throws Exception {
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        int count = 0;

        if (connection != null) {
            StringBuffer sql = new StringBuffer();  //因为有多个sql，需要一个流来缓存
            sql.append("select count(1) as count from smbms_user u,smbms_role r where u.userRole=r.id");
            ArrayList<Object> arrayList = new ArrayList<>();//存放sql参数

            if (!StringUtils.isNullOrEmpty(userName)) {
                sql.append(" and u.userName like ?");   //and前需要加一个空格
                arrayList.add("%" + userName + "%");
            }

            if (userRole > 0) {
                sql.append(" and u.userRole = ?");
                arrayList.add(userRole);
            }

            Object params[] = arrayList.toArray(); //将arrayList转换为数组
            System.out.println("getUserCount->sql:" + sql.toString());

            resultSet = BaseDao.executeQuery(connection, sql.toString(), preparedStatement, resultSet, params);
            if (resultSet.next()) {
                count = resultSet.getInt("count");  //从结果集中获取最终的数量
            }

            BaseDao.closeResource(null, preparedStatement, resultSet);
        }
        return count;
    }


    /**
     * 通过条件查询userList
     * @param connection
     * @param userName
     * @param userRole
     * @param currentPageNo
     * @param pageSize
     * @return
     * @throws Exception
     */
    @Override
    public List<User> getUserList(Connection connection, String userName, int userRole, int currentPageNo, int pageSize) throws Exception {
        PreparedStatement pstm = null;
        ResultSet rs = null;
        List<User> userList = new ArrayList<User>();
        if (connection != null) {
            StringBuffer sql = new StringBuffer();
            sql.append("select u.*,r.roleName as userRoleName from smbms_user u,smbms_role r where u.userRole = r.id");
            ArrayList<Object> arrayList = new ArrayList<Object>();
            if (!StringUtils.isNullOrEmpty(userName)) {
                sql.append(" and u.userName like ?");
                arrayList.add("%" + userName + "%");
            }
            if (userRole > 0) {
                sql.append(" and u.userRole = ?");
                arrayList.add(userRole);
            }
            //在数据库中，分页显示 limit startIndex，pageSize；总数
            //当前页  (当前页-1)*页面大小
            //0,5	1,0	 01234
            //5,5	5,0	 56789
            //10,5	10,0 10~
            sql.append(" order by createdDate DESC limit ?,?");
            currentPageNo = (currentPageNo - 1) * pageSize;
            arrayList.add(currentPageNo);
            arrayList.add(pageSize);

            Object params[] = arrayList.toArray();
            System.out.println("sql --> " + sql.toString());

            rs = BaseDao.executeQuery(connection, sql.toString(), pstm, rs, params);
            while (rs.next()) {
                User _user = new User();
                _user.setId(rs.getInt("id"));
                _user.setUserCode(rs.getString("userCode"));
                _user.setUserName(rs.getString("userName"));
                _user.setGender(rs.getInt("gender"));
                _user.setBirthday(rs.getDate("birthday"));
                _user.setPhone(rs.getString("phone"));
                _user.setUserRole(rs.getInt("userRole"));
                _user.setUserRoleName(rs.getString("userRoleName"));
                userList.add(_user);
            }
            BaseDao.closeResource(null, pstm, rs);
        }
        return userList;
    }


    /**
     * 通过用户id删除用户
     *
     * @param connection
     * @param id
     * @return
     * @throws Exception
     */
    @Override
    public int deleteUserById(Connection connection, Integer id) throws Exception {
        PreparedStatement preparedStatement = null;
        int delete = 0; //记录删除的个数

        if (connection != null) {
            String sql = "delete from  smbms_user where id = ?";
            Object params[] = {id}; //存放id
            delete = BaseDao.executeUpdate(connection, preparedStatement, sql, params);
            BaseDao.closeResource(null, preparedStatement, null);
        }
        return delete;
    }

    /**
     * 根据UserCode查询用户信息
     * @param connection
     * @param userCode
     * @return
     * @throws Exception
     */
    @Override
    public User getUserNameByUserCode(Connection connection, String userCode) throws Exception {
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        User user = null;

        if (connection != null) {
            String sql = "select * from smbms_user where userCode=?";
            Object[] params = {userCode};
            resultSet = BaseDao.executeQuery(connection, sql, preparedStatement, resultSet, params);

            if (resultSet.next()){
                user = new User();
                user.setId(resultSet.getInt("id"));
                user.setUserCode(resultSet.getString("userCode"));
                user.setUserName(resultSet.getString("userName"));
                user.setUserPassword(resultSet.getString("userPassword"));
                user.setGender(resultSet.getInt("gender"));
                user.setBirthday(resultSet.getDate("birthday"));
                user.setPhone(resultSet.getString("phone"));
                user.setAddress(resultSet.getString("address"));
                user.setUserRole(resultSet.getInt("userRole"));
                user.setCreatedBy(resultSet.getInt("createdBy"));
                user.setCreatedDate(resultSet.getDate("createdDate"));
                user.setModifiedBy(resultSet.getInt("modifiedBy"));
                user.setModifiedDate(resultSet.getDate("modifiedDate"));
            }
            BaseDao.closeResource(null,preparedStatement,resultSet);
        }
        return user;
    }

    /**
     * 添加用户
     *
     * @param connection
     * @param user
     * @return
     * @throws Exception
     */
    @Override
    public int addUser(Connection connection, User user) throws Exception {
        PreparedStatement preparedStatement = null;
        int addRows = 0;

        if (connection != null) {
            String sql = "insert into smbms_user (userCode,userName,userPassword," +
                    "userRole,gender,birthday,phone,address,createdDate,createdBy) " +
                    "values(?,?,?,?,?,?,?,?,?,?)";

            Object params[] = {user.getUserCode(), user.getUserName(), user.getUserPassword(),
                    user.getUserRole(), user.getGender(), user.getBirthday(), user.getPhone(),
                    user.getAddress(), user.getCreatedDate(), user.getCreatedBy()};

            addRows = BaseDao.executeUpdate(connection, preparedStatement, sql, params);//获取受影响的行数
            BaseDao.closeResource(null, preparedStatement, null);
        }
        return addRows;
    }

    /**
     * 通過id查詢用戶信息
     * @param connection
     * @param id
     * @return
     * @throws Exception
     */
    @Override
    public User getUserById(Connection connection, String id) throws Exception {
        User user = null;
        PreparedStatement preparedStatement = null;
        ResultSet rs = null;
        try {
            if (connection != null) {
                String sql = "select u.*,r.roleName as userRoleName from smbms_user u,smbms_role r where u.id=? and u.userRole = r.id";
                Object params[] = {id};
                rs = BaseDao.executeQuery(connection, sql, preparedStatement, rs, params);
                if (rs.next()) {
                    user = new User();
                    user.setId(rs.getInt("id"));
                    user.setUserCode(rs.getString("userCode"));
                    user.setUserName(rs.getString("userName"));
                    user.setUserPassword(rs.getString("userPassword"));
                    user.setGender(rs.getInt("gender"));
                    user.setBirthday(rs.getDate("birthday"));
                    user.setPhone(rs.getString("phone"));
                    user.setAddress(rs.getString("address"));
                    user.setUserRole(rs.getInt("userRole"));
                    user.setCreatedBy(rs.getInt("createdBy"));
                    user.setCreatedDate(rs.getTimestamp("creationDate"));
                    user.setModifiedBy(rs.getInt("modifyBy"));
                    user.setModifiedDate(rs.getTimestamp("modifyDate"));
                    user.setUserRoleName(rs.getString("userRoleName"));
                }
                BaseDao.closeResource(null,preparedStatement,rs);
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return user;
    }

    /**
     * 修改用户信息
     * @param connection
     * @param user
     * @return modifyRows
     * @throws Exception
     */
    @Override
    public int modify(Connection connection, User user) throws Exception {
        PreparedStatement preparedStatement = null;
        int modifyRows = 0;

        if (connection!=null){
            String sql = "update smbms_user set userName=?,gender=?,birthday=?,phone=?,address=?,userRole=?,modifyBy=?,modifyDate=? where id = ? ";
            Object params[] = {user.getUserName(),user.getGender(),user.getBirthday(),
                    user.getPhone(),user.getAddress(),user.getUserRole(),user.getModifiedBy(),
                    user.getModifiedDate(),user.getId()};
            modifyRows = BaseDao.executeUpdate(connection, preparedStatement, sql, params);
            BaseDao.closeResource(null,preparedStatement,null);
        }
        return modifyRows;
    }
}
