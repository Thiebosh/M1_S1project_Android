package fr.yncrea.m1_s1project_android.RecyclerView;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButtonToggleGroup;

import java.util.ArrayList;

import fr.yncrea.m1_s1project_android.R;
import fr.yncrea.m1_s1project_android.models.Channel;

public class MainBoardAdapterView extends RecyclerView.Adapter<MainBoardViewHolder> {
    private final Context mContext;
    private final View mView;
    private final ArrayList<Channel> mChannelList;

    private int mFocusedIndex = -1;
    private int mDigitSelected = -1;

    public MainBoardAdapterView(Context context, View view, ArrayList<Channel> channelList) {
        this.mContext = context;
        this.mView = view;
        this.mChannelList = channelList != null ? channelList : new ArrayList<>();//secu
    }

    public void updateChannelList(ArrayList<Channel> tmp, int index) {
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
        holder.setInteractions(this, mContext, mView, mChannelList.get(position), position);

        mView.findViewById(R.id.plus).setOnClickListener(v -> {
            Log.d("testy", "adapterview click on plus " + position);
            //mDigitSelected = 1;
        });
        mView.findViewById(R.id.moins).setOnClickListener(v -> {
            Log.d("testy", "adapterview click on moins " + position);
            //mDigitSelected = 1;
        });
    }

    @Override
    public int getItemCount() {
        return mChannelList.size();
    }

    public MainBoardViewHolder getFocusedViewHolder() {
        //if pas à l'écran, modifie pas à la main mais avec un update

        RecyclerView recycler = mView.findViewById(R.id.mainboard_recycler);
        return (MainBoardViewHolder) recycler.getChildViewHolder(recycler.getChildAt(mFocusedIndex));
    }

    public int getFocusedIndex() {
        return mFocusedIndex;
    }

    public void setFocusedIndex(final int index) {
        mFocusedIndex = index;

        ((EditText) mView.findViewById(R.id.selectedInput)).setHint("value for channel "+index);
        ((EditText) mView.findViewById(R.id.minInputSelected)).setHint(String.valueOf(mChannelList.get(index).getMinValue()));
        ((EditText) mView.findViewById(R.id.maxInputSelected)).setHint(String.valueOf(mChannelList.get(index).getMaxValue()));
    }

    public int getDigitSelected() {
        return mDigitSelected;
    }

    public void setDigitSelected(final int digit) {
        mDigitSelected = digit;
    }
}
