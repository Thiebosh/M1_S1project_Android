package fr.yncrea.m1_s1project_android.RecyclerView;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import fr.yncrea.m1_s1project_android.R;

public class BackupViewHolder extends RecyclerView.ViewHolder {
    private final TextView slotText;

    public BackupViewHolder(@NonNull View itemView) {
        super(itemView);
        slotText = itemView.findViewById(R.id.slot_textview);
    }

    public void setBackupDisplay(Context context, int pos){
        slotText.setText(context.getString(R.string.slot, pos));

    }
}
