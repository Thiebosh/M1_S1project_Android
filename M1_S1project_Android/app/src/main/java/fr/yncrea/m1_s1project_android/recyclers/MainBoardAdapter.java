package fr.yncrea.m1_s1project_android.recyclers;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.os.Vibrator;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.Objects;

import fr.yncrea.m1_s1project_android.R;
import fr.yncrea.m1_s1project_android.interfaces.BluetoothParent;
import fr.yncrea.m1_s1project_android.models.Channel;
import fr.yncrea.m1_s1project_android.models.Scale;
import fr.yncrea.m1_s1project_android.models.Unit;


public class MainBoardAdapter extends RecyclerView.Adapter<MainBoardHolder> {
    private final ArrayList<Channel> mChannelList;

    private final Resources mResources;

    private MainBoardHolder mLastHolderSelected = null;
    private int mFocusedIndex = -1; //mLastHolderSelected.getAdapterPosition() peut casser aux extremes
    private int mDigitSelected = -1;

    private final ToggleButton mAllOn;
    private final ToggleButton mAllOff;

    private final TextInputEditText mMin;
    private final TextInputEditText mMax;
    private final TextInputEditText mCurrent;

    private final TextInputLayout mHintMin;
    private final TextInputLayout mHintMax;
    private final TextInputLayout mHintCurrent;

    private final Button mMore;
    private final Button mLess;

    public MainBoardAdapter(View view, ArrayList<Channel> channelList) {
        mResources = view.getContext().getResources();

        this.mChannelList = channelList != null ? channelList : new ArrayList<>();//secu

        mAllOn = view.findViewById(R.id.frag_main_allOn);
        mAllOff = view.findViewById(R.id.frag_main_allOff);

        mMin = view.findViewById(R.id.frag_main_input_min);
        mMax = view.findViewById(R.id.frag_main_input_max);
        mCurrent = view.findViewById(R.id.frag_main_input_current);

        mHintMin = view.findViewById(R.id.frag_main_hint_min);
        mHintMax = view.findViewById(R.id.frag_main_hint_max);
        mHintCurrent = view.findViewById(R.id.frag_main_hint_current);

        mMore = view.findViewById(R.id.frag_main_button_more);
        mLess = view.findViewById(R.id.frag_main_button_less);
    }

    public void updateChannelListData(ArrayList<Channel> data, int index) {
        if (index == -1) {
            boolean active = data.get(0).isActive();
            for (int i = 0; i < mChannelList.size(); ++i) mChannelList.get(i).setActive(active);
            this.notifyDataSetChanged();
        }
        else {
            mChannelList.set(index, data.get(index));
            this.notifyItemChanged(index, mLastHolderSelected);//modifie instance plutôt que de la recréer
            if (mFocusedIndex != -1 && mChannelList.get(index).getId() == mChannelList.get(mFocusedIndex).getId()) {
                setLastHolderSelected(mLastHolderSelected, mChannelList.get(index), mFocusedIndex);
            }
        }
    }

