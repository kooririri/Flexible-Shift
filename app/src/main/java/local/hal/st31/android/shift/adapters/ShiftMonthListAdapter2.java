package local.hal.st31.android.shift.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.preference.PreferenceManager;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import local.hal.st31.android.shift.R;
import local.hal.st31.android.shift.beans.ShiftTypeBean;
import local.hal.st31.android.shift.db.DataAccess;
import local.hal.st31.android.shift.db.DatabaseHelper;
import local.hal.st31.android.shift.utils.GlobalUtils;

public class ShiftMonthListAdapter2 extends RecyclerView.Adapter<ShiftMonthListAdapter2.ShiftMonthListViewHolder>  {
    private  List<List<ShiftTypeBean>> list;
    private LayoutInflater mInflater;
    private ViewGroup viewGroup;
    private DatabaseHelper _helper;
    private SQLiteDatabase db;
    int count = 1;

    static class ShiftMonthListViewHolder extends RecyclerView.ViewHolder {
        TextView textView;
        RecyclerView recyclerView;

        public ShiftMonthListViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.txDate);
            recyclerView = itemView.findViewById(R.id.recyclerView);

        }
    }

    public ShiftMonthListAdapter2(Context context) {
        mInflater = LayoutInflater.from(context);

    }


    @NonNull
    @Override
    public ShiftMonthListAdapter2.ShiftMonthListViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        this.viewGroup = viewGroup;
        View view = mInflater.inflate(R.layout.cell_shift_submit_list, viewGroup, false);
        _helper = new DatabaseHelper(GlobalUtils.getInstance().context);
        db = _helper.getWritableDatabase();
        return new ShiftMonthListAdapter2.ShiftMonthListViewHolder(view);
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(@NonNull ShiftMonthListAdapter2.ShiftMonthListViewHolder shiftMonthListViewHolder, int position) {
//        TempBean tempBean = list.get(position);
        SharedPreferences ps = PreferenceManager.getDefaultSharedPreferences(GlobalUtils.getInstance().context);
        int groupId = ps.getInt("groupId",0);
        int type_number = DataAccess.getShiftTypeNum(db,groupId);
        List<ShiftTypeBean> data = list.get(position);
//        shiftMonthListViewHolder.textView.setText((position+1) + "日");
        List<Integer> month = new ArrayList<>();
        for(int i = 0;i < data.size(); i ++){
            if(i % type_number == 0){
                month.add(i);
            }
        }
        for(int i = 0;i < month.size(); i ++){
            shiftMonthListViewHolder.textView.setText((data.get(i).getDate()).substring(8)+"日");
        }


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
        ShiftOptionAdapter2 shiftOptionAdapter2 = new ShiftOptionAdapter2(viewGroup.getContext());
        shiftOptionAdapter2.setShiftList(data);
        shiftOptionAdapter2.notifyDataSetChanged();
        LinearLayoutManager layoutManager = new LinearLayoutManager(viewGroup.getContext());
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);//横並び
        shiftMonthListViewHolder.recyclerView.setLayoutManager(layoutManager);
        shiftMonthListViewHolder.recyclerView.setAdapter(shiftOptionAdapter2);
//        shiftOptionAdapter.notifyDataSetChanged();
//        shiftHopeList = new ArrayList<>();
        shiftOptionAdapter2.setListener(new ShiftOptionAdapter2.onShiftTypeClickListener() {

            @Override
            public void onItemClick(int i, ShiftTypeBean res) {
                if(res.getKaburuFlag() == 1&&res.getSelectedFlag()==8){
                    if(count <= 3){
                        res.setSelectedFlag(0);
                    }else{
                        Toast.makeText(GlobalUtils.getInstance().context,"3日以上変更できない。",Toast.LENGTH_SHORT).show();
                    }
                    Log.e("countjj",count+"");
                    count ++ ;
                }else{
                    Toast.makeText(GlobalUtils.getInstance().context,"被ってる日しか変更できない。",Toast.LENGTH_SHORT).show();
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

