package com.example.myapplication;

import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class ShoppingListAdapter extends RecyclerView.Adapter<ShoppingListAdapter.ViewHolder> {

    private final List<ShoppingListItem> items;
    private final OnItemInteractionListener interactionListener;

    public interface OnItemInteractionListener {
        void onItemRemove(int position);
        void onItemCheckChanged(int position, boolean isChecked);
    }

    public ShoppingListAdapter(List<ShoppingListItem> items, OnItemInteractionListener listener) {
        this.items = items;
        this.interactionListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.shopping_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ShoppingListItem item = items.get(position);

        holder.sequence.setText(String.format("%d.", item.getSequence()));
        holder.description.setText(item.getDescription());
        holder.quantity.setText(String.format("Qtd: %d", item.getQuantity()));

        // Set checkbox state without triggering the listener
        holder.checkBox.setOnCheckedChangeListener(null);
        holder.checkBox.setChecked(item.isChecked());

        // Apply strikethrough based on checked state
        if (item.isChecked()) {
            holder.description.setPaintFlags(holder.description.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        } else {
            holder.description.setPaintFlags(holder.description.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
        }

        // Re-set the listener
        holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            interactionListener.onItemCheckChanged(holder.getAdapterPosition(), isChecked);
        });

        holder.removeItemButton.setOnClickListener(v -> interactionListener.onItemRemove(holder.getAdapterPosition()));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        CheckBox checkBox;
        TextView sequence, description, quantity;
        ImageButton removeItemButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            checkBox = itemView.findViewById(R.id.item_checkbox);
            sequence = itemView.findViewById(R.id.item_sequence);
            description = itemView.findViewById(R.id.item_description);
            quantity = itemView.findViewById(R.id.item_quantity);
            removeItemButton = itemView.findViewById(R.id.button_remove_item);
        }
    }
}
