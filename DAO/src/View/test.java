package View;

import DAO.BaseDAO;
import DAO.CustomerDAOImpl;
import JDBCUtils.JDBCUtils;
import bean.Customer;
import org.junit.Test;

import java.sql.Connection;
import java.sql.Date;
import java.util.List;

/**
 * Description:
 * @author theoldzheng@163.com  @ZYD
 * @create 2021.3.8 20:50
 */
public class test {
    @Test
    public void test01() {

//        Date date1 = new Date(System.currentTimeMillis());
//        Customer customer = new Customer(1, "王江", "wangjiang@163.com", date1);

        Connection conn = JDBCUtils.getConnection();
        System.out.println(conn);
//        List<Customer> all = new CustomerDAOImpl().getAll(conn);
//        System.out.println(all);


    }

}
