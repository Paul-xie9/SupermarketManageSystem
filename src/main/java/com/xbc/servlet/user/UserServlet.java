package com.xbc.servlet.user;

import com.alibaba.fastjson.JSONArray;
import com.mysql.cj.util.StringUtils;
import com.xbc.entity.Role;
import com.xbc.entity.User;
import com.xbc.service.role.RoleServiceImpl;
import com.xbc.service.user.UserService;
import com.xbc.service.user.UserServiceImpl;
import com.xbc.util.Constants;
import com.xbc.util.PageSupport;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * 用户的相关servlet复用
 */
public class UserServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/html;charset=utf-8");//设置参数目的是输出字体含有中文时做相应的处理
        System.out.println("\n=============进入UserServlet...==========\n");

        /*=============================== 修改用户密码 ===================================*/
        String method = req.getParameter("method");
        if (method.equals("savepwd") && method != null) {       //修改密码
            System.out.println("savepwd");
            this.updataPassword(req, resp);

        } else if (method.equals("pwdmodify_ajax") && method != null) {    //"pwdmodify_ajax" 是通过Ajax里面传递的参数得到的，验证密码
            System.out.println("pwdmodify_ajax");
            this.passwordModify(req, resp);

        } else if (method.equals("query") && method != null) {  //query从前端获取到的，显示从数据库中查到的所有用户信息
            System.out.println("query");
            this.query(req, resp);

        } else if (method.equals("deluser_ajax") && method != null) {//"deluser_ajax",通过ajax里面传递的参数得到，删除用户
            System.out.println("deluser_ajax");
            this.deleteUserById(req, resp);

        } else if (method.equals("getrolelist") && method != null) { //"getrolelist_ajax",通过ajax里面的参数得到所有的角色列表
            System.out.println("getrolelist");
            this.getRoleList(req, resp);

        } else if (method.equals("ucexist") && method != null) {//"ucexist",通过ajax里面的参数得到其userCode
            System.out.println("ucexist");
            this.userCodeExist(req, resp);

        } else if (method.equals("addUser") && method != null) {  //"addUser",通过前端获得
            System.out.println("addUser");
            this.addUser(req, resp);

        }else if (method.equals("modifyUser") && method != null){   //modifyUser从js中获取
            System.out.println("modifyUser");
            this.getUserById(req,resp,"usermodify.jsp");

        } else if (method.equals("modifysave") && method != null) { //从前端页面获取
            System.out.println("modifysave");
            this.modifyUser(req, resp);
        }else if (method.equals("view") && method != null){//从js中获取
            this.getUserById(req,resp,"userview.jsp");
        }

        System.out.println("\n=============UserServlet结束...==========\n");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doGet(req, resp);
    }


    /**
     * 提取修改密码的方法，实现servlet复用
     *
     * @param req
     * @param resp
     */
    public void updataPassword(HttpServletRequest req, HttpServletResponse resp) {
        //从session里面拿id
        Object o = req.getSession().getAttribute(Constants.USER_SESSION);   //获取当前用户的session
        String newpassword = req.getParameter("newpassword");   //获取前端输入的新密码
        boolean flag = false;

        if (o != null && !StringUtils.isNullOrEmpty(newpassword)) { //if里面可以写成 o!=null&& newpassword！=null&& newpassword.length!=null
            UserService userService = new UserServiceImpl();
            flag = userService.updatePassWord(((User) o).getId(), newpassword);
            if (flag) {
                req.setAttribute("message", "密码修改成功，请重新登录！");
                req.getSession().removeAttribute(Constants.USER_SESSION);   //密码修改成功，移除当前session
            } else {
                req.setAttribute("message", "密码修改失败！");
            }
        } else {
            req.setAttribute("message", "新密码有问题！");
        }
        try {
            req.getRequestDispatcher("/jsp/pwdmodify.jsp").forward(req, resp);
        } catch (ServletException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 验证用户的旧密码，从session中拿到
     * 密码验证
     *
     * @param req
     * @param resp
     */
    public void passwordModify(HttpServletRequest req, HttpServletResponse resp) {
        Object o = req.getSession().getAttribute(Constants.USER_SESSION);
        String oldpassword_ajax = req.getParameter("oldpassword_ajax"); //ajax中从前端获取到的密码

        HashMap<String, String> resultMap = new HashMap<>();

        if (o == null) {   //session失效，session过期了
            resultMap.put("result", "sessionerror");
        } else if (StringUtils.isNullOrEmpty(oldpassword_ajax)) { //输入的密码为空
            resultMap.put("result", "error");
        } else { //验证正确
            String userPassword = ((User) o).getUserPassword(); //获取session中用户的密码
            if (oldpassword_ajax.equals(userPassword)) {
                resultMap.put("result", "true");
            } else {
                resultMap.put("result", "false");
            }
        }

        //把resultMap转换成json对象输出
        resp.setContentType("application/json");
        try {
            PrintWriter writer = resp.getWriter();
            writer.write(JSONArray.toJSONString(resultMap));    //阿里巴巴的一个json工具类，转换格式的
            writer.flush(); //刷新
            writer.close(); //关闭
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * 重点，难点！
     * 查询用户列表信息
     *
     * @param req
     * @param resp
     */
    private void query(HttpServletRequest req, HttpServletResponse resp) {
        //查询用户列表
        //从前端获取数据
        //查询用户列表
        String queryUserName = req.getParameter("queryname");
        String temp = req.getParameter("queryUserRole");
        String pageIndex = req.getParameter("pageIndex");
        int queryUserRole = 0;

        //获取用户列表
        UserServiceImpl userService = new UserServiceImpl();
        List<User> userList = null;

        //第一此请求肯定是走第一页，页面大小固定的
        //设置页面容量
        int pageSize = Constants.PAGE_SIZE;
        ;//把它设置在配置文件里,后面方便修改
        //当前页码
        int currentPageNo = 1;

        if (queryUserName == null) {
            queryUserName = "";
        }
        if (temp != null && !temp.equals("")) {
            queryUserRole = Integer.parseInt(temp);
        }
        if (pageIndex != null) {
            currentPageNo = Integer.parseInt(pageIndex);
        }
        //获取用户总数（分页	上一页：下一页的情况）
        //总数量（表）
        int totalCount = userService.getUserCount(queryUserName, queryUserRole);

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

        userList = userService.getUserList(queryUserName, queryUserRole, currentPageNo, pageSize);
        req.setAttribute("userList", userList);

        RoleServiceImpl roleService = new RoleServiceImpl();
        List<Role> roleList = roleService.getRoleList();
        req.setAttribute("roleList", roleList);

        //设置页码
        req.setAttribute("totalCount", totalCount);
        req.setAttribute("currentPageNo", currentPageNo);
        req.setAttribute("totalPageCount", totalPageCount);

        req.setAttribute("queryUserName", queryUserName);
        req.setAttribute("queryUserRole", queryUserRole);

        //返回前端
        try {
            req.getRequestDispatcher("userlist.jsp").forward(req, resp);
        } catch (ServletException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * 根据 用户id删除此用户
     *
     * @param req
     * @param resp
     */
    public void deleteUserById(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        String uid = req.getParameter("uid_ajax");   //从ajax获取uid
        int deleteId = 0;
        try {
            deleteId = Integer.parseInt(uid);   //将获取到的uid转换成int型
        } catch (NumberFormatException e) {
            e.printStackTrace();
            deleteId = 0;
        }

        HashMap<String, String> resultMap = new HashMap<>();
        if (deleteId <= 0) {
            resultMap.put("delResult", "notexist");
        } else {
            UserService userService = new UserServiceImpl();
            if (userService.deleteUserById(deleteId)) {
                resultMap.put("delResult", "true");
                req.getRequestDispatcher(req.getContextPath()+"/jsp/user.do?method=query").forward(req,resp);
            } else {
                resultMap.put("delResult", "false");
            }
        }

        //把resultMap转换成json对象输出
        resp.setContentType("application/json");
        try {
            PrintWriter writer = resp.getWriter();
            writer.write(JSONArray.toJSONString(resultMap));    //阿里巴巴的一个json工具类，转换格式的
            writer.flush(); //刷新
            writer.close(); //关闭
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取用户角色表单内容
     *
     * @param req
     * @param resp
     */
    public void getRoleList(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        RoleServiceImpl roleService = new RoleServiceImpl();
        List<Role> roleList = roleService.getRoleList();

        //把roleList转换成json对象输出
        resp.setContentType("application/json");
        PrintWriter outPrintWriter = resp.getWriter();
        outPrintWriter.write(JSONArray.toJSONString(roleList));
        outPrintWriter.flush();
        outPrintWriter.close();
    }

    /**
     * 通过userCode判断是否已存在此user
     *
     * @param req
     * @param resp
     * @throws IOException
     */
    public void userCodeExist(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String userCode = req.getParameter("userCode"); //从前端获取userCoude

        HashMap<String, String> resultMap = new HashMap<String, String>();
        if (StringUtils.isNullOrEmpty(userCode)) {//userCode == null || userCode.equals("")
            resultMap.put("userCode", "exist");
        } else {
            UserService userService = new UserServiceImpl();
            User user = userService.getUserNameByUserCode(userCode);
            if (null != user) {
                resultMap.put("userCode", "exist");
            } else {
                resultMap.put("userCode", "notexist");
            }
        }

        //把resultMap转为json字符串以json的形式输出
        //配置上下文的输出类型
        resp.setContentType("application/json");
        //从response对象中获取往外输出的writer对象
        PrintWriter outPrintWriter = resp.getWriter();
        //把resultMap转为json字符串 输出
        outPrintWriter.write(JSONArray.toJSONString(resultMap));
        outPrintWriter.flush();//刷新
        outPrintWriter.close();//关闭流
    }

    /**
     * 添加用户
     *
     * @param req
     * @param resp
     */
    public void addUser(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        /*================================ 从前端获取参数 ===============================*/
        System.out.println("进入addUser...");
        String userCode = req.getParameter("userCode"); //从前端获取到的userCode
        String userName = req.getParameter("userName"); //从前端获取到的userName
        String userPassword = req.getParameter("userPassword"); //从前端获取到的userPassword
        String gender = req.getParameter("gender"); //从前端获取到的gender
        String birthday = req.getParameter("birthday"); //从前端获取到的birthday
        String phone = req.getParameter("phone"); //从前端获取到的phone
        String address = req.getParameter("address"); //从前端获取到的address
        String userRole = req.getParameter("userRole"); //从前端获取到的userRole
        /*================================= 设置user的信息 =======================================*/
        User user = new User();
        user.setUserCode(userCode);
        user.setUserName(userName);
        user.setUserPassword(userPassword);
        user.setGender(Integer.valueOf(gender));
        user.setPhone(phone);
        user.setAddress(address);
        user.setUserRole(Integer.valueOf(userRole));
        try {
            user.setBirthday(new SimpleDateFormat("yyyy-MM-dd").parse(birthday));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        /*================================== 设置user的创建时间和被创建者 ================================*/
        User userSession = (User) req.getSession().getAttribute(Constants.USER_SESSION);    //从当前session中获取到对应的用户
        user.setCreatedDate(new Date());
        user.setCreatedBy(userSession.getId());

        UserServiceImpl userService = new UserServiceImpl();
        Boolean addUser = userService.addUser(user);
        if (addUser) {
            req.setAttribute("tips", "添加用户成功！");
            resp.sendRedirect(req.getContextPath() + "/jsp/user.do?method=query");
        } else {
            req.getRequestDispatcher("useradd.jsp").forward(req, resp);
        }

    }

    /**
     * 通過id查到用戶，并实现跳转页面
     * @param req
     * @param resp
     * @param url
     * @throws ServletException
     * @throws IOException
     */
    public void getUserById(HttpServletRequest req, HttpServletResponse resp, String url) throws ServletException, IOException {
        String uid = req.getParameter("uid");   //從Ajax獲取
        if (!StringUtils.isNullOrEmpty(uid)) {  //调用后台方法得到user对象
            UserServiceImpl userService = new UserServiceImpl();
            User userById = userService.getUserById(uid);
            req.setAttribute("user", userById); //user存入查到的user信息
            req.getRequestDispatcher(url).forward(req,resp);
        }
    }

    /**
     * 修改用于信息
     * @param req
     * @param resp
     * @throws IOException
     * @throws ServletException
     */
    public void modifyUser(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        System.out.println("进入modifyUser...");
        /*============================从前端获取信息==========================*/
        String uid = req.getParameter("uid");
        String userName = req.getParameter("userName");
        String gender = req.getParameter("gender");
        String birthday = req.getParameter("birthday");
        String phone = req.getParameter("phone");
        String address = req.getParameter("address");
        String userRole = req.getParameter("userRole");
        /*================================= 设置user的信息 =======================================*/
        User user = new User();
        user.setUserName(userName);
        user.setGender(Integer.valueOf(gender));
        user.setPhone(phone);
        user.setAddress(address);
        user.setUserRole(Integer.valueOf(userRole));
        try {
            user.setBirthday(new SimpleDateFormat("yyyy-MM-dd").parse(birthday));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        /*================================== 设置user的创建时间和被创建者 ================================*/
        User userSession = (User) req.getSession().getAttribute(Constants.USER_SESSION);    //从当前session中获取到对应的用户
        user.setCreatedDate(new Date());
        user.setCreatedBy(userSession.getId());

        UserServiceImpl userService = new UserServiceImpl();
        Boolean addUser = userService.modifyUser(user);
        if (addUser) {
            req.setAttribute("tips", "修改用户成功！");
            resp.sendRedirect(req.getContextPath() + "/jsp/user.do?method=query");
        } else {
            req.getRequestDispatcher("usermodify.jsp").forward(req, resp);
        }
    }
}


