package local.hal.st31.android.shift.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtils {
    public static String date2String(Date date,String format){
        SimpleDateFormat sdf =   new SimpleDateFormat( format );
        return sdf.format(date);
    }

    public static Date string2Date(String dateStr,String format){
        SimpleDateFormat sdf =   new SimpleDateFormat( format );
        try {
            return sdf.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return new Date();
    }

    /**
     * 年月による日数を算出
     */
    public static int getDaysByYearMonth(int year, int month) {

        Calendar a = Calendar.getInstance();
        a.set(Calendar.YEAR, year);
        a.set(Calendar.MONTH, month - 1);
        a.set(Calendar.DATE, 1);
        a.roll(Calendar.DATE, -1);
        int maxDate = a.get(Calendar.DATE);
        return maxDate;
    }
}
