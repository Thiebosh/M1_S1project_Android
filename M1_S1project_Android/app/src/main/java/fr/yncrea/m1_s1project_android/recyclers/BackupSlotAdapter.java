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
import android.widget.Toast;

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
                if (this != adapter.getLastHolderSelected()) {
                    if (adapter.getLastHolderSelected() != null) {
                        adapter.getLastHolderSelected().getSlotContainer().setBackgroundColor(itemView.getContext().getResources().getColor(R.color.gray));
                    }

                    mSlotContainer.setBackgroundColor(itemView.getContext().getResources().getColor(R.color.yellow));

                    adapter.setLastHolderSelected(this, getAdapterPosition());

                    //si pas encore acquis données de cette config => tableau de bool faux, met à true quand demandé ? permet de passer outre slots vides
                    ((BluetoothParent) getActivity(context)).sendData("slot"+getAdapterPosition());

                    //sinon
                    //displayer.setChannelList(config);
                    //displayer.notifyDataSetChanged();
                }
            });
        }

    }

    private final ArrayList<Generator> mConfigList;//arrayList<bool> mConfigList => récupère tableau de bool des acquis ou non
    private final BackupConfigAdapter mConfigDisplayer;

    private BackupSlotAdapter.Holder mLastHolderSelected = null;
    private int mFocusedIndex = -1;

    private final Button mSave;
    private final Button mLoad;
    private final Button mDelete;


    public BackupSlotAdapter(View view, BackupConfigAdapter configDisplayer, ArrayList<Generator> configList) {
        this.mConfigDisplayer = configDisplayer;
        this.mConfigList = configList != null ? configList : new ArrayList<>(8);//secu

        mSave = view.findViewById(R.id.frag_back_button_save);
        mLoad = view.findViewById(R.id.frag_back_button_load);
        mDelete = view.findViewById(R.id.frag_back_button_delete);
    }

    public void updateChannelListData(ArrayList<Channel> tmp, int index) {
        /*
        if (index == -1) {
            boolean active = tmp.get(0).isActive();
            for (int i = 0; i < mChannelList.size(); ++i) mChannelList.get(i).setActive(active);
            this.notifyDataSetChanged();
        }
        else if (index == -2) {
            mChannelList.clear();
            mChannelList.addAll(tmp);
            this.notifyDataSetChanged();
        }
        else {
            mChannelList.set(index, tmp.get(index));
            this.notifyItemChanged(index);
        }*/
    }

    @Override
    public int getItemCount() {
        return mConfigList.size();
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
        mSave.setEnabled(true);


        if (!mConfigList.get(mFocusedIndex).getChannelList().isEmpty()) mLoad.setActivated(true);
        if (!mConfigList.get(mFocusedIndex).getChannelList().isEmpty()) mDelete.setActivated(true);
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


        Context context = holder.itemView.getContext();
        mSave.setOnClickListener(v -> {
            //set tous les canaux à faux
            //Generator save = (new Generator(((BluetoothParent) context).getGenerator())).setAllChannelActive(false);//constructeur par copie ?
            //Generator save = (((BluetoothParent) getActivity(context)).getGenerator()).setAllChannelActive(false);
            Generator save = new Generator();
            save.setChannelList((ArrayList<Channel>) (((BluetoothParent) getActivity(context)).getGenerator()).getChannelList().clone());
            save.setAllChannelActive(false);

            if (save != mConfigList.get(mFocusedIndex)) {//verifier que fonctionne bien
                //remplace config précédente
                mConfigList.set(mFocusedIndex, save);

                //actualise oneConfigRecycler (config affichée)
                mConfigDisplayer.setChannelList(save.getChannelList());
                mConfigDisplayer.notifyDataSetChanged();

                //envoie commande à arduino : contient déjà données
                ((BluetoothParent) getActivity(context)).sendData("save_slot_"+mFocusedIndex);

                mLoad.setActivated(true);
                mDelete.setActivated(true);
            }
        });

        mLoad.setOnClickListener(v -> {
            //récupère config
            ArrayList<Channel> savedConfig = mConfigList.get(mFocusedIndex).getChannelList();

            if (savedConfig != ((BluetoothParent) getActivity(context)).getGenerator().setAllChannelActive(false).getChannelList()) {//vérifier que fonctionne sans casser mainboard
                //la charge
                ((BluetoothParent) getActivity(context)).getGenerator().setChannelList(savedConfig);

                //envoie commande à arduino : contient déjà données
                ((BluetoothParent) getActivity(context)).sendData("load_slot_" + mFocusedIndex);
            }
        });

        mDelete.setOnClickListener(v -> {
            //supprime config
            mConfigList.get(mFocusedIndex).setChannelList(new ArrayList<>());

            //actualise oneConfigRecycler (config affichée)
            mConfigDisplayer.setChannelList(new ArrayList<>());
            mConfigDisplayer.notifyDataSetChanged();

            //envoie commande à arduino : contient déjà données
            ((BluetoothParent) getActivity(context)).sendData("delete_slot_"+mFocusedIndex);

            mLoad.setActivated(false);
            mDelete.setActivated(false);
        });
    }
}
