package weatherservice.it;

import static javax.ws.rs.core.HttpHeaders.CACHE_CONTROL;
import static javax.ws.rs.core.HttpHeaders.EXPIRES;
import static javax.ws.rs.core.UriBuilder.fromUri;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;

import com.fasterxml.jackson.databind.node.ObjectNode;
import java.net.URI;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import javax.ws.rs.core.Response;
import org.junit.jupiter.api.Test;

class DaylightIT extends BaseIT {

  @Test
  void returnDaylightWithCacheControls() {
    Response response = client.target(daylightUrl()).request().get();

    ObjectNode entity = response.readEntity(ObjectNode.class);
    assertThat(entity.get("sunrise").asText(), equalTo("06:00+02:00"));
    assertThat(entity.get("sunset").asText(), equalTo("18:00+02:00"));
    assertThat(response.getStatus(), equalTo(200));
    assertThat(response.getHeaderString(CACHE_CONTROL),
        allOf(containsString("no-transform"), containsString("max-age=86400")));
    assertThat(response.getHeaderString(EXPIRES), equalTo(today() + " 23:59:59 GMT"));
  }

  private String today() {
    ZoneOffset stockholmOffset = ZoneOffset.of("+02:00");
    DateTimeFormatter httpDateFormat = DateTimeFormatter.ofPattern("EEE, dd MMM yyyy");
    return LocalDate.now(stockholmOffset).format(httpDateFormat);
  }

  private URI daylightUrl() {
    return fromUri("http://localhost").port(SERVICE.getLocalPort())
        .path("daylights/stockholm/today").build();
  }
}
