package OrderSystem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

public class OperateTransaction {

    /**
     * 购买商品
     * （形成订单，减少商品数量）
     * @param conn
     * @param goodsid
     * @param goodsnum
     * @param orderid
     * @param orderkind
     * @throws SQLException
     */
    public void buyTransaction(Connection conn, int goodsid, int goodsnum,
                               int orderid, int orderkind) throws SQLException {

        try {
            jdbcUtils.startTransaction(conn);

            ResultSet rs = null;
            PreparedStatement st = null;
            rs = jdbcUtils.selectOnlyGoods(conn, st, rs, goodsid);

            // 查找要购买的商品是否存在，若存在，记录信息
            boolean p = false;
            int goodsprice = 0;
            int goodsnumall = 0;
            String goodsname = null;
            while(rs.next()) {
                // 检验商品是否存在且数量充足
                if(goodsid == rs.getInt("goodsid")
                    && goodsnum <= rs.getInt("goodsnum")) {
                    // 商品存在
                    p = true;
                    goodsprice = rs.getInt("goodsprice");
                    goodsnumall = rs.getInt("goodsnum");
                    goodsname = rs.getString("goodsname");
                }
            }

            if(!p) {
                // 商品不存在
                jdbcUtils.isExistedGoodsException(p);
            }else {
                // 改变商品数量
                jdbcUtils.changeGoods(conn, goodsid, goodsname, goodsprice,
                        goodsnumall - goodsnum);

                // 添加订单
                jdbcUtils.addOrder(conn, orderid, orderkind, goodsid,
                        goodsnum, goodsnum * goodsprice);
            }

            // 完成订单
            jdbcUtils.commitTransaction(conn);
            System.out.println("交易成功！");

        }catch (SQLException e) {
            jdbcUtils.rollbackTransaction(conn);
            e.printStackTrace();
        }
    }

    /**
     * 撤销订单
     * （在撤销订单的同时，恢复该订单相关商品的数量）
     * @param conn
     * @param orderid
     * @throws SQLException
     */
    public void withdrawTransaction(Connection conn, int orderid) throws SQLException {

        try {
            // 开启事务
            jdbcUtils.startTransaction(conn);

            ResultSet rs1 = null,rs2 = null;
            PreparedStatement st = null;

            // 获取订单列表
            rs1 = jdbcUtils.selectOnlyOrder(conn, st, rs1, orderid);

            // 查看是否存在要删除的订单，并获取对应商品id
            int goodsid = 0;
            int goodsnum = 0;
            boolean p = false;
            while(rs1.next()) {
                if(orderid == rs1.getInt("orderid")) {
                    p = true;
                    goodsid = rs1.getInt("goodsid");
                    goodsnum = rs1.getInt("goodsnum");
                }
            }

            if(!p) {
                jdbcUtils.isExistedOrderException(p);
            }else {
                // 若存在该订单，则先将商品数量恢复
                rs2 = jdbcUtils.selectOnlyGoods(conn,st, rs2, goodsid);
                // 获取商品信息以备修改
                String goodsname = null;
                int goodsprice = 0;
                int goodsnumall = 0;

                while(rs2.next()) {
                    goodsname = rs2.getString("goodsname");
                    goodsprice = rs2.getInt("goodsprice");
                    goodsnumall = rs2.getInt("goodsnum");
                }

                // 恢复商品数量
                int i = jdbcUtils.changeGoods(conn, goodsid, goodsname,goodsprice,goodsnumall + goodsnum);
                if(i > 0) {
                    System.out.println("恢复相关商品数量！");
                }

                // 删除订单
                jdbcUtils.deleteOrder(conn,orderid);

                // 提交
                jdbcUtils.commitTransaction(conn);
                System.out.println("撤销订单成功！");
            }
        } catch (SQLException e) {
            jdbcUtils.rollbackTransaction(conn);
            e.printStackTrace();
        }
    }

    /**
     * 回收商品
     * （即删除商品，也删除存在该商品的订单）
     * @param conn
     * @param goodsid
     * @throws SQLException
     */
    public void deleteGoods(Connection conn, int goodsid) throws SQLException {

        try {
            // 开启事务
            jdbcUtils.startTransaction(conn);

            // 寻找该商品
            ResultSet rs1 = null;
            PreparedStatement st = null;
            rs1 = jdbcUtils.selectOnlyGoods(conn, st, rs1, goodsid);
            boolean p = false;
            while (rs1.next()) {
                if(goodsid == rs1.getInt("goodsid")) {
                    p = true;
                }
            }

            // 不存在则抛出异常
            if(!p) {
                jdbcUtils.isExistedGoodsException(p);
            }

            // 寻找与该商品有关订单
            boolean q = false;
            int orderid = 0;
            ResultSet rs2 = null;
            rs2 = jdbcUtils.selectOrder(conn, rs2, goodsid);
            while (rs2.next()) {
                if(goodsid == rs2.getInt("goodsid")) {
                    q = true;
                    orderid = rs2.getInt("orderid");
                }
            }

            // 删除与该商品有关订单
            if(q) {
                int i = jdbcUtils.deleteOrder(conn, orderid);
                if(i > 0) {
                    System.out.println("删除与该商品有关订单成功！");
                }

            }

            // 删除该商品
            if(p) {
                int i = jdbcUtils.deleteGoods(conn,goodsid);
                if(i > 0) {
                    System.out.println("删除该商品成功！");
                }
            }
            // 提交事务
            jdbcUtils.commitTransaction(conn);

        }catch (SQLException e) {
            jdbcUtils.rollbackTransaction(conn);
            e.printStackTrace();
        }
    }


}
