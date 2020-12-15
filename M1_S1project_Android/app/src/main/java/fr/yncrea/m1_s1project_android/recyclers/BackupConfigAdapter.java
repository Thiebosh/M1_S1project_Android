package fr.yncrea.m1_s1project_android.recyclers;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import fr.yncrea.m1_s1project_android.R;
import fr.yncrea.m1_s1project_android.models.Channel;

public class BackupConfigAdapter extends RecyclerView.Adapter<BackupConfigAdapter.Holder> {

    public static class Holder extends RecyclerView.ViewHolder {

        private final TextView mId;
        private final TextView mMin;
        private final TextView mMax;
        private final TextView mCurrent;

        public Holder(@NonNull View itemView) {
            super(itemView);

            mId = itemView.findViewById(R.id.item_channel_text_id);
            mMin = itemView.findViewById(R.id.item_channel_text_min);
            mMax = itemView.findViewById(R.id.item_channel_text_max);
            mCurrent = itemView.findViewById(R.id.item_channel_text_current);
        }

        public void setDisplay(final Channel channel) {
            mId.setText(itemView.getContext().getString(R.string.input, channel.getId()));

            String qualifier = channel.getScale().name()+channel.getUnit().name();
            String min = channel.getMinValue()+qualifier;
            String max = channel.getMaxValue()+qualifier;
            String current = channel.getCurrentValue()+qualifier;

            mMin.setText(min);
            mMax.setText(max);
            mCurrent.setText(current);
        }
    }

    private final ArrayList<Channel> mChannelList;

    public BackupConfigAdapter(ArrayList<Channel> channelList) {
        this.mChannelList = channelList != null ? channelList : new ArrayList<>();//secu
    }

    public void updateChannelListData(ArrayList<Channel> tmp, int index) {
        /*
        if (index == -1) {
            boolean active = tmp.get(0).isActive();
            for (int i = 0; i < mChannelList.size(); ++i) mChannelList.get(i).setActive(active);
            this.notifyDataSetChanged();
        }
        else if (index == -2) {
            mChannelList.clear();
            mChannelList.addAll(tmp);
            this.notifyDataSetChanged();
        }
        else {
            mChannelList.set(index, tmp.get(index));
            this.notifyItemChanged(index);
        }*/
    }

    @Override
    public int getItemCount() {
        return mChannelList.size();
    }

    public ArrayList<Channel> getChannelList() {
        return mChannelList;
    }

    public void setChannelList(ArrayList<Channel> channelList) {
        mChannelList.clear();
        mChannelList.addAll(channelList);
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Holder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_channel_display, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        holder.setDisplay(mChannelList.get(position));
    }
}
