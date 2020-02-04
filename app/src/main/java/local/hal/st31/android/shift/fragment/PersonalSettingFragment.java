package local.hal.st31.android.shift.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TableRow;

import local.hal.st31.android.shift.BlackListActivity;
import local.hal.st31.android.shift.LoginActivity;
import local.hal.st31.android.shift.R;
import local.hal.st31.android.shift.utils.GlobalUtils;


public class PersonalSettingFragment extends Fragment {
    private View fragmentView;
    private Button logoutButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
       fragmentView = inflater.inflate(R.layout.fragment_personal_setting,container,false);
       init();
       return fragmentView;

    }

    private void init(){
        TableRow blackListRow = fragmentView.findViewById(R.id.blackListBlock);
        logoutButton = fragmentView.findViewById(R.id.btn_quit);
        blackListRow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), BlackListActivity.class);
                startActivity(intent);
            }
        });
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), LoginActivity.class);
                startActivity(intent);
                GlobalUtils.getInstance().logout();
                SharedPreferences sp = GlobalUtils.getInstance().mainActivity.getSharedPreferences("login", GlobalUtils.getInstance().context.MODE_PRIVATE);
                if(sp!=null){
                    sp.edit().clear().commit();
                }
            }
        });
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

}
