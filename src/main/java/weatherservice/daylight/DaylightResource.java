package weatherservice.daylight;

import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static java.time.ZoneOffset.UTC;
import static java.util.concurrent.TimeUnit.DAYS;

import io.dropwizard.jersey.caching.CacheControl;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Response;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetTime;
import java.time.ZoneOffset;
import java.util.Date;

@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
@Path("/daylights/stockholm/today")
public class DaylightResource {

  private static final ZoneOffset STOCKHOLM_OFFSET = ZoneOffset.of("+02:00");

  @GET
  @CacheControl(maxAge = 1, maxAgeUnit = DAYS)
  public Response get() {
    DaylightDto daylight = new DaylightDto();
    daylight.setSunrise(OffsetTime.of(6, 0, 0, 0, STOCKHOLM_OFFSET));
    daylight.setSunset(OffsetTime.of(18, 0, 0, 0, STOCKHOLM_OFFSET));
    return Response.ok(daylight).expires(endOfTheDayUtcTime()).build();
  }

  @SuppressWarnings("JdkObsolete")
  private Date endOfTheDayUtcTime() {
    LocalDate today = LocalDate.now(STOCKHOLM_OFFSET);
    LocalTime endOfTheDayTime = LocalTime.MAX;
    return Date.from(LocalDateTime.of(today, endOfTheDayTime).toInstant(UTC));
  }
}
