package com.xbc.servlet.provider;

import com.xbc.entity.Provider;
import com.xbc.service.provider.ProviderServiceImpl;
import com.xbc.util.Constants;
import com.xbc.util.PageSupport;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

public class ProviderServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/html;charset=utf-8");//设置参数目的是输出字体含有中文时做相应的处理
        /*============================ 获取method的值 =======================*/
        String method = req.getParameter("method");
        if (method != null & method.equals("query")) {    //从前端获取
            System.out.println("query");
            this.query(req,resp);

        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doGet(req, resp);
    }

    public void query(HttpServletRequest req, HttpServletResponse resp) {
        //查询用户列表
        //从前端获取数据
        //查询用户列表
        String queryProCode = req.getParameter("queryProCode");
        String queryProName = req.getParameter("queryProName");
        String pageIndex = req.getParameter("pageIndex");

        //获取供应商列表
        ProviderServiceImpl providerService = new ProviderServiceImpl();
        List<Provider> providerList = null;

        //第一此请求肯定是走第一页，页面大小固定的
        //设置页面容量
        int pageSize = Constants.PAGE_SIZE;
        ;//把它设置在配置文件里,后面方便修改
        //当前页码
        int currentPageNo = 1;

        if (queryProCode == null) {
            queryProCode = "";
        }

        if (queryProName == null) {
            queryProName = "";
        }

        if (pageIndex != null) {
            currentPageNo = Integer.parseInt(pageIndex);
        }


        int totalCount = providerService.getProvierCount();

        //总页数支持
        PageSupport pageSupport = new PageSupport();
        pageSupport.setCurrentPageNo(currentPageNo);
        pageSupport.setPageSize(pageSize);
        pageSupport.setTotalCount(totalCount);

        int totalPageCount = pageSupport.getTotalPageCount();//总共有几页

        System.out.println("totalCount =" + totalCount);
        System.out.println("pageSize =" + pageSize);
        System.out.println("totalPageCount =" + totalPageCount);
        //控制首页和尾页
        //如果页面小于 1 就显示第一页的东西
        if (currentPageNo < 1) {
            currentPageNo = 1;
        } else if (currentPageNo > totalPageCount) {//如果页面大于了最后一页就显示最后一页
            currentPageNo = totalPageCount;
        }

        providerList = providerService.getProviderList(queryProName, queryProCode, currentPageNo, pageSize);
        req.setAttribute("providerList", providerList);

        //设置页码
        req.setAttribute("totalCount", totalCount);
        req.setAttribute("currentPageNo", currentPageNo);
        req.setAttribute("totalPageCount", totalPageCount);

        req.setAttribute("queryProCode", queryProCode);
        req.setAttribute("queryProName", queryProName);

        //返回前端
        try {
            req.getRequestDispatcher("providerlist.jsp").forward(req, resp);
        } catch (ServletException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
