package weatherservice.it;

import static io.dropwizard.testing.ResourceHelpers.resourceFilePath;
import static java.util.logging.Level.INFO;
import static java.util.logging.Logger.getLogger;
import static org.glassfish.jersey.client.ClientProperties.CONNECT_TIMEOUT;
import static org.glassfish.jersey.client.ClientProperties.READ_TIMEOUT;
import static org.glassfish.jersey.logging.LoggingFeature.DEFAULT_LOGGER_NAME;
import static org.glassfish.jersey.logging.LoggingFeature.Verbosity.PAYLOAD_ANY;

import io.dropwizard.Configuration;
import io.dropwizard.client.JerseyClientBuilder;
import io.dropwizard.testing.junit5.DropwizardAppExtension;
import io.dropwizard.testing.junit5.DropwizardExtensionsSupport;
import java.lang.invoke.MethodHandles;
import javax.ws.rs.client.Client;
import org.glassfish.jersey.logging.LoggingFeature;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.extension.ExtendWith;
import weatherservice.bootstrap.WeatherServiceApplication;

@ExtendWith(DropwizardExtensionsSupport.class)
abstract class BaseIT {

    protected static final DropwizardAppExtension<Configuration> SERVICE =
            new DropwizardAppExtension<>(
                WeatherServiceApplication.class,
                resourceFilePath("integration.yml"));

    protected static Client client;

    @BeforeAll
    static void setUpClass() {
        client = new JerseyClientBuilder(SERVICE.getEnvironment())
                .build(MethodHandles.lookup().lookupClass().getName())
                .property(CONNECT_TIMEOUT, 2000)
                .property(READ_TIMEOUT, 3000)
                .register(new LoggingFeature(getLogger(DEFAULT_LOGGER_NAME), INFO, PAYLOAD_ANY, 1024));
    }
}
