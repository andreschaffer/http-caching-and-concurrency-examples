package weatherservice.daylight;

import java.time.OffsetTime;

public class DaylightDto {

    private OffsetTime sunrise;
    private OffsetTime sunset;

    public OffsetTime getSunrise() {
        return sunrise;
    }

    public void setSunrise(OffsetTime sunrise) {
        this.sunrise = sunrise;
    }

    public OffsetTime getSunset() {
        return sunset;
    }

    public void setSunset(OffsetTime sunset) {
        this.sunset = sunset;
    }
}
