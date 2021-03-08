package UpdateWithTX;

import ConnectionsUtils.JDBCUtils;
import org.junit.Test;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * className: updateWithTX<p>
 * description: 数据库事务测试
 * @author theoldzheng@163.com  @ZYD
 * @create 2021.3.8 15:44
 */
public class updateWithTX {
    @Test
    public void test01() {
        //情景描述，当转账时，由于系统故障(网络等)造成中断，但是数据库内容被改变，导致严重错误
        //数据库事务负责绑定 转账时的发送房和接收方的内容为一个事务，当操作都完成，那么才能算是一个事务的完成
        //回滚的要求。只能回滚到上一个提交点 commit
        //针对于commit 数据库的DDL操作完成后会自动完成commit 且无法通过 更改自动提交操作完成 取消提交
        //            数据库的DML操作默认情况下是每执行完都会进行一次提交，但是可以通过设置取消自动提交，进而控制commit点
        //默认关闭连接时，也会自动提交数据
/**
 *数据库隔离级别
 * READ-UNCOMMITTED
 * READ-COMMITTED  适用
 * REPEATABLE -READ
 */
        //处理案例
        Connection conn = null;
        try {
            //获取连接  让处于同一个事务中的两个事情都使用同一个connection连接
            conn = JDBCUtils.getConnection();
            //1.关闭自动提交
            conn.setAutoCommit(false);

            String sql1 = "update test01 set money = money - 100 where id = ?";

            JDBCUtils.updateData1(conn, sql1, 1);

            //模拟网络中断测试效果
//            System.out.println( 1 / 0);

            String sql2 = "update test01 set money = money + 100 where id = ?";
            JDBCUtils.updateData1(conn, sql2, 2);
            //2.手动提交数据
            conn.commit();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            //3.处理异常：回滚
            try {
                conn.rollback();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } finally {
            try { //打开自动提交
                conn.setAutoCommit(true);
            } catch (Exception throwables) {
                throwables.printStackTrace();
            }
            JDBCUtils.closeConnection(conn, null);
        }
    }

    @Test
    public void test02() throws SQLException {
        //代码实现对数据库隔离级别的设置为我们想要的结果
        //获取连接
        Connection conn = JDBCUtils.getConnection();
        //修改隔离级别
        conn.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
//        conn.setAutoCommit(false);


    }
}
