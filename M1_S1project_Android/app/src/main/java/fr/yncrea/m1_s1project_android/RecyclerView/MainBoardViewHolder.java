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

import java.util.ArrayList;

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

    public MainBoardViewHolder(@NonNull View itemView) {
        super(itemView);
        mContainer = itemView.findViewById(R.id.item_channel_container);
        mChannelActivation = itemView.findViewById(R.id.activation);

        mDigitGroup = itemView.findViewById(R.id.toggleButton);
        mDigit1 = itemView.findViewById(R.id.digit1);
        mDigit2 = itemView.findViewById(R.id.digit2);
        mDigit3 = itemView.findViewById(R.id.digit3);
        mDigit4 = itemView.findViewById(R.id.digit4);
    }

    public void increaseVisibility(Context context) {
        mContainer.setBackgroundColor(context.getResources().getColor(R.color.yellow));
        //mContainer.setBackground(context.getResources().getDrawable(R.drawable.background_item));
        //mDigitGroup.setSelectionRequired(true);
    }

    public void decreaseVisibility() {
        mContainer.setBackgroundColor(Color.TRANSPARENT);
        //mDigitGroup.setSelectionRequired(false);
    }

    public void setInitialDisplay(Context context, Channel channel) {
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
            //position = getAdapterPosition();
            //getItemId();

            //si checké
                //si nouvelle carte
                    //si pas première carte, lance impulsion pour désactiver digit de précédente
                    //update display
                //update digit -> au pire, est ré update par la boucle...


            ArrayList<Integer> digitsIds = new ArrayList<>();
            digitsIds.add(mDigit1.getId());
            digitsIds.add(mDigit2.getId());
            digitsIds.add(mDigit3.getId());
            digitsIds.add(mDigit4.getId());

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
}
