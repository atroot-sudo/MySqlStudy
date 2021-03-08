package bean;

import DAO.BaseDAO;
import DAO.CustomerDAOImpl;
import org.junit.Test;

import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.util.List;

/**
 * Description:
 * @author theoldzheng@163.com  @ZYD
 * @create 2021.3.8 21:28
 */
public class Test1 {
    @Test
    public void test01() {

        Date date1 = new Date(System.currentTimeMillis());
        Customer customer = new Customer(1, "王江", "wangjiang@163.com", date1);

        Connection conn = BaseDAO.getConnection();
        if (conn != null){
            System.out.println("数据库连接成功......");
        }


//        List<Customer> list = new CustomerDAOImpl().getAll(conn);
        //测试获取操作
        Customer customerById = new CustomerDAOImpl().getCustomerById(conn, 2);
        System.out.println(customerById);
        //测试更改操作
        Customer customerById1 = new CustomerDAOImpl().getCustomerById(conn, 1);
        System.out.println(customerById1);

        new CustomerDAOImpl().update(conn,customer);
        Customer customerById2 = new CustomerDAOImpl().getCustomerById(conn, 1);
        System.out.println(customerById2);

        //测试删除操作
        new CustomerDAOImpl().deleteById(conn,5); // 删除成龙
        Customer customer1 = new Customer(66,"葫芦娃","huluwa@qq.com",date1);
        //测试插入操作
        new CustomerDAOImpl().insert(conn,customer1);

        try {
            conn.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }


    }
}
