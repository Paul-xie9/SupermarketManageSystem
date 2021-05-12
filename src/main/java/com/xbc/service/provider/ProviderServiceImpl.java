package com.xbc.service.provider;

import com.xbc.dao.BaseDao;
import com.xbc.dao.provider.ProviderDao;
import com.xbc.dao.provider.ProviderDaoImpl;
import com.xbc.dao.user.UserDao;
import com.xbc.dao.user.UserDaoImpl;
import com.xbc.entity.Provider;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class ProviderServiceImpl implements ProviderService {
    /**
     * 必须的
     */
    private ProviderDao providerDao;

    //private BillDao  billDao;
    public ProviderServiceImpl() {
        providerDao = new ProviderDaoImpl();
        // billDao = new BillDaoImpl();
    }


    /**
     * 返回供应商List
     *
     * @param proName
     * @param proCode
     * @param currentPageNo
     * @param pageSize
     * @return
     */
    @Override
    public List<Provider> getProviderList(String proName, String proCode, int currentPageNo, int pageSize) {
        Connection conn = null;
        List<Provider> providerList = null;

        try {
            conn = BaseDao.getConnection();
            providerList = providerDao.getProviderList(conn, proName, proCode, currentPageNo, pageSize);
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            BaseDao.closeResource(conn,null,null);
        }
        return providerList;
    }

    /**
     * 获取供应商数量
     * @return 返回总数
     */
    @Override
    public int getProvierCount() {
        Connection conn = null;
        int count = 0;

        try{
            conn = BaseDao.getConnection();
            count = providerDao.getProvierCount(conn);
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            BaseDao.closeResource(conn,null,null);
        }
        return count;
    }
}
