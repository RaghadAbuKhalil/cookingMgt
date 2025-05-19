package org;

public class Ingredient {
    private final String name;
    private final  String status;
    private  final String dietaryCategory;
    private int quantity;

    public Ingredient(String name, String status, String dietaryCategory, int quantity) {
        this.name = name;
        this.status = status;
        this.dietaryCategory = dietaryCategory;
        this.quantity = quantity;
    }

    public String getName() {
        return name;
    }

    public String getStatus() {
        return status;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getDietaryCategory() {
        return dietaryCategory;
    }
}
