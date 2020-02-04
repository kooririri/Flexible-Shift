package local.hal.st31.android.shift.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.preference.PreferenceManager;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import local.hal.st31.android.shift.R;

import local.hal.st31.android.shift.beans.ShiftTypeBean;
import local.hal.st31.android.shift.db.DataAccess;
import local.hal.st31.android.shift.db.DatabaseHelper;
import local.hal.st31.android.shift.utils.DateUtils;
import local.hal.st31.android.shift.utils.GlobalUtils;

public class ShiftMonthListAdapter extends RecyclerView.Adapter<ShiftMonthListAdapter.ShiftMonthListViewHolder> {

//    private List<TempBean> list;
    private  List<List<ShiftTypeBean>> list;
    private LayoutInflater mInflater;
    private ViewGroup viewGroup;
    private DatabaseHelper _helper;
    private SQLiteDatabase db;

    static class ShiftMonthListViewHolder extends RecyclerView.ViewHolder {
        TextView textView;
        RecyclerView recyclerView;

        public ShiftMonthListViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.txDate);
            recyclerView = itemView.findViewById(R.id.recyclerView);

        }
    }

    public ShiftMonthListAdapter(Context context) {
        mInflater = LayoutInflater.from(context);

    }


    @NonNull
    @Override
    public ShiftMonthListViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        this.viewGroup = viewGroup;
        View view = mInflater.inflate(R.layout.cell_shift_submit_list, viewGroup, false);
        _helper = new DatabaseHelper(GlobalUtils.getInstance().context);
        db = _helper.getWritableDatabase();
        return new ShiftMonthListViewHolder(view);
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(@NonNull ShiftMonthListViewHolder shiftMonthListViewHolder, int position) {
//        TempBean tempBean = list.get(position);
        SharedPreferences ps = PreferenceManager.getDefaultSharedPreferences(GlobalUtils.getInstance().context);
        int groupId = ps.getInt("groupId",0);
        int type_number = DataAccess.getShiftTypeNum(db,groupId);
        List<ShiftTypeBean> data = list.get(position);
        shiftMonthListViewHolder.textView.setText((position+1) + "日");

//        List<Integer> month = new ArrayList<>();
//        for(int i = 0;i < data.size(); i ++){
//            if(i % type_number == 0){
//                month.add(i);
//            }
//        }
//        for(int i = 0;i < month.size(); i ++){
//            shiftMonthListViewHolder.textView.setText((data.get(i).getDate()).substring(8)+"日");
//        }


//        Map<String, JSONArray> kaburuMap = GlobalUtils.getInstance().kaburuMap;
//        JSONArray jsonArray = kaburuMap.get("2");
//        Log.e("ppqq",jsonArray.length()+"");
//        String tempPositon = String.valueOf(position);
//        if(position <10){
//            tempPositon = "0" + position;
//        }
//        for (int i =0;i<jsonArray.length();i++){
//            try {
//                String day = jsonArray.getString(i);
//                if(tempPositon.equals(day.substring(8))){
//                    shiftMonthListViewHolder.textView.setBackgroundColor(R.color.sblue);
//                }
//
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//        }

//        shiftMonthListViewHolder.textView.setBackgroundColor(R.color.sblue);
        ShiftOptionAdapter shiftOptionAdapter = new ShiftOptionAdapter(viewGroup.getContext());
        shiftOptionAdapter.setShiftList(data);
        shiftOptionAdapter.notifyDataSetChanged();
        LinearLayoutManager layoutManager = new LinearLayoutManager(viewGroup.getContext());
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);//横並び
        shiftMonthListViewHolder.recyclerView.setLayoutManager(layoutManager);
        shiftMonthListViewHolder.recyclerView.setAdapter(shiftOptionAdapter);
//        shiftOptionAdapter.notifyDataSetChanged();
//        shiftHopeList = new ArrayList<>();
        shiftOptionAdapter.setListener(new ShiftOptionAdapter.onShiftTypeClickListener() {
            @Override
            public void onItemClick(int i, ShiftTypeBean res) {

                if (res.getSelectedFlag() == 0) {
                    res.setSelectedFlag(1);
                } else {
                    res.setSelectedFlag(0);

                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    public void setList(List<List<ShiftTypeBean>> list) {
        this.list = list;
        notifyDataSetChanged();
    }




}

