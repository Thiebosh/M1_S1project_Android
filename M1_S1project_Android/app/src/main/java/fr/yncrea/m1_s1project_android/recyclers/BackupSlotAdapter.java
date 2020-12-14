package fr.yncrea.m1_s1project_android.recyclers;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import fr.yncrea.m1_s1project_android.R;
import fr.yncrea.m1_s1project_android.interfaces.BluetoothParent;
import fr.yncrea.m1_s1project_android.models.Channel;
import fr.yncrea.m1_s1project_android.models.Generator;

public class BackupSlotAdapter extends RecyclerView.Adapter<BackupSlotAdapter.Holder> {

    public static class Holder extends RecyclerView.ViewHolder {

        private final TextView mSlotContainer;
        public Context ctx;


        public Holder(@NonNull View itemView) {
            super(itemView);
            mSlotContainer = itemView.findViewById(R.id.slot_textview);
            ctx = itemView.getContext();
        }

        public TextView getSlotContainer(){
            return mSlotContainer;
        }

        public void setDisplay(final int position) {
            mSlotContainer.setText(itemView.getContext().getString(R.string.slot, position));
            mSlotContainer.setTag(position);
        }

        public void setInteractions(BackupSlotAdapter adapter, BackupConfigAdapter displayer, final ArrayList<Channel> config, Context context) {
            mSlotContainer.setOnClickListener(v -> {
                ((BluetoothParent) getActivity(context)).sendData("slot"+getAdapterPosition());
                //apply color on item
                //displayer.getChannelList().clear();
                //displayer.getChannelList().addAll(config);
                //displayer.notifyDataSetChanged();
                if (this != adapter.getLastHolderSelected()) {//pour empecher deselection de digit
                    if (adapter.getLastHolderSelected() != null) {
                        adapter.getLastHolderSelected().getSlotContainer().setBackgroundColor(itemView.getContext().getResources().getColor(R.color.gray));
                    }

                    mSlotContainer.setBackgroundColor(itemView.getContext().getResources().getColor(R.color.yellow));

                    adapter.setLastHolderSelected(this);

                }
            });
        }

    }

    private final ArrayList<Generator> mConfigList;
    private final BackupConfigAdapter mConfigDisplayer;
    private BackupSlotAdapter.Holder mLastHolderSelected = null;

    public Holder getLastHolderSelected() {
        return mLastHolderSelected;
    }

    public void setLastHolderSelected(Holder holder){
        this.mLastHolderSelected = holder;
    }


    public BackupSlotAdapter(BackupConfigAdapter configDisplayer, ArrayList<Generator> configList) {
        this.mConfigDisplayer = configDisplayer;
        this.mConfigList = configList != null ? configList : new ArrayList<>();//secu
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Holder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_slot, parent, false));
        //return new Holder(LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_1, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        holder.setDisplay(position);
        holder.setInteractions(this, mConfigDisplayer, mConfigList.get(position).getChannelList(), holder.itemView.getContext());
        //((TextView)holder.itemView).setText(holder.itemView.getContext().getString(R.string.slot, position));


    }

    @Override
    public int getItemCount() {
        return mConfigList.size();
    }

    public int getFocusedIndex() {
        return 1;
    }

    private static Activity getActivity(Context context) {
        while (context instanceof ContextWrapper) {
            if (context instanceof Activity) {
                return (Activity)context;
            }
            context = ((ContextWrapper)context).getBaseContext();
        }
        return null;
    }
}
