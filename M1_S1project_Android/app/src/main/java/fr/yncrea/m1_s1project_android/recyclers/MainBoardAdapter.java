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
                int id = view.getId();
                String input = ((EditText) view).getText().toString();

                //réduction dans limites de l'acceptable
                boolean updateDisplay = false;
                boolean negative = false;

                if (input.startsWith("-")) {
                    input = input.substring(1);
                    negative = true;
                }

                Unit unit = mChannelList.get(mFocusedIndex).getUnit();
                Scale limitScale = Scale.getMaxValue(unit);
                Scale currentScale = mChannelList.get(mFocusedIndex).getScale();
                //troncature
                if (id == R.id.frag_main_input_current && input.length() >= 2) {
                    if (input.charAt(1) != '.' && currentScale.getValue() < limitScale.getValue()) {
                        input = String.valueOf(Scale.changeScale(Double.parseDouble(input), currentScale, limitScale));
                        mChannelList.get(mFocusedIndex).setScale(limitScale);
                        mLastHolderSelected.setScale(limitScale);
                        currentScale = limitScale;
                    }

                    if (input.charAt(1) == '.') {
                        if (input.length() > 5) input = input.substring(0, 5);//1 chiffre avant virgule, virgule, 3 chiffres apres virgule
                        updateDisplay = true;
                    }
                    else {
                        ((EditText) view).setText(String.valueOf(mChannelList.get(mFocusedIndex).getCurrentValue()));
                        return false;
                    }
                }
                else {//min ou max
                    if (unit == Unit.V && input.length() > 5) {//1 chiffre avant virgule, virgule, 3 chiffres apres virgule
                        input = input.substring(0, 5);
                        updateDisplay = true;
                    }
                    if (unit == Unit.I && input.length() > 4) {//4 chiffres avant virgule
                        input = input.substring(0, 4);
                        updateDisplay = true;
                    }
                }

                if (negative) input = "-"+input;
                if (updateDisplay) ((EditText) view).setText(input);
                //fin réduction

                double min = mChannelList.get(mFocusedIndex).getMinValue();
                double max = mChannelList.get(mFocusedIndex).getMaxValue();
                double current = mChannelList.get(mFocusedIndex).getCurrentValue();

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
                absMax = (int) Scale.changeScale(absMax, Scale._, limitScale);
                int absMin = -1 * absMax;

                if (id == R.id.frag_main_input_min && value != min) {
                    if (absMin <= value && value < max) mChannelList.get(mFocusedIndex).setMinValue(value);
                    else mMinimum.setText(String.valueOf(min));
                }
                else if (id == R.id.frag_main_input_max && value != max) {
                    if (min < value && value <= absMax) mChannelList.get(mFocusedIndex).setMaxValue(value);
                    else mMaximum.setText(String.valueOf(max));
                }
                else if (id == R.id.frag_main_input_current && value != current) {
                    double limitScaledValue = Scale.changeScale(value, currentScale, limitScale);

                    if (min <= limitScaledValue && limitScaledValue <= max) {//comparaison sur même échelle
                        mChannelList.get(mFocusedIndex).setCurrentValue(value);//application de la valeur sur l'échelle du canal
                        mLastHolderSelected.setDigitsDisplay(value);
                    }
                    else mSelection.setText(String.valueOf(current));
                }
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

        //compare with limits on same scale
        Scale valueScale = mChannelList.get(mFocusedIndex).getScale();
        Scale limitScale = Scale.getMaxValue(mChannelList.get(mFocusedIndex).getUnit());
        double scaledValue = Scale.changeScale(mChannelList.get(mFocusedIndex).getCurrentValue(), valueScale, limitScale);

        mMore.setEnabled(false);
        mLess.setEnabled(false);

        if (scaledValue < mChannelList.get(mFocusedIndex).getMaxValue()) mMore.setEnabled(true);
        if (scaledValue > mChannelList.get(mFocusedIndex).getMinValue()) mLess.setEnabled(true);
    }

    public void setSelection(final double value) {
        mSelection.setText(String.valueOf(value));
    }

    private void variation(final Context context, final int step) {
        (step > 0 ? mLess : mMore).setEnabled(true);

        //cause approx issues
        //value = Double.parseDouble((BigDecimal.valueOf(value + sign * pow(10, -(mDigitSelected - 1)))).setScale(3, RoundingMode.CEILING).toString());

        //normalize number description
        StringBuilder litteralValue = new StringBuilder(String.valueOf(mChannelList.get(mFocusedIndex).getCurrentValue()));
        if (mChannelList.get(mFocusedIndex).getCurrentValue() >= 0) litteralValue.insert(0, '+');
        while (litteralValue.length() < 6) litteralValue.append('0');//+x.xxx ou -x.xxx

        //double to int : exact values
        StringBuilder stepBuilder = new StringBuilder("1");
        while (stepBuilder.length() < (4 - mDigitSelected)) stepBuilder.append('0');//4 digits - le 1 positionné
        if (step < 0) stepBuilder.insert(0, '-');
        int integerStep = Integer.parseInt(stepBuilder.toString());

        litteralValue.deleteCharAt(2);
        int integerValue = Integer.parseInt(litteralValue.toString());//xxxx ou -xxxx
        integerValue += integerStep;//plus de pb d'approx

        //int to double : exact representation
        litteralValue = new StringBuilder(String.valueOf(integerValue));
        boolean isPositive = integerValue >= 0;
        if (litteralValue.length() == (isPositive ? 4 : 5)) litteralValue.insert(isPositive ? 1 : 2, '.');
        else {
            if (Math.abs(integerValue) < 10000) {
                litteralValue.insert(isPositive ? 0 : 1, "0.");
                while (litteralValue.length() < (isPositive ? 5 : 6)) litteralValue.insert(isPositive ? 2 : 3, '0');
            }
            else litteralValue.insert(isPositive ? 2 : 3, '.');//si necessaire, flag pour changement de scale
        }
        double value = Double.parseDouble(litteralValue.toString());

        //compare with limits on same scale
        Scale valueScale = mChannelList.get(mFocusedIndex).getScale();
        Scale limitScale = Scale.getMaxValue(mChannelList.get(mFocusedIndex).getUnit());
        double scaledValue = Scale.changeScale(value, valueScale, limitScale);

        //change limit scales
        if (scaledValue >= mChannelList.get(mFocusedIndex).getMaxValue()) {
            mMore.setEnabled(false);
            value = mChannelList.get(mFocusedIndex).getMaxValue();
            ((Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE)).vibrate(400);
        } else if (scaledValue <= mChannelList.get(mFocusedIndex).getMinValue()) {
            mLess.setEnabled(false);
            value = mChannelList.get(mFocusedIndex).getMinValue();
            ((Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE)).vibrate(400);
        }
        else if (Math.abs(value) > 9.999) {
            if (valueScale == limitScale) value = isPositive ? 9.999 : -9.999;
            else {
                //valide si troncature / arrondi... :
                //value = Scale.changeScale(value, mChannelList.get(mFocusedIndex).getScale(), limitScale);//2 échelles par unité
                value = Double.parseDouble((isPositive ? "+" : "-") + "0.0" + (int) Math.abs(value));

                mChannelList.get(mFocusedIndex).setScale(limitScale);
                mLastHolderSelected.setScale(limitScale);
            }
        }

        //update all
        mSelection.setText(String.valueOf(value));
        mLastHolderSelected.setDigitsDisplay(value);
        mChannelList.get(mFocusedIndex).setCurrentValue(value);

        ((BluetoothParent) context).sendData((new Channel()).setId(mFocusedIndex).setCurrentValue(value));
    }
}
