package fr.yncrea.m1_s1project_android.RecyclerView;

import android.content.Context;
import android.os.Vibrator;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import fr.yncrea.m1_s1project_android.R;
import fr.yncrea.m1_s1project_android.interfaces.BluetoothParent;
import fr.yncrea.m1_s1project_android.models.Channel;


public class MainBoardAdapterView extends RecyclerView.Adapter<MainBoardViewHolder> implements View.OnKeyListener {
    private final Context mContext;
    private final View mView;
    private final ArrayList<Channel> mChannelList;

    private int mFocusedIndex = -1;
    private int mDigitSelected = -1;

    private EditText mMinimum;
    private EditText mMaximum;
    private EditText mSelection;

    private Button mPlus;
    private Button mMoins;

    public MainBoardAdapterView(Context context, View view, ArrayList<Channel> channelList) {
        this.mContext = context;
        this.mView = view;
        this.mChannelList = channelList != null ? channelList : new ArrayList<>();//secu
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

    @NonNull
    @Override
    public MainBoardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_channel, parent, false);
        return new MainBoardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MainBoardViewHolder holder, int position) {
        holder.setInitialDisplay(this, mContext, mChannelList.get(position));
        holder.setInteractions(this, mContext, mView, mChannelList.get(position), position);

        mMinimum = mView.findViewById(R.id.minInputSelected);
        mMaximum = mView.findViewById(R.id.maxInputSelected);
        mSelection = mView.findViewById(R.id.selectedInput);

        mMaximum.setOnKeyListener(this);
        mMinimum.setOnKeyListener(this);
        mSelection.setOnKeyListener(this);
        /* //peut remplacer les this
        new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                return false;
            }
        };
        */

        mPlus = mView.findViewById(R.id.plus);
        mPlus.setOnClickListener(v -> crement(holder, +1));
        mMoins = mView.findViewById(R.id.moins);
        mMoins.setOnClickListener(v -> crement(holder, -1));
    }

    private void crement(final MainBoardViewHolder holder, final int step) {
        if (mFocusedIndex != -1) {
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
            }
            else if (number < 0) {
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
            }
            else digits[digit] = Character.forDigit(number, 10);

            double value = Double.parseDouble(new String(digits));

            mSelection.setText(String.valueOf(value));
            holder.setDigitsDisplay(value);
            mChannelList.get(mFocusedIndex).setCurrentValue(value);
            ((BluetoothParent) mContext).sendData((new Channel()).setId(mFocusedIndex).setCurrentValue(value));
        }
    }

    @Override
    public int getItemCount() {
        return mChannelList.size();
    }

    public MainBoardViewHolder getFocusedViewHolder() {
        //quand scroll, décale indices ?
        RecyclerView recycler = mView.findViewById(R.id.mainboard_recycler);
        //recycler.getChildCount();
        return (MainBoardViewHolder) recycler.getChildViewHolder(recycler.getChildAt(mFocusedIndex));
    }

    public int getFocusedIndex() {
        return mFocusedIndex;
    }

    public void setFocusedIndex(final int index) {
        mFocusedIndex = index;

        mSelection.setEnabled(true);
        mSelection.setText(String.valueOf(mChannelList.get(mFocusedIndex).getCurrentValue()));
        mMinimum.setEnabled(true);
        mMinimum.setText(String.valueOf(mChannelList.get(mFocusedIndex).getMinValue()));
        mMaximum.setEnabled(true);
        mMaximum.setText(String.valueOf(mChannelList.get(mFocusedIndex).getMaxValue()));
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
            int id = v.getId();
            if (id == R.id.minInputSelected && input != mChannelList.get(mFocusedIndex).getMinValue()) {
                mChannelList.get(mFocusedIndex).setMinValue(input);
                notifyItemChanged(mFocusedIndex);
            } else if (id == R.id.maxInputSelected && input != mChannelList.get(mFocusedIndex).getMaxValue()) {
                mChannelList.get(mFocusedIndex).setMaxValue(input);
                notifyItemChanged(mFocusedIndex);
            } else if (id == R.id.selectedInput && input != mChannelList.get(mFocusedIndex).getCurrentValue()) {
                mChannelList.get(mFocusedIndex).setCurrentValue(input);
                notifyItemChanged(mFocusedIndex);
                Log.d("testy", "current changed");
            }

        }
        return false;
    }
}
