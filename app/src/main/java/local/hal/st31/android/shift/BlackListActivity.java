
package local.hal.st31.android.shift;

import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.larswerkman.holocolorpicker.ColorPicker;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.header.BezierRadarHeader;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import org.angmarch.views.NiceSpinner;
import org.angmarch.views.OnSpinnerItemSelectedListener;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import local.hal.st31.android.shift.adapters.GroupMemberAdapter;
import local.hal.st31.android.shift.beans.BlackListBean;
import local.hal.st31.android.shift.db.DataAccess;
import local.hal.st31.android.shift.db.DatabaseHelper;
import local.hal.st31.android.shift.popup.ColorChangePopup;
import local.hal.st31.android.shift.utils.GlobalUtils;
import me.zhanghai.android.materialratingbar.MaterialRatingBar;

public class BlackListActivity extends BaseActivity {

    private static final String GROUP_URL = "http://flexibleshift.sakura.ne.jp/shift_app_backend/controllers/group_controller.php";
    private static final String BLACK_URL = "http://flexibleshift.sakura.ne.jp/shift_app_backend/controllers/test.php";

    private int userMemberId;
    private List<String> groupNameList;
    private String selectedGroup;
    private List<Integer> groupIdList;
    private int selectedGroupId;
    private List<String> groupMemberList;
    private List<Integer> groupMemberIdList;
    private RecyclerView recyclerView;
    private List<BlackListBean> blackMemberList;
    GroupMemberAdapter groupMemberAdapter;
//    private ColorPicker picker;
    private DatabaseHelper _helper;
    private SQLiteDatabase db;
    private ColorChangePopup popup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_black_list);
        groupNameList = new ArrayList<>();
        groupNameList.add("--------");
        groupIdList = new ArrayList<>();
        groupIdList.add(0);
//        picker = findViewById(R.id.picker);
//        picker.setOldCenterColor(picker.getColor());
        _helper = new DatabaseHelper(getApplicationContext());
        db = _helper.getWritableDatabase();

        SharedPreferences sp = getSharedPreferences("login", getApplicationContext().MODE_PRIVATE);
        userMemberId = sp.getInt("userId",0);
        Map<String,Integer> map = new HashMap<>();
        map.put("userId",userMemberId);
        map.put("postNo",1);
        Gson gson = new GsonBuilder()
                .serializeNulls()
                .setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE)
                .create();
        String jsonGroupData = gson.toJson(map);
        GroupReceiver groupReceiver = new GroupReceiver();
        groupReceiver.execute(GROUP_URL,jsonGroupData);

        RefreshLayout refreshLayout = findViewById(R.id.refreshLayout);
        refreshLayout.setRefreshHeader(new BezierRadarHeader(getApplicationContext()).setEnableHorizontalDrag(true));
