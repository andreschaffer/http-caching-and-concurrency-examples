package weatherservice.it;

import static javax.ws.rs.client.Entity.json;
import static javax.ws.rs.core.HttpHeaders.ETAG;
import static javax.ws.rs.core.HttpHeaders.IF_MATCH;
import static javax.ws.rs.core.HttpHeaders.IF_NONE_MATCH;
import static javax.ws.rs.core.Response.Status.NOT_MODIFIED;
import static javax.ws.rs.core.Response.Status.NO_CONTENT;
import static javax.ws.rs.core.Response.Status.OK;
import static javax.ws.rs.core.Response.Status.PRECONDITION_FAILED;
import static javax.ws.rs.core.UriBuilder.fromUri;
import static org.hamcrest.CoreMatchers.both;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import com.fasterxml.jackson.databind.node.ObjectNode;
import java.net.URI;
import javax.ws.rs.core.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ClimateIT extends BaseIT {

  @BeforeEach
  void setUp() {
    climateForceUpdate(10, 80);
  }

  @Test
  void returnClimateWithETag() {
    Response response = client.target(climateUrl()).request().get();
    ObjectNode entity = response.readEntity(ObjectNode.class);
    assertThat(entity.get("temperature").asInt(), equalTo(10));
    assertThat(entity.get("humidity").asInt(), equalTo(80));
    assertThat(response.getStatus(), equalTo(OK.getStatusCode()));
    assertThat(response.getHeaderString(ETAG), notNullValue());
  }

  @Test
  void returnNotModifiedWhenIfNoneMatchPreconditionIsFalse() {
    String etag = etagFromClimateGetRequest();
    Response response = client.target(climateUrl()).request().header(IF_NONE_MATCH, etag).get();
    response.close();
    assertThat(response.getStatus(), equalTo(NOT_MODIFIED.getStatusCode()));
  }

  @Test
  void returnClimateWhenIfNoneMatchPreconditionIsTrue() {
    String etag = etagFromClimateGetRequest();

    climateForceUpdate(30, 90);

    Response response = client.target(climateUrl()).request().header(IF_NONE_MATCH, etag).get();
    ObjectNode entity = response.readEntity(ObjectNode.class);
    assertThat(entity.get("temperature").asInt(), equalTo(30));
    assertThat(entity.get("humidity").asInt(), equalTo(90));
    assertThat(response.getStatus(), equalTo(OK.getStatusCode()));
    assertThat(response.getHeaderString(ETAG), notNullValue());
  }

  @Test
  void updateClimateWhenIfMatchPreconditionIsTrue() {
    String etag = etagFromClimateGetRequest();
    ObjectNode climate = climateDto(20, 75);
    Response response = client.target(climateUrl()).request().header(IF_MATCH, etag)
        .put(json(climate));
    response.close();
    assertThat(response.getStatus(), equalTo(NO_CONTENT.getStatusCode()));
    assertThat(response.getHeaderString(ETAG), both(notNullValue()).and(not(equalTo(etag))));
  }

  @Test
  void returnPreconditionFailedOnClimateUpdateWhenIfMatchPreconditionIsFalse() {
    String etag = etagFromClimateGetRequest();

    climateForceUpdate(25, 85);

    ObjectNode climate = climateDto(25, 75);
    Response response = client.target(climateUrl()).request().header(IF_MATCH, etag)
        .put(json(climate));
    response.close();
    assertThat(response.getStatus(), equalTo(PRECONDITION_FAILED.getStatusCode()));
  }

  private void climateForceUpdate(int temperature, int humidity) {
    ObjectNode climate = climateDto(temperature, humidity);
    Response response = client.target(climateUrl()).request().put(json(climate));
    response.close();
    assertThat(response.getStatus(), equalTo(NO_CONTENT.getStatusCode()));
  }

  private ObjectNode climateDto(int temperature, int humidity) {
    ObjectNode climate = SERVICE.getObjectMapper().createObjectNode();
    climate.put("temperature", temperature);
    climate.put("humidity", humidity);
    return climate;
  }

  private String etagFromClimateGetRequest() {
    Response response = client.target(climateUrl()).request().get();
    response.close();
    assertThat(response.getStatus(), equalTo(OK.getStatusCode()));
    return response.getHeaderString(ETAG);
  }

  private URI climateUrl() {
    return fromUri("http://localhost").port(SERVICE.getLocalPort()).path("climates/stockholm/now")
        .build();
  }
}
