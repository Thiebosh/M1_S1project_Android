package fr.yncrea.m1_s1project_android.recyclers;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import fr.yncrea.m1_s1project_android.R;
import fr.yncrea.m1_s1project_android.models.Channel;
import fr.yncrea.m1_s1project_android.models.Generator;

public class BackupSlotAdapter extends RecyclerView.Adapter<BackupSlotAdapter.Holder> {

    public static class Holder extends RecyclerView.ViewHolder {

        private final TextView mSlotContainer;

        public Holder(@NonNull View itemView) {
            super(itemView);
            mSlotContainer = itemView.findViewById(R.id.slot_textview);
        }

        public void setDisplay(final int position) {
            mSlotContainer.setText(itemView.getContext().getString(R.string.slot, position));
        }

        public void setInteractions(BackupConfigAdapter displayer, final ArrayList<Channel> config) {
            mSlotContainer.setOnClickListener(v -> {
                //apply color on item
                displayer.getChannelList().clear();
                displayer.getChannelList().addAll(config);
                displayer.notifyDataSetChanged();
                
                //mSlotContainer.setBackgroundColor(itemView.getContext().getResources().getColor(R.color.yellow));
            });
        }
    }

    private final ArrayList<Generator> mConfigList;
    private final BackupConfigAdapter mConfigDisplayer;

    public BackupSlotAdapter(BackupConfigAdapter configDisplayer, ArrayList<Generator> configList) {
        this.mConfigDisplayer = configDisplayer;
        this.mConfigList = configList != null ? configList : new ArrayList<>();//secu
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_slot, parent, false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        holder.setDisplay(position);
        holder.setInteractions(mConfigDisplayer, mConfigList.get(position).getChannelList());
    }

    @Override
    public int getItemCount() {
        return mConfigList.size();
    }

    public int getFocusedIndex() {
        return 1;
    }
}
