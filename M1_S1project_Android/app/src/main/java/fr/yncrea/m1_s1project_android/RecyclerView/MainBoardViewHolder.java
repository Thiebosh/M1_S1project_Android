package fr.yncrea.m1_s1project_android.RecyclerView;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.Button;

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
    private final Button mDigit1;
    private final Button mDigit2;
    private final Button mDigit3;
    private final Button mDigit4;

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
