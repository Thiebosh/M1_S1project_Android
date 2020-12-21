package fr.yncrea.m1_s1project_android.recyclers;

import android.graphics.Color;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.button.MaterialButtonToggleGroup;

import java.util.ArrayList;
import java.util.Arrays;

import fr.yncrea.m1_s1project_android.R;
import fr.yncrea.m1_s1project_android.interfaces.BluetoothParent;
import fr.yncrea.m1_s1project_android.models.Channel;
import fr.yncrea.m1_s1project_android.models.Scale;
import fr.yncrea.m1_s1project_android.models.Unit;


public class MainBoardHolder extends RecyclerView.ViewHolder {

    private final ConstraintLayout mContainer;
    private final MaterialButton mChannelActivation;

    private final MaterialButtonToggleGroup mDigitGroup;
    private final ArrayList<Integer> mDigitsIds;
    private final Button mDigitSign;
    private final Button mDigit1;
    private final Button mDigit2;
    private final Button mDigit3;
    private final Button mDigit4;

    private final Spinner mScaleSpinner;
    private final Spinner mUnitSpinner;
    private final ArrayList<String> mScaleData = new ArrayList<>(); //change selon unit
    private final ArrayAdapter<String> mScaleAdapter;
    private final ArrayAdapter<String> mUnitAdapter;//fixe

    public MainBoardHolder(@NonNull View itemView) {
        super(itemView);

        mContainer = itemView.findViewById(R.id.item_channel_container);
        mChannelActivation = itemView.findViewById(R.id.item_channel_button_onOff);

        mDigitGroup = itemView.findViewById(R.id.item_channel_toggle_digits);
        mDigitSign = itemView.findViewById(R.id.item_channel_button_digit_sign);
        mDigit1 = itemView.findViewById(R.id.item_channel_button_digit1);
        mDigit2 = itemView.findViewById(R.id.item_channel_button_digit2);
        mDigit3 = itemView.findViewById(R.id.item_channel_button_digit3);
        mDigit4 = itemView.findViewById(R.id.item_channel_button_digit4);
        mDigitsIds = new ArrayList<>(Arrays.asList(mDigit1.getId(), mDigit2.getId(), mDigit3.getId(), mDigit4.getId()));

        mScaleSpinner = itemView.findViewById(R.id.item_channel_spinner_scale);
        mUnitSpinner = itemView.findViewById(R.id.item_channel_spinner_unit);

        mScaleAdapter = new ArrayAdapter<>(itemView.getContext(), android.R.layout.simple_spinner_item, mScaleData);
        mScaleAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mScaleSpinner.setAdapter(mScaleAdapter);

        mUnitAdapter = new ArrayAdapter<>(itemView.getContext(), android.R.layout.simple_spinner_item, Unit.getNames());
        mUnitAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mUnitSpinner.setAdapter(mUnitAdapter);
    }

    public void setScale(final Scale scale) {
        mScaleSpinner.setSelection(mScaleAdapter.getPosition(scale.name()));
    }

    public void decreaseVisibility() {//appel par un autre holder
        mContainer.setBackgroundColor(Color.TRANSPARENT);

        mDigitGroup.setSelectionRequired(false);
        mDigitGroup.clearChecked();
    }

    public void setInitialDisplay(MainBoardAdapter adapter, Channel channel) {
        mChannelActivation.setBackgroundColor(itemView.getContext().getResources().getColor(channel.isActive() ? R.color.green : R.color.red));
        mChannelActivation.setText(itemView.getContext().getString(R.string.frag_main_channel_id, channel.getId()));
        setDigitsDisplay(channel.getCurrentValue());

        mScaleData.clear();
        mScaleData.addAll(Scale.getNames(channel.getUnit()));
        mScaleAdapter.notifyDataSetChanged();
        mScaleSpinner.setSelection(mScaleAdapter.getPosition(channel.getScale().name()));

        mUnitSpinner.setSelection(mUnitAdapter.getPosition(channel.getUnit().name()));
        
        if (MainBoardHolder.this == adapter.getLastHolderSelected()) {//cas item recyclé
            if (adapter.getDigitSelected() == -1) mContainer.callOnClick();
            else mDigitGroup.check(adapter.getDigitSelected());//englobe
        }
    }

    public void setDigitsDisplay(final double value) {
        StringBuilder tmp = new StringBuilder(String.valueOf(Math.abs(value)));
        tmp.insert(0, value >= 0 ? '+' : '-');
        while (tmp.length() < 6) tmp.append('0');
        char[] digits = tmp.toString().toCharArray();

        mDigitSign.setText(String.valueOf(digits[0]));
        mDigit1.setText(String.valueOf(digits[1]));
        mDigit2.setText(String.valueOf(digits[3]));
        mDigit3.setText(String.valueOf(digits[4]));
        mDigit4.setText(String.valueOf(digits[5]));
    }

