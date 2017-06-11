package weatherservice.climate;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

public class ClimateRepositoryTest {

    private ClimateRepository climateRepository = new ClimateRepository();

    @Test
    public void returnSafeCopy() throws Exception {
        ClimateDto climate = climateRepository.get();
        Integer expectedTemperature = climate.getTemperature();

        climate.setTemperature(30);

        Integer actualTemperature = climateRepository.get().getTemperature();
        assertThat(actualTemperature, equalTo(expectedTemperature));
    }
}