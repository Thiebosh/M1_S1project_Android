package fr.yncrea.m1_s1project_android.recyclers;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Vibrator;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Objects;

import fr.yncrea.m1_s1project_android.R;
import fr.yncrea.m1_s1project_android.interfaces.BluetoothParent;
import fr.yncrea.m1_s1project_android.models.Channel;
import fr.yncrea.m1_s1project_android.models.Scale;
import fr.yncrea.m1_s1project_android.models.Unit;


public class MainBoardAdapter extends RecyclerView.Adapter<MainBoardHolder> {
    private final ArrayList<Channel> mChannelList;

    private MainBoardHolder mLastHolderSelected = null;
    private int mFocusedIndex = -1; //mLastHolderSelected.getAdapterPosition() peut casser aux extremes
    private int mDigitSelected = -1;

    private final ToggleButton mAllOn;
    private final ToggleButton mAllOff;

    private final EditText mMinimum;
    private final EditText mMaximum;
    private final EditText mSelection;

    private final Button mMore;
    private final Button mLess;

    public MainBoardAdapter(View view, ArrayList<Channel> channelList) {
        this.mChannelList = channelList != null ? channelList : new ArrayList<>();//secu

        mAllOn = view.findViewById(R.id.frag_main_allOn);
        mAllOff = view.findViewById(R.id.frag_main_allOff);

        mMinimum = view.findViewById(R.id.frag_main_input_min);
        mMaximum = view.findViewById(R.id.frag_main_input_max);

        mSelection = view.findViewById(R.id.frag_main_input_current);

        mMore = view.findViewById(R.id.frag_main_button_more);
        mLess = view.findViewById(R.id.frag_main_button_less);
    }