    public void setInteractions(MainBoardAdapter adapter, Channel channel, int position) {
        mChannelActivation.setOnClickListener(v -> {
            channel.setActive(!channel.isActive());

            adapter.getAllOn().setChecked(false);
            adapter.getAllOff().setChecked(false);

            mChannelActivation.setBackgroundColor(itemView.getContext().getResources().getColor(channel.isActive() ? R.color.green : R.color.red));

            BluetoothParent.mGenerator.getChannel(channel.getId()).setActive(channel.isActive());

            ((BluetoothParent) itemView.getContext()).sendData((new Channel()).setId(channel.getId()).setActive(channel.isActive()));
        });

        mContainer.setOnClickListener(v -> {
            if (MainBoardHolder.this != adapter.getLastHolderSelected()) {//pour empecher deselection de digit
                if (adapter.getLastHolderSelected() != null) {
                    adapter.getLastHolderSelected().decreaseVisibility();
                }

                mContainer.setBackgroundColor(itemView.getContext().getResources().getColor(R.color.colorSecondaryLight));

                mDigitGroup.setSelectionRequired(true);

                adapter.setLastHolderSelected(MainBoardHolder.this, channel, position);
            }
        });

        mDigitGroup.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (isChecked) {
                if (this != adapter.getLastHolderSelected()) mContainer.callOnClick();
                adapter.setDigitSelected(mDigitsIds.indexOf(checkedId));
            }
        });

        mScaleSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Scale selectedScale = Scale.valueOf((String) adapterView.getAdapter().getItem(i));

                if (channel.getScale() != selectedScale) {
                    //if (channel.isActive()) mChannelActivation.callOnClick();

                    if (selectedScale.getValue() == Scale.getMaxValue(channel.getUnit()).getValue()) {//risque de dépassement des limites
                        if (channel.getCurrentValue() > channel.getMaxValue()) {
                            channel.setCurrentValue(channel.getMaxValue());
                            setDigitsDisplay(channel.getCurrentValue());
                            if (adapter.getLastHolderSelected() != null) {
                                adapter.setSelection(channel.getCurrentValue());
                            }
                            ((BluetoothParent) itemView.getContext()).sendData((new Channel()).setId(channel.getId()).setCurrentValue(channel.getMaxValue()));
                        }
                        else if (channel.getCurrentValue() < channel.getMinValue()) {
                            channel.setCurrentValue(channel.getMinValue());
                            setDigitsDisplay(channel.getCurrentValue());
                            if (adapter.getLastHolderSelected() != null) {
                                adapter.setSelection(channel.getCurrentValue());
                            }
                            ((BluetoothParent) itemView.getContext()).sendData((new Channel()).setId(channel.getId()).setCurrentValue(channel.getMinValue()));
                        }
                    }

                    channel.setScale(selectedScale);
                    ((BluetoothParent) itemView.getContext()).sendData((new Channel()).setId(channel.getId()).setScale(selectedScale));

                    int digit = adapter.getDigitSelected();

                    if (MainBoardHolder.this == adapter.getLastHolderSelected()) {
                        adapter.setLastHolderSelected(MainBoardHolder.this, channel, position);
                    }

                    if (digit != -1) adapter.setDigitSelected(digit);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        mUnitSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Unit selected = Unit.valueOf((String) adapterView.getAdapter().getItem(i));

                if (channel.getUnit() != selected) {
                    if (channel.isActive()) mChannelActivation.callOnClick();

                    mScaleData.clear();
                    mScaleData.addAll(Scale.getNames(selected));
                    mScaleAdapter.notifyDataSetChanged();

                    if (!Scale.getNamesValues(selected).contains(channel.getScale())) {
                        channel.setScale(Scale.getNamesValues(selected).get(0));
                    }
                    mScaleSpinner.setSelection(mScaleAdapter.getPosition(channel.getScale().name()));

                    channel.setUnit(selected);

                    channel.setCurrentValue(0);
                    setDigitsDisplay(0);
                    if (adapter.getLastHolderSelected() != null) adapter.setSelection(0);

                    ((BluetoothParent) itemView.getContext()).sendData((new Channel()).setId(channel.getId())
                            .setActive(false).setUnit(selected).setScale(channel.getScale()).setCurrentValue(0));

                    if (MainBoardHolder.this == adapter.getLastHolderSelected()) {
                        adapter.setLastHolderSelected(MainBoardHolder.this, channel, position);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }
}
