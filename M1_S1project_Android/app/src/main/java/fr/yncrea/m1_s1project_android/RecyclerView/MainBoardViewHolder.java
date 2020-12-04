package fr.yncrea.m1_s1project_android.RecyclerView;

import android.content.Context;
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


public class MainBoardViewHolder extends RecyclerView.ViewHolder {

    private final ConstraintLayout mContainer;
    private final MaterialButton mChannelActivation;

    private final MaterialButtonToggleGroup mDigitGroup;
    private final ArrayList<Integer> digitsIds;
    private final Button mDigit1;
    private final Button mDigit2;
    private final Button mDigit3;
    private final Button mDigit4;

    private final Spinner mScaleSpinner;
    private final Spinner mUnitSpinner;
    private final ArrayList<String> mScaleData = new ArrayList<>(); //change selon unit
    private final ArrayAdapter<String> mScaleAdapter;
    private final ArrayAdapter<String> mUnitAdapter;//fixe

    public MainBoardViewHolder(@NonNull View itemView) {
        super(itemView);

        mContainer = itemView.findViewById(R.id.item_channel_container);
        mChannelActivation = itemView.findViewById(R.id.activation);

        mDigitGroup = itemView.findViewById(R.id.toggleButton);
        mDigit1 = itemView.findViewById(R.id.digit1);
        mDigit2 = itemView.findViewById(R.id.digit2);
        mDigit3 = itemView.findViewById(R.id.digit3);
        mDigit4 = itemView.findViewById(R.id.digit4);
        digitsIds = new ArrayList<>(Arrays.asList(mDigit1.getId(), mDigit2.getId(), mDigit3.getId(), mDigit4.getId()));

        mScaleSpinner = itemView.findViewById(R.id.scaleSpinner);
        mUnitSpinner = itemView.findViewById(R.id.unitSpinner);

        mScaleAdapter = new ArrayAdapter<>(itemView.getContext(), android.R.layout.simple_spinner_item, mScaleData);
        mScaleAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mScaleSpinner.setAdapter(mScaleAdapter);

        mUnitAdapter = new ArrayAdapter<>(itemView.getContext(), android.R.layout.simple_spinner_item, Unit.getNames());
        mUnitAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mUnitSpinner.setAdapter(mUnitAdapter);
    }

    public void setInitialDisplay(MainBoardAdapterView adapter, Context context, Channel channel) {
        mChannelActivation.setBackgroundColor(context.getResources().getColor(channel.isActive() ? R.color.green : R.color.red));
        mChannelActivation.setText(context.getString(R.string.input, channel.getId()));
        setDigitsDisplay(channel.getCurrentValue());

        mScaleData.clear();
        mScaleData.addAll(Scale.getNames(channel.getUnit()));
        mScaleAdapter.notifyDataSetChanged();
        mScaleSpinner.setSelection(mScaleAdapter.getPosition(channel.getScale().name()));

        mUnitSpinner.setSelection(mUnitAdapter.getPosition(channel.getUnit().name()));

        if (adapter.getLastHolderSelected() == this) {
            if (adapter.getDigitSelected() == -1) mContainer.callOnClick();
            else mDigitGroup.check(adapter.getDigitSelected());//englobe
        }
    }

    public void setInteractions(MainBoardAdapterView adapter, Context context, Channel channel, int position) {
        mChannelActivation.setOnClickListener(v -> {
            channel.setActive(!channel.isActive());

            adapter.getAllOn().setChecked(false);
            adapter.getAllOff().setChecked(false);

            mChannelActivation.setBackgroundColor(context.getResources().getColor(channel.isActive() ? R.color.green : R.color.red));

            ((BluetoothParent) context).getGenerator().getChannel(channel.getId()).setActive(channel.isActive());

            ((BluetoothParent) context).sendData((new Channel()).setId(channel.getId()).setActive(channel.isActive()));
        });

        mContainer.setOnClickListener(v -> {
            //if (this != adapter.getLastHolderSelected()) {//pour empecher deselection de digit
            if (adapter.getLastHolderSelected() != null)
                adapter.getLastHolderSelected().decreaseVisibility();

            mContainer.setBackgroundColor(context.getResources().getColor(R.color.yellow));
            //mContainer.setBackground(context.getResources().getDrawable(R.drawable.background_item));

            mDigitGroup.setSelectionRequired(true);

            adapter.setLastHolderSelected(this, channel, position);
            //}
        });

        mDigitGroup.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (isChecked) {
                if (this != adapter.getLastHolderSelected()) mContainer.callOnClick();
                adapter.setDigitSelected(digitsIds.indexOf(checkedId));
            }
        });

        mScaleSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Scale selected = Scale.valueOf((String) adapterView.getAdapter().getItem(i));

                if (channel.getScale() != selected) {
                    if (channel.isActive()) mChannelActivation.callOnClick();

                    channel.setScale(selected);
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
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    public void decreaseVisibility() {//appel par un autre holder
        mContainer.setBackgroundColor(Color.TRANSPARENT);

        mDigitGroup.setSelectionRequired(false);
        mDigitGroup.clearChecked();
    }

    public void setDigitsDisplay(final double value) {
        StringBuilder tmp = new StringBuilder(String.valueOf(value));
        while (tmp.length() < 5) tmp.append('0');
        char[] digits = tmp.toString().toCharArray();

        mDigit1.setText(String.valueOf(digits[0]));
        mDigit2.setText(String.valueOf(digits[2]));
        mDigit3.setText(String.valueOf(digits[3]));
        mDigit4.setText(String.valueOf(digits[4]));
    }
}
