package weatherservice.climate;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

import java.util.Optional;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.EntityTag;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
@Path("/climates/stockholm/now")
public class ClimateResource {

  private final ClimateRepository stockholmClimateRepository;
  private final Object transactionLock;
  private final ETagGenerator etagGenerator;

  public ClimateResource(ClimateRepository climateRepository) {
    this.stockholmClimateRepository = climateRepository;
    this.transactionLock = new Object();
    this.etagGenerator = new ETagGenerator();
  }

  @GET
  public Response get(@Context Request request) {
    ClimateDto currentClimate = stockholmClimateRepository.get();
    EntityTag currentETag = etagGenerator.etagFor(currentClimate);

    Optional<Response> notModifiedResponse = evaluateETagPrecondition(request, currentETag);
    return notModifiedResponse.orElse(Response.ok(currentClimate).tag(currentETag).build());
  }

  @PUT
  public Response put(@Context Request request, @NotNull ClimateDto climate) {
    synchronized (transactionLock) {
      ClimateDto currentClimate = stockholmClimateRepository.get();
      EntityTag currentETag = etagGenerator.etagFor(currentClimate);

      Optional<Response> preconditionFailedResponse = evaluateETagPrecondition(request,
          currentETag);
      if (preconditionFailedResponse.isPresent()) {
        return preconditionFailedResponse.get();
      }

      stockholmClimateRepository.save(climate);
    }

    EntityTag etag = etagGenerator.etagFor(climate);
    return Response.noContent().tag(etag).build();
  }

  private Optional<Response> evaluateETagPrecondition(Request request, EntityTag currentETag) {
    ResponseBuilder notModifiedResponseBuilder = request.evaluatePreconditions(currentETag);
    return Optional.ofNullable(notModifiedResponseBuilder).map(ResponseBuilder::build);
  }
}