    @Override
    public int getItemCount() {
        return mChannelList.size();
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

    public int getDigitSelected() {
        return mDigitSelected;
    }

    public void setSelection(final double value) {
        mCurrent.setText(String.valueOf(value));
    }

    public void setLastHolderSelected(final MainBoardHolder holder, final Channel channel, final int position) {
        mLastHolderSelected = holder;
        mFocusedIndex = position;

        mDigitSelected = -1;
        mMore.setEnabled(false);
        mLess.setEnabled(false);

        mCurrent.setEnabled(true);
        mMin.setEnabled(true);
        mMax.setEnabled(true);

        mCurrent.setText(String.valueOf(channel.getCurrentValue()));
        mMin.setText(String.valueOf(channel.getMinValue()));
        mMax.setText(String.valueOf(channel.getMaxValue()));

        mMin.setMinimumWidth((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 75, mResources.getDisplayMetrics()));
        mMax.setMinimumWidth((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 75, mResources.getDisplayMetrics()));


        Scale limitScale = Objects.requireNonNull(Scale.scaleOf(mResources.getInteger(
                channel.getUnit() == Unit.V ?
                        R.integer.absolute_limit_volt_scale_display : R.integer.absolute_limit_ampere_scale_display)));

        mHintCurrent.setHint(holder.itemView.getContext().getString(R.string.inputScaleUnit, channel.getId(), channel.getScale().name(), channel.getUnit().name()));
        mHintMin.setHint(holder.itemView.getContext().getString(R.string.minScaleUnit, limitScale.name(), channel.getUnit().name()));
        mHintMax.setHint(holder.itemView.getContext().getString(R.string.maxScaleUnit, limitScale.name(), channel.getUnit().name()));
    }

    public void setDigitSelected(final int digit) {
        mDigitSelected = digit;
        Channel channel = mChannelList.get(mFocusedIndex);

        //compare with limits on same scale
        Scale valueScale = channel.getScale();
        Scale limitScale = Objects.requireNonNull(Scale.scaleOf(mResources.getInteger(
                channel.getUnit() == Unit.V ?
                        R.integer.absolute_limit_volt_scale_value : R.integer.absolute_limit_ampere_scale_value)));

        double scaledValue = Scale.changeScale(channel.getCurrentValue(), valueScale, limitScale);

        mMore.setEnabled(false);
        mLess.setEnabled(false);

        if (scaledValue < channel.getMaxValue()) mMore.setEnabled(true);
        if (scaledValue > channel.getMinValue()) mLess.setEnabled(true);
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
                Channel channel = mChannelList.get(mFocusedIndex);

                //réduction dans limites de l'acceptable : formatting
                boolean updateDisplay = false;
                boolean negative = false;

                if (input.startsWith("-")) {
                    input = input.substring(1);
                    negative = true;
                }

                Unit unit = channel.getUnit();
                //troncature
                if (id == R.id.frag_main_input_current && input.length() >= 2) {
                    Scale maxScale = Scale.getMaxValue(unit);
                    Scale currentScale = channel.getScale();

                    if (input.charAt(1) != '.' && currentScale.getValue() < maxScale.getValue()) {
                        input = String.valueOf(Scale.changeScale(Double.parseDouble(input), currentScale, maxScale));
                        channel.setScale(maxScale);
                        mLastHolderSelected.setScale(maxScale);
                        mHintCurrent.setHint(holder.itemView.getContext().getString(R.string.inputScaleUnit, channel.getId(), channel.getScale().name(), channel.getUnit().name()));
                        ((BluetoothParent) holder.itemView.getContext()).sendData((new Channel()).setId(channel.getId()).setScale(maxScale));
                        updateDisplay = true;
                    }

                    if (input.charAt(1) == '.') {
                        if (input.length() > 5) {
                            Scale minScale = Scale.getMinValue(unit);

                            if (currentScale.getValue() > minScale.getValue() && input.startsWith("0.00")) {//change scale
                                input = String.valueOf(Scale.changeScale(Double.parseDouble(input), currentScale, minScale));
                                Log.d("testy", "new input : "+input);
                                channel.setScale(minScale);
                                mLastHolderSelected.setScale(minScale);
                                mHintCurrent.setHint(holder.itemView.getContext().getString(R.string.inputScaleUnit, channel.getId(), channel.getScale().name(), channel.getUnit().name()));
                                ((BluetoothParent) holder.itemView.getContext()).sendData((new Channel()).setId(channel.getId()).setScale(minScale));
                            }
                            else input = input.substring(0, 5);//1 avant virgule, virgule, 3 apres virgule

                            updateDisplay = true;
                        }
                    }
                    else {
                        ((EditText) view).setText(String.valueOf(channel.getCurrentValue()));
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
                //fin réduction / formatting

                //debut cast
                double min = channel.getMinValue();
                double max = channel.getMaxValue();
                double current = channel.getCurrentValue();

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
                //fin cast

                //début vérif limites
                Scale limitScale = Objects.requireNonNull(Scale.scaleOf(mResources.getInteger(
                        unit == Unit.V ? R.integer.absolute_limit_volt_scale_value : R.integer.absolute_limit_ampere_scale_value)));
                int absMax = mResources.getInteger(unit == Unit.V ? R.integer.absolute_limit_volt_value : R.integer.absolute_limit_ampere_value);
                absMax = (int) Scale.changeScale(absMax, Scale._, limitScale);
                int absMin = -1 * absMax;

                if (id == R.id.frag_main_input_min && value != min) {
                    if (absMin <= value && value < max) {
                        channel.setMinValue(value);
                        ((BluetoothParent) holder.itemView.getContext()).sendData((new Channel()).setId(channel.getId()).setMinValue(value));
                    }
                    else mMin.setText(String.valueOf(min));
                }
                else if (id == R.id.frag_main_input_max && value != max) {
                    if (min < value && value <= absMax) {
                        channel.setMaxValue(value);
                        ((BluetoothParent) holder.itemView.getContext()).sendData((new Channel()).setId(channel.getId()).setMaxValue(value));
                    }
                    else mMax.setText(String.valueOf(max));
                }
                else if (id == R.id.frag_main_input_current && value != current) {
                    double limitScaledValue = Scale.changeScale(value, channel.getScale(), unit == Unit.V ? limitScale : Scale.u);//rustine

                    if (min <= limitScaledValue && limitScaledValue <= max) {//comparaison sur même échelle
                        channel.setCurrentValue(value);//application de la valeur sur l'échelle du canal
                        mLastHolderSelected.setDigitsDisplay(value);
                        ((BluetoothParent) holder.itemView.getContext()).sendData((new Channel()).setId(channel.getId()).setCurrentValue(value));
                    }
                    else mCurrent.setText(String.valueOf(current));
                }
            }
            //fin vérif limites

            return false;
        };
        mMax.setOnKeyListener(keyListener);
        mMin.setOnKeyListener(keyListener);
        mCurrent.setOnKeyListener(keyListener);

        mMore.setOnClickListener(v -> variation(holder.itemView.getContext(), +1));
        mLess.setOnClickListener(v -> variation(holder.itemView.getContext(), -1));
    }

    private void variation(final Context context, final int step) {
        (step > 0 ? mLess : mMore).setEnabled(true);
        Channel channel = mChannelList.get(mFocusedIndex);

        //cause approx issues
        //value = Double.parseDouble((BigDecimal.valueOf(value + sign * pow(10, -(mDigitSelected - 1)))).setScale(3, RoundingMode.CEILING).toString());

        //normalize number description
        StringBuilder litteralValue = new StringBuilder(String.valueOf(channel.getCurrentValue()));
        if (channel.getCurrentValue() >= 0) litteralValue.insert(0, '+');
        while (litteralValue.length() < 6) litteralValue.append('0');//+x.xxx ou -x.xxx

        //double to int : exact values
        StringBuilder stepBuilder = new StringBuilder("1");
        while (stepBuilder.length() < (4 - mDigitSelected)) stepBuilder.append('0');//4 digits - le 1 positionné
        if (step < 0) stepBuilder.insert(0, '-');
        int integerStep = Integer.parseInt(stepBuilder.toString());

        litteralValue.deleteCharAt(2);
        if (litteralValue.charAt(0) == '+') litteralValue.deleteCharAt(0);
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
        Scale valueScale = channel.getScale();
        Scale limitScale = Objects.requireNonNull(Scale.scaleOf(mResources.getInteger(
                channel.getUnit() == Unit.V ?
                        R.integer.absolute_limit_volt_scale_value : R.integer.absolute_limit_ampere_scale_value)));
        double scaledValue = Scale.changeScale(value, valueScale, limitScale);

        //change limit scales
        if (scaledValue >= channel.getMaxValue()) {
            mMore.setEnabled(false);
            value = channel.getMaxValue();
            ((Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE)).vibrate(mResources.getInteger(R.integer.duration_vibration_button_more_less));
        } else if (scaledValue <= channel.getMinValue()) {
            mLess.setEnabled(false);
            value = channel.getMinValue();
            ((Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE)).vibrate(mResources.getInteger(R.integer.duration_vibration_button_more_less));
        }
        else if (Math.abs(value) > 9.999) {
            if (valueScale == limitScale) value = isPositive ? 9.999 : -9.999;
            else {
                //valide si troncature ou arrondi... :
                //value = Scale.changeScale(value, channel.getScale(), limitScale);//2 échelles par unité
                value = Double.parseDouble((isPositive ? "+" : "-") + "0.0" + (int) Math.abs(value));

                channel.setScale(limitScale);
                mLastHolderSelected.setScale(limitScale);
                mHintCurrent.setHint(context.getString(R.string.inputScaleUnit, channel.getId(), channel.getScale().name(), channel.getUnit().name()));

                ((BluetoothParent) context).sendData((new Channel()).setId(channel.getId()).setScale(limitScale));
            }
        }

        //update all
        mCurrent.setText(String.valueOf(value));
        mLastHolderSelected.setDigitsDisplay(value);
        channel.setCurrentValue(value);

        ((BluetoothParent) context).sendData((new Channel()).setId(mFocusedIndex).setCurrentValue(value));
    }
}
