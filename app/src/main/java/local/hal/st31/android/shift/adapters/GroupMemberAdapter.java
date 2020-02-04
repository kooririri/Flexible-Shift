package local.hal.st31.android.shift.adapters;


import android.content.Context;
import android.graphics.Color;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import local.hal.st31.android.shift.R;
import local.hal.st31.android.shift.beans.BlackListBean;

public class GroupMemberAdapter extends RecyclerView.Adapter<GroupMemberAdapter.GroupMemberViewHolder> {

    private LayoutInflater mInflater;
    private List<BlackListBean> data;
    private onMemberClickListener listener;

    public GroupMemberAdapter(Context context){
        mInflater = LayoutInflater.from(context);
    }

    static class GroupMemberViewHolder extends RecyclerView.ViewHolder{
        TextView nameTextView;

        public GroupMemberViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.txName);
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
        if(listener != null){
            groupMemberViewHolder.itemView.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    listener.onItemClick(position,bean);
                    notifyDataSetChanged();
                }
            });
            if (bean.getBlackRank() == 1) {
                groupMemberViewHolder.itemView.setBackgroundColor(bean.getColorCode());
            }
            if(bean.getBlackRank() == 0){
                groupMemberViewHolder.itemView.setBackgroundColor(Color.WHITE);
            }
        }

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