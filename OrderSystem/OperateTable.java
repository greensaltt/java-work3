package OrderSystem;

import java.sql.*;
import java.util.Date;

public class OperateTable {

    /**
     * 增加商品
     *
     * @param conn
     * @param goodsid
     * @param goodsname
     * @param goodsprice
     * @param goodsnum
     */
    public void addGoods(Connection conn, int goodsid, String goodsname, int goodsprice, int goodsnum) {

        try {
            // 用于查询表内是否已存在同一id的商品
            ResultSet rsnow = null;
            Statement stnow = conn.createStatement();
            String sql = "select `goodsid` from trade_goods";
            rsnow = stnow.executeQuery(sql);

            boolean p = false;
            while (rsnow.next()) {
                if (goodsid == rsnow.getInt(1)) {
                    // 存在id相同商品，抛出商品已存在异常
                    p = true;
                    jdbcUtils.isExistedGoodsException(p);
                }
            }
            // 不存在相同id商品，可以插入
            if (!p) {
                int i = jdbcUtils.addGoods(conn, goodsid, goodsname, goodsprice, goodsnum);
                if (i > 0) System.out.println("添加商品成功！");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 添加订单
     * @param conn
     * @param orderid
     * @param orderkind
     * @param goodsid
     * @param goodsnum
     */
    public void addOrder(Connection conn, int orderid, int orderkind, int goodsid,
                         int goodsnum) {

        try {
            // 用于查询表内是否已存在同一id的订单
            ResultSet rsnow = null;
            Statement stnow = conn.createStatement();
            String sql = "select `orderid` from trade_order";
            rsnow = stnow.executeQuery(sql);

            boolean p = false;
            while (rsnow.next()) {
                if (orderid == rsnow.getInt(1)) {
                    // 若已存在，抛出异常
                    p = true;
                    jdbcUtils.isExistedOrderException(p);
                }
            }

            if (!p) {
                // 用于计算订单价格
                ResultSet rs2 = null;
                PreparedStatement st = null;
                rs2 = jdbcUtils.selectOnlyGoods(conn, st, rs2, goodsid);
                int orderprice = 0;
                while (rs2.next()) {
                    orderprice = rs2.getInt("goodsprice") * goodsnum;
                }

                int i = jdbcUtils.addOrder(conn, orderid, orderkind, goodsid, goodsnum, orderprice);
                if (i > 0) {
                    System.out.println("添加订单成功！");
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public void deleteGoods(Connection conn, int goodsid) {

        try {
            ResultSet rsnow = null;
            Statement stnow = conn.createStatement();
            String sql = "select `goodsid` from trade_goods";
            rsnow = stnow.executeQuery(sql);

            // 删除商品
            boolean p = false;
            int i = 0;
            while (rsnow.next()) {
                if (goodsid == rsnow.getInt(1)) {
                    p = true;
                    i = jdbcUtils.deleteGoods(conn, goodsid);
                    if (i > 0) {
                        System.out.println("删除商品成功！");
                    }
                    break;
                }
            }
            if (!p) {
                jdbcUtils.isExistedGoodsException(p);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 通过订单id删除订单
     * @param conn
     * @param orderid
     */
    public void deleteOrder(Connection conn, int orderid) {

        try {
            // 检测是否存在
            ResultSet rs1 = null;
            Statement st = conn.createStatement();
            String sql = "select `orderid` from trade_order";
            rs1 = st.executeQuery(sql);

            boolean p = false;
            while (rs1.next()) {
                if (orderid == rs1.getInt(1)) {
                    p = true;
                    int i = jdbcUtils.deleteOrder(conn, orderid);
                    if (i > 0) {
                        // 若存在，删除该订单
                        System.out.println("删除订单成功！");
                    }
                }
            }

            if (!p) {
                jdbcUtils.isExistedOrderException(p);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 改变产品
     * @param conn
     * @param goodsid
     * @param goodsname
     * @param goodsprice
     * @param goodsnum
     */
    public void changeGoods(Connection conn, int goodsid, String goodsname,
                            int goodsprice, int goodsnum) {

        try {
            int i = jdbcUtils.changeGoods(conn, goodsid, goodsname, goodsprice, goodsnum);
            if (i > 0) {
                System.out.println("修改商品成功！");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 修改订单
     * @param conn
     * @param orderid
     * @param orderkind
     * @param goodsid
     * @param goodsnum
     * @param order_set
     * @param orderprice
     */
    public void changeOrder(Connection conn, int orderid, int orderkind, int goodsid,
                            int goodsnum, Date order_set, int orderprice) {

        try {
            int i = jdbcUtils.changeOrder(conn, orderid, orderkind, goodsid, goodsnum, order_set, orderprice);
            if (i > 0) {
                System.out.println("修改订单成功！");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 通过商品id查商品
     * @param conn
     * @param st
     * @param rs
     * @param goodsid
     */
    public void selectOnlyGoods(Connection conn, PreparedStatement st, ResultSet rs, int goodsid) {
        try {
            rs = jdbcUtils.selectOnlyGoods(conn, st, rs, goodsid);
            boolean p = false;
            while (rs.next()) {
                if (rs.getInt(1) != 0) {
                    p = true;
                    System.out.println("商品编号: " + rs.getInt("goodsid"));
                    System.out.println("商品名称: " + rs.getString("goodsname"));
                    System.out.println("商品价格: " + rs.getInt("goodsprice"));
                    System.out.println("商品库存: " + rs.getInt("goodsnum"));
                }
            }

            if (!p) {
                jdbcUtils.isExistedGoodsException(p);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    /**
     * 通过订单id查订单
     * @param conn
     * @param st
     * @param rs
     * @param orderid
     */
    public void selectOnlyOrder(Connection conn, PreparedStatement st, ResultSet rs, int orderid) {
        try {
            rs = jdbcUtils.selectOnlyOrder(conn, st, rs, orderid);
            boolean p = false;
            while (rs.next()) {
                if (rs.getInt(1) != 0) {
                    p = true;
                    System.out.println("订单编号: " + rs.getInt("orderid"));
                    System.out.println("订单批次: " + rs.getInt("orderkind"));
                    System.out.println("商品编号: " + rs.getInt("goodsid"));
                    System.out.println("购买数量: " + rs.getInt("goodsnum"));
                    System.out.println("下单时间: " + rs.getDate("order_set"));
                    System.out.println("订单总价: " + rs.getInt("orderprice"));
                }
            }

            if (!p) {
                jdbcUtils.isExistedOrderException(p);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 通过订单id查商品
     * @param conn
     * @param rs
     * @param orderid
     */
    public void selectGoods(Connection conn, ResultSet rs, int orderid) {
        try {
            rs = jdbcUtils.selectGoods(conn, rs, orderid);
            boolean p = false;

            while (rs.next()) {
                if (rs.getInt("goodsid") != 0) {
                    p = true;
                    System.out.println("相关商品编号为: " + rs.getInt("goodsid"));
                    System.out.println("相关商品名称为: " + rs.getString("goodsname"));
                    System.out.println("相关商品价格为: " + rs.getInt("goodsprice"));
                    System.out.println("相关商品库存为: " + rs.getInt("goodsnum"));
                }
            }

            if (!p) {
                jdbcUtils.isExistedGoodsException(p);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 通过商品id查订单
     * @param conn
     * @param rs
     * @param goodsid
     */
    public void selectOrder(Connection conn, ResultSet rs, int goodsid) {
        try {
            rs = jdbcUtils.selectOrder(conn, rs, goodsid);
            boolean p = false;

            while (rs.next()) {
                if (rs.getInt("orderid") != 0) {
                    p = true;
                    System.out.println("相关订单编号为: " + rs.getInt("orderid"));
                    System.out.println("相关订单批次为: " + rs.getInt("orderkind"));
                    System.out.println("相关商品编号为: " + rs.getInt("goodsid"));
                    System.out.println("相关商品数量为: " + rs.getInt("goodsnum"));
                    System.out.println("相关订单下单时间为: " + rs.getDate("order_set"));
                    System.out.println("相关订单总额为: " + rs.getInt("orderprice"));
                }
            }

            if (!p) {
                jdbcUtils.isExistedOrderException(p);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 对商品进行排序后输出
     * @param conn
     * @param type 排序依据
     */
    public void sequenceGoods(Connection conn, String type) {

        try {
            // 获得商品列表的所有信息
            ResultSet rs = null;
            Statement st = null;
            String sql = "select * from trade_goods order by " + type;
            st = conn.createStatement();
            rs = st.executeQuery(sql);

            System.out.println("商品编号\t商品名\t商品价格\t商品余量");
            while(rs.next()) {
                System.out.println(rs.getInt("goodsid") + "\t" +
                        rs.getString("goodsname")+ "\t" +
                        rs.getInt("goodsprice") + "\t" +
                        rs.getInt("goodsnum"));
            }

        }catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 对订单进行排序后输出
     * @param conn
     * @param type 排序依据
     */
    public void sequenceOrder(Connection conn, String type) {

        try {
            // 获得订单列表的所有信息
            ResultSet rs = null;
            Statement st = null;
            String sql = "select * from trade_order order by " + type;
            st = conn.createStatement();
            rs = st.executeQuery(sql);

            System.out.println("订单编号\t订单批次\t购买商品编号\t购买商品数量\t下单时间\t订单总额");
            while(rs.next()) {
                System.out.println(rs.getInt("orderid") + "\t" +
                        rs.getInt("orderkind")+ "\t" +
                        rs.getInt("goodsid") + "\t" +
                        rs.getInt("goodsnum") + "\t" +
                        rs.getDate("order_set") + "\t" +
                        rs.getInt("orderprice"));
            }

        }catch (SQLException e) {
            e.printStackTrace();
        }
    }

}

