package weatherservice.bootstrap;

import io.dropwizard.Application;
import io.dropwizard.Configuration;
import io.dropwizard.setup.Environment;
import weatherservice.climate.ClimateRepository;
import weatherservice.climate.ClimateResource;
import weatherservice.daylight.DaylightResource;

import static com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS;

public class WeatherServiceApplication extends Application<Configuration> {

    public static void main(String[] args) throws Exception {
        new WeatherServiceApplication().run(args);
    }

    @Override
    public void run(Configuration configuration, Environment environment) throws Exception {
        configureObjectMapper(environment);
        registerResources(environment);
    }

    private void configureObjectMapper(Environment environment) {
        environment.getObjectMapper().configure(WRITE_DATES_AS_TIMESTAMPS, false);
    }

    private void registerResources(Environment environment) {
        environment.jersey().register(new DaylightResource());
        environment.jersey().register(new ClimateResource(new ClimateRepository()));
    }
}
