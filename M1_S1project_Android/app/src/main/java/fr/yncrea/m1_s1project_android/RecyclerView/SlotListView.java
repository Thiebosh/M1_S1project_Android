package fr.yncrea.m1_s1project_android.RecyclerView;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import fr.yncrea.m1_s1project_android.R;

public class SlotListView extends RecyclerView.Adapter<SlotListView.ViewHolder> {

    public SlotListView() {
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView slotText;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            slotText = itemView.findViewById(R.id.slot_textview);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.slot_item, parent, false);
        //View view = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_1, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        //setBackupDisplay(holder.itemView.getContext(), position);
        holder.slotText.setText(holder.itemView.getContext().getString(R.string.slot, position));
        //((TextView)holder.itemView).setText(holder.itemView.getContext().getString(R.string.slot, position));
        //((TextView)holder.itemView.findViewById(R.id.slot_textview)).setText(R.string.app_name);
    }

    @Override
    public int getItemCount() {
        return 8;
    }

    public void setBackupDisplay(Context context, int pos){
        //holder.slotText.setText(context.getString(R.string.slot, pos));

    }
}
