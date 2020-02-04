package local.hal.st31.android.shift;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.messaging.FirebaseMessaging;

import local.hal.st31.android.shift.utils.GlobalUtils;

public class BaseActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        GlobalUtils.getInstance().addActivity(this);
        FirebaseMessaging.getInstance().subscribeToTopic("test");


    }

}
