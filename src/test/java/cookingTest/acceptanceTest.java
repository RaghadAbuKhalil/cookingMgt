package cookingTest;
import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import io.cucumber.junit.CucumberOptions.SnippetType;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
        features = "C:\\Users\\ragha\\IdeaProjects\\cookingSweet\\src\\test\\resources\\cases",
        monochrome = true,
        snippets = SnippetType.CAMELCASE,
        glue = {"cookingTest"}
)


public class acceptanceTest {
}