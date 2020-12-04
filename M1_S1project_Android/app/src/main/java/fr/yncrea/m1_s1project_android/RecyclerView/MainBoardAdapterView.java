package fr.yncrea.m1_s1project_android.RecyclerView;

import android.content.Context;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import fr.yncrea.m1_s1project_android.R;
import fr.yncrea.m1_s1project_android.interfaces.BluetoothParent;
import fr.yncrea.m1_s1project_android.models.Channel;


public class MainBoardAdapterView extends RecyclerView.Adapter<MainBoardViewHolder> {
    private final Context mContext;
    private final ArrayList<Channel> mChannelList;

    private MainBoardViewHolder mLastHolderSelected = null;
    private int mFocusedIndex = -1; //mLastHolderSelected.getAdapterPosition() peut casser aux extremes
    private int mDigitSelected = -1;

    private final ToggleButton mAllOn;
    private final ToggleButton mAllOff;

    private final EditText mMinimum;
    private final EditText mMaximum;
    private final EditText mSelection;

    private final Button mPlus;
    private final Button mMoins;

    public MainBoardAdapterView(Context context, View view, ArrayList<Channel> channelList) {
        this.mContext = context;
        this.mChannelList = channelList != null ? channelList : new ArrayList<>();//secu

        mAllOn = view.findViewById(R.id.AllOn);
        mAllOff = view.findViewById(R.id.AllOff);

        mMinimum = view.findViewById(R.id.minInputSelected);
        mMaximum = view.findViewById(R.id.maxInputSelected);

        mSelection = view.findViewById(R.id.selectedInput);

        mPlus = view.findViewById(R.id.plus);
        mMoins = view.findViewById(R.id.moins);
    }

    @NonNull
    @Override
    public MainBoardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MainBoardViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_channel, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MainBoardViewHolder holder, int position) {
        holder.setInitialDisplay(this, mContext, mChannelList.get(position));
        holder.setInteractions(this, mContext, mChannelList.get(position), position);

        View.OnKeyListener keyListener = (view, keyCode, event) -> {
            if(mFocusedIndex != -1 && keyCode == 66) {
                double input = Double.parseDouble((mSelection.getText().toString()));
                int id = view.getId();
                if (id == R.id.minInputSelected && input != mChannelList.get(mFocusedIndex).getMinValue()) {
                    mChannelList.get(mFocusedIndex).setMinValue(input);
                    notifyItemChanged(mFocusedIndex);
                } else if (id == R.id.maxInputSelected && input != mChannelList.get(mFocusedIndex).getMaxValue()) {
                    mChannelList.get(mFocusedIndex).setMaxValue(input);
                    notifyItemChanged(mFocusedIndex);
                } else if (id == R.id.selectedInput && input != mChannelList.get(mFocusedIndex).getCurrentValue()) {
                    mChannelList.get(mFocusedIndex).setCurrentValue(input);
                    notifyItemChanged(mFocusedIndex);
                }
            }
            return false;
        };
        mMaximum.setOnKeyListener(keyListener);
        mMinimum.setOnKeyListener(keyListener);
        mSelection.setOnKeyListener(keyListener);

        mPlus.setOnClickListener(v -> variation(+1));
        mMoins.setOnClickListener(v -> variation(-1));
    }

    /*
    @Override
    public void onBindViewHolder(@NonNull MainBoardViewHolder holder, int position, List<Object> payloads) {
        if(!payloads.isEmpty()) {
            if (payloads.get(0) instanceof Double) {
                Log.d("testy", "binder recieve "+payloads.get(0));
                holder.setDigitsDisplay(((Double) payloads.get(0)));
            }
        }
        else super.onBindViewHolder(holder,position, payloads);
    }
    */

    @Override
    public int getItemCount() {
        return mChannelList.size();
    }

    public void updateChannelListData(ArrayList<Channel> tmp, int index) {
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

    public ToggleButton getAllOn() {
        return mAllOn;
    }

    public ToggleButton getAllOff() {
        return mAllOff;
    }

    public MainBoardViewHolder getLastHolderSelected() {
        return mLastHolderSelected;
    }

    public void setLastHolderSelected(MainBoardViewHolder holder, final Channel channel, final int position) {
        mLastHolderSelected = holder;
        mFocusedIndex = position;

        mDigitSelected = -1;
        mPlus.setEnabled(false);
        mMoins.setEnabled(false);


        mSelection.setEnabled(true);
        mSelection.setText(String.valueOf(channel.getCurrentValue()));
        mMinimum.setEnabled(true);
        mMinimum.setText(String.valueOf(channel.getMinValue()));
        mMaximum.setEnabled(true);
        mMaximum.setText(String.valueOf(channel.getMaxValue()));
    }

    public int getDigitSelected() {
        return mDigitSelected;
    }

    public void setDigitSelected(final int digit) {
        mDigitSelected = digit;

        if (mChannelList.get(mFocusedIndex).getCurrentValue() > 0.000) mMoins.setEnabled(true);
        if (mChannelList.get(mFocusedIndex).getCurrentValue() < 9.999) mPlus.setEnabled(true);
    }

    private void variation(final int step) {
        (step > 0 ? mMoins : mPlus).setEnabled(true);

        //cause approx issues
        //value = Double.parseDouble((BigDecimal.valueOf(value + sign * pow(10, -mDigitSelected))).setScale(3, RoundingMode.CEILING).toString());

        StringBuilder tmp = new StringBuilder(String.valueOf(mChannelList.get(mFocusedIndex).getCurrentValue()));
        while (tmp.length() < 5) tmp.append('0');
        char[] digits = tmp.toString().toCharArray();


        int digit = mDigitSelected + (mDigitSelected == 0 ? 0 : 1);

        int number = Character.getNumericValue(digits[digit]) + step;

        if (number > 9) {
            while (number > 9 && digit > 0) {//tant que retenue
                number -= 10;
                digits[digit] = Character.forDigit(number, 10);//base 10

                if (digit == 2) --digit;//saute le point
                number = Character.getNumericValue(digits[--digit]) + 1;//prend digit précédent
            }
            if (number < 10) digits[digit] = Character.forDigit(number, 10);//protège digit 0
            else if (number == 10) {
                digits[0] = '9';
                digits[2] = '9';
                digits[3] = '9';
                digits[4] = '9';

                mPlus.setEnabled(false);
                ((Vibrator) mContext.getSystemService(Context.VIBRATOR_SERVICE)).vibrate(400);
            }
        } else if (number < 0) {
            while (number < 0 && digit > 0) {//tant que retenue
                number += 10;
                digits[digit] = Character.forDigit(number, 10);//base 10

                if (digit == 2) --digit;//saute le point
                number = Character.getNumericValue(digits[--digit]) - 1;//prend digit précédent
            }
            if (number > -1) digits[digit] = Character.forDigit(number, 10);//protège digit 0
            else if (number == -1) {
                digits[0] = '0';
                digits[2] = '0';
                digits[3] = '0';
                digits[4] = '0';

                mMoins.setEnabled(false);
                ((Vibrator) mContext.getSystemService(Context.VIBRATOR_SERVICE)).vibrate(400);
            }
        } else digits[digit] = Character.forDigit(number, 10);

        double value = Double.parseDouble(new String(digits));

        mSelection.setText(String.valueOf(value));
        mLastHolderSelected.setDigitsDisplay(value);

        mChannelList.get(mFocusedIndex).setCurrentValue(value);

        ((BluetoothParent) mContext).sendData((new Channel()).setId(mFocusedIndex).setCurrentValue(value));
    }
}
