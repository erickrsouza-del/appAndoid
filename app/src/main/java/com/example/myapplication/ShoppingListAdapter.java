package com.example.myapplication;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class ShoppingListAdapter extends RecyclerView.Adapter<ShoppingListAdapter.ViewHolder> {

    private final List<ShoppingListItem> items;
    private final OnItemRemoveListener removeListener;

    public interface OnItemRemoveListener {
        void onItemRemove(int position);
    }

    public ShoppingListAdapter(List<ShoppingListItem> items, OnItemRemoveListener removeListener) {
        this.items = items;
        this.removeListener = removeListener;
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
        holder.removeItemButton.setOnClickListener(v -> removeListener.onItemRemove(position));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView sequence, description, quantity;
        ImageButton removeItemButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            sequence = itemView.findViewById(R.id.item_sequence);
            description = itemView.findViewById(R.id.item_description);
            quantity = itemView.findViewById(R.id.item_quantity);
            removeItemButton = itemView.findViewById(R.id.button_remove_item);
        }
    }
}
