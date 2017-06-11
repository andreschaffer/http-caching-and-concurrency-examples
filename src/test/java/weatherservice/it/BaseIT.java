package weatherservice.it;

import io.dropwizard.Configuration;
import io.dropwizard.client.JerseyClientBuilder;
import io.dropwizard.testing.junit.DropwizardAppRule;
import org.glassfish.jersey.logging.LoggingFeature;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import weatherservice.bootstrap.WeatherServiceApplication;

import javax.ws.rs.client.Client;
import java.lang.invoke.MethodHandles;

import static io.dropwizard.testing.ResourceHelpers.resourceFilePath;
import static java.util.logging.Level.INFO;
import static java.util.logging.Logger.getLogger;
import static org.glassfish.jersey.client.ClientProperties.CONNECT_TIMEOUT;
import static org.glassfish.jersey.client.ClientProperties.READ_TIMEOUT;
import static org.glassfish.jersey.logging.LoggingFeature.DEFAULT_LOGGER_NAME;
import static org.glassfish.jersey.logging.LoggingFeature.Verbosity.PAYLOAD_ANY;

public abstract class BaseIT {

    @ClassRule
    public static final DropwizardAppRule<Configuration> SERVICE =
            new DropwizardAppRule<>(WeatherServiceApplication.class, resourceFilePath("integration.yml"));

    protected static Client client;

    @BeforeClass
    public static void setUpClass() throws Exception {
        client = new JerseyClientBuilder(SERVICE.getEnvironment())
                .build(MethodHandles.lookup().lookupClass().getName())
                .property(CONNECT_TIMEOUT, 2000)
                .property(READ_TIMEOUT, 3000)
                .register(new LoggingFeature(getLogger(DEFAULT_LOGGER_NAME), INFO, PAYLOAD_ANY, 1024));
    }
}
