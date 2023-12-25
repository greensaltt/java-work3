package OrderSystem;

import org.apache.commons.pool2.UsageTracking;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Date;

public class test {
    public static void main(String[] args) {
        Connection conn  = null;
        PreparedStatement st = null;
        ResultSet rs = null;
        OperateTable operatetable = new OperateTable();
        OperateTransaction operatetransaction = new OperateTransaction();

        // 共有两张表，trade_order与trade_goods
        // trade_goods 含有字段：goodsid(primary key),goodsname,goodsprice,goodsnum(库存)
        // trade_order 含有字段：orderid,orderkind(批次),goodsid(购买的商品),
        //                      goodsnum(数量),order_set(下单时间),orderprice(订单总额，自动计算)

        try {
            conn = jdbcUtils.getConncetion();

            // ======================== 添加商品 ============================

            operatetable.addGoods(conn, 1, "泡面", 5, 1000);
            operatetable.addGoods(conn, 2, "苹果", 10, 500);
            operatetable.addGoods(conn, 3, "螃蟹", 50, 500);
            operatetable.addGoods(conn, 4, "奶茶", 10, 500);
            operatetable.addGoods(conn, 5, "棒棒糖", 1, 1000);
            operatetable.addGoods(conn, 6, "蛋黄酥", 8, 800);
            operatetable.addGoods(conn, 7, "便当", 100, 500);
            // 输出：添加商品成功！ *7

            operatetable.addGoods(conn, 1, "bala", 20, 20);
            // 输出：OrderSystem.MyException: 商品已存在！

            // ========================== 购买交易 ==========================


            operatetransaction.buyTransaction(conn, 6, 100, 1, 1);
            //交易成功

            operatetransaction.buyTransaction(conn, 4, 200, 2, 1);
            // 交易成功

            operatetransaction.buyTransaction(conn, 2, 500, 3, 2);
            // 交易成功

            operatetransaction.buyTransaction(conn, 2, 100, 4, 3);
            // OrderSystem.MyException: 商品不存在！  (在上一笔订单中卖完)

            operatetransaction.buyTransaction(conn, 7, 200, 5, 3);
            // 交易成功！

            // ======================= 排序 =============================

            operatetable.sequenceOrder(conn, "orderprice");
            /*订单编号	订单批次	购买商品编号	购买商品数量	下单时间	订单总额
                1	1	6	100	2023-12-26	800
                2	1	4	200	2023-12-26	2000
                3	2	2	500	2023-12-26	5000
                5	3	7	200	2023-12-26	20000
             */

            operatetable.sequenceGoods(conn, "goodsnum");
            /*
            商品编号	商品名	商品价格	商品余量
                2	苹果	10	0
                4	奶茶	10	300
                7	便当	100	300
                3	螃蟹	50	500
                6	蛋黄酥	8	700
                1	泡面	5	1000
                5	棒棒糖	1	1000
             */

            // ======================= 撤回商品=============================

            operatetransaction.deleteGoods(conn, 6);
            // 删除与该商品有关订单成功！
            // 删除该商品成功！

            // ========================= 撤销订单 ===========================

            operatetransaction.withdrawTransaction(conn, 2);
            // 恢复相关商品数量！
            // 撤销订单成功！


        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            jdbcUtils.release(conn, st, rs);
        }


    }

}
