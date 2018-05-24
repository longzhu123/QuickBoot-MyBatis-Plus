package org.yj.auto.produce.mvc.util;

import java.lang.reflect.Field;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by yangjie on 2017/8/9.
 */
public class DbUtil {


    public static Connection getConnection(){

        Connection connection = null;
        try {
            Class.forName(PropertiesUtil.get("driverClass"));
            connection = DriverManager.getConnection(PropertiesUtil.get("url"),PropertiesUtil.get("username"),PropertiesUtil.get("password"));
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return  connection;
    }

    public  static  int executeUpdate(String sql,Object [] obj){
        Connection con = null;
        PreparedStatement pre = null;
        int row = 0;
        try {
            con = getConnection();
            pre = con.prepareStatement(sql);
            if(obj != null){
                for (int i = 0; i < obj.length; i++) {
                    pre.setObject(i+1, obj[i]);
                }
            }
            row = pre.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return  row;
    }

    public static  <T> List<T> executeQuery(Class<T> clazz, String sql, Object [] obj){
        List list = new ArrayList();
        Connection con = null;
        PreparedStatement pre = null;
        ResultSet rs = null;
        try {
            con = getConnection();
            pre = con.prepareStatement(sql);
            if(obj != null){
                for (int i = 0; i < obj.length; i++) {
                    pre.setObject(i+1, obj[i]);
                }
            }
            rs = pre.executeQuery();
            while(rs.next()){
                Object object = clazz.getDeclaredConstructor(new Class[]{}).newInstance(new Object[]{});
                Field[] field = clazz.getDeclaredFields();
                for (Field f : field) {
                    f.setAccessible(true);
                    String name = f.getName();
                    if(isExistColumn(rs,name.toUpperCase())){
                        String typeName = f.getType().getSimpleName();
                        if (typeName.equals("int")) {
                            f.setInt(object, rs.getInt(name));
                        } else if (typeName.equals("double")) {
                            f.setDouble(object, rs.getDouble(name));
                        } else if (typeName.equals("String")) {
                            f.set(object, rs.getString(name));
                        } else if (typeName.equals("Date")) {
                            f.set(object, rs.getDate(name));
                        }
                    }
                }
                list.add(object);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }catch (Exception e) {
            e.printStackTrace();
        }finally{
            closeAll(con, pre, rs);
        }

        return list;
    }


    public static  void closeAll(Connection con,PreparedStatement pre,ResultSet rs){
        try {
            if(rs != null){
                rs.close();
            }
            if(pre != null){
                pre.close();
            }
            if(con != null){
                con.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    /**
     * 判断查询结果集中是否存在某列
     * @param rs 查询结果集
     * @param columnName 列名
     * @return true 存在; false 不存咋
     */
    public static boolean isExistColumn(ResultSet rs, String columnName) {
        try {
            if (rs.findColumn(columnName) > 0 ) {
                return true;
            }
        }
        catch (SQLException e) {
            return false;
        }
        return false;
    }


}
