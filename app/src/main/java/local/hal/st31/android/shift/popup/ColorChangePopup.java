package local.hal.st31.android.shift.popup;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.app.TimePickerDialog;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.larswerkman.holocolorpicker.ColorPicker;

import local.hal.st31.android.shift.R;
import local.hal.st31.android.shift.beans.BlackListBean;
import local.hal.st31.android.shift.beans.SelfScheduleBean;
import local.hal.st31.android.shift.db.DataAccess;
import local.hal.st31.android.shift.db.DatabaseHelper;
import local.hal.st31.android.shift.fragment.HomeFragment;
import razerdp.basepopup.BasePopupWindow;
import studio.carbonylgroup.textfieldboxes.SimpleTextChangedWatcher;
import studio.carbonylgroup.textfieldboxes.TextFieldBoxes;

public class ColorChangePopup extends BasePopupWindow {
    private View popupView;
    private Button sendButton;
    private ColorPicker picker;
    private int colorCode;
    private BlackListBean blackListBean;

    public ColorChangePopup(Context context, BlackListBean blackListBean) {
        super(context);
        this.blackListBean = blackListBean;
    }

    public int getColorCode(){
        return colorCode;
    }

    @Override
    public View onCreateContentView() {
        popupView = createPopupById(R.layout.popup_color_change);
        colorCode = -8126720;
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
        sendButton = popupView.findViewById(R.id.btn_send);
        picker = popupView.findViewById(R.id.color_picker);
        picker.setOnColorChangedListener(new ColorPicker.OnColorChangedListener() {
            @Override
            public void onColorChanged(int color) {
                colorCode = color;
            }
        });
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                blackListBean.setColorCode(colorCode);
                ColorChangePopup.this.dismiss();
                Toast.makeText(getContext(),colorCode+"",Toast.LENGTH_LONG).show();
            }
        });
    }

}
