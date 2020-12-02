package fr.yncrea.m1_s1project_android.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import fr.yncrea.m1_s1project_android.R;

public class BackupAdapterView extends RecyclerView.Adapter<BackupViewHolder> {
    public BackupAdapterView() {
    }

    @NonNull
    @Override
    public BackupViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.slot_item, parent, false);
        return new BackupViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BackupViewHolder holder, int position) {
        holder.setBackupDisplay();

    }

    @Override
    public int getItemCount() {
        return 5;
    }
}
