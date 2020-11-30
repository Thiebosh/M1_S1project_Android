package fr.yncrea.m1_s1project_android.RecyclerView;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.JsonObject;

import java.util.ArrayList;

import fr.yncrea.m1_s1project_android.R;
import fr.yncrea.m1_s1project_android.models.Channel;

public class MainBoardAdapterView extends RecyclerView.Adapter<MainBoardViewHolder> {
    private final Context mContext;
    private final ArrayList<Channel> mChannelList;
    private int mFocusedIndex = -1;//initial

    public MainBoardAdapterView(Context mContext, ArrayList<Channel> channelList) {
        this.mContext = mContext;
        this.mChannelList = channelList != null ? channelList : new ArrayList<>();//secu
    }

    public void updateChannelList(ArrayList<Channel> tmp, int index) {
        if (index == -1) {
            //mChannelList.set(index, tmp.get(index));
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
        }
    }

    @NonNull
    @Override
    public MainBoardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_channel, parent, false);
        return new MainBoardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MainBoardViewHolder holder, int position) {
        holder.setInitialDisplay(mContext, mChannelList.get(position));
        holder.setInteractions(mContext, mChannelList.get(position));

        holder.getContainer().setOnClickListener(view -> {
            if (mFocusedIndex != position) {
                if (mFocusedIndex != -1) MainBoardAdapterView.this.notifyItemChanged(mFocusedIndex);//to decrease visibility
                MainBoardAdapterView.this.notifyItemChanged(position);//to increase visibility
                mFocusedIndex = position;
                //...
            }
        });
        if (position == mFocusedIndex) holder.increaseVisibility(mContext);
        else holder.decreaseVisibility();
    }

    @Override
    public int getItemCount() {
        return mChannelList.size();
    }
}
