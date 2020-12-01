package fr.yncrea.m1_s1project_android.RecyclerView;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.JsonObject;

import java.util.ArrayList;

import fr.yncrea.m1_s1project_android.R;
import fr.yncrea.m1_s1project_android.interfaces.BluetoothParent;
import fr.yncrea.m1_s1project_android.models.Channel;

public class MainBoardAdapterView extends RecyclerView.Adapter<MainBoardViewHolder> implements View.OnKeyListener {
    private final Context mContext;
    private final View mView;
    private final ArrayList<Channel> mChannelList;

    private int mFocusedIndex = -1;//initial
    private int mDigitSelected = -1;

    public MainBoardAdapterView(Context mContext, View view, ArrayList<Channel> channelList) {
        this.mContext = mContext;
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

        EditText mMinimum = mView.findViewById(R.id.minInputSelected);
        EditText mMaximum = mView.findViewById(R.id.maxInputSelected);
        EditText mSelection = mView.findViewById(R.id.selectedInput);

        holder.getContainer().setOnClickListener(view -> {
            if (mFocusedIndex != position) {
                if (mFocusedIndex != -1) notifyItemChanged(mFocusedIndex);//to decrease visibility
                notifyItemChanged(position);//to increase visibility
                mFocusedIndex = position;

                mSelection.setEnabled(true);
                mSelection.setText(String.valueOf(mChannelList.get(mFocusedIndex).getCurrentValue()));
                mMinimum.setEnabled(true);
                mMinimum.setText(String.valueOf(mChannelList.get(mFocusedIndex).getMinValue()));
                mMaximum.setEnabled(true);
                mMaximum.setText(String.valueOf(mChannelList.get(mFocusedIndex).getMaxValue()));
            }
        });
        if (position == mFocusedIndex) holder.increaseVisibility(mContext);
        else holder.decreaseVisibility();


        mView.findViewById(R.id.moins).setOnClickListener(v -> {
            Log.d("testy", "adapterview click on moins " + position);
            //mDigitSelected = 1;
            /*if(mFocusedIndex != -1){
                switch (mDigitSelected){
                    case 2131296395:
                        mChannelList.get(mFocusedIndex).setCurrentValue(mChannelList.get(mFocusedIndex).getCurrentValue()-0.001);
                        Log.d("testy", "moins 0,001");
                        notifyDataSetChanged();
                        break;

                }
            }*/

        });

        mMaximum.setOnKeyListener(this);
        mMinimum.setOnKeyListener(this);
        mSelection.setOnKeyListener(this);
        /*mMinimum.setOnKeyListener((View vi, int keyCode, KeyEvent event) -> {
            if(mFocusedIndex != -1 && keyCode == 66) {
                double minInput = Double.parseDouble(mMinimum.getText().toString());
                if(minInput != mChannelList.get(mFocusedIndex).getMinValue()){
                    mChannelList.get(mFocusedIndex).setMinValue(minInput);
                    Log.d("testy", "min changed");
                    notifyItemChanged(mFocusedIndex);
                }
            }
            return false;
        });

        mMaximum.setOnKeyListener((View vi, int keyCode, KeyEvent event) -> {
            if(mFocusedIndex != -1 && keyCode == 66) {
                double maxInput = Double.parseDouble(mMaximum.getText().toString());
                if(maxInput != mChannelList.get(mFocusedIndex).getMaxValue()){
                    mChannelList.get(mFocusedIndex).setMaxValue(maxInput);
                    Log.d("testy", "max changed");
                    notifyItemChanged(mFocusedIndex);
                }
            }
            return false;
        });*/

    }

    @Override
    public int getItemCount() {
        return mChannelList.size();
    }

    public int getFocusedIndex() {
        return mFocusedIndex;
    }

    public void setFocusedIndex(final int index) {
        mFocusedIndex = index;
    }

    public int getDigitSelected() {
        return mDigitSelected;
    }

    public void setDigitSelected(final int digit) {
        mDigitSelected = digit;
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        //Log.d("testy", "enter override onKey");
        if(mFocusedIndex != -1 && keyCode == 66) {
            double input = Double.parseDouble(((EditText) mView.findViewById(v.getId())).getText().toString());
            switch (v.getId()) {
                case R.id.minInputSelected:
                    if(input != mChannelList.get(mFocusedIndex).getMinValue()) mChannelList.get(mFocusedIndex).setMinValue(input);
                    break;

                case R.id.maxInputSelected:
                    if(input != mChannelList.get(mFocusedIndex).getMaxValue()) mChannelList.get(mFocusedIndex).setMaxValue(input);
                    break;

                case R.id.selectedInput:
                    if(input != mChannelList.get(mFocusedIndex).getCurrentValue()){
                        mChannelList.get(mFocusedIndex).setCurrentValue(input);
                        Log.d("testy", "current changed");
                    }
                    break;
            }
            notifyItemChanged(mFocusedIndex);
        }
        return false;
    }
}
