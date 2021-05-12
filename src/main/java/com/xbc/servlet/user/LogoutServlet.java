package com.xbc.servlet.user;

import com.xbc.util.Constants;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 退出登录，即移除session就可以了
 */
public class LogoutServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("\n=============LogoutServlet开始...==========\n");
        req.getSession().removeAttribute(Constants.USER_SESSION);   //移除session
        resp.sendRedirect(req.getContextPath()+"/login.jsp"); //返回登录界面
        System.out.println("\n=============LogoutServlet结束...==========\n");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doGet(req, resp);
    }
}
