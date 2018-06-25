package network.reborn.core.Util;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by ethan on 1/20/2017.
 */
public class TimeUtil {
    public static String getCurrentTime() {
        Calendar c = Calendar.getInstance();
        DateFormat df = DateFormat.getInstance();
        Boolean dst = TimeZone.getTimeZone("America/New_York")
                .inDaylightTime(new Date());
        System.out.println("TimeZone // " + "DST:" + dst.booleanValue());

        df.setTimeZone(TimeZone.getTimeZone("EST"));

        c.setTime(new Date());
        if (dst) {
            c.add(Calendar.HOUR, 1);
        }
        return df.format(c.getTime()) + " EST";
    }
}
