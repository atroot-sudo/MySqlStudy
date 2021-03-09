package bean;

import ConnectionsUtils.JDBCUtils;
import DAO.CustomerDAOImpl;
import org.apache.commons.dbutils.QueryLoader;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.junit.Test;

import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;

/**
 * Description:
 * @author theoldzheng@163.com  @ZYD
 * @create 2021.3.8 21:28
 */
public class Test1 {
    @Test
    public void test01() throws Exception {

        Date date1 = new Date(System.currentTimeMillis());
        Customer customer = new Customer(1, "孔乙己", "kongyiji@163.com", date1);
        CustomerDAOImpl customerDAO = new CustomerDAOImpl();
        Connection conn = JDBCUtils.getConnection2();
        if (conn != null) {
            System.out.println("数据库连接成功......");
        }
        //关于阿帕奇的操作数据库的类的测试
        QueryRunner queryRunner = new QueryRunner();
        String sql = "insert into customers(id,name,email,birth) value(?,?,?,?)";

        int a = queryRunner.update(conn, sql, 5,"成龙", "zhangsan@qq.com", date1);
        System.out.println("对" + a + "条数据造成影响！");

//        List<Customer> list = new CustomerDAOImpl().getAll(conn);
        //测试获取操作
        Customer customerById = new CustomerDAOImpl().getCustomerById(conn, 2);
        System.out.println(customerById);
        //测试更改操作
        Customer customerById1 = new CustomerDAOImpl().getCustomerById(conn, 1);
        System.out.println(customerById1);

        new CustomerDAOImpl().update(conn, customer);
        Customer customerById2 = new CustomerDAOImpl().getCustomerById(conn, 1);
        System.out.println(customerById2);

        //测试删除操作
//        new CustomerDAOImpl().deleteById(conn, 5); // 删除成龙
        Customer customer1 = new Customer(66, "葫芦娃", "huluwa@qq.com", date1);
        //测试插入操作
        new CustomerDAOImpl().insert(conn, customer1);

        Date maxBirth = new CustomerDAOImpl().getMaxBirth(conn);
        LocalDate localDate = maxBirth.toLocalDate();
        System.out.println("最大生日为：" + localDate);
        Long count = customerDAO.getCount(conn);
        System.out.println("记录条数为：" + count);

        try {
            conn.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }


    }
}
