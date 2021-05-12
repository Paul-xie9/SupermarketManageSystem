package com.xbc.dao;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.Properties;

/**
 * 操作数据库的公共类
 */
public class BaseDao {
    private static String driver;
    private static String url;
    private static String username;
    private static String password;

    static { //静态代码块，类一加载的时候就初始化了
        Properties properties = new Properties();
        //通过类加载器读取对应的资源
        InputStream resource = BaseDao.class.getClassLoader().getResourceAsStream("db.properties");

        try {
            properties.load(resource);
        } catch (IOException e) {
            e.printStackTrace();
        }

        /*以上表示资源已经读取到了，下面是获取对应具体的值*/

        driver = properties.getProperty("driver");
        url = properties.getProperty("url");
        username = properties.getProperty("username");
        password = properties.getProperty("password");
    }

    /*==============连接数据库==============================*/
    public static Connection getConnection() {
        Connection connection = null;
        try {
            Class.forName(driver);
            connection = DriverManager.getConnection(url, username, password);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return connection;
    }

    /*=====================编写查询公共方法=========================*/
    public static ResultSet executeQuery(Connection connection, String sql,PreparedStatement preparedStatement,ResultSet resultSet, Object[] params) {
        try {
            preparedStatement = connection.prepareStatement(sql);   //预编译sql，在后面直接执行就行了
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        for (int i = 0; i < params.length; i++) {
            try {
                //setObject,占位符从1开始，但是我们的数组是从0开始
                preparedStatement.setObject(i + 1, params[i]);
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }

        try {
            resultSet = preparedStatement.executeQuery();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return resultSet;
    }

    /*===================编写增删改公共方法=============================*/
    public static int executeUpdate(Connection connection, PreparedStatement preparedStatement ,String sql, Object[] params) throws Exception {
        preparedStatement = connection.prepareStatement(sql);//预编译sql

        for (int i = 0; i < params.length; i++) {
            preparedStatement.setObject(i + 1, params[i]);
        }

        int updateRows = preparedStatement.executeUpdate();
        return updateRows;
    }


    /*===========================关闭连接=================================*/
    public static boolean closeResource(Connection connection,PreparedStatement preparedStatement, ResultSet resultSet)  {
        boolean flag = true;

        if (resultSet!=null){
            try {
                resultSet.close();
                resultSet = null;   //GC回收
            } catch (SQLException throwables) {
                throwables.printStackTrace();
                flag = false;
            }
        }

        if (connection!=null){
            try {
                connection.close();
                connection = null;
            } catch (SQLException throwables) {
                throwables.printStackTrace();
                flag = false;
            }
        }

        if (preparedStatement!=null){
            try {
                preparedStatement.close();
                preparedStatement = null;
            } catch (SQLException throwables) {
                throwables.printStackTrace();
                flag = false;
            }
        }

        return flag;
    }

}
