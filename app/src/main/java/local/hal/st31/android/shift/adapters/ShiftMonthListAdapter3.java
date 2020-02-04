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
import android.widget.Toast;

import java.util.List;

import local.hal.st31.android.shift.R;
import local.hal.st31.android.shift.beans.ShiftTypeBean;
import local.hal.st31.android.shift.db.DataAccess;
import local.hal.st31.android.shift.db.DatabaseHelper;
import local.hal.st31.android.shift.utils.GlobalUtils;

public class ShiftMonthListAdapter3 extends RecyclerView.Adapter<ShiftMonthListAdapter3.ShiftMonthListViewHolder> {

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

    public ShiftMonthListAdapter3(Context context) {
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

        ShiftOptionAdapter3 shiftOptionAdapter = new ShiftOptionAdapter3(viewGroup.getContext());
        shiftOptionAdapter.setShiftList(data);
        shiftOptionAdapter.notifyDataSetChanged();
        LinearLayoutManager layoutManager = new LinearLayoutManager(viewGroup.getContext());
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);//横並び
        shiftMonthListViewHolder.recyclerView.setLayoutManager(layoutManager);
        shiftMonthListViewHolder.recyclerView.setAdapter(shiftOptionAdapter);
//        shiftOptionAdapter.notifyDataSetChanged();
//        shiftHopeList = new ArrayList<>();
        shiftOptionAdapter.setListener(new ShiftOptionAdapter3.onShiftTypeClickListener() {
            @Override
            public void onItemClick(int i, ShiftTypeBean res) {
//                if (res.getSelectedFlag() == 0) {
//                    res.setSelectedFlag(7);
//                } else {
//                    res.setSelectedFlag(0);
//
//                }
                Toast.makeText(GlobalUtils.getInstance().context,"シフトもう確定されました。",Toast.LENGTH_SHORT).show();
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

