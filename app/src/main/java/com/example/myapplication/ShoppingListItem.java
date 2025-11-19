package com.example.myapplication;

public class ShoppingListItem {
    private final int sequence;
    private final String description;
    private final int quantity;

    public ShoppingListItem(int sequence, String description, int quantity) {
        this.sequence = sequence;
        this.description = description;
        this.quantity = quantity;
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
}
