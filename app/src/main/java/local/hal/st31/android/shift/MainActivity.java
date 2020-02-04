package local.hal.st31.android.shift;

import android.os.Build;
import android.os.Bundle;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.messaging.FirebaseMessaging;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.NonNull;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import local.hal.st31.android.shift.fragment.HomeFragment;
import local.hal.st31.android.shift.fragment.PersonalSettingFragment;
import local.hal.st31.android.shift.fragment.ShiftSubmitFragment;
import local.hal.st31.android.shift.fragment.WebViewFragment;
import local.hal.st31.android.shift.utils.GlobalUtils;

public class MainActivity extends BaseActivity {
    //ALTER TABLE `tbname`
    //  DROP PRIMARY KEY,
    //   ADD PRIMARY KEY(
    //     `id`,
    //     `username`);
    private TextView mTextMessage;
    private BottomNavigationView bottomNavigationView;
    private HomeFragment homeFragment;
    private WebViewFragment webViewFragment;
    private ShiftSubmitFragment shiftSubmitFragment;
    private PersonalSettingFragment personalSettingFragment;
    private Fragment[] fragments;
    private int lastfragment;//用于记录上个选择的Fragment
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);

//        BottomNavigationView navView = findViewById(R.id.nav_view);
//        mTextMessage = findViewById(R.id.message);
//        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        initFragment();
        GlobalUtils.getInstance().setContext(getApplicationContext());
        GlobalUtils.getInstance().mainActivity = this;
        FirebaseMessaging.getInstance().subscribeToTopic("test");

    }

    private void initFragment(){
        homeFragment = new HomeFragment();
        shiftSubmitFragment = new ShiftSubmitFragment();
        webViewFragment = new WebViewFragment();
        personalSettingFragment = new PersonalSettingFragment();
        fragments = new Fragment[]{homeFragment,shiftSubmitFragment,webViewFragment,personalSettingFragment};
        lastfragment=0;
        getSupportFragmentManager().beginTransaction().replace(R.id.a,homeFragment).show(homeFragment).commit();
        bottomNavigationView=findViewById(R.id.nav_view);

        bottomNavigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }



    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    if(lastfragment!=0)
                    {
                        switchFragment(lastfragment,0);
                        lastfragment=0;

                    }
                    return true;
                case R.id.navigation_shift_submit:
                    if(lastfragment!=1)
                    {
                        switchFragment(lastfragment,1);
                        lastfragment=1;

                    };
                    return true;
                case R.id.navigation_notifications:
                    if(lastfragment!=2)
                    {
                        switchFragment(lastfragment,2);
                        lastfragment=2;

                    }
                    return true;
                case R.id.navigation_personal_setting:
                    if(lastfragment!=3)
                    {
                        switchFragment(lastfragment,3);
                        lastfragment=3;

                    }
                    return true;
            }
            return false;
        }
        private void switchFragment(int lastfragment,int index)
        {
            FragmentTransaction transaction =getSupportFragmentManager().beginTransaction();
            transaction.hide(fragments[lastfragment]);//隐藏上个Fragment
            if(fragments[index].isAdded()==false)
            {
                transaction.add(R.id.a,fragments[index]);


            }
            transaction.show(fragments[index]).commitAllowingStateLoss();
        }
    };

    protected void setHalfTransparent() {
        if (Build.VERSION.SDK_INT >= 21) {//21表示5.0
            View decorView = getWindow().getDecorView();
            int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            decorView.setSystemUiVisibility(option);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        } else if (Build.VERSION.SDK_INT >= 19) {//19表示4.4
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //虚拟键盘也透明
            // getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
    }
}
