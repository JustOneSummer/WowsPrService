package com.shinoaki.module.wows.wowshttptools.service;

import com.shinoaki.module.wows.wowshttptools.calculate.data.NumberShipAvgData;
import com.shinoaki.module.wows.wowshttptools.data.GameDataInfo;
import lombok.Getter;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * 战绩计算服务
 *
 * @author LinXun
 * @date 2021/4/16 9:32 星期五
 */
public class WowsPrService {
    public static final int BATTLES = 10;

    private WowsPrService() {

    }

    public static int pr(GameInfoPrAvgData data) {
        double nd = data.getDataInfo().getDamage() / data.avgDamage();
        double nf = data.getDataInfo().getFrags() / data.avgFrags();
        double nw = data.getDataInfo().getWins() / data.avgWins();
        return result(nd, nf, nw);
    }

    public static int number(double data) {
        return (int) Math.ceil(data);
    }

    /**
     * 场均击杀
     *
     * @param battles 战斗场次
     * @param frags   击杀数
     * @return 结果
     */
    public static double frags(long battles, long frags) {
        return doubleCheck(((double) frags) / battles);
    }


    /**
     * 场均
     *
     * @param battles     场次
     * @param damageDealt 场均
     * @return 场均
     */
    public static double damage(long battles, Long damageDealt) {
        if (damageDealt <= 0 || battles <= 0) {
            return 0;
        }
        return doubleCheck(damageDealt.doubleValue() / battles);
    }

    /**
     * 胜率
     *
     * @param wins    胜利场次
     * @param battles 场次
     * @return 胜率
     */
    public static double wins(long battles, double wins) {
        if (wins <= 0 || battles <= 0) {
            return 0;
        }
        return 100.0 * doubleCheck((wins / battles));
    }

    /**
     * 经验
     *
     * @param xp      总经验
     * @param battles 场次
     * @return 经验
     */
    public static double xp(long battles, double xp) {
        if (xp <= 0 || battles <= 0) {
            return 0;
        }
        return doubleCheck(xp / battles);
    }

    /**
     * KD
     *
     * @param battles         场次
     * @param frags           击杀
     * @param survivedBattles 存活场次
     * @return kd
     */
    public static double kd(long battles, double frags, double survivedBattles) {
        double v = battles - survivedBattles;
        if (frags <= 0) {
            return 0;
        } else if (v <= 0) {
            return frags;
        } else {
            return doubleCheck(frags / v);
        }
    }

    /**
     * 命中
     *
     * @param hit   命中
     * @param shots 发射
     * @return 命中率
     */
    public static double hit(double hit, double shots) {
        if (hit <= 0 || shots <= 0) {
            return 0;
        }
        return 100.0 * doubleCheck((hit / shots));
    }

    /**
     * 抹0
     *
     * @param data 数据
     * @return 四舍五入 zero小数点
     */
    public static double doubleToZero(double data) {
        return BigDecimal.valueOf(data).setScale(0, RoundingMode.HALF_UP).doubleValue();
    }

    /**
     * 四舍五入
     *
     * @param data 数据
     * @return 四舍五入
     */
    public static double doubleToTwo(double data) {
        return BigDecimal.valueOf(data).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }

    /**
     * 双精度溢出验证
     *
     * @param data 双精度数值
     * @return NaN等全部返回0
     */
    public static double doubleCheck(double data) {
        if (Double.isInfinite(data)) {
            return 0;
        } else if (Double.isNaN(data)) {
            return 0;
        } else {
            return data;
        }
    }

    /**
     * 检测是否为空
     *
     * @param data 数据
     * @return 结果
     */
    public static boolean checkNull(NumberShipAvgData data) {
        return data == null || data.getWinRate() <= 0 || data.getAverageDamageDealt() <= 0 || data.getAverageFrags() <= 0;
    }

    /**
     * 计算结果
     *
     * @param nd 场均
     * @param nf 平均击杀
     * @param nw 胜率
     * @return 结果
     */
    private static int result(double nd, double nf, double nw) {
        double maxNd = Math.max(0, (nd - 0.4) / (1 - 0.4));
        double maxNf = Math.max(0, (nf - 0.1) / (1 - 0.1));
        double maxNw = Math.max(0, (nw - 0.7) / (1 - 0.7));
        return (int) Math.floor(700 * maxNd + 300 * maxNf + 150 * maxNw);
    }
}
