package PreparedStatementTest;

import ConnectionsUtils.JDBCUtils;
import bean.Customer;
import org.junit.Test;

import java.io.*;
import java.sql.*;
import java.util.List;
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
        } catch (Exception throwables) {
            throwables.printStackTrace();
        } finally {
            //关闭连接
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
            if (ps != null) {
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
    public void test02() {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = JDBCUtils.getConnection();
            String sql = "update student set name = ? where id = ?";
            //预编译SQL语句
            ps = conn.prepareStatement(sql);

            ps.setString(1, "迪丽热巴");
            ps.setInt(2, 2);

            ps.execute();
        } catch (Exception throwables) {
            throwables.printStackTrace();
        } finally {
            //关闭资源
            JDBCUtils.closeConnection(conn, ps);
        }
    }

    @Test
    public void test03() {
        //若sql语句错误则会导致执行失败导致出错
        String sql = "insert into student(id,name,age,class) value (?,?,?,?)";
        JDBCUtils.updateData(sql, 5, "玉皇大帝", "1000", 100);
    }

    @Test
    public void test04() {
        InputStream is = null;
        Connection conn = null;
        ResultSet resultSet = null;
        try {
            //连接数据库进行操作
            ClassLoader classLoader = prepareStatement.class.getClassLoader();
            is = classLoader.getResourceAsStream("jdbc.properties");

            Properties pro = new Properties();
            pro.load(is);

            String url = pro.getProperty("url");
            String classDriver = pro.getProperty("classDriver");
            String user = pro.getProperty("user");
            String password = pro.getProperty("password");

            //加载驱动
            Class.forName(classDriver);

            //进行连接操作
            conn = DriverManager.getConnection(url, user, password);

            //准备进行操作
            String sql = "select  * from student where id = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            //对占位符进行填充
            ps.setObject(1, 2);

            //执行上述sql，并返回一个结果集
            resultSet = ps.executeQuery();

            //进行对返回的结果集进行操作，类似于迭代器，对下一个位置进行检测并返回，若有内容则返回布尔值
            if (resultSet.next()) {
                //对返回的一条信息进行信息获取
                int id = resultSet.getInt(1);
                String name = resultSet.getString(2);
                int age = resultSet.getInt(3);
                int classNum = resultSet.getInt(4);

                //不建议使用该方法
//                System.out.println(id + name + age + Class);
                //建议使用结果集类进行封装
//                Customer customer = new Customer(id, name, age, classNum);
//                System.out.println(customer);

            }
        } catch (Exception throwables) {
            throwables.printStackTrace();
        } finally {
            //关闭资源
            if (resultSet != null){
                try {
                    resultSet.close();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
            if (is != null){
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (conn != null){
                try {
                    conn.close();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
        }
    }

    //对封装后的查询方法进行测设
    @Test
    public  void test05(){
        String sql = "select id,name,age from student where name = ?";
        Customer customer = JDBCUtils.searchInformation(sql, "迪丽热巴");
        System.out.println(customer);
    }
    @Test
    public void test069(){
        String sql = "select id,name,age from student where id < ?";
        List<Customer> forList = JDBCUtils.getForList(Customer.class, sql, 5);
        forList.forEach(System.out :: println);
    }

    //向数据库中添加一张图片
    @Test
    public void test10() throws FileNotFoundException {
        //获取连接
        Connection conn = JDBCUtils.getConnection();
        String sql = "update student set photo = ? where name = ? ";
        FileInputStream is = new FileInputStream(new File("QQ图片.jpg"));
        JDBCUtils.updateData(sql,is,"姜子牙");

    }

    @Test
    public void test11() throws Exception {
        //批量操作数据库时的优化操作
        //两种方式 ： 通俗的称为 “攒sql”  以及 关闭自动提交数据库
        //获取连接
        Connection conn = JDBCUtils.getConnection();
        String sql = "insert into student(id,name,age,class) value (?,?,?,?)";
        long start = System.currentTimeMillis();
        PreparedStatement ps = conn.prepareStatement(sql);
        for (int i = 5; i < 10000; i++) {
            ps.setObject(1,i );
            ps.setObject(2,"test");
            ps.setObject(3,i +1);
            ps.setObject(4,i +2);

            //攒sql的方式实现优化
            ps.addBatch();
            //执行
            if ( i % 500 ==0){
                ps.executeBatch();
                //清理batch
                ps.clearBatch();
            }
        }

        long end = System.currentTimeMillis();   // 18681 普通方式为18秒  用Batch则为15623

        System.out.println((end - start));

        JDBCUtils.closeConnection(conn,ps);
    }
}