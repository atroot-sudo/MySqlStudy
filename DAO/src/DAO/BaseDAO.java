package DAO;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Description: BaseDAO
 * @author theoldzheng@163.com  @ZYD
 * @create 2021.3.8 18:25
 */
public abstract class BaseDAO<T> {
    private Class<T> clazz = null;
    /**
    *Description //获取当前BaseDAO的子类继承的父类中的泛型
    *@Param
    *@return
    */
    /*{
        Type genericSuperclass = this.getClass().getGenericSuperclass();
        ParameterizedType parameterizedType = (ParameterizedType) genericSuperclass;
        Type[] typeArguments = parameterizedType.getActualTypeArguments();//获取了父类的泛型参数
        clazz = (Class<T>) typeArguments[0];//泛型的第一个参数
    }*/

    /**
     * Description 用于获取数据库连接，返回一个Connection对象
     * @return java.sql.Connection
     * @Param []
     */
    public static Connection getConnection() {
        Connection conn = null;
        try {
            //提供加载器，准备加载配置文件

            ClassLoader classLoader = BaseDAO.class.getClassLoader();
            InputStream resourceAsStream = classLoader.getResourceAsStream("jdbc.properties");

            //进行加载  注意：只会识别到当前的src目录下
            Properties pro = new Properties();
            pro.load(resourceAsStream);

            //直接对properties进行获取
            String url = pro.getProperty("url");
            String user = pro.getProperty("user");
            String password = pro.getProperty("password");
            String classDriver = pro.getProperty("classDriver");
            //加载驱动
            Class.forName(classDriver);

            //进行连接
            conn = DriverManager.getConnection(url, user, password);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return conn;
    }

    /**
     * Description 用于关闭数据库连接
     * @return void
     * @Param [connection, statement]
     */
    public static void closeConnection(Connection connection, Statement statement) {
        //关闭连接
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
        if (statement != null) {
            try {
                statement.close();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
    }

    /**
     * Description 用于关闭数据库连接的重载方法(带结果集)
     * @return void
     * @Param [connection, statement, resultSet]
     */
    public static void closeConnection(Connection connection, Statement statement, ResultSet resultSet) {
        //关闭连接
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
        if (statement != null) {
            try {
                statement.close();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
        if (resultSet != null) {
            try {
                resultSet.close();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
    }

    /**
     * Description 实现对数据库的增删改的操作，升级版，需要传入连接Connection
     * @return 返回int型参数，若返回0，修改失败，大于0，修改成功
     * @Param
     */
    public static int updateData(Connection conn, String sql, Object... args) {

        PreparedStatement ps = null;
        try {
            //预编译SQL语句
            ps = conn.prepareStatement(sql);

            //填充占位符
            for (int i = 0; i < args.length; i++) { //要注意的是，这里的循环次数，由sql语句中的占位符决定，这里获取长度作为循环次数
                ps.setObject(i + 1, args[i]); // 注意 +1，prepareIndex从1开始
            }
            //开始执行操作
            return ps.executeUpdate();

        } catch (Exception throwables) {
            throwables.printStackTrace();
        } finally {
            BaseDAO.closeConnection(null, ps);
        }
        return 0;
    }

    /**
     * Description 用于返回查询单额全部参数 考虑事务
     * @return java.util.List<T>
     * @Param [clazz, conn, sql, args]
     */
    public static <T> List<T> getForList(Class<T> clazz, Connection conn, String sql, Object... args) {
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = conn.prepareStatement(sql);
            //进行占位符填充
            for (int i = 0; i < args.length; i++) {
                ps.setObject(i + 1, args[i]);
            }

            //执行并获取结果集
            rs = ps.executeQuery();
            //获取结果集的元数据
            ResultSetMetaData metaData = rs.getMetaData();

            //获取列数
            int columnCount = metaData.getColumnCount();

            ArrayList<T> list = new ArrayList<>();
            while (rs.next()) {
                T t = clazz.newInstance();
                for (int i = 0; i < columnCount; i++) {
                    //获取列值
                    Object columnValue = rs.getObject(i + 1);
                    //获取列名，准备进行对应
                    //获取列名的常规调用方法 resultSet.getColumnName()
                    //为了解决某些情况下，列名不匹配的情况，需要获取列的别名 getColumnLabel()，在没有别名的时候也会获得列名
                    //String columnName = metaData.getColumnName(i + 1);
                    String columnName = metaData.getColumnLabel(i + 1);
                    //通过反射来获取对应的属性进行设置赋值
                    Field field = clazz.getDeclaredField(columnName);

                    //进行设置属性
                    field.setAccessible(true);
                    field.set(t, columnValue);

                }
                //将对选哪个添加到list
                list.add(t);
            }
            return list;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //关闭资源
            BaseDAO.closeConnection(null, ps, rs);
        }
        return null;
    }

    /**
     * Description 通用的查询操作,实现对表数据的查询，返回一条数据结果
     * @return T
     * @Param [clazz, conn, sql, args]
     */
    public static <T> T searchInformation(Class<T> clazz, Connection conn, String sql, Object... args) {
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = conn.prepareStatement(sql);
            //进行占位符填充
            for (int i = 0; i < args.length; i++) {
                ps.setObject(i + 1, args[i]);
            }

            //执行并获取结果集
            rs = ps.executeQuery();
            //获取结果集的元数据
            ResultSetMetaData metaData = rs.getMetaData();
            //获取列数
            int columnCount = metaData.getColumnCount();

            T t = clazz.newInstance();
            if (rs.next()) {
                for (int i = 0; i < columnCount; i++) {
                    //获取列值
                    Object columnValue = rs.getObject(i + 1);
                    //获取列名，准备进行对应
                    //获取列名的常规调用方法 rs.getColumnName()
                    //为了解决某些情况下，列名不匹配的情况，需要获取列的别名 getColumnLabel()，在没有别名的时候也会获得列名
                    //String columnName = metaData.getColumnName(i + 1);
                    String columnName = metaData.getColumnLabel(i + 1);
                    //通过反射来获取对应的属性进行设置赋值
                    Field field = clazz.getDeclaredField(columnName);

                    //进行设置属性
                    field.setAccessible(true);
                    field.set(t, columnValue);

                }
                return t;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //关闭资源
            BaseDAO.closeConnection(null, ps, rs);
        }
        return null;
    }

    /**
     * Description 查询特殊值的通用方法
     * @return E
     * @Param [conn, sql, args]
     */
    public <E> E getValue(Connection conn, String sql, Object... args) {
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = conn.prepareStatement(sql);
            //填充占位符
            for (int i = 0; i < args.length; i++) {
                ps.setObject(i + 1, args[i]);
            }

            //执行操作 并返回一个结果集
            rs = ps.executeQuery();
            if (rs.next()) {
                return (E) rs.getObject(1);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
        }
        BaseDAO.closeConnection(null, ps, rs);

        return null;
    }
}
