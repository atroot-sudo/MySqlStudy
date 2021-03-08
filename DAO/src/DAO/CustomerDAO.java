package DAO;

import bean.Customer;

import java.sql.Connection;
import java.sql.Date;
import java.util.List;

/**
 * Description:此接口用于规范针对于Customer表的操作
 * @author theoldzheng@163.com  @ZYD
 * @create 2021.3.8 20:07
 */
public interface CustomerDAO {
    void insert(Connection conn, Customer customer);

    void deleteById(Connection conn, int id);

    void update(Connection conn, Customer customer);

    Customer getCustomerById(Connection conn, int id);

    List<Customer> getAll(Connection conn);

    Long getCount(Connection conn);

    Date getMaxBirth(Connection conn);
}
