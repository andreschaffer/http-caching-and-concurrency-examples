package weatherservice.climate;


import jakarta.validation.constraints.NotNull;

public class ClimateDto {

  @NotNull
  private Integer temperature;

  @NotNull
  private Integer humidity;

  public Integer getTemperature() {
    return temperature;
  }

  public void setTemperature(Integer temperature) {
    this.temperature = temperature;
  }

  public Integer getHumidity() {
    return humidity;
  }

  public void setHumidity(Integer humidity) {
    this.humidity = humidity;
  }
}
