package com.xbc.dao.provider;

import com.xbc.entity.Provider;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public interface ProviderDao {
    //获取供应商信息
    public List<Provider> getProviderList(Connection connection,String proName, String proCode,int currentPageNo, int pageSize) throws SQLException;

    //获取供应商总数
    public int getProvierCount(Connection connection) throws SQLException;
}
