package DAO;

import bean.Customer;

import java.sql.Connection;
import java.sql.Date;
import java.util.List;

/**
 * Description:
 * @author theoldzheng@163.com  @ZYD
 * @create 2021.3.8 20:15
 */
public class CustomerDAOImpl extends BaseDAO<Customer> implements CustomerDAO {
    @Override
    public void insert(Connection conn, Customer customer) {
        String sql = "insert into customers(name,email,birth) value(?,?,?)";
        updateData(conn, customer.getName(), customer.getEmail(), customer.getBirth());
    }

    @Override
    public void deleteById(Connection conn, int id) {
        String sql = "delete from customers where id = ?";
        updateData(conn, sql, id);
    }

    @Override
    public void update(Connection conn, Customer customer) {
        String sql = "update customers  set(name,email,birth) where id = ?";
        updateData(conn, sql, customer.getName(), customer.getEmail(), customer.getBirth(), customer.getId());
    }

    @Override
    public Customer getCustomerById(Connection conn, int id) {
        String sql = "select * from customers where id = ? ";
        searchInformation(Customer.class, conn, sql, id);
        return null;
    }

    @Override
    public List<Customer> getAll(Connection conn) {
        String sql = "select * from customers";
        List<Customer> list = getAll(conn);
        return list;
    }

    @Override
    public Long getCount(Connection conn) {
        String sql = "select count(*) from customers";
        return getValue(conn, sql);
    }

    @Override
    public Date getMaxBirth(Connection conn) {
        String sql = "select max(birth) from customers";
        return getValue(conn, sql);
    }
}
