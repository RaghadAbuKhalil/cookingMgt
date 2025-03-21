package org;
import java.util.HashMap;
import java.util.Map;
import org.MealAllergyChecker;

public class DietaryAndAllergies {
    private static Map<String , String> customerPreferences ;
    public static  MealAllergyChecker   mealAllergyChecker1=new MealAllergyChecker();

    public DietaryAndAllergies() {
        customerPreferences = new HashMap<String, String>();


    }

    public static void setCustomerPreferences(String dietry,String allergies) {
        DietaryAndAllergies.customerPreferences.put("Diet",dietry);
        DietaryAndAllergies.customerPreferences.put("Allergies",allergies);


    }
    public static  boolean  checkAllergies(String meal){
        if (meal == null || !mealAllergyChecker1.getMealIngredients().containsKey(meal)) {
            System.out.println("Error: Meal not found or null.");
            return false;
        }

        String allergies = customerPreferences.get("Allergies");
        if (allergies == null) {
            System.out.println("No allergies specified.");
            return false;
        }

        boolean containsAllergen = mealAllergyChecker1.getMealIngredients().get(meal).contains(allergies);
        if (containsAllergen) {
            System.out.println("Warning: The meal contains an allergen: " + allergies);
        }
        return containsAllergen;


    }

    public static Map<String, String> getCustomerPreferences() {
        return customerPreferences;
    }
}



