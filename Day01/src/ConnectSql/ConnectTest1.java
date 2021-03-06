package ConnectSql;

import java.sql.Connection;
import java.sql.Driver;
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
     * 创建mysql数据库连接：
     * 1.导入mysql的驱动jar包
     * 2.建立lib关联
     * 3.代码实现连接
     *
     * The new driver class is `com.mysql.cj.jdbc.Driver'
     * url:
     * jdbc:mysql
     * test
     */
    public static void main(String[] args) throws SQLException {
        //1.创建driver对象 并抛出异常(暂时)
        Driver driver = new com.mysql.cj.jdbc.Driver();

        //2.创建配置文件，将用户名密码封装(用于连接数据库)
        Properties info = new Properties();
        info.setProperty("user","root");
        info.setProperty("password","123456");

        //创建连接
        //创建字符串存放当前数据库的url
        String url = "jdbc:mysql://localhost:3306/testforconnection";
        Connection connection = driver.connect(url,info);

        System.out.println(connection);
    }
}
