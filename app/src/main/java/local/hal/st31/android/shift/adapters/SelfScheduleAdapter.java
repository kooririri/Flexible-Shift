package local.hal.st31.android.shift.adapters;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import local.hal.st31.android.shift.R;
import local.hal.st31.android.shift.beans.SelfScheduleBean;

public class SelfScheduleAdapter extends RecyclerView.Adapter<SelfScheduleAdapter.SelfScheduleViewHolder>  {

    private LayoutInflater mInflater;
    private List<SelfScheduleBean> data;
    private OnItemClickListener   mOnItemClickListener;

    public SelfScheduleAdapter(Context context){
        mInflater = LayoutInflater.from(context);
    }

    static class SelfScheduleViewHolder extends RecyclerView.ViewHolder {
        TextView timeTextView;
        TextView workTextVIew;
        TextView memoTextView;
        LinearLayout itemLayout;

        public SelfScheduleViewHolder(@NonNull View itemView) {
            super(itemView);
            timeTextView = itemView.findViewById(R.id.tx_time);
            workTextVIew = itemView.findViewById(R.id.tx_work);
            memoTextView = itemView.findViewById(R.id.tx_memo);
            itemLayout = itemView.findViewById(R.id.itemLayout);

        }
    }

    @NonNull
    @Override
    public SelfScheduleViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = mInflater.inflate(R.layout.cell_self_schedule_item,viewGroup,false);
        return new SelfScheduleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SelfScheduleViewHolder selfScheduleViewHolder, final int position) {
        SelfScheduleBean bean = data.get(position);
        selfScheduleViewHolder.timeTextView.setText(bean.getStartTime()+"-"+bean.getEndTime()
        );
        selfScheduleViewHolder.workTextVIew.setText(bean.getWork());
        selfScheduleViewHolder.memoTextView.setText(bean.getMemo());
        if (mOnItemClickListener != null) {
            selfScheduleViewHolder.itemLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mOnItemClickListener.onItemClick(view, position);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return data == null ? 0 : data.size();
    }

    public void setData(List<SelfScheduleBean> data){
        this.data = data;
    }

        public interface OnItemClickListener{
            void onItemClick(View view, int position);
        }

        public void setOnItemClickListener(OnItemClickListener mOnItemClickListener){
            this.mOnItemClickListener = mOnItemClickListener;
        }
}
