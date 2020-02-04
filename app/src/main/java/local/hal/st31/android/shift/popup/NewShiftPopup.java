package local.hal.st31.android.shift.popup;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.app.TimePickerDialog;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import androidx.annotation.NonNull;

import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import local.hal.st31.android.shift.R;
import local.hal.st31.android.shift.beans.SelfScheduleBean;
import local.hal.st31.android.shift.db.DataAccess;
import local.hal.st31.android.shift.db.DatabaseHelper;
import local.hal.st31.android.shift.fragment.HomeFragment;
import local.hal.st31.android.shift.utils.DateUtils;
import razerdp.basepopup.BasePopupWindow;
import studio.carbonylgroup.textfieldboxes.SimpleTextChangedWatcher;
import studio.carbonylgroup.textfieldboxes.TextFieldBoxes;

public class NewShiftPopup extends BasePopupWindow {
    private View popupView;
    private LinearLayout LLStart;
    private LinearLayout LLEnd;
    private TextView startTime;
    private TextView endTime;
    private String startHour;
    private String endHour;
    private String startMinute;
    private String endMinute;
    private TextFieldBoxes tfbOccupation;
    private TextFieldBoxes tfbMemo;
    private String occupation;
    private String memo;
    private DatabaseHelper _helper;
    private Button sendButton;

    public NewShiftPopup(Context context) {
        super(context);
    }

    @Override
    public View onCreateContentView() {
        popupView = createPopupById(R.layout.popup_shift_add);
        initView();
        return popupView;
    }

    @Override
    protected Animator onCreateShowAnimator() {
        ObjectAnimator showAnimator = ObjectAnimator.ofFloat(getDisplayAnimateView(), View.TRANSLATION_Y, getHeight() * 0.75f, 0);
        showAnimator.setDuration(1000);
        showAnimator.setInterpolator(new OvershootInterpolator(6));
        return showAnimator;
    }

    @Override
    protected Animator onCreateDismissAnimator() {
        ObjectAnimator showAnimator = ObjectAnimator.ofFloat(getDisplayAnimateView(), View.TRANSLATION_Y, 0, getHeight() * 0.75f);
        showAnimator.setDuration(1000);
        showAnimator.setInterpolator(new OvershootInterpolator(-6));
        return showAnimator;
    }

    private void initView(){
        _helper = new DatabaseHelper(getContext());
        sendButton = popupView.findViewById(R.id.btn_send);
        LLStart = popupView.findViewById(R.id.self_shift_start_time);
        LLStart.setOnClickListener(new onTimeBlockClickListener());
        LLEnd = popupView.findViewById(R.id.self_shift_end_time);
        LLEnd.setOnClickListener(new onTimeBlockClickListener());
        startTime = popupView.findViewById(R.id.label_start_time);
        endTime = popupView.findViewById(R.id.label_end_time);
        tfbOccupation = popupView.findViewById(R.id.text_field_boxes_occupation);
        tfbMemo = popupView.findViewById(R.id.text_field_boxes_memo);
        tfbOccupation.setHasClearButton(true);
        tfbMemo.setHasClearButton(true);
        tfbOccupation.setSimpleTextChangeWatcher(new SimpleTextChangedWatcher() {
            @Override
            public void onTextChanged(String theNewText, boolean isError) {
                occupation = theNewText;
                Toast.makeText(getContext(),occupation,Toast.LENGTH_SHORT).show();
            }
        });
        tfbMemo.setSimpleTextChangeWatcher(new SimpleTextChangedWatcher() {
            @Override
            public void onTextChanged(String theNewText, boolean isError) {
                memo = theNewText;
                Toast.makeText(getContext(),memo,Toast.LENGTH_SHORT).show();
            }
        });



        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SQLiteDatabase db = _helper.getWritableDatabase();
                SelfScheduleBean selfScheduleBean = new SelfScheduleBean();
                selfScheduleBean.setWork(occupation);
                selfScheduleBean.setMemo(memo);
                selfScheduleBean.setStartTime(startHour+":"+startMinute);
                selfScheduleBean.setEndTime(endHour+":"+endMinute);
                selfScheduleBean.setDate(new HomeFragment().selectedDate);
                DataAccess.selfScheduleInsert(db,selfScheduleBean);

                Toast.makeText(getContext(),"登録しました",Toast.LENGTH_LONG).show();
            }
        });
    }


    private class onTimeBlockClickListener implements View.OnClickListener{
        @Override
        public void onClick(View view){
            if(view.getId() == R.id.self_shift_start_time) {
                TimePickerDialog dialog = new TimePickerDialog(getContext(), new StartTimePickerDialogSetListener(), 0, 0, true);
                dialog.show();
            }else if (view.getId() == R.id.self_shift_end_time){
                TimePickerDialog dialog = new TimePickerDialog(getContext(), new EndTimePickerDialogSetListener(), 0, 0, true);
                dialog.show();
            }
        }
    }

    private class StartTimePickerDialogSetListener implements TimePickerDialog.OnTimeSetListener{
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
//            String msg = "開始時間選択ダイアログ：" + hourOfDay + "時" + minute + "分";
//            Toast.makeText(getContext(),msg,Toast.LENGTH_SHORT).show();
            if(hourOfDay < 10){
                startHour = "0"+String.valueOf(hourOfDay);
            }else{
                startHour = String.valueOf(hourOfDay);
            }
            if(minute < 10){
                startMinute = "0"+String.valueOf(minute);
            }else{
                startMinute = String.valueOf(minute);
            }

            startTime.setText(startHour+"："+startMinute);
        }
    }

    private class EndTimePickerDialogSetListener implements TimePickerDialog.OnTimeSetListener{
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
//            String msg = "終了時間選択ダイアログ：" + hourOfDay + "時" + minute + "分";
//            Toast.makeText(getContext(),msg,Toast.LENGTH_SHORT).show();
            if(hourOfDay < 10){
                endHour = "0"+String.valueOf(hourOfDay);
            }else{
                endHour = String.valueOf(hourOfDay);
            }
            if(minute < 10){
                endMinute = "0"+String.valueOf(minute);
            }else{
                endMinute = String.valueOf(minute);
            }
            endTime.setText(endHour+"："+endMinute);
        }
    }
}
