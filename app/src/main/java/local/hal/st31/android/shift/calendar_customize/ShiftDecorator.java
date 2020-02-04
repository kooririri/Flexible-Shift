package local.hal.st31.android.shift.calendar_customize;

import android.database.sqlite.SQLiteDatabase;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;

import org.threeten.bp.LocalDate;

import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;

import local.hal.st31.android.shift.db.DataAccess;
import local.hal.st31.android.shift.db.DatabaseHelper;
import local.hal.st31.android.shift.fragment.HomeFragment;
import local.hal.st31.android.shift.utils.DateUtils;
import local.hal.st31.android.shift.utils.GlobalUtils;

public class ShiftDecorator implements DayViewDecorator {
    private HomeFragment homeFragment;
    private DatabaseHelper _helper;
    private final Calendar calendar = Calendar.getInstance();

    @Override
    public boolean shouldDecorate(CalendarDay day) {
        Date date = new Date();
        String dateStr = DateUtils.date2String(date, "yyyy-MM-dd");
        Date parse = DateUtils.string2Date(dateStr, "yyyy-MM-dd");
        if (day.getDate().equals(parse)) {
            return true;
        }
        int year = day.getYear();
        int month = day.getMonth();
        int day1 = day.getDay();

        _helper = new DatabaseHelper(GlobalUtils.getInstance().getContext());
        SQLiteDatabase db = _helper.getWritableDatabase();
        LinkedList<String> list = DataAccess.getAvailableDate(db);
        for(String availableDate : list){
            if((year+"-"+month+"-"+day1).equals(availableDate)){
                return true;
            }
        }
        return false;
    }

    @Override
    public void decorate(DayViewFacade view) {
        view.addSpan(new CircleBackGroundSpan());
    }
}
