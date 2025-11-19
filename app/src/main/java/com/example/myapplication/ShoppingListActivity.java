package com.example.myapplication;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ShoppingListActivity extends BaseActivity implements ShoppingListAdapter.OnItemRemoveListener {

    private RecyclerView recyclerView;
    private EditText editTextDescription, editTextQuantity;
    private Button buttonAddItem;

    private List<ShoppingListItem> shoppingList;
    private ShoppingListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_list);

        recyclerView = findViewById(R.id.recycler_view_shopping_list);
        editTextDescription = findViewById(R.id.edit_text_item_description);
        editTextQuantity = findViewById(R.id.edit_text_item_quantity);
        buttonAddItem = findViewById(R.id.button_add_item);

        shoppingList = new ArrayList<>();
        adapter = new ShoppingListAdapter(shoppingList, this);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        buttonAddItem.setOnClickListener(v -> addItem());
    }

    private void addItem() {
        String description = editTextDescription.getText().toString().trim();
        String quantityStr = editTextQuantity.getText().toString().trim();

        if (TextUtils.isEmpty(description)) {
            Toast.makeText(this, "A descrição não pode estar vazia.", Toast.LENGTH_SHORT).show();
            return;
        }

        int quantity = 1; // Default quantity
        if (!TextUtils.isEmpty(quantityStr)) {
            try {
                quantity = Integer.parseInt(quantityStr);
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Quantidade inválida.", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        // The sequence is just the next position in the list
        int sequence = shoppingList.size() + 1;
        shoppingList.add(new ShoppingListItem(sequence, description, quantity));
        adapter.notifyItemInserted(shoppingList.size() - 1);

        // Clear input fields
        editTextDescription.setText("");
        editTextQuantity.setText("");
    }

    @Override
    public void onItemRemove(int position) {
        if (position >= 0 && position < shoppingList.size()) {
            shoppingList.remove(position);
            // This is not the most efficient way, but it's simple and works for this case
            // It re-calculates all sequences and re-binds all items
            for (int i = 0; i < shoppingList.size(); i++) {
                 ShoppingListItem current = shoppingList.get(i);
                 shoppingList.set(i, new ShoppingListItem(i + 1, current.getDescription(), current.getQuantity()));
            }
            adapter.notifyDataSetChanged(); 
        }
    }
}