    @NonNull
    @Override
    public MainBoardHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MainBoardHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_channel_edit, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MainBoardHolder holder, int position) {
        holder.setInitialDisplay(this, mChannelList.get(position));
        holder.setInteractions(this, mChannelList.get(position), position);

        @SuppressLint("NonConstantResourceId") View.OnKeyListener keyListener = (view, keyCode, event) -> {
            if(keyCode == 66 && event.getAction() == KeyEvent.ACTION_DOWN) {
                String input = ((EditText) view).getText().toString();

                //réduction dans limites de l'acceptable
                boolean updateDisplay = false;

                boolean negative = false;
                if (input.startsWith("-")) {
                    input = input.substring(1);
                    negative = true;
                }

                Unit unit = mChannelList.get(mFocusedIndex).getUnit();
                //troncature
                if (unit == Unit.V && input.length() > 5) {//1 chiffre avant virgule, virgule, 3 chiffres apres virgule
                    input = input.substring(0, 5);
                    updateDisplay = true;
                }
                else if (unit == Unit.I && input.length() > 4) {//4 chiffres avant virgule
                    input = input.substring(0, 4);
                    updateDisplay = true;
                }

                if (negative) input = "-"+input;
                //fin réduction

                double min = mChannelList.get(mFocusedIndex).getMinValue();
                double max = mChannelList.get(mFocusedIndex).getMaxValue();
                double current = mChannelList.get(mFocusedIndex).getCurrentValue();

                int id = view.getId();
                double value = -1;
                try {
                    value = Double.parseDouble(input);
                }
                catch (Exception ignore) {
                    switch(id) {
                        case R.id.frag_main_input_min:
                            value = min;
                            break;
                        case R.id.frag_main_input_max:
                            value = max;
                            break;
                        case R.id.frag_main_input_current:
                            value = current;
                            break;
                    }
                    ((EditText) view).setText(String.valueOf(value));
                    return false;
                }

                int absMax = holder.itemView.getContext().getResources().getInteger(unit == Unit.V ?
                        R.integer.absolute_limit_volt_value :
                        R.integer.absolute_limit_ampere_value);
                int absMin = -1*absMax;

                if (id == R.id.frag_main_input_min && value != min) {
                    if (absMin <= value && value < max) mChannelList.get(mFocusedIndex).setMinValue(value);
                    else mMinimum.setText(String.valueOf(min));
                }
                else if (id == R.id.frag_main_input_max && value != max) {
                    if (min < value && value <= absMax) mChannelList.get(mFocusedIndex).setMaxValue(value);
                    else mMaximum.setText(String.valueOf(max));
                }
                else if (id == R.id.frag_main_input_current && value != current) {
                    double convertedValue = Scale.changeScale(value,
                            mChannelList.get(mFocusedIndex).getScale(),
                            Objects.requireNonNull(Scale.scaleOf(holder.itemView.getContext().getResources().getInteger(
                                    unit == Unit.V ?
                                            R.integer.absolute_limit_volt_scale :
                                            R.integer.absolute_limit_ampere_scale))));

                    //garder échelle des bornes pour comparer min max

                    //remettre echelle du canal avant de vérifier et rétablir

                    if (value != convertedValue) updateDisplay = true;

                    Log.d("testy", "passe de "+value+" à "+convertedValue);
                    value = convertedValue;
                    input = String.valueOf(value);
                    if (min <= value && value <= max) {
                        mChannelList.get(mFocusedIndex).setCurrentValue(value);
                        mLastHolderSelected.setDigitsDisplay(value);
                    }
                    else mSelection.setText(String.valueOf(current));
                }

                if (updateDisplay) ((EditText) view).setText(input);
            }

            return false;
        };
        mMaximum.setOnKeyListener(keyListener);
        mMinimum.setOnKeyListener(keyListener);
        mSelection.setOnKeyListener(keyListener);

        mMore.setOnClickListener(v -> variation(holder.itemView.getContext(), +1));
        mLess.setOnClickListener(v -> variation(holder.itemView.getContext(), -1));
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

    public MainBoardHolder getLastHolderSelected() {
        return mLastHolderSelected;
    }

    public void setLastHolderSelected(MainBoardHolder holder, final Channel channel, final int position) {
        mLastHolderSelected = holder;
        mFocusedIndex = position;

        mDigitSelected = -1;
        mMore.setEnabled(false);
        mLess.setEnabled(false);

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

        if (mChannelList.get(mFocusedIndex).getCurrentValue() > 0.000) mLess.setEnabled(true);
        if (mChannelList.get(mFocusedIndex).getCurrentValue() < 9.999) mMore.setEnabled(true);
    }

    private void variation(final Context context, final int step) {
        (step > 0 ? mLess : mMore).setEnabled(true);

        //cause approx issues
        //value = Double.parseDouble((BigDecimal.valueOf(value + sign * pow(10, -mDigitSelected))).setScale(3, RoundingMode.CEILING).toString());

        StringBuilder tmp = new StringBuilder(String.valueOf(mChannelList.get(mFocusedIndex).getCurrentValue()));
        while (tmp.length() < 5) tmp.append('0');
        char[] digits = tmp.toString().toCharArray();


        int digit = mDigitSelected + (mDigitSelected == 0 ? 0 : 1);

        int number = Character.getNumericValue(digits[digit]) + step;

        boolean isMax = false;
        boolean isMin = false;
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

                isMax = true;
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

                isMin = true;
            }
        } else digits[digit] = Character.forDigit(number, 10);

        double value = Double.parseDouble(new String(digits));

        if (isMax || value >= mChannelList.get(mFocusedIndex).getMaxValue()) {
            mMore.setEnabled(false);
            value = mChannelList.get(mFocusedIndex).getMaxValue();
            ((Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE)).vibrate(400);
        }
        else if (isMin || value <= mChannelList.get(mFocusedIndex).getMinValue()) {
            mLess.setEnabled(false);
            value = mChannelList.get(mFocusedIndex).getMinValue();
            ((Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE)).vibrate(400);
        }

        mSelection.setText(String.valueOf(value));
        mLastHolderSelected.setDigitsDisplay(value);
        mChannelList.get(mFocusedIndex).setCurrentValue(value);

        ((BluetoothParent) context).sendData((new Channel()).setId(mFocusedIndex).setCurrentValue(value));
    }
}
