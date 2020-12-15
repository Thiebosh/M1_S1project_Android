package fr.yncrea.m1_s1project_android.recyclers;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import fr.yncrea.m1_s1project_android.R;
import fr.yncrea.m1_s1project_android.interfaces.BluetoothParent;
import fr.yncrea.m1_s1project_android.models.Channel;
import fr.yncrea.m1_s1project_android.models.Generator;

public class BackupStoreAdapter extends RecyclerView.Adapter<BackupStoreAdapter.Holder> {

    public static class Holder extends RecyclerView.ViewHolder {

        private final TextView mStoreContainer;
        public Context mContext;

        public Holder(@NonNull View itemView) {
            super(itemView);
            mStoreContainer = itemView.findViewById(R.id.store_textview);
            mContext = itemView.getContext();
        }

        public TextView getStoreContainer(){
            return mStoreContainer;
        }

        public void setDisplay(final int position) {
            mStoreContainer.setBackgroundColor(mContext.getResources().getColor(
                    BluetoothParent.mIsStores.get(position) ? R.color.colorPrimary : R.color.gray));

            mStoreContainer.setText(mContext.getString(R.string.store, position));
            mStoreContainer.setTag(position);
        }

        public void setInteractions(BackupStoreAdapter adapter, BackupConfigAdapter displayer, Context context) {
            mStoreContainer.setOnClickListener(v -> {
                if (this != adapter.getLastHolderSelected()) {
                    if (adapter.getLastHolderSelected() != null) {
                        adapter.getLastHolderSelected().getStoreContainer().setBackgroundColor(mContext.getResources().getColor(
                                BluetoothParent.mIsStores.get(adapter.getFocusedIndex()) ? R.color.colorPrimary : R.color.gray));
                    }
                    mStoreContainer.setBackgroundColor(mContext.getResources().getColor(R.color.green));

                    adapter.setLastHolderSelected(this, getLayoutPosition());

                    if(BluetoothParent.mIsStores.get(getLayoutPosition()) && BluetoothParent.mBackupGenerators.get(getLayoutPosition()).getChannelList().isEmpty()){
                        ((BluetoothParent) getActivity(context)).sendData("store"+getLayoutPosition());
                        displayer.setChannelList(new ArrayList<>());
                    }
                    else displayer.setChannelList(BluetoothParent.mBackupGenerators.get(getLayoutPosition()).getChannelList());
                    displayer.notifyDataSetChanged();
                }
            });
        }

    }

    private final BackupConfigAdapter mConfigDisplayer;

    private BackupStoreAdapter.Holder mLastHolderSelected = null;
    private int mFocusedIndex = -1;

    private final Button mSave;
    private final Button mLoad;
    private final Button mDelete;


    public BackupStoreAdapter(View view, BackupConfigAdapter configDisplayer) {
        this.mConfigDisplayer = configDisplayer;

        mSave = view.findViewById(R.id.frag_back_button_save);
        mLoad = view.findViewById(R.id.frag_back_button_load);
        mDelete = view.findViewById(R.id.frag_back_button_delete);
    }

    public void updateChannelListData(ArrayList<Channel> data, int index) {
        Log.d("testy", "update in store adapter");
        BluetoothParent.mBackupGenerators.set(index, (new Generator()).setChannelList(data));
        mConfigDisplayer.updateChannelListData(data);

        mSave.setEnabled(true);
        mLoad.setEnabled(true);
        mDelete.setEnabled(true);
    }

    @Override
    public int getItemCount() {
        return BluetoothParent.mIsStores.size();
    }

    private static Activity getActivity(Context context) {
        while (context instanceof ContextWrapper) {
            if (context instanceof Activity) return (Activity)context;
            context = ((ContextWrapper)context).getBaseContext();
        }
        return null;
    }

    public Holder getLastHolderSelected() {
        return mLastHolderSelected;
    }

    public int getFocusedIndex() {
        return mFocusedIndex;
    }

    public void setLastHolderSelected(final Holder holder, final int position){
        this.mLastHolderSelected = holder;
        mFocusedIndex = position;

        mSave.setVisibility(View.VISIBLE);
        mLoad.setVisibility(View.VISIBLE);
        mDelete.setVisibility(View.VISIBLE);

        if (!BluetoothParent.mIsStores.get(mFocusedIndex)) {
            mSave.setEnabled(true);
            mLoad.setEnabled(false);
            mDelete.setEnabled(false);
        }
        else if (BluetoothParent.mBackupGenerators.get(mFocusedIndex).getChannelList().isEmpty()) {
            mSave.setEnabled(false);
            mLoad.setEnabled(false);
            mDelete.setEnabled(false);
        }
        else {
            mSave.setEnabled(true);
            mLoad.setEnabled(true);
            mDelete.setEnabled(true);
        }
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Holder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_store, parent, false));
        //return new Holder(LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_1, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        holder.setDisplay(position);
        holder.setInteractions(this, mConfigDisplayer, holder.itemView.getContext());
        //((TextView)holder.itemView).setText(holder.itemView.getContext().getString(R.string.store, position));


        Context context = holder.itemView.getContext();
        mSave.setOnClickListener(v -> {
            Generator toSave = (new Generator()).setChannelList(BluetoothParent.mGenerator.getChannelList()).setAllChannelActive(false);

            BluetoothParent.mBackupGenerators.set(mFocusedIndex, toSave);
            BluetoothParent.mIsStores.set(mFocusedIndex, true);

            notifyItemChanged(position, mLastHolderSelected);//change coloration si nécessaire
            mConfigDisplayer.setChannelList(toSave.getChannelList());
            mConfigDisplayer.notifyDataSetChanged();

            ((BluetoothParent) getActivity(context)).sendData("save_store_" + mFocusedIndex);

            mLoad.setEnabled(true);
            mDelete.setEnabled(true);
        });

        mLoad.setOnClickListener(v -> {
            ArrayList<Channel> savedConfig = BluetoothParent.mBackupGenerators.get(mFocusedIndex).getChannelList();
            BluetoothParent.mGenerator.setChannelList(savedConfig);

            ((BluetoothParent) getActivity(context)).sendData("load_store_" + mFocusedIndex);
        });

        mDelete.setOnClickListener(v -> {
            BluetoothParent.mBackupGenerators.get(mFocusedIndex).setChannelList(new ArrayList<>());
            BluetoothParent.mIsStores.set(mFocusedIndex, false);

            notifyItemChanged(position, mLastHolderSelected);//change coloration si nécessaire
            mConfigDisplayer.setChannelList(new ArrayList<>());
            mConfigDisplayer.notifyDataSetChanged();

            ((BluetoothParent) getActivity(context)).sendData("delete_store_"+mFocusedIndex);

            mLoad.setEnabled(false);
            mDelete.setEnabled(false);
        });
    }
}
