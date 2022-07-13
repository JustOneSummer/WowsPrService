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


    public static int pr(GameDataInfo info, NumberShipAvgData data) {
        AvgData avgData = new AvgData(info, data);
        if (checkNull(avgData)) {
            return 0;
        }
        return pr(info, avgData);
    }

    public static int pr(GameDataInfo info, AvgData data) {
        if (checkNull(data)) {
            return 0;
        }
        return pr(info.getDamage(), info.getFrags(), info.getWins(), data);
    }

    public static int pr(long damage, long frags, long wins, AvgData data) {
        double nd = damage / data.damage();
        double nf = frags / data.frags();
        double nw = wins / data.wins();
        double maxNd = Math.max(0, (nd - 0.4) / (1 - 0.4));
        double maxNf = Math.max(0, (nf - 0.1) / (1 - 0.1));
        double maxNw = Math.max(0, (nw - 0.7) / (1 - 0.7));
        return result(maxNd, maxNf, maxNw);
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
            return -1;
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
     * 四舍五入 zero小数点
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
    public static boolean checkNull(AvgData data) {
        return data == null || data.getWinRate() <= 0 || data.getAverageDamageDealt() <= 0 || data.getAverageFrags() <= 0;
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
     * @param nDamage 场均
     * @param nFrags  平均击杀
     * @param ngWins  胜率
     * @return 结果
     */
    private static int result(double nDamage, double nFrags, double ngWins) {
        return (int) Math.floor(700 * nDamage + 300 * nFrags + 150 * ngWins);
    }

    @Getter
    public static class AvgData {
        private Long userBattle;
        private long shipId;
        /**
         * 胜率
         */
        private double winRate;
        /**
         * 场均伤害
         */
        private double averageDamageDealt;
        /**
         * 场均击杀
         */
        private double averageFrags;

        public AvgData() {
        }

        public AvgData(GameDataInfo dataInfo, NumberShipAvgData data) {
            this.userBattle = dataInfo.getBattles();
            this.shipId = dataInfo.getShipId();
            this.winRate = data.getWinRate();
            this.averageDamageDealt = data.getAverageDamageDealt();
            this.averageFrags = data.getAverageFrags();
        }

        public AvgData(long battles, NumberShipAvgData data) {
            this.userBattle = battles;
            this.shipId = 0;
            this.winRate = data.getWinRate();
            this.averageDamageDealt = data.getAverageDamageDealt();
            this.averageFrags = data.getAverageFrags();
        }

        public AvgData(long shipId, long userBattle, double winRate, double averageDamageDealt, double averageFrags) {
            this.userBattle = userBattle;
            this.shipId = shipId;
            this.winRate = winRate;
            this.averageDamageDealt = averageDamageDealt;
            this.averageFrags = averageFrags;
        }

        public AvgData(double winRate, double averageDamageDealt, double averageFrags) {
            this.winRate = winRate;
            this.averageDamageDealt = averageDamageDealt;
            this.averageFrags = averageFrags;
        }

        public void prAdd(long battle, NumberShipAvgData avgData) {
            AvgData data = new AvgData(battle, avgData);
            this.winRate += data.wins();
            this.averageDamageDealt += data.damage();
            this.averageFrags += data.frags();
        }

        public double damage() {
            return userBattle == null ? averageDamageDealt : userBattle * averageDamageDealt;
        }

        public double frags() {
            return userBattle == null ? averageFrags : userBattle * averageFrags;
        }

        public double wins() {
            return userBattle == null ? winRate : userBattle * winRate / 100;
        }

        public long getShipId() {
            return shipId;
        }
    }
}
