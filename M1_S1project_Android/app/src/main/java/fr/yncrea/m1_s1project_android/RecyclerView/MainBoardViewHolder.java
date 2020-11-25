package fr.yncrea.m1_s1project_android.RecyclerView;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import fr.yncrea.m1_s1project_android.R;
import fr.yncrea.m1_s1project_android.models.Channel;

public class MainBoardViewHolder extends RecyclerView.ViewHolder {

    public Button mChannelActivation;
    public EditText mChannelValue;
    public ToggleButton mChannelType;

    public MainBoardViewHolder(@NonNull View itemView) {
        super(itemView);
        mChannelActivation = itemView.findViewById(R.id.activation);
        mChannelValue = itemView.findViewById(R.id.value);
        mChannelType = itemView.findViewById(R.id.mode);

        itemView.setOnClickListener(v -> {

            Log.d("itemViewClick", "screen");
        });

    }

    public void setInitialDisplay(Context context, Channel channel){

        mChannelActivation.setBackgroundColor(context.getResources().getColor(channel.isActive() ? R.color.green : R.color.red));
        //context.getResources().getString(R.string.input);
        mChannelActivation.setText(context.getResources().getString(R.string.input, String.valueOf(channel.getId())));
    }

    public void setInteractions(Context context, Channel channel){

        mChannelActivation.setOnClickListener(v -> {
            channel.setActive(!channel.isActive());
            mChannelActivation.setBackgroundColor(context.getResources().getColor(channel.isActive() ? R.color.green : R.color.red));

        });

    }
}
