package local.hal.st31.android.shift.adapters;


import android.content.Context;
import android.graphics.Color;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import local.hal.st31.android.shift.R;
import local.hal.st31.android.shift.beans.BlackListBean;
import local.hal.st31.android.shift.utils.GlobalUtils;

public class GroupMemberAdapter extends RecyclerView.Adapter<GroupMemberAdapter.GroupMemberViewHolder> {

    private LayoutInflater mInflater;
    private List<BlackListBean> data;
    private onMemberClickListener listener;

    public GroupMemberAdapter(Context context){
        mInflater = LayoutInflater.from(context);
    }

    static class GroupMemberViewHolder extends RecyclerView.ViewHolder{
        TextView nameTextView;
        RatingBar ratingBar;

        public GroupMemberViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.txName);
            ratingBar = itemView.findViewById(R.id.ratingBar);
        }
    }
    @NonNull
    @Override
    public GroupMemberAdapter.GroupMemberViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = mInflater.inflate(R.layout.gird_group_member_item,viewGroup,false);
        return new GroupMemberViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GroupMemberAdapter.GroupMemberViewHolder groupMemberViewHolder, final int position) {
        final BlackListBean bean = data.get(position);
        groupMemberViewHolder.nameTextView.setText(bean.getNickName());
        groupMemberViewHolder.ratingBar.setRating(bean.getBlackRank());
        if(listener != null){
            groupMemberViewHolder.itemView.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    listener.onItemClick(position,bean);
//                    notifyDataSetChanged();
                }
            });
        }
        if(bean.getBlackRank() == 0){
            groupMemberViewHolder.nameTextView.setBackgroundColor(Color.WHITE);
        }else if (bean.getBlackRank() > 0) {
            groupMemberViewHolder.nameTextView.setBackgroundColor(bean.getColorCode());
        }
        groupMemberViewHolder.ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                bean.setBlackRank((int) rating);
                if(bean.getBlackRank() == 0){
                    groupMemberViewHolder.nameTextView.setBackgroundColor(Color.WHITE);
                }else if(bean.getBlackRank() > 0){
                    groupMemberViewHolder.nameTextView.setBackgroundColor(bean.getColorCode());
                }
                Toast.makeText(GlobalUtils.getInstance().context,bean.getNickName()+"   "+rating+"",Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return data == null ? 0 : data.size();
    }

    public void setData(List<BlackListBean> data){
        this.data = data;
    }

    public interface onMemberClickListener{
        void onItemClick(int position,BlackListBean blackListBean);
    }

    public onMemberClickListener getListener() {
        return listener;
    }

    public void setListener(onMemberClickListener listener) {
        this.listener = listener;
    }
}