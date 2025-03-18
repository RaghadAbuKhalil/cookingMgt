package org;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MealAllergyChecker {
    private static Map<String, List<String>>  mealIngredients = new HashMap<>();

    static {

        mealIngredients.put("Strawberry CAKE", Arrays.asList("Flour", "Sugar", "Eggs", "Milk", "Cocoa","strawberry"));
        mealIngredients.put("Peanut Butter Cookies", Arrays.asList("Flour", "Sugar", "Peanuts", "Butter"));
        mealIngredients.put("Caesar Salad", Arrays.asList("Lettuce", "Croutons", "Parmesan", "Eggs"));
    }

    public MealAllergyChecker() {

    }
    public static boolean containsAllergen(String meal, String allergen) {
        List<String> ingredients = mealIngredients.get(meal);
        return ingredients != null && ingredients.contains(allergen);


}

    public static Map<String, List<String>> getMealIngredients() {
        return mealIngredients;
    }
}
