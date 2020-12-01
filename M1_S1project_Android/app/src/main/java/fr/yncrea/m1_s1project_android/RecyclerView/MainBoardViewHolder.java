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

import com.google.android.material.button.MaterialButtonToggleGroup;

import fr.yncrea.m1_s1project_android.R;
import fr.yncrea.m1_s1project_android.interfaces.BluetoothParent;
import fr.yncrea.m1_s1project_android.models.Channel;


public class MainBoardViewHolder extends RecyclerView.ViewHolder {

    private final ConstraintLayout mContainer;
    private final Button mChannelActivation;
    private final MaterialButtonToggleGroup mDigitGroup;
    private final Button mDigit1;
    private final Button mDigit2;
    private final Button mDigit3;
    private final Button mDigit4;
    private Button mPlus;
    private Button mMoins;
    //private final TextView mChannelValue;

    public MainBoardViewHolder(@NonNull View itemView) {
        super(itemView);
        mContainer = itemView.findViewById(R.id.item_channel_container);
        mChannelActivation = itemView.findViewById(R.id.activation);

        mDigitGroup = itemView.findViewById(R.id.toggleButton);
        mDigit1 = itemView.findViewById(R.id.digit1);
        mDigit2 = itemView.findViewById(R.id.digit2);
        mDigit3 = itemView.findViewById(R.id.digit3);
        mDigit4 = itemView.findViewById(R.id.digit4);

        //mChannelValue = itemView.findViewById(R.id.value);
    }

    public ConstraintLayout getContainer() {
        return mContainer;
    }

    public void increaseVisibility(Context context) {
        mContainer.setBackgroundColor(context.getResources().getColor(R.color.yellow));
        //mContainer.setBackground(context.getResources().getDrawable(R.drawable.background_item));
    }

    public void decreaseVisibility() {
        mContainer.setBackgroundColor(Color.TRANSPARENT);
    }

    public void setInitialDisplay(Context context, Channel channel){
        mChannelActivation.setBackgroundColor(context.getResources().getColor(channel.isActive() ? R.color.green : R.color.red));
        mChannelActivation.setText(context.getString(R.string.input, channel.getId()));

        StringBuilder tmp = new StringBuilder(String.valueOf(channel.getCurrentValue()));
        while (tmp.length() < 5) tmp.append('0');
        char[] digits = tmp.toString().toCharArray();
        mDigit1.setText(String.valueOf(digits[0]));
        mDigit2.setText(String.valueOf(digits[2]));
        mDigit3.setText(String.valueOf(digits[3]));
        mDigit4.setText(String.valueOf(digits[4]));
    }

    public void setInteractions(MainBoardAdapterView adapter, Context context, View mainView, Channel channel, int position){
        mChannelActivation.setOnClickListener(v -> {
            channel.setActive(!channel.isActive());

            mChannelActivation.setBackgroundColor(context.getResources().getColor(channel.isActive() ? R.color.green : R.color.red));
            //Log.d("testy",((ToggleButton) v.findViewById(R.id.AllOff)).isChecked()+"");
            ((ToggleButton) mainView.findViewById(R.id.AllOn)).setChecked(false);
            ((ToggleButton) mainView.findViewById(R.id.AllOff)).setChecked(false);

            ((BluetoothParent) context).getGenerator().getChannel(channel.getId()).setActive(channel.isActive());

            ((BluetoothParent) context).sendData((new Channel()).setId(channel.getId()).setActive(channel.isActive()));
        });

        mDigitGroup.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (isChecked) {
                Log.d("testy", "active digit "+checkedId+" du canal "+position);

                if (adapter.getDigitSelected() != -1) {//décoche précédent
                    group.uncheck(adapter.getDigitSelected());
                }
                adapter.setDigitSelected(checkedId);
            }
            else {
                Log.d("testy", "désactive digit "+checkedId+" du canal "+position);
                adapter.setDigitSelected(-1);
            }
            //if (adapter.getDigitSelected() != -1) adapter.notifyItemChanged(adapter.getFocusedIndex());//uncheck last checked?

            //adapter.setFocusedIndex(position);
            //adapter.setDigitSelected(isChecked ? checkedId : -1);

            //if (group.getCheckedButtonId() == -1) group.check(checkedId);
        });
        //mDigitGroup.getChildAt(1).setOnClickListener();
        /*
        mDigit1.setOnClickListener(v -> {
            mDigitGroup.clearChecked();
            mDigitGroup.check(0);
        });

        mDigit2.setOnClickListener(v -> {
            mDigitGroup.clearChecked();
            mDigitGroup.
        });

        mDigit3.setOnClickListener(v -> {
            mDigitGroup.clearChecked();
            mDigitGroup.check(2);
        });

        mDigit4.setOnClickListener(v -> {
            mDigitGroup.clearChecked();
            mDigitGroup.check(3);
        });

         */
    }
}
