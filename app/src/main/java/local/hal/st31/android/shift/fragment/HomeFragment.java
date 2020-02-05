package local.hal.st31.android.shift.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.haibin.calendarview.Calendar;
import com.haibin.calendarview.CalendarView;


import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import local.hal.st31.android.shift.NewSelfShiftAddActivity;
import local.hal.st31.android.shift.R;
import local.hal.st31.android.shift.adapters.SelfScheduleAdapter;
import local.hal.st31.android.shift.beans.SelfScheduleBean;
import local.hal.st31.android.shift.calendar_customize.ShiftDecorator;
import local.hal.st31.android.shift.db.DataAccess;
import local.hal.st31.android.shift.db.DatabaseHelper;
import local.hal.st31.android.shift.utils.DateUtils;
import local.hal.st31.android.shift.utils.GlobalUtils;

public class HomeFragment extends Fragment implements CalendarView.OnCalendarSelectListener, CalendarView.OnYearChangeListener{
    private View fragmentView;
    private CalendarView calendarView;
    public String selectedDate;
    private TextView dateLabel;
    private DatabaseHelper _helper;
    private RecyclerView selfScheduleRecyclerView;
    private SelfScheduleAdapter selfScheduleAdapter;
    private SQLiteDatabase db;
    TextView mTextMonthDay;
    TextView mTextYear;
    TextView mTextLunar;
    TextView mTextCurrentDay;
    RelativeLayout mRelativeTool;
    private int userId;
    ArrayList<SelfScheduleBean> data;



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        fragmentView = inflater.inflate(R.layout.fragment_home,container,false);
        _helper = new DatabaseHelper(getContext());
        selectedDate = DateUtils.date2String(new Date(),"yyyy-MM-dd");
        SharedPreferences sp = GlobalUtils.getInstance().mainActivity.getSharedPreferences("login", GlobalUtils.getInstance().context.MODE_PRIVATE);
        userId= sp.getInt("userId",0);
        return fragmentView;
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onResume() {
        super.onResume();
        initFragmentView();
        ImageView imgAdd = fragmentView.findViewById(R.id.btn_add);
        imgAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), NewSelfShiftAddActivity.class);
                intent.putExtra("date",selectedDate);
                intent.putExtra("userId",userId);
                startActivity(intent);
                selfScheduleAdapter. notifyDataSetChanged();
            }
        });
    }


    //Viewを初期化
    private void initFragmentView() {
        selfScheduleRecyclerView = fragmentView.findViewById(R.id.self_schedule_recyclerView);

        calendarView = fragmentView.findViewById(R.id.calendarView);
        mTextMonthDay = fragmentView.findViewById(R.id.tv_month_day);
        mTextYear = fragmentView.findViewById(R.id.tv_year);
        mTextLunar = fragmentView.findViewById(R.id.tv_lunar);
        mRelativeTool = fragmentView.findViewById(R.id.rl_tool);
        mTextCurrentDay = fragmentView.findViewById(R.id.tv_current_day);
        fragmentView.findViewById(R.id.fl_current).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calendarView.scrollToCurrent();
            }
        });
        calendarView.setOnCalendarSelectListener(this);
        calendarView.setOnYearChangeListener(this);
        mTextYear.setText(String.valueOf(calendarView.getCurYear()));
//        mYear = calendarView.getCurYear();
        mTextMonthDay.setText(calendarView.getCurMonth() + "月" + calendarView.getCurDay() + "日");
        mTextLunar.setText("今日");
        mTextCurrentDay.setText(String.valueOf(calendarView.getCurDay()));
        dateLabel = fragmentView.findViewById(R.id.date_label);
