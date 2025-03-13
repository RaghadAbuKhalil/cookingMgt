package cookingTest;



import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;
import io.cucumber.junit.platform.engine.Constants;

    @Suite
    @IncludeEngines("cucumber")
    @SelectClasspathResource("cases")
    @ConfigurationParameter(key = Constants.PLUGIN_PROPERTY_NAME, value = "pretty")
    @ConfigurationParameter(key = Constants.GLUE_PROPERTY_NAME, value = "cookingTest")
    public class RunCucumberTest {
    }


