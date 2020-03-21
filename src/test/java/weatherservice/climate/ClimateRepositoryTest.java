package weatherservice.climate;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import org.junit.jupiter.api.Test;

class ClimateRepositoryTest {

  private ClimateRepository climateRepository = new ClimateRepository();

  @Test
  void returnSafeCopy() {
    ClimateDto climate = climateRepository.get();
    Integer expectedTemperature = climate.getTemperature();

    climate.setTemperature(30);

    Integer actualTemperature = climateRepository.get().getTemperature();
    assertThat(actualTemperature, equalTo(expectedTemperature));
  }
}