//        selectedDate = DateUtils.date2String(new Date(),"yyyy-MM-dd");
        dateLabel.setText(selectedDate);

        reload();
        initData();
    }

    private void reload(){
        db = _helper.getWritableDatabase();
        data = DataAccess.selfScheduleSelectByDate(db, selectedDate,userId);
        selfScheduleAdapter = new SelfScheduleAdapter(getContext());
        selfScheduleAdapter.setData(data);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);//横並び
        selfScheduleRecyclerView.setLayoutManager(layoutManager);
        selfScheduleRecyclerView.setAdapter(selfScheduleAdapter);
        selfScheduleAdapter.setOnItemClickListener(new SelfScheduleAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                showDialog(position);
                Log.d("remove","removed");
            }
        });
    }

    private void showDialog(final int index){
        final String[] options = {"削除","編集"};
        final SelfScheduleBean selectedRecord = data.get(index);
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(which == 0){
                    long id = selectedRecord.getId();
                    DataAccess.selfScheduleDelete(db,id);

                    reload();
                } else if(which == 1){
                    Intent intent = new Intent(getContext(), NewSelfShiftAddActivity.class);
                    Bundle extra = new Bundle();
                    extra.putSerializable("selfSchedule",selectedRecord);
                    extra.putInt("userId",userId);
                    extra.putString("date",selectedDate);
                    intent.putExtras(extra);
                    startActivity(intent);
                }

            }
        });
        builder.setNegativeButton("Cancel",null);
        builder.create().show();
    }


    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onCalendarOutOfRange(Calendar calendar) {

    }

    @Override
    public void onCalendarSelect(Calendar calendar, boolean isClick) {
        mTextLunar.setVisibility(View.VISIBLE);
        mTextYear.setVisibility(View.VISIBLE);
        mTextMonthDay.setText(calendar.getMonth() + "月" + calendar.getDay() + "日");
        mTextYear.setText(String.valueOf(calendar.getYear()));
        mTextLunar.setText(calendar.getLunar());
//        mYear = calendar.getYear();
        selectedDate = calendar.getYear()+"-"+calendar.getMonth()+"-"+calendar.getDay();
        dateLabel.setText(selectedDate);
        reload();
        initData();
    }

    @Override
    public void onYearChange(int year) {

    }
    private Calendar getSchemeCalendar(int year, int month, int day, int color, String text) {
        Calendar calendar = new Calendar();
        calendar.setYear(year);
        calendar.setMonth(month);
        calendar.setDay(day);
        calendar.setSchemeColor(color);//如果单独标记颜色、则会使用这个颜色
        calendar.setScheme(text);
        return calendar;
    }

    private void initData() {
//        int year = calendarView.getCurYear();
//        int month = calendarView.getCurMonth();
        db = _helper.getWritableDatabase();
        Map<String, Calendar> map = new HashMap<>();
        List<String> availableDate = DataAccess.getAvailableDate(db);
        for(int i = 0;i < availableDate.size(); i++){
            int number = DataAccess.getNumberOfSelfScheduleByDate(db,availableDate.get(i),userId);

            int year = Integer.valueOf(availableDate.get(i).substring(0,4));
            int month = 0;
            int day = 0;
            if(availableDate.get(i).substring(6,7).equals("-")){
                month = Integer.valueOf(availableDate.get(i).substring(5,6));
                if (availableDate.get(i).length() == 8){
                    day = Integer.valueOf(availableDate.get(i).substring(7,8));
                }else{
                    day = Integer.valueOf(availableDate.get(i).substring(7,9));
                }
            }else{
                month = Integer.valueOf(availableDate.get(i).substring(5,7));
                if (availableDate.get(i).length() == 9){
                    day = Integer.valueOf(availableDate.get(i).substring(7,8));
                }else{
                    day = Integer.valueOf(availableDate.get(i).substring(7,9));
                }
            }
//            Log.e("ppssll",availableDate.get(i)+"    "+month+"    "+day);
            if(number > 0){
                map.put(getSchemeCalendar(year, month, day, 0xFF40db25, number*10+"").toString(),
                        getSchemeCalendar(year, month, day, 0xFF40db25, number*10+""));
            }
        }


        //此方法在巨大的数据量上不影响遍历性能，推荐使用
        calendarView.setSchemeDate(map);
    }

}