//        ClassicsHeader header = fragmentView.findViewById(R.id.header);
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                load();
                refreshLayout.finishRefresh(2000/*,false*/);//传入false表示刷新失败
            }
        });
    }

    private void spinnerHandler(){
        NiceSpinner niceSpinner = findViewById(R.id.group_spinner);
        niceSpinner.attachDataSource(groupNameList);
        niceSpinner.setOnSpinnerItemSelectedListener(new OnSpinnerItemSelectedListener() {
            @Override
            public void onItemSelected(NiceSpinner parent, View view, int position, long id) {
                selectedGroup = parent.getItemAtPosition(position).toString();
                selectedGroupId = groupIdList.get(position);
                load();
            }
        });
    }
    private void load(){
        Map<String,Integer> map = new HashMap<>();
        map.put("groupId",selectedGroupId);
        map.put("userId",userMemberId);
        map.put("postNo",2);
        Gson gson = new GsonBuilder()
                .serializeNulls()
                .setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE)
                .create();
        String jsonMemberData = gson.toJson(map);
        GroupMemberReceiver groupMemberReceiver = new GroupMemberReceiver();
        groupMemberReceiver.execute(GROUP_URL,jsonMemberData);
    }

    private void swipeRecyclerViewHandler(){
        blackMemberList = new ArrayList<>();
        blackMemberList = DataAccess.getAllBlackMember(db,selectedGroupId);
        recyclerView = findViewById(R.id.memberRecyclerView);
        groupMemberAdapter = new GroupMemberAdapter(getApplicationContext());
        groupMemberAdapter.setListener(new GroupMemberAdapter.onMemberClickListener() {
            @Override
            public void onItemClick(int position, final BlackListBean blackListBean) {
                popup = new ColorChangePopup(getApplicationContext(),blackListBean);
                popup.showPopupWindow();
            }
        });
        groupMemberAdapter.setData(blackMemberList);
        groupMemberAdapter.notifyDataSetChanged();
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);//縦並び
//        GridLayoutManager layoutManager = new GridLayoutManager(getApplicationContext(),4);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(groupMemberAdapter);


    }

    public void sendButtonClick(View view){
        Gson gson = new GsonBuilder()
                .serializeNulls()
                .setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE)
                .create();
        String submitJson = gson.toJson(blackMemberList);
        GroupMemberPoster groupMemberPoster = new GroupMemberPoster();
        groupMemberPoster.execute(BLACK_URL,submitJson);
        db = _helper.getWritableDatabase();
        for(int i = 0;i < blackMemberList.size(); i++){
            DataAccess.blackListReplace(db,blackMemberList.get(i));
        }
    }

    private class GroupReceiver extends AsyncTask<String,Void,String> {

        private static final String DEBUG_TAG = "GroupReceiver";
        @Override
        protected String doInBackground(String... params) {
            String uri = params[0];
            String jsonData = params[1];
            HttpURLConnection con = null;
            InputStream is = null;
            String result = "";

            try {
                URL url = new URL(uri);
                con = (HttpURLConnection) url.openConnection();
                con.setRequestProperty("Content-Type", "application/json; utf-8"); // 追記
                con.setRequestProperty("Accept", "application/json");
                con.setRequestMethod("POST");
                con.setDoOutput(true);
                // サーバーへ送るJSONをセットする
                DataOutputStream wr = new DataOutputStream(con.getOutputStream());
                wr.writeBytes(jsonData);
                wr.flush();
                wr.close();

                con.connect();
                int status = con.getResponseCode();
                if(status != 200){
                    throw new IOException("ステータスコード："+status);
                }
                is = con.getInputStream();
                result = GlobalUtils.getInstance().is2String(is);
            } catch (MalformedURLException ex) {
                Log.e(DEBUG_TAG, "URL変換失敗", ex);
            } catch (IOException ex) {
                Log.e(DEBUG_TAG, "通信失敗", ex);
            } finally {
                if (con != null) {
                    con.disconnect();
                }
                if (is != null) {
                    try {
                        is.close();
                    } catch (IOException ex) {
                        Log.e(DEBUG_TAG, "InputStream解放失敗", ex);
                    }
                }
            }
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            JSONObject jsonObject = null;
            try {
                jsonObject = new JSONObject(result);
                JSONArray jsonArray = jsonObject.getJSONArray("result");
                for(int i = 0 ; i < jsonArray.length() ; i++){
                    JSONObject data = jsonArray.getJSONObject(i);
                    String groupName = data.getString("group_name");
                    int groupId = data.getInt("group_id");
                    groupNameList.add(groupName);
                    groupIdList.add(groupId);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            spinnerHandler();

        }
    }

    private class GroupMemberReceiver extends AsyncTask<String,Void,String> {

        private static final String DEBUG_TAG = "GroupMemberReceiver";
        @Override
        protected String doInBackground(String... params) {
            String uri = params[0];
            String jsonData = params[1];
            HttpURLConnection con = null;
            InputStream is = null;
            String result = "";

            try {
                URL url = new URL(uri);
                con = (HttpURLConnection) url.openConnection();
                con.setRequestProperty("Content-Type", "application/json; utf-8"); // 追記
                con.setRequestProperty("Accept", "application/json");
                con.setRequestMethod("POST");
                con.setDoOutput(true);
                // サーバーへ送るJSONをセットする
                DataOutputStream wr = new DataOutputStream(con.getOutputStream());
                wr.writeBytes(jsonData);
                wr.flush();
                wr.close();

                con.connect();
                int status = con.getResponseCode();
                if(status != 200){
                    throw new IOException("ステータスコード："+status);
                }
                is = con.getInputStream();
                result = GlobalUtils.getInstance().is2String(is);
            } catch (MalformedURLException ex) {
                Log.e(DEBUG_TAG, "URL変換失敗", ex);
            } catch (IOException ex) {
                Log.e(DEBUG_TAG, "通信失敗", ex);
            } finally {
                if (con != null) {
                    con.disconnect();
                }
                if (is != null) {
                    try {
                        is.close();
                    } catch (IOException ex) {
                        Log.e(DEBUG_TAG, "InputStream解放失敗", ex);
                    }
                }
            }
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            JSONObject jsonObject = null;

            groupMemberList = new ArrayList<>();
            groupMemberIdList = new ArrayList<>();
//            blackMemberList = new ArrayList<>();
            try {
                jsonObject = new JSONObject(result);
                JSONArray jsonArray = jsonObject.getJSONArray("result");
                for(int i = 0 ; i < jsonArray.length() ; i++){
                    JSONObject data = jsonArray.getJSONObject(i);
                    String memberName = data.getString("nickname");
                    int userId = data.getInt("group_member_id");
                    int blackRank = data.getInt("black_rank");
                    int colorCode = data.getInt("color_code");
                        BlackListBean blackListBean = new BlackListBean();
                        blackListBean.setNickName(memberName);
                        blackListBean.setUserId(userId);
                        blackListBean.setBlackRank(blackRank);
                        blackListBean.setGroupId(selectedGroupId);
                        blackListBean.setColorCode(colorCode);
//                        blackMemberList.add(blackListBean);
                        groupMemberList.add(memberName);
                        groupIdList.add(userId);
                        DataAccess.blackListReplace(db,blackListBean);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            swipeRecyclerViewHandler();
        }
    }


    private class GroupMemberPoster extends AsyncTask<String,Void,String> {

        private static final String DEBUG_TAG = "GroupMemberPoster";
        @Override
        protected String doInBackground(String... params) {
            String uri = params[0];
            String jsonData = params[1];
            HttpURLConnection con = null;
            InputStream is = null;
            String result = "";

            try {
                URL url = new URL(uri);
                con = (HttpURLConnection) url.openConnection();
                con.setRequestProperty("Content-Type", "application/json; utf-8"); // 追記
                con.setRequestProperty("Accept", "application/json");
                con.setRequestMethod("POST");
                con.setDoOutput(true);
                // サーバーへ送るJSONをセットする
//                DataOutputStream wr = new DataOutputStream(con.getOutputStream());
//                wr.writeBytes(jsonData);
//                wr.flush();
//                wr.close();
//
//                con.connect();

                OutputStream os = con.getOutputStream();
                os.write(jsonData.getBytes());
                os.flush();
                os.close();
                int status = con.getResponseCode();
                if(status != 200){
                    throw new IOException("ステータスコード："+status);
                }
                is = con.getInputStream();
                result = GlobalUtils.getInstance().is2String(is);
            } catch (MalformedURLException ex) {
                Log.e(DEBUG_TAG, "URL変換失敗", ex);
            } catch (IOException ex) {
                Log.e(DEBUG_TAG, "通信失敗", ex);
            } finally {
                if (con != null) {
                    con.disconnect();
                }
                if (is != null) {
                    try {
                        is.close();
                    } catch (IOException ex) {
                        Log.e(DEBUG_TAG, "InputStream解放失敗", ex);
                    }
                }
            }
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            String status = "";

            try {
                JSONObject jsonObject = new JSONObject(result);
                status = jsonObject.getString("status");
                if(status.equals("ok")){
                    Toast.makeText(getApplicationContext(),"登録しました。",Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(getApplicationContext(),"登録失敗しました。",Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}