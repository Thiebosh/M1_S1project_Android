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
        public Context ctx;

        public Holder(@NonNull View itemView) {
            super(itemView);
            mStoreContainer = itemView.findViewById(R.id.store_textview);
            ctx = itemView.getContext();
        }

        public TextView getStoreContainer(){
            return mStoreContainer;
        }

        public void setDisplay(final int position) {
            if(BluetoothParent.isStore.get(position)){
                mStoreContainer.setBackgroundColor(itemView.getContext().getResources().getColor(R.color.green));
            }
            mStoreContainer.setText(itemView.getContext().getString(R.string.store, position));
            mStoreContainer.setTag(position);
        }

        public void setInteractions(BackupStoreAdapter adapter, BackupConfigAdapter displayer, Context context) {
            mStoreContainer.setOnClickListener(v -> {
                if (this != adapter.getLastHolderSelected()) {
                    if (adapter.getLastHolderSelected() != null) {
                        adapter.getLastHolderSelected().getStoreContainer().setBackgroundColor(itemView.getContext().getResources().getColor(R.color.gray));
                    }

                    mStoreContainer.setBackgroundColor(itemView.getContext().getResources().getColor(R.color.yellow));

                    adapter.setLastHolderSelected(this, getAdapterPosition());


                    //Log.d("testy", "direct test "+BluetoothParent.mGenerator.getChannel(5).getCurrentValue());

                    //si pas encore acquis données de cette config => tableau de bool faux, met à true quand demandé ? permet de passer outre stores vides
                    if(BluetoothParent.isStoreCharged.get(getAdapterPosition()) && BluetoothParent.mBackupGenerator.get(getAdapterPosition()).getChannelList().isEmpty()){
                        ((BluetoothParent) getActivity(context)).sendData("store"+getAdapterPosition());
                        displayer.setChannelList(new ArrayList<>());
                        displayer.notifyDataSetChanged();
                    }
                    else {
                        displayer.setChannelList(BluetoothParent.mBackupGenerator.get(getAdapterPosition()).getChannelList());
                        displayer.notifyDataSetChanged();
                        Log.d("testy", "direct test " + BluetoothParent.isStoreCharged.get(getAdapterPosition()));
                    }
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
        BluetoothParent.mBackupGenerator.set(index, (new Generator()).setChannelList(data));
        mConfigDisplayer.updateChannelListData(data);

        mSave.setEnabled(true);
        mLoad.setEnabled(true);
        mDelete.setEnabled(true);
    }

    @Override
    public int getItemCount() {
        return BluetoothParent.isStore.size();
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

    public void setLastHolderSelected(final Holder holder, final int position){
        this.mLastHolderSelected = holder;
        mFocusedIndex = position;

        mSave.setVisibility(View.VISIBLE);
        mLoad.setVisibility(View.VISIBLE);
        mDelete.setVisibility(View.VISIBLE);

        if (!BluetoothParent.isStoreCharged.get(mFocusedIndex)) {
            mSave.setEnabled(true);
            mLoad.setEnabled(false);
            mDelete.setEnabled(false);
        }
        else if (BluetoothParent.mBackupGenerator.get(mFocusedIndex).getChannelList().isEmpty()) {
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
            //set tous les canaux à faux
            //Generator save = (new Generator(((BluetoothParent) context).getGenerator())).setAllChannelActive(false);//constructeur par copie ?
            //Generator save = (((BluetoothParent) getActivity(context)).getGenerator()).setAllChannelActive(false);
            Generator save = new Generator();
            //save.setChannelList((ArrayList<Channel>) (((BluetoothParent) getActivity(context)).getGenerator()).getChannelList().clone());
            save.setChannelList(BluetoothParent.mGenerator.getChannelList());
            save.setAllChannelActive(false);

            if (save != BluetoothParent.mBackupGenerator.get(mFocusedIndex)) {//verifier que fonctionne bien
                //remplace config précédente
                BluetoothParent.mBackupGenerator.set(mFocusedIndex, save);

                //actualise oneConfigRecycler (config affichée)
                mConfigDisplayer.setChannelList(save.getChannelList());
                mConfigDisplayer.notifyDataSetChanged();

                //envoie commande à arduino : contient déjà données
                ((BluetoothParent) getActivity(context)).sendData("save_store_"+mFocusedIndex);

                mLoad.setActivated(true);
                mDelete.setActivated(true);
            }
        });

        mLoad.setOnClickListener(v -> {
            //récupère config
            ArrayList<Channel> savedConfig = BluetoothParent.mBackupGenerator.get(mFocusedIndex).getChannelList();

            if (savedConfig != BluetoothParent.mGenerator.setAllChannelActive(false).getChannelList()) {//vérifier que fonctionne sans casser mainboard
                //la charge
                BluetoothParent.mGenerator.setChannelList(savedConfig);

                //envoie commande à arduino : contient déjà données
                ((BluetoothParent) getActivity(context)).sendData("load_store_" + mFocusedIndex);
            }
        });

        mDelete.setOnClickListener(v -> {
            //supprime config
            BluetoothParent.mBackupGenerator.get(mFocusedIndex).setChannelList(new ArrayList<>());

            //actualise oneConfigRecycler (config affichée)
            mConfigDisplayer.setChannelList(new ArrayList<>());
            mConfigDisplayer.notifyDataSetChanged();

            //envoie commande à arduino : contient déjà données
            ((BluetoothParent) getActivity(context)).sendData("delete_store_"+mFocusedIndex);

            mLoad.setActivated(false);
            mDelete.setActivated(false);
        });
    }
}
