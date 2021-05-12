package com.xbc.dao.role;

import com.xbc.dao.BaseDao;
import com.xbc.entity.Role;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class RoleDaoImpl implements RoleDao {
    /**
     * 获取角色列表
     *
     * @param connection
     * @return
     * @throws Exception
     */
    @Override
    public List<Role> getRoleList(Connection connection) throws Exception {
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        ArrayList<Role> roleArrayList = new ArrayList<>();  //接收返回的结果集

        if (connection != null) {
            String sql = "select *from smbms_role";
            Object[] params = {};
            resultSet = BaseDao.executeQuery(connection, sql, preparedStatement, resultSet, params);

            while (resultSet.next()) {
                Role role = new Role();
                role.setId(resultSet.getInt("id"));
                role.setRoleCode(resultSet.getString("roleCode"));
                role.setRoleName(resultSet.getString("roleName"));
                roleArrayList.add(role);    //将从数据库中获取到的数据放到集合中
            }
            BaseDao.closeResource(null, preparedStatement, resultSet);
        }
        return roleArrayList;
    }
}
