package local.hal.st31.android.shift.adapters;

import android.content.Context;
import android.graphics.Color;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import local.hal.st31.android.shift.R;
import local.hal.st31.android.shift.beans.ShiftTypeBean;

public class ShiftOptionAdapter extends RecyclerView.Adapter<ShiftOptionAdapter.ShiftOptionViewHolder> {

    private LayoutInflater mInflater;
    private List<ShiftTypeBean> shiftList;
    private onShiftTypeClickListener listener;

    static class ShiftOptionViewHolder extends RecyclerView.ViewHolder{
        LinearLayout shiftBlock;
        TextView optionBlock;
        TextView startTime;
        TextView endTime;
        ImageView isKaburu;
        ImageView arrowImage;
        public ShiftOptionViewHolder(@NonNull View itemView) {
            super(itemView);
            shiftBlock = itemView.findViewById(R.id.shift_block);
            optionBlock = itemView.findViewById(R.id.shift_option);
            startTime = itemView.findViewById(R.id.tx_start_time);
            endTime = itemView.findViewById(R.id.tx_end_time);
            isKaburu = itemView.findViewById(R.id.is_kaburu);
            arrowImage = itemView.findViewById(R.id.arrow_image);
        }
    }



    public ShiftOptionAdapter(Context context){
        mInflater = LayoutInflater.from(context);
    }
    public void setShiftList(List<ShiftTypeBean> shiftList){
        this.shiftList = shiftList;
    }

    @NonNull
    @Override
    public ShiftOptionViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = mInflater.inflate(R.layout.grid_shift_option,viewGroup,false);
        return new ShiftOptionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ShiftOptionViewHolder shiftOptionViewHolder, final int position) {
        final ShiftTypeBean res = shiftList.get(position);
        shiftOptionViewHolder.optionBlock.setText(res.getTypeName());
        shiftOptionViewHolder.startTime.setText(res.getBeginTime().substring(0,5));
        shiftOptionViewHolder.endTime.setText(res.getEndTime().substring(0,5));
//        Log.e("kaburuka",res.toString());
        if (res.getSelfScheduleFlag() == 1){
            shiftOptionViewHolder.isKaburu.setImageResource(R.drawable.baseline_event_note_24);
        }

        shiftOptionViewHolder.itemView.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                listener.onItemClick(position,res);
                notifyDataSetChanged();
            }

        });

        if (res.getSelectedFlag() == 0) {
//            shiftOptionViewHolder.itemView.setBackgroundColor(Color.WHITE);
            shiftOptionViewHolder.itemView.setBackgroundResource(R.drawable.button_unclicked_style);
            shiftOptionViewHolder.arrowImage.setImageResource(R.drawable.baseline_keyboard_arrow_right_24);
            shiftOptionViewHolder.optionBlock.setTextColor(Color.BLACK);
            shiftOptionViewHolder.startTime.setTextColor(Color.BLACK);
            shiftOptionViewHolder.endTime.setTextColor(Color.BLACK);
        }else if(res.getSelectedFlag() == 1 ||res.getSelectedFlag() == 9){
            shiftOptionViewHolder.itemView.setBackgroundResource(R.drawable.button_clicked_style);
            shiftOptionViewHolder.arrowImage.setImageResource(R.drawable.baseline_arrow_forward_ios_white_18);
            shiftOptionViewHolder.optionBlock.setTextColor(Color.WHITE);
            shiftOptionViewHolder.startTime.setTextColor(Color.WHITE);
            shiftOptionViewHolder.endTime.setTextColor(Color.WHITE);
        }
    }


    @Override
    public int getItemCount() {
        return shiftList == null ? 0 : shiftList.size();
    }

    public interface onShiftTypeClickListener{
        void onItemClick(int position,ShiftTypeBean shiftTypeBean);
    }

    public onShiftTypeClickListener getListener() {
        return listener;
    }

    public void setListener(onShiftTypeClickListener listener) {
        this.listener = listener;
    }

}

