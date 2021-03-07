package ConnectionsUtils;

import ConnectSql.ConnectTest1;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.Properties;

/**
 * className: connection<p>
 * description: 返回一个connetion对象 和 实现关闭资源的操作
 * @author theoldzheng@163.com  @ZYD
 * @create 2021.3.7 20:49
 */
public class JDBCUtils {
    public static Connection getConnection(){
        Connection conn = null;
        try {
            //提供加载器
            ClassLoader classLoader = ConnectTest1.class.getClassLoader();
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
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return  conn;
    }

    public static void closeConnection(Connection connection, Statement statement){
        //关闭连接
        if (connection != null){
            try {
                connection.close();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
        if (statement != null){
            try {
                statement.close();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
    }
    //实现增删改查操作的封装
    public static void updateData(String sql, Object ...args){
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = JDBCUtils.getConnection();
            //预编译SQL语句
            ps = conn.prepareStatement(sql);

            //填充占位符
            for (int i = 0; i < args.length; i++) { //要注意的是，这里的循环次数，由sql语句中的占位符决定，这里获取长度作为循环次数
                ps.setObject(i + 1,args[i]); // 注意 +1，prepareIndex从1开始
            }

            //开始执行操作
            ps.execute();
        } catch (Exception throwables) {
            throwables.printStackTrace();
        } finally {
            //关闭资源
            JDBCUtils.closeConnection(conn,ps);
        }
    }
}
