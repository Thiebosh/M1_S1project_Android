package fr.yncrea.m1_s1project_android.RecyclerView;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ToggleButton;

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


public class MainBoardViewHolder extends RecyclerView.ViewHolder {

    private final ConstraintLayout mContainer;
    private final MaterialButton mChannelActivation;

    private final MaterialButtonToggleGroup mDigitGroup;
    private final ArrayList<Integer> digitsIds;
    private final MaterialButton mDigit1;
    private final MaterialButton mDigit2;
    private final MaterialButton mDigit3;
    private final MaterialButton mDigit4;

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
    }

    public void setInitialDisplay(MainBoardAdapterView adapter, Context context, Channel channel) {
        mChannelActivation.setBackgroundColor(context.getResources().getColor(channel.isActive() ? R.color.green : R.color.red));
        mChannelActivation.setText(context.getString(R.string.input, channel.getId()));
        setDigitsDisplay(channel.getCurrentValue());

        /*
        if (getAdapterPosition() == adapter.getFocusedIndex()) {
            //increaseVisibility(context);
            if (adapter.getDigitSelected() != -1) {
                mDigitGroup.check(adapter.getDigitSelected());
            }
        }
         */
    }

    public void setInteractions(MainBoardAdapterView adapter, Context context, View mainView, Channel channel, int position) {
        mChannelActivation.setOnClickListener(v -> {
            channel.setActive(!channel.isActive());

            mChannelActivation.setBackgroundColor(context.getResources().getColor(channel.isActive() ? R.color.green : R.color.red));

            ((ToggleButton) mainView.findViewById(R.id.AllOn)).setChecked(false);
            ((ToggleButton) mainView.findViewById(R.id.AllOff)).setChecked(false);

            ((BluetoothParent) context).getGenerator().getChannel(channel.getId()).setActive(channel.isActive());

            ((BluetoothParent) context).sendData((new Channel()).setId(channel.getId()).setActive(channel.isActive()));
        });

        mContainer.setOnClickListener(v -> {
            if (adapter.getFocusedIndex() != -1) adapter.getFocusedViewHolder().decreaseVisibility();
            increaseVisibility(context);
            adapter.setFocusedIndex(position);
        });

        mDigitGroup.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (isChecked) {
                if (position != adapter.getFocusedIndex()) {
                    if (adapter.getFocusedIndex() != -1) {
                        adapter.getFocusedViewHolder().mDigitGroup.uncheck(digitsIds.get(adapter.getDigitSelected()));
                    }
                    mContainer.callOnClick();//update focused index
                }
                adapter.setDigitSelected(digitsIds.indexOf(checkedId));
            }
        });
    }

    public void increaseVisibility(Context context) {
        mContainer.setBackgroundColor(context.getResources().getColor(R.color.yellow));
        //mContainer.setBackground(context.getResources().getDrawable(R.drawable.background_item));
        mDigitGroup.setSelectionRequired(true);
    }

    public void decreaseVisibility() {
        mContainer.setBackgroundColor(Color.TRANSPARENT);
        mDigitGroup.setSelectionRequired(false);
        mDigitGroup.clearChecked();
    }

    public void setDigitsDisplay(final double value) {
        Log.d("testy", "update display with "+value);
        StringBuilder tmp = new StringBuilder(String.valueOf(value));
        while (tmp.length() < 5) tmp.append('0');
        char[] digits = tmp.toString().toCharArray();

        mDigit1.setText(String.valueOf(digits[0]));
        mDigit2.setText(String.valueOf(digits[2]));
        mDigit3.setText(String.valueOf(digits[3]));
        mDigit4.setText(String.valueOf(digits[4]));
    }
}
