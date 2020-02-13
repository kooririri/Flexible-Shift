package local.hal.st31.android.shift.adapters;

import android.content.Context;
import android.graphics.Color;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import local.hal.st31.android.shift.R;
import local.hal.st31.android.shift.beans.ShiftTypeBean;

public class ShiftOptionAdapter2 extends RecyclerView.Adapter<ShiftOptionAdapter2.ShiftOptionViewHolder>{
    private LayoutInflater mInflater;
    private List<ShiftTypeBean> shiftList;
    private ShiftOptionAdapter2.onShiftTypeClickListener listener;

    static class ShiftOptionViewHolder extends RecyclerView.ViewHolder{
        LinearLayout shiftBlock;
        TextView optionBlock;
        TextView startTime;
        TextView endTime;
        ImageView isKaburu;
        ImageView arrowImage;
        ImageView kaburuImage;
        public ShiftOptionViewHolder(@NonNull View itemView) {
            super(itemView);
            shiftBlock = itemView.findViewById(R.id.shift_block);
            optionBlock = itemView.findViewById(R.id.shift_option);
            startTime = itemView.findViewById(R.id.tx_start_time);
            endTime = itemView.findViewById(R.id.tx_end_time);
            isKaburu = itemView.findViewById(R.id.is_kaburu);
            arrowImage = itemView.findViewById(R.id.arrow_image);
            kaburuImage = itemView.findViewById(R.id.kaburuImage);
        }
    }



    public ShiftOptionAdapter2(Context context){
        mInflater = LayoutInflater.from(context);
    }
    public void setShiftList(List<ShiftTypeBean> shiftList){
        this.shiftList = shiftList;
    }

    @NonNull
    @Override
    public ShiftOptionAdapter2.ShiftOptionViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = mInflater.inflate(R.layout.grid_shift_option,viewGroup,false);
        return new ShiftOptionAdapter2.ShiftOptionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ShiftOptionAdapter2.ShiftOptionViewHolder shiftOptionViewHolder, final int position) {
        final ShiftTypeBean res = shiftList.get(position);
        shiftOptionViewHolder.optionBlock.setText(res.getTypeName());
        shiftOptionViewHolder.startTime.setText(res.getBeginTime().substring(0,5));
        shiftOptionViewHolder.endTime.setText(res.getEndTime().substring(0,5));
        Log.e("kaburuka",res.toString());
        if (res.getSelfScheduleFlag() == 1){
            shiftOptionViewHolder.isKaburu.setImageResource(R.drawable.baseline_event_note_24);
//            shiftOptionViewHolder.isKaburu.setBackgroundResource(R.drawable.circle_item);
        }

        shiftOptionViewHolder.itemView.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                listener.onItemClick(position,res);
                notifyDataSetChanged();
            }

        });

        if (res.getSelectedFlag() == 0) {
            shiftOptionViewHolder.itemView.setBackgroundResource(R.drawable.button_unclicked_style);
            shiftOptionViewHolder.arrowImage.setImageResource(R.drawable.baseline_keyboard_arrow_right_24);
            shiftOptionViewHolder.optionBlock.setTextColor(Color.BLACK);
            shiftOptionViewHolder.startTime.setTextColor(Color.BLACK);
            shiftOptionViewHolder.endTime.setTextColor(Color.BLACK);
        }
        if (res.getSelectedFlag() == 8){
            shiftOptionViewHolder.itemView.setBackgroundResource(R.drawable.button_clicked_style);
            shiftOptionViewHolder.arrowImage.setImageResource(R.drawable.baseline_arrow_forward_ios_white_18);
            shiftOptionViewHolder.optionBlock.setTextColor(Color.WHITE);
            shiftOptionViewHolder.startTime.setTextColor(Color.WHITE);
            shiftOptionViewHolder.endTime.setTextColor(Color.WHITE);
        }
        if (res.getKaburuFlag() == 1){
            shiftOptionViewHolder.kaburuImage.setImageResource(R.drawable.baseline_error_outline_white_18);
//                shiftOptionViewHolder.isKaburu.setText("被ってる");
//                shiftOptionViewHolder.isKaburu.setBackgroundColor(Color.RED);
//                shiftOptionViewHolder.isKaburu.setTextColor(Color.WHITE);
        }else if(res.getKaburuFlag() == 2){
            shiftOptionViewHolder.kaburuImage.setImageResource(R.drawable.baseline_warning_white_24);
        }

    }


    @Override
    public int getItemCount() {
        return shiftList == null ? 0 : shiftList.size();
    }

    public interface onShiftTypeClickListener{
        void onItemClick(int position,ShiftTypeBean shiftTypeBean);
    }

    public ShiftOptionAdapter2.onShiftTypeClickListener getListener() {
        return listener;
    }

    public void setListener(ShiftOptionAdapter2.onShiftTypeClickListener listener) {
        this.listener = listener;
    }

}
