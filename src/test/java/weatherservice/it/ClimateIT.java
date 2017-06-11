package weatherservice.it;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.Before;
import org.junit.Test;

import javax.ws.rs.core.Response;
import java.net.URI;

import static javax.ws.rs.client.Entity.json;
import static javax.ws.rs.core.HttpHeaders.*;
import static javax.ws.rs.core.UriBuilder.fromUri;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

public class ClimateIT extends BaseIT {

    @Before
    public void setUp() throws Exception {
        climateForceUpdate(10, 80);
    }

    private void climateForceUpdate(int temperature, int humidity) {
        ObjectNode climate = climateDto(temperature, humidity);
        Response response = client.target(climateUrl()).request().put(json(climate));
        response.close();
        assertThat(response.getStatus(), equalTo(204));
    }

    private ObjectNode climateDto(int temperature, int humidity) {
        ObjectNode climate = SERVICE.getObjectMapper().createObjectNode();
        climate.put("temperature", temperature);
        climate.put("humidity", humidity);
        return climate;
    }

    @Test
    public void returnClimateWithETag() throws Exception {
        Response response = client.target(climateUrl()).request().get();
        ObjectNode entity = response.readEntity(ObjectNode.class);
        assertThat(entity.get("temperature").asInt(), equalTo(10));
        assertThat(entity.get("humidity").asInt(), equalTo(80));
        assertThat(response.getStatus(), equalTo(200));
        assertThat(response.getHeaderString(ETAG), notNullValue());
    }

    @Test
    public void returnNotModifiedWhenIfNoneMatchPreconditionIsFalse() throws Exception {
        String eTag;
        {
            Response response = client.target(climateUrl()).request().get();
            response.close();
            assertThat(response.getStatus(), equalTo(200));
            eTag = response.getHeaderString(ETAG);
        }
        Response response = client.target(climateUrl()).request().header(IF_NONE_MATCH, eTag).get();
        response.close();
        assertThat(response.getStatus(), equalTo(304));
    }

    @Test
    public void returnClimateWhenIfNoneMatchPreconditionIsTrue() throws Exception {
        String eTag;
        {
            Response response = client.target(climateUrl()).request().get();
            response.close();
            assertThat(response.getStatus(), equalTo(200));
            eTag = response.getHeaderString(ETAG);
        }

        climateForceUpdate(30, 90);

        Response response = client.target(climateUrl()).request().header(IF_NONE_MATCH, eTag).get();
        ObjectNode entity = response.readEntity(ObjectNode.class);
        assertThat(entity.get("temperature").asInt(), equalTo(30));
        assertThat(entity.get("humidity").asInt(), equalTo(90));
        assertThat(response.getStatus(), equalTo(200));
        assertThat(response.getHeaderString(ETAG), notNullValue());
    }

    @Test
    public void updateClimateWhenIfMatchPreconditionIsTrue() throws Exception {
        String eTag;
        {
            Response response = client.target(climateUrl()).request().get();
            response.close();
            assertThat(response.getStatus(), equalTo(200));
            eTag = response.getHeaderString(ETAG);
        }
        ObjectNode climate = climateDto(20, 75);
        Response response = client.target(climateUrl()).request().header(IF_MATCH, eTag).put(json(climate));
        response.close();
        assertThat(response.getStatus(), equalTo(204));
        assertThat(response.getHeaderString(ETAG), both(notNullValue()).and(not(equalTo(eTag))));
    }

    @Test
    public void returnPreconditionFailedOnClimateUpdateWhenIfMatchPreconditionIsFalse() throws Exception {
        String eTag;
        {
            Response response = client.target(climateUrl()).request().get();
            response.close();
            assertThat(response.getStatus(), equalTo(200));
            eTag = response.getHeaderString(ETAG);
        }

        climateForceUpdate(25, 85);

        ObjectNode climate = climateDto(25, 75);
        Response response = client.target(climateUrl()).request().header(IF_MATCH, eTag).put(json(climate));
        response.close();
        assertThat(response.getStatus(), equalTo(412));
    }

    private URI climateUrl() {
        return fromUri("http://localhost").port(SERVICE.getLocalPort()).path("climates/stockholm/now").build();
    }
}
