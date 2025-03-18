package org;
 import java.util.HashMap;
import java.util.Map;
import org.MealAllergyChecker;

    public class DietaryAndAllergies {
        private static Map<String , String> customerPreferences ;
        public static  MealAllergyChecker   MealAllergyChecker1=new MealAllergyChecker();

        public DietaryAndAllergies() {
                customerPreferences = new HashMap<String, String>();


        }

        public static void setCustomerPreferences(String dietry,String allergies) {
            DietaryAndAllergies.customerPreferences.put("Diet",dietry);
            DietaryAndAllergies.customerPreferences.put("Allergies",allergies);


        }
        public static  boolean  checkAllergies(String meal){
            System.out.println(MealAllergyChecker1.getMealIngredients().get(meal) );
            boolean test=MealAllergyChecker1.getMealIngredients().get(meal).contains( DietaryAndAllergies.customerPreferences.get("Allergies"));

   // return MealAllergyChecker1.getMealIngredients().get(meal).contains( DietaryAndAllergies.customerPreferences.get("Allergies"));
        return test;


        }

        public static Map<String, String> getCustomerPreferences() {
            return customerPreferences;
        }

    }


