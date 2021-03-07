package PreparedStatementTest;

import ConnectionsUtils.JDBCUtils;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Properties;

/**
 * className: prepareStatement<p>
 * description: prepareStatement测试
 * @author theoldzheng@163.com  @ZYD
 * @create 2021.3.7 20:05
 */
public class prepareStatement {
    /**
     * 由于Statement的一些弊端，我们使用PrepareStatement
     */

    //实现增加操作
    @Test
    public void test01() {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            //首先获取数据库连接
            //获取加载
            InputStream is = prepareStatement.class.getClassLoader().getResourceAsStream("jdbc.properties");

            Properties pro = new Properties();
            pro.load(is);

            String classDriver = pro.getProperty("classDriver");
            String url = pro.getProperty("url");
            String user = pro.getProperty("user");
            String password = pro.getProperty("password");

            //加载驱动
            Class.forName(classDriver);

            //创建连接
            conn = DriverManager.getConnection(url, pro);
            if (conn != null) {
                System.out.println("连接数据库成功");
            }


            //获取连接成功开始操作数据库，通过connection来获取PrepareStatement对象
            String sql = "insert into student(name,age,class) value(?,?,?)";
            ps = conn.prepareStatement(sql);

            //开始填充占位符
            ps.setString(1, "姜子牙");
            ps.setInt(2, 18);
            ps.setInt(3, 2);

            //开始执行操作
            ps.execute();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            //关闭连接
            if (conn != null){
                try {
                    conn.close();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
            if (ps != null){
                try {
                    ps.close();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
        }
    }
    //实现修改操作
    @Test
    public void test02(){
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = JDBCUtils.getConnection();
            String sql = "update student set name = ? where id = ?";
            //预编译SQL语句
            ps = conn.prepareStatement(sql);

            ps.setString(1,"迪丽热巴");
            ps.setInt(2,2);

            ps.execute();
        } catch (Exception throwables) {
            throwables.printStackTrace();
        } finally {
            //关闭资源
            JDBCUtils.closeConnection(conn,ps);
        }
    }
    @Test
    public void test03(){
        //若sql语句错误则会导致执行失败导致出错
        String sql = "delete from student where id = ?";

        JDBCUtils.updateData(sql,1);
    }
}
