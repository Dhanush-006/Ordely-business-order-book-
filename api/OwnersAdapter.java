package com.saveetha.orderly_book.api;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.saveetha.orderly_book.api.User;

import java.util.List;

public class OwnersAdapter extends RecyclerView.Adapter<OwnersAdapter.OwnerViewHolder> {

    public interface OnOwnerClickListener {
        void onOwnerClick(User owner);
    }

    private List<User> owners;
    private OnOwnerClickListener listener;

    public OwnersAdapter(List<User> owners, OnOwnerClickListener listener) {
        this.owners = owners;
        this.listener = listener;
    }

    @NonNull
    @Override
    public OwnerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(android.R.layout.simple_list_item_1, parent, false);
        return new OwnerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OwnerViewHolder holder, int position) {
        User owner = owners.get(position);
        holder.tvName.setText(owner.getName() + " - " + owner.getBusinessName());
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onOwnerClick(owner);
        });
    }

    @Override
    public int getItemCount() {
        return owners.size();
    }

    static class OwnerViewHolder extends RecyclerView.ViewHolder {
        TextView tvName;

        public OwnerViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(android.R.id.text1);
        }
    }
}
