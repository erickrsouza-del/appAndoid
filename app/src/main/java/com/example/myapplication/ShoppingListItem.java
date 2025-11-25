package com.example.myapplication;

public class ShoppingListItem {
    private int sequence;
    private final String description;
    private final int quantity;
    private boolean isChecked;

    public ShoppingListItem(int sequence, String description, int quantity) {
        this.sequence = sequence;
        this.description = description;
        this.quantity = quantity;
        this.isChecked = false; // Default to not checked
    }

    public int getSequence() {
        return sequence;
    }

    public String getDescription() {
        return description;
    }

    public int getQuantity() {
        return quantity;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public void setSequence(int sequence) {
        this.sequence = sequence;
    }
}
