package com.xbc.service.role;

import com.xbc.dao.BaseDao;
import com.xbc.dao.role.RoleDao;
import com.xbc.dao.role.RoleDaoImpl;
import com.xbc.entity.Role;

import java.sql.Connection;
import java.util.List;

public class RoleServiceImpl implements RoleService {
    /**
     * 引入Dao
     */
    private RoleDao roleDao;

    public RoleServiceImpl() {
        roleDao = new RoleDaoImpl();
    }

    /**
     * 获取角色列表
     *
     * @return
     */
    @Override
    public List<Role> getRoleList() {
        Connection connection = null;
        List<Role> roleList = null;

        try {
            connection = BaseDao.getConnection();
            roleList = roleDao.getRoleList(connection);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            BaseDao.closeResource(connection, null, null);
        }

        return roleList;
    }
}
