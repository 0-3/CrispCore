package network.reborn.core.Util;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class Lag implements Runnable {

    public static int TICK_COUNT = 0;
    public static long[] TICKS = new long[600];
    public static long LAST_TICK = 0L;

    public static double getTPS() {
        return getTPS(100);
    }

    public static double getTPS(int ticks) {
        if (TICK_COUNT < ticks) {
            return 0.0D;
        }
        int target = (TICK_COUNT - 1 - ticks) % TICKS.length;
        long elapsed = System.currentTimeMillis() - TICKS[target];

        return ticks / (elapsed / 1000.0D);
    }

    public static long getElapsed(int tickID) {
        long time = TICKS[(tickID % TICKS.length)];
        return System.currentTimeMillis() - time;
    }

    public static String getMaxRam() {
        BigDecimal ram = (new BigDecimal(Runtime.getRuntime().maxMemory())).setScale(2).divide(new BigDecimal(1048576), RoundingMode.HALF_EVEN);
        return ram.toString();
    }

    public static String getUsedRAM() {
        BigDecimal ram = (new BigDecimal(Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory())).setScale(2).divide(new BigDecimal(1048576), RoundingMode.HALF_EVEN);
        return ram.toString();
    }

    public static String getFreeRAM() {
        BigDecimal ram = (new BigDecimal(Runtime.getRuntime().maxMemory() - (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()))).setScale(2).divide(new BigDecimal(1048576), RoundingMode.HALF_EVEN);
        return ram.toString();
    }

    public void run() {
        TICKS[(TICK_COUNT % TICKS.length)] = System.currentTimeMillis();
        TICK_COUNT += 1;
    }

}