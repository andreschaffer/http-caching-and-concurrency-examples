package weatherservice.daylight;

import static java.time.ZoneOffset.UTC;
import static java.util.concurrent.TimeUnit.DAYS;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

import io.dropwizard.jersey.caching.CacheControl;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetTime;
import java.time.ZoneOffset;
import java.util.Date;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

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

  private Date endOfTheDayUtcTime() {
    LocalDate today = LocalDate.now(STOCKHOLM_OFFSET);
    LocalTime endOfTheDayTime = LocalTime.MAX;
    return Date.from(LocalDateTime.of(today, endOfTheDayTime).toInstant(UTC));
  }
}
