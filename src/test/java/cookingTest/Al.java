package cookingTest;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.Assert;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

public class Al {

    private static final Logger logger = Logger.getLogger(Al.class.getName());

    private String dietaryRestriction;
        private List<String> ingredients;
        private int time;
        private String recommendedRecipe;

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
            try {

                Connection conn = DriverManager.getConnection("jdbc:sqlite:database.db");
                Statement stmt = conn.createStatement();
                stmt.execute("PRAGMA invalid_syntax_here");
            } catch (SQLException e) {
               logger.warning("error happened in get connection to data base");
            }
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
                        .filter(i -> !i.equals("olive oil"))
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



