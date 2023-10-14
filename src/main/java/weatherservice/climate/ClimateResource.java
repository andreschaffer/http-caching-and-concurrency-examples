package weatherservice.climate;


import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;

import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.EntityTag;
import jakarta.ws.rs.core.Request;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.ResponseBuilder;
import java.util.Optional;

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
