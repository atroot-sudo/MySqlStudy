package ConnectionsUtils;


import bean.Customer;
import com.alibaba.druid.pool.DruidDataSourceFactory;
import com.mchange.v2.c3p0.ComboPooledDataSource;

import javax.sql.DataSource;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * className: connection<p>
 * description: 返回一个connection对象 和 实现关闭资源的操作
 * @author theoldzheng@163.com  @ZYD
 * @create 2021.3.7 20:49
 */
@SuppressWarnings({"DuplicatedCode", "UnusedReturnValue", "unused"})
public class JDBCUtils {
    /**
     * Description 使用数据库连接池提供一个Connection对象（基于c3p0）
     * @Param []
     * @return java.sql.Connection
     */
    private static ComboPooledDataSource cpds = new ComboPooledDataSource("helloc3p0");

    //对数据库连接操作的封装
    @SuppressWarnings("DuplicatedCode")
    /**
     *Description 基于阿里的Druid(德鲁伊)的数据库连接池的测试
     *@Param []
     *@return java.sql.Connection
     */
    public static Connection getConnection2() throws Exception {
        Properties pros = new Properties();
        ClassLoader systemClassLoader = ClassLoader.getSystemClassLoader();
        InputStream rs = systemClassLoader.getResourceAsStream("DruidConnection.properties");
        pros.load(rs);

        DataSource dataSource = DruidDataSourceFactory.createDataSource(pros);
        Connection conn = dataSource.getConnection();
        return conn;
    }

    public static Connection getConnection1() throws SQLException {

        //获取数据库连接池
        Connection connection = cpds.getConnection();
        return connection;

    }


    public static Connection getConnection() {
        Connection conn = null;
        try {
            //提供加载器，准备加载配置文件

            ClassLoader classLoader = JDBCUtils.class.getClassLoader();
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

    //实现增删改查操作的封装
    //返回值，int 为0，则更改失败，其余责成功
    public static int updateData(String sql, Object... args) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = JDBCUtils.getConnection();
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
            //关闭资源
            JDBCUtils.closeConnection(conn, ps);
        }
        return 0;
    }

    public static int updateData1(Connection conn, String sql, Object... args) {

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
        }
        return 0;
    }

    //针对当前已有表student的select查询（单条信息查询）
    public static Customer searchInformation(String sql, Object... args) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet resultSet = null;
        try {
            //获取数据库连接
            conn = JDBCUtils.getConnection();
            ps = conn.prepareStatement(sql);
            //进行占位符填充
            for (int i = 0; i < args.length; i++) {
                ps.setObject(i + 1, args[i]);
            }

            //执行并获取结果集
            resultSet = ps.executeQuery();
            //获取结果集的元数据
            ResultSetMetaData metaData = resultSet.getMetaData();

            //获取列数
            int columnCount = metaData.getColumnCount();

            Customer customer = new Customer();
            if (resultSet.next()) {
                for (int i = 0; i < columnCount; i++) {
                    //获取列值
                    Object columnValue = resultSet.getObject(i + 1);
                    //获取列名，准备进行对应
                    //获取列名的常规调用方法 resultSet.getColumnName()
                    //为了解决某些情况下，列名不匹配的情况，需要获取列的别名 getColumnLabel()，在没有别名的时候也会获得列名
                    //String columnName = metaData.getColumnName(i + 1);
                    String columnName = metaData.getColumnLabel(i + 1);
                    //通过反射来获取对应的属性进行设置赋值
                    Field field = Customer.class.getDeclaredField(columnName);

                    //进行设置属性
                    field.setAccessible(true);
                    field.set(customer, columnValue);

                }
                return customer;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //关闭资源
            JDBCUtils.closeConnection(conn, ps, resultSet);
        }


        return null;
    }

    //通用的查询操作
    public static <T> T searchInformation(Class<T> clazz, String sql, Object... args) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet resultSet = null;
        try {
            //获取数据库连接
            conn = JDBCUtils.getConnection();
            ps = conn.prepareStatement(sql);
            //进行占位符填充
            for (int i = 0; i < args.length; i++) {
                ps.setObject(i + 1, args[i]);
            }

            //执行并获取结果集
            resultSet = ps.executeQuery();
            //获取结果集的元数据
            ResultSetMetaData metaData = resultSet.getMetaData();

            //获取列数
            int columnCount = metaData.getColumnCount();

            T t = clazz.newInstance();
            if (resultSet.next()) {
                for (int i = 0; i < columnCount; i++) {
                    //获取列值
                    Object columnValue = resultSet.getObject(i + 1);
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
                return t;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //关闭资源
            JDBCUtils.closeConnection(conn, ps, resultSet);
        }


        return null;
    }

    //声明通用的查询的泛型方法
    public static <T> List<T> getForList(Class<T> clazz, String sql, Object... args) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet resultSet = null;
        try {
            //获取数据库连接
            conn = JDBCUtils.getConnection();
            ps = conn.prepareStatement(sql);
            //进行占位符填充
            for (int i = 0; i < args.length; i++) {
                ps.setObject(i + 1, args[i]);
            }

            //执行并获取结果集
            resultSet = ps.executeQuery();
            //获取结果集的元数据
            ResultSetMetaData metaData = resultSet.getMetaData();

            //获取列数
            int columnCount = metaData.getColumnCount();

            ArrayList<T> list = new ArrayList<>();
            while (resultSet.next()) {
                T t = clazz.newInstance();
                for (int i = 0; i < columnCount; i++) {
                    //获取列值
                    Object columnValue = resultSet.getObject(i + 1);
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
            JDBCUtils.closeConnection(conn, ps, resultSet);
        }
        return null;
    }
}
