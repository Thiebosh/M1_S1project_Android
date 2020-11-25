package fr.yncrea.m1_s1project_android.RecyclerView;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import fr.yncrea.m1_s1project_android.R;
import fr.yncrea.m1_s1project_android.models.Channel;

public class MainBoardAdapterView extends RecyclerView.Adapter<MainBoardViewHolder> {
    private final Context mContext;
    private final ArrayList<Channel> channelList;

    public MainBoardAdapterView(Context mContext, ArrayList<Channel> channelList) {
        this.mContext = mContext;
        this.channelList = channelList;
    }

    @NonNull
    @Override
    public MainBoardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_channel, parent, false);
        return new MainBoardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MainBoardViewHolder holder, int position) {
        holder.setInitialDisplay(mContext, channelList.get(position));
        holder.setInteractions(mContext, channelList.get(position));

    }

    @Override
    public int getItemCount() {
        return channelList.size();
    }
}
