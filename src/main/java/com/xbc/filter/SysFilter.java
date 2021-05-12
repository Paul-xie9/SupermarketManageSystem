package com.xbc.filter;

import com.xbc.entity.User;
import com.xbc.util.Constants;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class SysFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        System.out.println("SysFilter开始...");
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        //过滤器，从session中获取用户
        User user = (User) request.getSession().getAttribute(Constants.USER_SESSION);

        if (user==null){//不在session中
            response.sendRedirect(request.getContextPath()+"/error.jsp");
        }else {
            filterChain.doFilter(servletRequest,servletResponse);
        }
        System.out.println("SysFilter结束...");
    }

    @Override
    public void destroy() {

    }
}
