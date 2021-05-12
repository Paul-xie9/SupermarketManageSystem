package com.xbc.servlet.user;

import com.xbc.entity.User;
import com.xbc.service.user.UserServiceImpl;
import com.xbc.util.Constants;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class LoginServlet extends HttpServlet {
    /**
     * Servlet：控制层，调用业务层代码
     * @param req
     * @param resp
     * @throws ServletException
     * @throws IOException
     */

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("\n==========进入LoginServlet...=============\n");
        /**
         * 获取前端传过来用户名和密码的参数
         */
        String userCode = req.getParameter("userCode");
        String userPassword = req.getParameter("userPassword");

        /**
         * 和数据库中的密码进行比对，调用业务层
         */
        UserServiceImpl userService = new UserServiceImpl();
        User user = userService.login(userCode, userPassword);  //这里已经把登录的人查出来了

        if (user!=null){
            //将用户的信息存到session中
            req.getSession().setAttribute(Constants.USER_SESSION,user); //将登录用户的所有信息存到session中
            //跳转到主页
            resp.sendRedirect("jsp/frame.jsp");
        }else {
            req.setAttribute("error","用户名和密码错误！");  //提示错误
            req.getRequestDispatcher("login.jsp").forward(req,resp); //转发到登录界面
        }
        System.out.println("\n============LoginServlet结束...===========\n");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doGet(req, resp);
    }
}
