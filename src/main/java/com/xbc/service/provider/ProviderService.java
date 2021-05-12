package com.xbc.service.provider;

import com.xbc.entity.Provider;

import java.util.List;

public interface ProviderService {
    //获取供应商List
    public List<Provider> getProviderList(String proName,String proCode, int currentPageNo, int pageSize);

    //获取供应商数量
    public int getProvierCount();
}
