package local.hal.st31.android.shift.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
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
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import local.hal.st31.android.shift.R;
import local.hal.st31.android.shift.adapters.ShiftMonthListAdapter;
import local.hal.st31.android.shift.adapters.ShiftMonthListAdapter2;
import local.hal.st31.android.shift.adapters.ShiftMonthListAdapter3;
import local.hal.st31.android.shift.beans.SelfScheduleBean;
import local.hal.st31.android.shift.beans.ShiftRequestBean;
import local.hal.st31.android.shift.beans.ShiftTypeBean;
import local.hal.st31.android.shift.db.DataAccess;
import local.hal.st31.android.shift.db.DatabaseHelper;
import local.hal.st31.android.shift.utils.DateUtils;
import local.hal.st31.android.shift.utils.GlobalUtils;
import local.hal.st31.android.shift.utils.LogUtil;

public class ShiftSubmitFragment extends Fragment {
    private View fragmentView;

    private RecyclerView shiftListView;
    private ShiftMonthListAdapter shiftMonthListAdapter;
    private ShiftMonthListAdapter2 shiftMonthListAdapter2;
    private ShiftMonthListAdapter3 shiftMonthListAdapter3;
    private TextView dateLabel;
    private TextView noDataMessage;
    private List<List<ShiftTypeBean>> dataList;
    private DatabaseHelper _helper;
    private SQLiteDatabase db;
    private Button submitButton;
    int savedId = 0;
    int savedShiftId = 0;
    private List<String> groupNameList;
    private List<Integer> groupIdList;
    private String selectedGroup;
    private int selectedGroupId;
    private int buttonFlag = 0;
    List<SelfScheduleBean> selfList;

//    private static final String URLPost0 = "http://10.0.2.2/shift_app_backend/controllers/shift_controller0.php";
//    private static final String URLPost1 = "http://10.0.2.2/shift_app_backend/controllers/shift_controller1.php";
//    private static final String URLPost2= "http://10.0.2.2/shift_app_backend/controllers/shift_controller2.php";
//    private static final String URLPost3 = "http://10.0.2.2/shift_app_backend/controllers/shift_controller3.php";
    private static final String URLPost0 = "http://flexibleshift.sakura.ne.jp/shift_app_backend/controllers/shift_controller0.php";
    private static final String URLPost1 = "http://flexibleshift.sakura.ne.jp/shift_app_backend/controllers/shift_controller1.php";
    private static final String URLPost2= "http://flexibleshift.sakura.ne.jp/shift_app_backend/controllers/shift_controller2.php";
    private static final String URLPost3 = "http://flexibleshift.sakura.ne.jp/shift_app_backend/controllers/shift_controller3.php";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        fragmentView = inflater.inflate(R.layout.fragment_shift_submit,container,false);
        shiftListView = fragmentView.findViewById(R.id.shiftListView);
        noDataMessage = fragmentView.findViewById(R.id.no_data_message);
        RefreshLayout refreshLayout = fragmentView.findViewById(R.id.refreshLayout);
        //设置 Header 为 贝塞尔雷达 样式
        refreshLayout.setRefreshHeader(new BezierRadarHeader(getContext()).setEnableHorizontalDrag(true));
//        ClassicsHeader header = fragmentView.findViewById(R.id.header);
        _helper = new DatabaseHelper(getContext());
        db = _helper.getWritableDatabase();
        groupNameList = new ArrayList<>();
        groupNameList.add("------");
        groupIdList = new ArrayList<>();
        groupIdList.add(0);
        SharedPreferences ps = PreferenceManager.getDefaultSharedPreferences(getContext());
        savedShiftId = ps.getInt("shiftId",0);
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                reloadData();
                refreshLayout.finishRefresh(2000/*,false*/);//传入false表示刷新失败
            }
        });
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.MONTH, 1);
        dateLabel = fragmentView.findViewById(R.id.day_text);
        dateLabel.setText(cal.get(Calendar.YEAR)+"年"+(cal.get(Calendar.MONTH)+1)+"月");
        if (buttonFlag == 9){
            fragmentView.findViewById(R.id.btn_submit).setEnabled(false);
        }else{
            fragmentView.findViewById(R.id.btn_submit).setEnabled(true);
        }
        return fragmentView;
    }

    @Override
    public void onResume() {
        super.onResume();
        initView();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    private void spinnerHandler(){
        NiceSpinner niceSpinner = fragmentView.findViewById(R.id.groups_spinner);
        niceSpinner.attachDataSource(groupNameList);
        niceSpinner.setOnSpinnerItemSelectedListener(new OnSpinnerItemSelectedListener() {
            @Override
            public void onItemSelected(NiceSpinner parent, View view, int position, long id) {
                selectedGroup = parent.getItemAtPosition(position).toString();
                selectedGroupId = groupIdList.get(position);
                reloadData();
            }
        });
    }

    private void reloadData(){
        Map<String,Integer> map = new HashMap<>();
        map.put("groupId",selectedGroupId);
        map.put("userId",savedId);
        Gson gson = new GsonBuilder()
                .serializeNulls()
                .setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE)
                .create();
        String jsonData = gson.toJson(map);
        ShiftTypeDataReceiver shiftTypeDataReceiver = new ShiftTypeDataReceiver();
        shiftTypeDataReceiver.execute(URLPost1,jsonData);
        SharedPreferences ps = PreferenceManager.getDefaultSharedPreferences(getContext());
        ps.edit().putInt("groupId",selectedGroupId).apply();
        SharedPreferences sp = GlobalUtils.getInstance().mainActivity.getSharedPreferences("login", GlobalUtils.getInstance().context.MODE_PRIVATE);
        savedId= sp.getInt("userId",0);
        JSONObject userIdObject = new JSONObject();
        try {
            userIdObject.put("user_id",savedId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        GroupIdReceiver groupIdReceiver = new GroupIdReceiver();
        groupIdReceiver.execute(URLPost0,userIdObject.toString());

    }
    private void initView(){
        //次の月（シフト）の日数を取得
        SharedPreferences sp = GlobalUtils.getInstance().mainActivity.getSharedPreferences("login", GlobalUtils.getInstance().context.MODE_PRIVATE);
        savedId= sp.getInt("userId",0);
        JSONObject userIdObject = new JSONObject();
        try {
            userIdObject.put("user_id",savedId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        GroupIdReceiver groupIdReceiver = new GroupIdReceiver();
        groupIdReceiver.execute(URLPost0,userIdObject.toString());

//        ShiftTypeDataReceiver shiftTypeDataReceiver = new ShiftTypeDataReceiver();
//        shiftTypeDataReceiver.execute(URLPost1,userIdObject.toString());

        submitButton = fragmentView.findViewById(R.id.btn_submit);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(buttonFlag == 0){
                    List<ShiftRequestBean> submitData = new ArrayList<>();
                    for (int i = 0 ; i < dataList.size() ; i ++ ){
                        List<ShiftTypeBean> list = dataList.get(i);
                        for (int j = 0 ; j < list.size() ; j ++ ){
                            ShiftRequestBean bean = new ShiftRequestBean();
                            bean.setShiftId(savedShiftId);
                            bean.setUserId(savedId);
                            bean.setShiftTypeId(list.get(j).getShiftTypeId());
                            bean.setSelectedFlag(list.get(j).getSelectedFlag());
                            bean.setDate(list.get(j).getDate());
                            bean.setId(list.get(j).getShiftRequestId());
                            DataAccess.shiftRequestReplace(db,bean);
                            submitData.add(bean);
                        }
                    }
                    Gson gson = new GsonBuilder()
                            .serializeNulls()
                            .setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE)
                            .create();
                    String submitJson = gson.toJson(submitData);
                    ShiftRequestPoster shiftRequestPoster = new ShiftRequestPoster();
                    shiftRequestPoster.execute(URLPost2,submitJson);
                }else if(buttonFlag == 1){
                    List<ShiftRequestBean> submitData = new ArrayList<>();
                    for (int i = 0 ; i < dataList.size() ; i ++ ){
                        List<ShiftTypeBean> list = dataList.get(i);
                        for (int j = 0 ; j < list.size() ; j ++ ){
                            ShiftRequestBean bean = new ShiftRequestBean();
                            bean.setShiftId(savedShiftId);
                            bean.setUserId(savedId);
                            bean.setShiftTypeId(list.get(j).getShiftTypeId());
                            bean.setSelectedFlag(list.get(j).getSelectedFlag());
                            bean.setDate(list.get(j).getDate());
                            bean.setId(list.get(j).getShiftRequestId());
                            bean.setKaburuFlag(list.get(j).getKaburuFlag());
                            DataAccess.shiftRequestReplace(db,bean);
                            submitData.add(bean);
                        }
                    }
                    Gson gson = new GsonBuilder()
                            .serializeNulls()
                            .setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE)
                            .create();
                    String submitJson = gson.toJson(submitData);

                    ShiftRequestPoster shiftRequestPoster = new ShiftRequestPoster();
                    shiftRequestPoster.execute(URLPost3,submitJson);
                }
            }
        });

    }

    private class GroupIdReceiver extends AsyncTask<String,Void,String>{

        private static final String DEBUG_TAG = "groupIdReceiver";
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
            String status = "";

            try {
                JSONObject jsonObject = new JSONObject(result);
                JSONArray jsonArray = jsonObject.getJSONArray("list");
                groupNameList = new ArrayList<>();
                groupNameList.add("------");
                groupIdList = new ArrayList<>();
                groupIdList.add(0);
                for(int i = 0 ; i < jsonArray.length() ; i++){
                    JSONObject data = jsonArray.getJSONObject(i);
                    String groupName = data.getString("name");
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

    private class ShiftRequestPoster extends AsyncTask<String,Void,String>{

        private static final String DEBUG_TAG = "ShiftRequestPoster";
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
            String status = "";

                try {
                    JSONObject jsonObject = new JSONObject(result);
                    status = jsonObject.getString("status");
                    if(status.equals("ok")){
                        Toast.makeText(getContext(),"シフト希望は登録しました。",Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(getContext(),"シフト希望の登録失敗しました。",Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
            }
        }
    }

    private class ShiftTypeDataReceiver extends AsyncTask<String,Void,String> {

        private static final String DEBUG_TAG = "ShiftTypeDataReceiver";
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
            try {
                Calendar calendar = Calendar.getInstance();
                calendar.add(Calendar.MONTH,+1);
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH)+1;
                String strMonth = String.valueOf(month);
                if (month < 10){
                    strMonth = "0" +strMonth;
                }
                int days = DateUtils.getDaysByYearMonth(year,month);
                JSONObject jsonObject = new JSONObject(result);
                int resFlg = jsonObject.getInt("res_flg");
                Log.e("pxl","resFlag = "+ "        "+resFlg);
                if(resFlg == 0){
                    int shiftId = jsonObject.getInt("shift_id");
                    if(shiftId != savedShiftId){
                        SharedPreferences ps = PreferenceManager.getDefaultSharedPreferences(getContext());
                        ps.edit().putInt("shiftId",shiftId).apply();
                    }
                    JSONArray shiftTypeDatas = jsonObject.getJSONArray("shift_type_datas");
                    List<ShiftTypeBean> list = new ArrayList<>();
                    int num = 0;
                    for(int num2 = 0;num2<shiftTypeDatas.length();num2++){
                        JSONObject dataObject = shiftTypeDatas.getJSONObject(num2);
                        ShiftTypeBean bean = new ShiftTypeBean();
                        bean.setShiftTypeId(dataObject.getInt("type_id"));
                        bean.setShiftId(dataObject.getInt("group_id"));
                        bean.setBeginTime(dataObject.getString("begin_time"));
                        bean.setEndTime(dataObject.getString("end_time"));
                        bean.setTypeName(dataObject.getString("type_name"));
                        bean.setComment(dataObject.getString("comment"));
                        list.add(bean);
                        DataAccess.shiftTypeReplace(db,bean);

                        num++;

                    }
                    int count = 1;
                    for(int i = 1;i<=days;i++){
                        String date = year + "-" + month + "-" + i;
                        for(int j=0;j<num;j++){
                            ShiftRequestBean shiftRequestBean = new ShiftRequestBean();
                            int tempShiftId = savedShiftId;
                            shiftRequestBean.setShiftId(tempShiftId);
                            int tempTypeId = list.get(j).getShiftTypeId();
                            shiftRequestBean.setShiftTypeId(tempTypeId);
                            shiftRequestBean.setId(count);
                            shiftRequestBean.setDate(date);
                            shiftRequestBean.setSelectedFlag(0);
                            shiftRequestBean.setKaburuFlag(0);
                            Date a = new Date();
                            shiftRequestBean.setId(count);
                            selfList = DataAccess.selfScheduleSelectByDate(db,date,savedId);
                            ShiftTypeBean shiftType = DataAccess.getShiftTypeByTypeId(db,tempTypeId);
                            int beginHour = Integer.valueOf(shiftType.getBeginTime().substring(0,2));
//                        int beginMin = Integer.valueOf(shiftType.getBeginTime().substring(3));
                            int endHour = Integer.valueOf(shiftType.getEndTime().substring(0,2));
//                        int endMin = Integer.valueOf(shiftType.getEndTime().substring(3));
                            for(int n=0;n<selfList.size();n++){
                                int selfBeginHour = Integer.valueOf(selfList.get(n).getStartTime().substring(0,2));
                                int selfBeginMin = Integer.valueOf(selfList.get(n).getStartTime().substring(3));
                                int selfEndHour = Integer.valueOf(selfList.get(n).getEndTime().substring(0,2));
                                int selfEndMin = Integer.valueOf(selfList.get(n).getEndTime().substring(3));
//                                Log.e("asdop","beginHour:"+beginHour+"    "+"endHour:"+endHour + "selfBeginHour:"+selfBeginHour +"  "+"selfEndHour:"+selfEndHour);
                                if(selfBeginHour <= beginHour){
                                    if(selfEndHour >= beginHour){
                                        shiftRequestBean.setSelfScheduleFlag(1);
                                    }
                                }
                                if (selfBeginHour >= beginHour && selfBeginHour <= endHour){
                                    shiftRequestBean.setSelfScheduleFlag(1);
                                }
                            }
                            DataAccess.shiftRequestReplace(db,shiftRequestBean);
                            count ++;
                        }
                    }
                    shiftMonthListAdapter = new ShiftMonthListAdapter(getContext());
                    dataList = getData();

                    shiftMonthListAdapter.setList(dataList);
                    shiftMonthListAdapter.notifyDataSetChanged();
                    LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
                    layoutManager.setOrientation(LinearLayoutManager.VERTICAL);//縦並び
                    shiftListView.setLayoutManager(layoutManager);
                    shiftListView.setAdapter(shiftMonthListAdapter);
                    buttonFlag = 0;
                    shiftListView.setVisibility(View.VISIBLE);
                    fragmentView.findViewById(R.id.no_data_layout).setVisibility(View.INVISIBLE);
                }else if(resFlg == 1){
                    int shiftId = jsonObject.getInt("shift_id");
                    if(shiftId != savedShiftId){
                        SharedPreferences ps = PreferenceManager.getDefaultSharedPreferences(getContext());
                        ps.edit().putInt("shiftId",shiftId).apply();
                    }
                    JSONArray shiftTypeDatas = jsonObject.getJSONArray("shift_type_datas");
                    JSONArray shiftRequestDatas = jsonObject.getJSONArray("shift_request_datas");
                    List<ShiftTypeBean> list = new ArrayList<>();
                    for(int num2 = 0;num2<shiftTypeDatas.length();num2++){
                        JSONObject dataObject = shiftTypeDatas.getJSONObject(num2);
                        ShiftTypeBean bean = new ShiftTypeBean();
                        bean.setShiftTypeId(dataObject.getInt("type_id"));
                        bean.setShiftId(dataObject.getInt("group_id"));
                        bean.setBeginTime(dataObject.getString("begin_time"));
                        bean.setEndTime(dataObject.getString("end_time"));
                        bean.setTypeName(dataObject.getString("type_name"));
                        bean.setComment(dataObject.getString("comment"));
                        list.add(bean);
                        DataAccess.shiftTypeReplace(db,bean);
                    }
                    for (int num3 = 0;num3<shiftRequestDatas.length();num3++){
                        JSONObject requestObject = shiftRequestDatas.getJSONObject(num3);
                        ShiftRequestBean shiftRequestBean = new ShiftRequestBean();
                        shiftRequestBean.setId(requestObject.getInt("id"));
                        shiftRequestBean.setUserId(requestObject.getInt("user_id"));
                        shiftRequestBean.setDate(requestObject.getString("date"));
                        shiftRequestBean.setShiftId(requestObject.getInt("shift_id"));
                        shiftRequestBean.setShiftTypeId(requestObject.getInt("type_id"));
                        shiftRequestBean.setSelectedFlag(requestObject.getInt("selected_flag"));
                        shiftRequestBean.setKaburuFlag(requestObject.getInt("kaburu_flag"));
                        String date = requestObject.getString("date");
                        String stringYear = date.substring(0,4);
                        String stringMonth = "";
                        String stringDay = "";
                        if(date.substring(5,6).equals("0")){
                            stringMonth = date.substring(6,7);

                        }else{
                            stringMonth = date.substring(5,6);
                        }
                        if (date.substring(8,9).equals("0")){
                            stringDay = date.substring(9,10);
                        }else{
                            stringDay = date.substring(8,10);
                        }
                        String strDate = stringYear + "-" + stringMonth + "-" + stringDay;
                        selfList = DataAccess.selfScheduleSelectByDate(db,strDate,savedId);
                        ShiftTypeBean shiftType = DataAccess.getShiftTypeByTypeId(db,requestObject.getInt("type_id"));
                        int beginHour = Integer.valueOf(shiftType.getBeginTime().substring(0,2));
//                        int beginMin = Integer.valueOf(shiftType.getBeginTime().substring(3));
                        int endHour = Integer.valueOf(shiftType.getEndTime().substring(0,2));
//                        int endMin = Integer.valueOf(shiftType.getEndTime().substring(3));
                        for(int i=0;i<selfList.size();i++){
                            int selfBeginHour = Integer.valueOf(selfList.get(i).getStartTime().substring(0,2));
                            int selfBeginMin = Integer.valueOf(selfList.get(i).getStartTime().substring(3));
                            int selfEndHour = Integer.valueOf(selfList.get(i).getEndTime().substring(0,2));
                            int selfEndMin = Integer.valueOf(selfList.get(i).getEndTime().substring(3));
                            if(selfBeginHour < beginHour){
                                if(selfEndHour > beginHour){
                                    shiftRequestBean.setSelfScheduleFlag(1);
                                }
                            }
                            if (selfBeginHour >= beginHour && selfBeginHour <= endHour){
                                shiftRequestBean.setSelfScheduleFlag(1);
                            }
                        }

                        DataAccess.shiftRequestReplace(db,shiftRequestBean);
                    }
                    shiftMonthListAdapter = new ShiftMonthListAdapter(getContext());
                    dataList = getData();
                    shiftMonthListAdapter.setList(dataList);
                    shiftMonthListAdapter.notifyDataSetChanged();
                    LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
                    layoutManager.setOrientation(LinearLayoutManager.VERTICAL);//縦並び
                    shiftListView.setLayoutManager(layoutManager);
                    shiftListView.setAdapter(shiftMonthListAdapter);
                    buttonFlag = 0;
                    shiftListView.setVisibility(View.VISIBLE);
                    fragmentView.findViewById(R.id.no_data_layout).setVisibility(View.INVISIBLE);
                }else if(resFlg == 2){
                    int shiftId = jsonObject.getInt("shift_id");
                    if(shiftId != savedShiftId){
                        SharedPreferences ps = PreferenceManager.getDefaultSharedPreferences(getContext());
                        ps.edit().putInt("shiftId",shiftId).apply();
                    }
                    JSONArray shiftTypeDatas = jsonObject.getJSONArray("shift_type_datas");
                    JSONArray shiftRequestDatas = jsonObject.getJSONArray("shift_request_datas");
                    List<ShiftTypeBean> list = new ArrayList<>();
                    for(int num2 = 0;num2<shiftTypeDatas.length();num2++){
                        JSONObject dataObject = shiftTypeDatas.getJSONObject(num2);
                        ShiftTypeBean bean = new ShiftTypeBean();
                        bean.setShiftTypeId(dataObject.getInt("type_id"));
                        bean.setShiftId(dataObject.getInt("group_id"));
                        bean.setBeginTime(dataObject.getString("begin_time"));
                        bean.setEndTime(dataObject.getString("end_time"));
                        bean.setTypeName(dataObject.getString("type_name"));
                        bean.setComment(dataObject.getString("comment"));
                        list.add(bean);
                        DataAccess.shiftTypeReplace(db,bean);
                    }
                    for (int num3 = 0;num3<shiftRequestDatas.length();num3++){
                        JSONObject requestObject = shiftRequestDatas.getJSONObject(num3);
                        ShiftRequestBean shiftRequestBean = new ShiftRequestBean();
                        shiftRequestBean.setId(requestObject.getInt("id"));
                        shiftRequestBean.setUserId(requestObject.getInt("user_id"));
                        shiftRequestBean.setDate(requestObject.getString("date"));
                        shiftRequestBean.setShiftId(requestObject.getInt("shift_id"));
                        shiftRequestBean.setShiftTypeId(requestObject.getInt("type_id"));
                        shiftRequestBean.setSelectedFlag(requestObject.getInt("selected_flag"));
                        shiftRequestBean.setKaburuFlag(requestObject.getInt("kaburu_flag"));
                        String date = requestObject.getString("date");
                        String stringYear = date.substring(0,4);
                        String stringMonth = "";
                        String stringDay = "";
                        if(date.substring(5,6).equals("0")){
                            stringMonth = date.substring(6,7);

                        }else{
                            stringMonth = date.substring(5,6);
                        }
                        if (date.substring(8,9).equals("0")){
                            stringDay = date.substring(9,10);
                        }else{
                            stringDay = date.substring(8,10);
                        }
                        String strDate = stringYear + "-" + stringMonth + "-" + stringDay;
                        selfList = DataAccess.selfScheduleSelectByDate(db,strDate,savedId);
                        ShiftTypeBean shiftType = DataAccess.getShiftTypeByTypeId(db,requestObject.getInt("type_id"));
                        int beginHour = Integer.valueOf(shiftType.getBeginTime().substring(0,2));
//                        int beginMin = Integer.valueOf(shiftType.getBeginTime().substring(3));
                        int endHour = Integer.valueOf(shiftType.getEndTime().substring(0,2));
//                        int endMin = Integer.valueOf(shiftType.getEndTime().substring(3));
                        for(int i=0;i<selfList.size();i++){
                            int selfBeginHour = Integer.valueOf(selfList.get(i).getStartTime().substring(0,2));
                            int selfBeginMin = Integer.valueOf(selfList.get(i).getStartTime().substring(3));
                            int selfEndHour = Integer.valueOf(selfList.get(i).getEndTime().substring(0,2));
                            int selfEndMin = Integer.valueOf(selfList.get(i).getEndTime().substring(3));
                            if(selfBeginHour < beginHour){
                                if(selfEndHour > beginHour){
                                    shiftRequestBean.setSelfScheduleFlag(1);
                                }
                            }
                            if (selfBeginHour >= beginHour && selfBeginHour <= endHour){
                                shiftRequestBean.setSelfScheduleFlag(1);
                            }
                        }
                        DataAccess.shiftRequestReplace(db,shiftRequestBean);
                    }
                    shiftMonthListAdapter2 = new ShiftMonthListAdapter2(getContext());
                    dataList = getData();
                    shiftMonthListAdapter2.setList(dataList);
                    shiftMonthListAdapter2.notifyDataSetChanged();
                    LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
                    layoutManager.setOrientation(LinearLayoutManager.VERTICAL);//縦並び
                    shiftListView.setLayoutManager(layoutManager);
                    shiftListView.setAdapter(shiftMonthListAdapter2);
                    buttonFlag = 1;
                    shiftListView.setVisibility(View.VISIBLE);
                    fragmentView.findViewById(R.id.no_data_layout).setVisibility(View.INVISIBLE);
                }else if (resFlg == 3){
                    int shiftId = jsonObject.getInt("shift_id");
                    if(shiftId != savedShiftId){
                        SharedPreferences ps = PreferenceManager.getDefaultSharedPreferences(getContext());
                        ps.edit().putInt("shiftId",shiftId).apply();
                    }
                    JSONArray shiftTypeDatas = jsonObject.getJSONArray("shift_type_datas");
                    JSONArray shiftRequestDatas = jsonObject.getJSONArray("shift_request_datas");
                    List<ShiftTypeBean> list = new ArrayList<>();
                    for(int num2 = 0;num2<shiftTypeDatas.length();num2++){
                        JSONObject dataObject = shiftTypeDatas.getJSONObject(num2);
                        ShiftTypeBean bean = new ShiftTypeBean();
                        bean.setShiftTypeId(dataObject.getInt("type_id"));
                        bean.setShiftId(dataObject.getInt("group_id"));
                        bean.setBeginTime(dataObject.getString("begin_time"));
                        bean.setEndTime(dataObject.getString("end_time"));
                        bean.setTypeName(dataObject.getString("type_name"));
                        bean.setComment(dataObject.getString("comment"));
                        list.add(bean);
                        DataAccess.shiftTypeReplace(db,bean);
                    }
                    for (int num3 = 0;num3<shiftRequestDatas.length();num3++){
                        JSONObject requestObject = shiftRequestDatas.getJSONObject(num3);
                        ShiftRequestBean shiftRequestBean = new ShiftRequestBean();
                        shiftRequestBean.setId(requestObject.getInt("id"));
                        shiftRequestBean.setUserId(requestObject.getInt("user_id"));
                        shiftRequestBean.setDate(requestObject.getString("date"));
                        shiftRequestBean.setShiftId(requestObject.getInt("shift_id"));
                        shiftRequestBean.setShiftTypeId(requestObject.getInt("type_id"));
                        shiftRequestBean.setSelectedFlag(requestObject.getInt("selected_flag"));
                        shiftRequestBean.setKaburuFlag(requestObject.getInt("kaburu_flag"));
                        String date = requestObject.getString("date");
                        String stringYear = date.substring(0,4);
                        String stringMonth = "";
                        String stringDay = "";
                        if(date.substring(5,6).equals("0")){
                            stringMonth = date.substring(6,7);

                        }else{
                            stringMonth = date.substring(5,6);
                        }
                        if (date.substring(8,9).equals("0")){
                            stringDay = date.substring(9,10);
                        }else{
                            stringDay = date.substring(8,10);
                        }
                        String strDate = stringYear + "-" + stringMonth + "-" + stringDay;
                        selfList = DataAccess.selfScheduleSelectByDate(db,strDate,savedId);
                        ShiftTypeBean shiftType = DataAccess.getShiftTypeByTypeId(db,requestObject.getInt("type_id"));
                        int beginHour = Integer.valueOf(shiftType.getBeginTime().substring(0,2));
//                        int beginMin = Integer.valueOf(shiftType.getBeginTime().substring(3));
                        int endHour = Integer.valueOf(shiftType.getEndTime().substring(0,2));
//                        int endMin = Integer.valueOf(shiftType.getEndTime().substring(3));
                        for(int i=0;i<selfList.size();i++){
                            int selfBeginHour = Integer.valueOf(selfList.get(i).getStartTime().substring(0,2));
                            int selfBeginMin = Integer.valueOf(selfList.get(i).getStartTime().substring(3));
                            int selfEndHour = Integer.valueOf(selfList.get(i).getEndTime().substring(0,2));
                            int selfEndMin = Integer.valueOf(selfList.get(i).getEndTime().substring(3));
                            if(selfBeginHour < beginHour){
                                if(selfEndHour > beginHour){
                                    shiftRequestBean.setSelfScheduleFlag(1);
                                }
                            }
                            if (selfBeginHour >= beginHour && selfBeginHour <= endHour){
                                shiftRequestBean.setSelfScheduleFlag(1);
                            }
                        }
                        DataAccess.shiftRequestReplace(db,shiftRequestBean);
                        shiftMonthListAdapter3 = new ShiftMonthListAdapter3(getContext());
                        dataList = getData();
                        shiftMonthListAdapter3.setList(dataList);
                        shiftMonthListAdapter3.notifyDataSetChanged();
                        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
                        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);//縦並び
                        shiftListView.setLayoutManager(layoutManager);
                        shiftListView.setAdapter(shiftMonthListAdapter3);
                        buttonFlag = 0;
                        shiftListView.setVisibility(View.VISIBLE);
                        fragmentView.findViewById(R.id.no_data_layout).setVisibility(View.INVISIBLE);
                        buttonFlag = 9;
                    }
                }else if (resFlg == 4){
                    shiftListView.setVisibility(View.INVISIBLE);
                    noDataMessage.setText(month+"月のシフトまだ考え中");
                    fragmentView.findViewById(R.id.no_data_layout).setVisibility(View.VISIBLE);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }


    private  List<List<ShiftTypeBean>> getData(){
        db = _helper.getWritableDatabase();
        SharedPreferences ps = PreferenceManager.getDefaultSharedPreferences(getContext());
        int shiftId = ps.getInt("shiftId",0);
        List<List<ShiftTypeBean>> data = new ArrayList<>();
        int num = DataAccess.getShiftTypeNum(db,selectedGroupId);

        List<Map<String,Object>> sourceData = DataAccess.getShiftRequestByShiftId(db,shiftId);
        Log.e("malebi",sourceData.toString());
        List<ShiftTypeBean> list = new ArrayList<>();
        if(num >0){
            for (int i = 0 ; i < sourceData.size() ; i ++){
                if(i % num == 0 ){
                    list = new ArrayList<>();
                }
                ShiftTypeBean bean = new ShiftTypeBean();
                bean.setBeginTime((String) sourceData.get(i).get("beginTime"));
                bean.setEndTime((String) sourceData.get(i).get("endTime"));
                bean.setTypeName((String) sourceData.get(i).get("typeName"));
                bean.setComment((String) sourceData.get(i).get("comment"));
                bean.setDate((String) sourceData.get(i).get("date"));
                if(sourceData.get(i).containsKey("shiftId")){
                    bean.setShiftId((Integer) sourceData.get(i).get("shiftId"));
                }
                if(sourceData.get(i).containsKey("shiftTypeId")){
                    bean.setShiftTypeId((Integer) sourceData.get(i).get("shiftTypeId"));
                }
                if(sourceData.get(i).containsKey("selectedFlag")){
                    bean.setSelectedFlag((Integer) sourceData.get(i).get("selectedFlag"));
                }
                if(sourceData.get(i).containsKey("id")){
                    bean.setShiftRequestId((Integer) sourceData.get(i).get("id"));
                }
                if(sourceData.get(i).containsKey("kaburuFlag")){
                    bean.setKaburuFlag((Integer) sourceData.get(i).get("kaburuFlag"));
                }
                if(sourceData.get(i).containsKey("selfScheduleFlag")){
                    bean.setSelfScheduleFlag((Integer) sourceData.get(i).get("selfScheduleFlag"));
                }

                list.add(bean);
                if (i % num == (num-1)){
                    data.add(list);
                }
            }
        }
        return data;
    }


//    private  List<List<ShiftTypeBean>> getTestData() {
//        Calendar calendar = Calendar.getInstance();
//        int year = calendar.get(Calendar.YEAR);
//        int month = calendar.get(Calendar.MONTH)+2;
//        int days = DateUtils.getDaysByYearMonth(year,month);
//
//        List<List<ShiftTypeBean>> data = new ArrayList<>();
//
//        for(int i = 1;i<=days;i++){
//            List<ShiftTypeBean> list = new ArrayList<>();
//            ShiftTypeBean bean = new ShiftTypeBean();
//            bean.setShiftId(1);
//            bean.setShiftTypeId(1);
//            bean.setBeginTime("10:00");
//            bean.setEndTime("19:00");
//            bean.setTypeName("A勤");
//            bean.setComment("休憩1時間");
//            list.add(bean);
//            bean = new ShiftTypeBean();
//            bean.setShiftId(2);
//            bean.setShiftTypeId(2);
//            bean.setBeginTime("12:00");
//            bean.setEndTime("21:00");
//            bean.setTypeName("B勤");
//            bean.setComment("休憩1時間");
//            list.add(bean);
//            bean = new ShiftTypeBean();
//            bean.setShiftId(3);
//            bean.setShiftTypeId(3);
//            bean.setBeginTime("12:00");
//            bean.setEndTime("21:00");
//            bean.setTypeName("C勤");
//            bean.setComment("休憩1時間");
//            list.add(bean);
////            bean = new ShiftTypeBean();
////            bean.setShiftId(4);
////            bean.setShiftTypeId(4);
////            bean.setBeginTime("12:00");
////            bean.setEndTime("21:00");
////            bean.setTypeName("D勤");
////            bean.setComment("休憩1時間");
////            list.add(bean);
////            bean = new ShiftTypeBean();
////            bean.setShiftId(5);
////            bean.setShiftTypeId(5);
////            bean.setBeginTime("12:00");
////            bean.setEndTime("21:00");
////            bean.setTypeName("E勤");
////            bean.setComment("休憩1時間");
////            list.add(bean);
//
//            data.add(list);
//        }
//
//        return data;
//    }

}
