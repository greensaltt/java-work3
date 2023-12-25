package OrderSystem;

import org.apache.commons.dbcp2.BasicDataSourceFactory;

import javax.sql.DataSource;
import java.io.InputStream;
import java.sql.*;
import java.util.Date;
import java.util.Properties;

public class jdbcUtils {

    /**
     * 读properties
     */
    private static DataSource dataSource = null;
    static {

        try {
            InputStream in = jdbcUtils.class.getClassLoader().getResourceAsStream("OrderSystem/DBCP.properties");
            Properties properties = new Properties();
            properties.load(in);

            dataSource = BasicDataSourceFactory.createDataSource(properties);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @return 获取连接
     * @throws SQLException
     */
    public static Connection getConncetion() throws SQLException {
        return dataSource.getConnection();
    }


    /**
     * 释放资源
     * @param conn
     * @param st
     * @param rs
     */
    public static void release(Connection conn, PreparedStatement st, ResultSet rs){
        if(rs!=null){
            try {
                rs.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if(st!=null){
            try {
                st.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if(conn!=null){
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 实现对商品的增加
     *
     * @return 返回改变的行数
     * @throws SQLException
     */
    public static int addGoods(Connection conn, int goodsid, String goodsname,
                               int goodsprice, int goodsnum) throws SQLException {

        String sql = "insert into `trade_goods`(`goodsid`,`goodsname`,`goodsprice`,`goodsnum`) VALUES(?,?,?,?)";
        PreparedStatement st = conn.prepareStatement(sql);

        st.setInt(1,goodsid);
        st.setString(2,goodsname);
        st.setInt(3,goodsprice);
        st.setInt(4,goodsnum);

        return st.executeUpdate();

    }

    /**
     *  实现对订单的增加
     *
     * @return 返回改变的行数
     * @throws SQLException
     */
    public static int addOrder(Connection conn, int orderid, int orderkind,
                               int goodsid, int goodsnum, int orderprice) throws SQLException {

        String sql = "insert into `trade_order`(`orderid`,`orderkind`,`goodsid`,`goodsnum`,`order_set`,`orderprice`) VALUES(?,?,?,?,?,?)";
        PreparedStatement st = conn.prepareStatement(sql);

        st.setInt(1,orderid);
        st.setInt(2,orderkind);
        st.setInt(3,goodsid);
        st.setInt(4,goodsnum);
        st.setDate(5, new java.sql.Date(new Date().getTime()));
        st.setInt(6,orderprice);

        return st.executeUpdate();

    }

    /**
     * 通过商品id实现对商品的删减
     *
     * @return 返回改变的行数
     * @throws SQLException
     */
    public static int deleteGoods(Connection conn, int goodsid) throws SQLException {

        String sql = "delete from `trade_goods` where `goodsid`=?";
        PreparedStatement st = conn.prepareStatement(sql);

        st.setInt(1,goodsid);

        return st.executeUpdate();

    }

    /**
     * 通过订单id实现对订单的删减
     *
     * @return 返回改变的行数
     * @throws SQLException
     */
    public static int deleteOrder(Connection conn, int orderid) throws SQLException {

        String sql = "delete from `trade_order` where `orderid`=?";
        PreparedStatement st = conn.prepareStatement(sql);

        st.setInt(1,orderid);

        return st.executeUpdate();

    }

    /**
     * 实现对商品的修改
     *
     * @return
     * @throws SQLException
     */
    public static int changeGoods(Connection conn, int goodsid, String goodsname,
                                  int goodsprice, int goodsnum) throws SQLException {

        String sql = "update trade_goods set `goodsname`=?,`goodsprice`=?,`goodsnum`=? where goodsid=?";
        PreparedStatement st = conn.prepareStatement(sql);

        st.setString(1,goodsname);
        st.setInt(2,goodsprice);
        st.setInt(3,goodsnum);
        st.setInt(4,goodsid);

        return st.executeUpdate();

    }

    /**
     * 实现对订单的修改
     *
     * @return
     * @throws SQLException
     */
    public static int changeOrder(Connection conn, int orderid, int orderkind, int goodsid,
                                  int goodsnum, Date order_set, int orderprice) throws SQLException {

        String sql = "update trade_order set `orderkind`=?,`goodsid`=?,`goodsnum`=?,`order_set`=?,`orderprice`=? where orderid=?";
        PreparedStatement st = conn.prepareStatement(sql);

        st.setInt(1,orderkind);
        st.setInt(2,goodsid);
        st.setInt(3,goodsnum);
        st.setDate(4, (java.sql.Date) order_set);
        st.setInt(5,orderprice);
        st.setInt(6,orderid);

        return st.executeUpdate();
    }

    /**
     * 实现对商品的单独查询
     *
     * @return 返回结果集
     * @throws SQLException
     */
    public static ResultSet selectOnlyGoods(Connection conn,PreparedStatement st, ResultSet rs, int goodsid) throws SQLException {


        String sql = "select `goodsid`,`goodsname`,`goodsprice`,`goodsnum` " +
                "from trade_goods " +
                "where goodsid=?";
        st = conn.prepareStatement(sql);

        st.setInt(1,goodsid);

        rs = st.executeQuery();

        return rs;
    }

    /**
     * 实现对订单的单独查询
     *
     * @return 返回结果集
     * @throws SQLException
     */
    public static ResultSet selectOnlyOrder(Connection conn, PreparedStatement st, ResultSet rs, int orderid) throws SQLException {

        String sql = "select `orderid`,`orderkind`,`goodsid`,`goodsnum`,`order_set`,`orderprice` " +
                "from trade_order " +
                "where orderid=? ";
        st = conn.prepareStatement(sql);

        st.setInt(1,orderid);

        rs = st.executeQuery();

        return rs;
    }

    /**
     * 通过订单id查询对应购买的商品信息
     *
     * @return 返回相关商品的结果集
     * @throws SQLException
     */
    public static ResultSet selectGoods(Connection conn, ResultSet rs, int orderid) throws SQLException {

        String sql = "select g.goodsid,`goodsname`,`goodsprice`,g.goodsnum " +
                "from trade_goods g " +
                "inner join trade_order o " +
                "on g.goodsid=o.goodsid " +
                "where o.orderid=?";
        PreparedStatement st = conn.prepareStatement(sql);

        st.setInt(1,orderid);

        rs = st.executeQuery();

        return rs;
    }

    /**
     * 通过商品id查询对应购买该商品的订单信息
     *
     * @return 返回相关订单的结果集
     * @throws SQLException
     */
    public static ResultSet selectOrder(Connection conn, ResultSet rs, int goodsid) throws SQLException {

        String sql = "select `orderid`,`orderkind`,o.goodsid,o.goodsnum,`order_set`,`orderprice` " +
                "from trade_order o " +
                "inner join trade_goods g " +
                "on o.goodsid=g.goodsid " +
                "where o.goodsid=?";
        PreparedStatement st = conn.prepareStatement(sql);

        st.setInt(1,goodsid);

        rs = st.executeQuery();

        return rs;
    }

    /**
     * 开始事务
     * @param conn
     * @throws SQLException
     */
    public static void startTransaction(Connection conn) throws SQLException {
        conn.setAutoCommit(false);
    }

    /**
     * 提交事务
     * @param conn
     * @throws SQLException
     */
    public static void commitTransaction(Connection conn) throws SQLException {
        conn.commit();
    }

    /**
     * 回滚事务
     * @param conn
     * @throws SQLException
     */
    public static void rollbackTransaction(Connection conn) throws SQLException {
        conn.rollback();
        System.out.println("操作失败！");
    }

    /**
     * 商品存在与否异常
     * @param isExisted
     */
    public static void isExistedGoodsException(boolean isExisted) {

        if(isExisted) {
            throw new MyException("商品已存在！");
        }else {
            throw new MyException("商品不存在！");
        }

    }

    /**
     * 订单存在与否异常
     * @param isExisted
     */
    public static void isExistedOrderException(boolean isExisted) {

        if(isExisted) {
            throw new MyException("订单已存在！");
        }else {
            throw new MyException("订单不存在！");
        }

    }


}
