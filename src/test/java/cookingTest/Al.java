package cookingTest;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.Assert;

import java.util.Arrays;
import java.util.List;

public class Al {


        private String dietaryRestriction;
        private List<String> ingredients;
        private int time;
        private String recommendedRecipe;

        // قاعدة بيانات للوصفات
        private static class Recipe {
            String name;
            List<String> ingredients;
            int timeRequired;
            String dietaryRestriction;

            Recipe(String name, List<String> ingredients, int timeRequired, String dietaryRestriction) {
                this.name = name;
                this.ingredients = ingredients;
                this.timeRequired = timeRequired;
                this.dietaryRestriction = dietaryRestriction;
            }
        }

        private List<Recipe> recipeDatabase = Arrays.asList(
                new Recipe("Spaghetti with Tomato Sauce", Arrays.asList("Tomatoes", "pasta", "basil", "olive oil"), 25, "Vegan"),
                new Recipe("Tomato Basil Soup", Arrays.asList("Tomatoes", "basil", "garlic"), 40, "Vegan"),
                new Recipe("Vegan Pesto Pasta", Arrays.asList("basil", "pasta", "olive oil", "garlic"), 20, "Vegan")
        );

        @Given("the customer has dietary restriction {string}")
        public void theCustomerHasDietaryRestriction(String restriction) {
            this.dietaryRestriction = restriction;
        }

        @And("the available ingredients are {string}, {string}, {string}")
        public void theAvailableIngredientsAre(String ing1, String ing2, String ing3) {
            this.ingredients = Arrays.asList(ing1, ing2, ing3);
        }

        @And("the available time is {int} minutes")
        public void theAvailableTimeIsMinutes(int time) {
            this.time = time;
        }

        @When("the recipe assistant is asked to recommend a recipe")
        public void theRecipeAssistantIsAskedToRecommendARecipe() {
            List<String> userIngredientsLower = ingredients.stream()
                    .map(String::toLowerCase)
                    .toList();

            for (Recipe recipe : recipeDatabase) {
                List<String> requiredIngredients = recipe.ingredients.stream()
                        .map(String::toLowerCase)
                        .filter(i -> !i.equals("olive oil")) // السماح بتجاهل زيت الزيتون
                        .toList();

                if (recipe.dietaryRestriction.equalsIgnoreCase(dietaryRestriction)
                        && time >= recipe.timeRequired
                        && userIngredientsLower.containsAll(requiredIngredients)) {
                    recommendedRecipe = recipe.name;
                    return;
                }
            }
            recommendedRecipe = "No suitable recipe found";
        }

        @Then("the assistant should recommend {string}")
        public void theAssistantShouldRecommend(String expectedRecipe) {
            Assert.assertEquals(expectedRecipe, recommendedRecipe);
        }
    }



