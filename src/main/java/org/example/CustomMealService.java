package org.example;

import org.DietaryAndAllergies;

import java.util.ArrayList;
import java.util.List;

public class CustomMealService {
    private DietaryAndAllergies dietaryAndAllergies = new DietaryAndAllergies();
private List<String>  availableIng= List.of("fish","chicken","beef","rice ");
    private List<String>  WantedIng = new ArrayList<>();
    public CustomMealService() {
    }
public void addIngredient(String ingr){
        if(availableIng.contains(ingr))  WantedIng.add(ingr);
        else System.out.println("Ingredient unavailable: " + ingr);
}

}
