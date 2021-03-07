package ConnectSql;

import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * className: ConnectTest1<p>
 * description: 连接mysql数据库测试
 * @author theoldzheng@163.com  @ZYD
 * @create 2021.3.6 21:36
 */
public class ConnectTest1 {
    /**
     * 方式一：特点不灵活
     * 创建mysql数据库连接：
     * 1.导入mysql的驱动jar包
     * 2.建立lib关联
     * 3.代码实现连接
     * <p>
     * The new driver class is `com.mysql.cj.jdbc.Driver'
     * url:
     * jdbc:mysql
     * test
     */
    @Test
    public void TestConnection() throws SQLException {
        //1.获取driver对象 并抛出异常(暂时)
        Driver driver = new com.mysql.cj.jdbc.Driver();

        //2.创建配置文件，将用户名密码封装(用于连接数据库)
        Properties info = new Properties();
        info.setProperty("user", "root");
        info.setProperty("password", "123456");

        //创建连接
        //创建字符串存放当前数据库的url
        String url = "jdbc:mysql://localhost:3306/testforconnection";
        Connection connection = driver.connect(url, info);

        System.out.println(connection);
    }

    /**
     * 方式二：实现对方式一的部分改变
     */
    @Test
    public void TestConnection1() throws Exception {
        //在获取Driver对象的时候我们采用反射动态的获取反射
        Class clazz = Class.forName("com.mysql.cj.jdbc.Driver");
        Driver driver = (Driver) clazz.newInstance();

        //提供连接时需要提供的信息
        String url = "jdbc:mysql://localhost:3306/testforconnection";
        Properties info = new Properties();
        info.setProperty("user", "root");
        info.setProperty("password", "123456");

        Connection connect = driver.connect(url, info);
        System.out.println(connect);
    }

    /**
     * 方式三：实现对方法二的部分改变
     * 使用DriverManager替换Driver
     */
    @Test
    public void TestConnect2() throws Exception {
        //获取Driver类的实现类对象
        Class clazz = Class.forName("com.mysql.cj.jdbc.Driver");
        Driver driver = (Driver) clazz.newInstance();

        //提供数据库连接信息
        String url = "jdbc:mysql://localhost:3306/testforconnection";
        String user = "root";
        String password = "123456";

        //注册驱动
        DriverManager.registerDriver(driver);

        //获取连接  这里有三种构造器可以使用
        Connection connection = DriverManager.getConnection(url, user, password);
        System.out.println(connection);
    }

    /**
     * 可以省略加载驱动的步骤(mysql内置静态代码块，实现驱动的注册)
     */
    @Test
    public void TestConnection3() throws Exception {
        //加载Driver
        Class clazz = Class.forName("com.mysql.cj.jdbc.Driver");


        //提供信息
        String url = "jdbc:mysql://localhost:3306/testforconnection";
        Properties info = new Properties();
        info.setProperty("user", "root");
        info.setProperty("password", "123456");

        Connection conn = DriverManager.getConnection(url, info);
        System.out.println(conn);
    }

    /**
     * 方法五  最终版
     * 通过配置文件的方式实现数据库连接
     * 比较灵活 可以实现对多个种类的数据库的灵活转换，可以执行较强
     * 数据库等基本信息被封装，不暴露于代码，有益于后期维护
     */
    @Test
    public void TestConnectionFinally() throws Exception {

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
        Connection conn = DriverManager.getConnection(url, user, password);
        System.out.println(conn);
    }

}













