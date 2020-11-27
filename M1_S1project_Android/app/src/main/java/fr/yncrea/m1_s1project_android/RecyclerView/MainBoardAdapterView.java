package fr.yncrea.m1_s1project_android.RecyclerView;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.JsonObject;

import java.util.ArrayList;

import fr.yncrea.m1_s1project_android.R;
import fr.yncrea.m1_s1project_android.models.Channel;

public class MainBoardAdapterView extends RecyclerView.Adapter<MainBoardViewHolder> {
    private final Context mContext;
    private final ArrayList<Channel> mChannelList;
    public SwitchCompat switch1;

    public MainBoardAdapterView(Context mContext, ArrayList<Channel> channelList) {
        this.mContext = mContext;
        this.mChannelList = channelList != null ? channelList : new ArrayList<>();//secu
    }

    public void updateChannelList(ArrayList<Channel> tmp, int index) {
        if (index == -1) {
            Log.d("testy update -1", " "+index+" "+tmp.get(7).getCurrentValue());
            for(int i = 0; i < tmp.size(); i++){
                mChannelList.get(i).setActive(tmp.get(i).isActive());
            }

            this.notifyDataSetChanged();
        }
        else if(index == -2){
            Log.d("testy update -2", " "+index+" "+tmp.get(7).getCurrentValue());
            mChannelList.clear();
            mChannelList.addAll(tmp);
            this.notifyDataSetChanged();
        }
        else {
            Log.d("not testy", ""+tmp.get(index).getId());
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

    }

    @Override
    public int getItemCount() {
        return mChannelList.size();
    }
}
