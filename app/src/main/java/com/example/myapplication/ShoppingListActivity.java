package com.example.myapplication;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

public class ShoppingListActivity extends BaseActivity implements ShoppingListAdapter.OnItemInteractionListener {

    private RecyclerView recyclerView;
    private EditText editTextDescription, editTextQuantity;
    private CoordinatorLayout coordinatorLayout;

    private List<ShoppingListItem> shoppingList;
    private ShoppingListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_list);

        coordinatorLayout = findViewById(R.id.coordinator_layout);
        recyclerView = findViewById(R.id.recycler_view_shopping_list);
        editTextDescription = findViewById(R.id.edit_text_item_description);
        editTextQuantity = findViewById(R.id.edit_text_item_quantity);
        Button buttonAddItem = findViewById(R.id.button_add_item);
        Button buttonClearChecked = findViewById(R.id.button_clear_checked);

        shoppingList = new ArrayList<>();
        adapter = new ShoppingListAdapter(shoppingList, this);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        buttonAddItem.setOnClickListener(v -> addItem());
        buttonClearChecked.setOnClickListener(v -> clearCheckedItems());
    }

    private void addItem() {
        String description = editTextDescription.getText().toString().trim();
        String quantityStr = editTextQuantity.getText().toString().trim();

        if (TextUtils.isEmpty(description)) {
            Toast.makeText(this, "A descrição não pode estar vazia.", Toast.LENGTH_SHORT).show();
            return;
        }

        int quantity = 1;
        if (!TextUtils.isEmpty(quantityStr)) {
            try {
                quantity = Integer.parseInt(quantityStr);
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Quantidade inválida.", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        int sequence = shoppingList.size() + 1;
        shoppingList.add(new ShoppingListItem(sequence, description, quantity));
        adapter.notifyItemInserted(shoppingList.size() - 1);
        recyclerView.scrollToPosition(shoppingList.size() - 1);

        editTextDescription.setText("");
        editTextQuantity.setText("");
    }

    private void clearCheckedItems() {
        List<ShoppingListItem> itemsToRemove = new ArrayList<>();
        for (ShoppingListItem item : shoppingList) {
            if (item.isChecked()) {
                itemsToRemove.add(item);
            }
        }

        if (itemsToRemove.isEmpty()) {
            Toast.makeText(this, "Nenhum item concluído para limpar.", Toast.LENGTH_SHORT).show();
            return;
        }

        shoppingList.removeAll(itemsToRemove);
        updateSequencesAndNotify();

        Snackbar.make(coordinatorLayout, itemsToRemove.size() + " itens removidos", Snackbar.LENGTH_LONG)
                .setAction("DESFAZER", v -> {
                    shoppingList.addAll(itemsToRemove);
                    // This is a simple resort, for a real app you might want to preserve original order
                    shoppingList.sort((o1, o2) -> Integer.compare(o1.getSequence(), o2.getSequence())); 
                    updateSequencesAndNotify();
                })
                .show();
    }

    @Override
    public void onItemRemove(int position) {
        if (position >= 0 && position < shoppingList.size()) {
            final ShoppingListItem removedItem = shoppingList.remove(position);
            updateSequencesAndNotify();

            Snackbar.make(coordinatorLayout, "Item removido", Snackbar.LENGTH_LONG)
                    .setAction("DESFAZER", v -> {
                        shoppingList.add(position, removedItem);
                        updateSequencesAndNotify();
                    })
                    .show();
        }
    }

    @Override
    public void onItemCheckChanged(int position, boolean isChecked) {
        if (position >= 0 && position < shoppingList.size()) {
            shoppingList.get(position).setChecked(isChecked);
            adapter.notifyItemChanged(position);
        }
    }

    private void updateSequencesAndNotify() {
        for (int i = 0; i < shoppingList.size(); i++) {
            shoppingList.get(i).setSequence(i + 1);
        }
        adapter.notifyDataSetChanged();
    }
}
