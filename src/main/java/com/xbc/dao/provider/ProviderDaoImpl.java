package com.xbc.dao.provider;

import com.mysql.cj.util.StringUtils;
import com.xbc.dao.BaseDao;
import com.xbc.entity.Provider;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ProviderDaoImpl implements ProviderDao {
    /**
     * 获取用户信息
     *
     * @return 返回供应商信息
     * @throws SQLException
     */
    @Override
    public List<Provider> getProviderList(Connection connection, String proName, String proCode, int currentPageNo, int pageSize) throws SQLException {
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        List<Provider> providerList = new ArrayList<>();
        if (connection != null) {
            StringBuffer sql = new StringBuffer();
            sql.append("select * from smbms_provider where 1=1 ");

            //追加sql语句
            ArrayList<Object> arrayList = new ArrayList<>();
            if (!StringUtils.isNullOrEmpty(proName)) {
                sql.append(" and proName like ?");
                arrayList.add("%" + proName + "%");
            }

            if (!StringUtils.isNullOrEmpty(proCode)) {
                sql.append("  and proCode like ?");
                arrayList.add("%" + proCode + "%");
            }

            //分页支持
            sql.append(" order by createdDate DESC limit ?,?");
            currentPageNo = (currentPageNo - 1) * pageSize;
            arrayList.add(currentPageNo);
            arrayList.add(pageSize);

            Object params[] = arrayList.toArray();
            System.out.println("sql --> " + sql.toString());

            resultSet = BaseDao.executeQuery(connection, sql.toString(), preparedStatement, resultSet, params);
            while (resultSet.next()) {
                Provider _provider = new Provider();
                _provider.setId(resultSet.getInt("id"));
                _provider.setProCode(resultSet.getString("proCode"));
                _provider.setProName(resultSet.getString("proName"));
                _provider.setProDesc(resultSet.getString("proDesc"));
                _provider.setProContact(resultSet.getString("proContact"));
                _provider.setProPhone(resultSet.getString("proPhone"));
                _provider.setProAddress(resultSet.getString("proAddress"));
                _provider.setProFax(resultSet.getString("proFax"));
                _provider.setCreatedDate(resultSet.getTimestamp("createdDate"));
                providerList.add(_provider);
            }
            BaseDao.closeResource(null, preparedStatement, resultSet);
        }
        return providerList;
    }

    /**
     * 获取供应商总数
     *
     * @param connection
     * @return
     * @throws SQLException
     */
    @Override
    public int getProvierCount(Connection connection) throws SQLException {
        PreparedStatement ps = null;
        ResultSet rs = null;
        int count = 0;

        if (connection != null) {
            connection = BaseDao.getConnection();
            String sql = "select count(1) as count from smbms_provider";

            Object params[] = {};
            rs = BaseDao.executeQuery(connection, sql, ps, rs, params);
            if (rs.next()) {
                count = rs.getInt("count");
            }
            BaseDao.closeResource(null, ps, rs);
        }
        return count;
    }
}
