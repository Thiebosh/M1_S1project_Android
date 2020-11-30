package fr.yncrea.m1_s1project_android.RecyclerView;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Objects;

import fr.yncrea.m1_s1project_android.R;
import fr.yncrea.m1_s1project_android.interfaces.BluetoothParent;
import fr.yncrea.m1_s1project_android.models.Channel;

import static fr.yncrea.m1_s1project_android.models.PowerSupply.I;
import static fr.yncrea.m1_s1project_android.models.PowerSupply.V;

public class MainBoardViewHolder extends RecyclerView.ViewHolder {

    private final ConstraintLayout mContainer;
    private final Button mChannelActivation;
    private final TextView mChannelValue;

    public MainBoardViewHolder(@NonNull View itemView) {
        super(itemView);
        mContainer = itemView.findViewById(R.id.item_channel_container);
        mChannelActivation = itemView.findViewById(R.id.activation);
        mChannelValue = itemView.findViewById(R.id.value);
    }

    public ConstraintLayout getContainer() {
        return mContainer;
    }

    public void increaseVisibility(Context context) {
        mContainer.setBackgroundColor(context.getResources().getColor(R.color.yellow));
        //mContainer.setBackground(context.getResources().getDrawable(R.drawable.item_background));
    }

    public void decreaseVisibility() {
        mContainer.setBackgroundColor(Color.TRANSPARENT);
    }

    public void setInitialDisplay(Context context, Channel channel){
        mChannelActivation.setBackgroundColor(context.getResources().getColor(channel.isActive() ? R.color.green : R.color.red));
        mChannelActivation.setText(context.getString(R.string.input, channel.getId()));

        mChannelValue.setHint(context.getString(R.string.input, channel.getId()));
        mChannelValue.setText(String.valueOf(channel.getCurrentValue()));
    }

    public void setInteractions(Context context, Channel channel){

        mChannelActivation.setOnClickListener(v -> {
            channel.setActive(!channel.isActive());
            mChannelActivation.setBackgroundColor(context.getResources().getColor(channel.isActive() ? R.color.green : R.color.red));

            ((BluetoothParent) Objects.requireNonNull(context)).getGenerator().getChannel(channel.getId()).setActive(channel.isActive());

            ((BluetoothParent) Objects.requireNonNull(context)).sendData((new Channel()).setId(channel.getId()).setActive(channel.isActive()));
        });
    }
}
