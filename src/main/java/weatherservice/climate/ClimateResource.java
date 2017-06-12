package weatherservice.climate;

import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.EntityTag;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import java.util.Optional;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
@Path("/climates/stockholm/now")
public class ClimateResource {

    private final ClimateRepository stockholmClimateRepository;
    private final Object transactionLock;
    private final ETagGenerator eTagGenerator;

    public ClimateResource(ClimateRepository climateRepository) {
        this.stockholmClimateRepository = climateRepository;
        this.transactionLock = new Object();
        this.eTagGenerator = new ETagGenerator();
    }

    @GET
    public Response get(@Context Request request) {
        ClimateDto currentClimate = stockholmClimateRepository.get();
        EntityTag currentETag = eTagGenerator.eTagFor(currentClimate);

        Optional<Response> notModifiedResponse = evaluateETagPrecondition(request, currentETag);
        if (notModifiedResponse.isPresent()) return notModifiedResponse.get();

        return Response.ok(currentClimate).tag(currentETag).build();
    }

    @PUT
    public Response put(@Context Request request, @NotNull ClimateDto climate) {
        synchronized (transactionLock) {
            ClimateDto currentClimate = stockholmClimateRepository.get();
            EntityTag currentETag = eTagGenerator.eTagFor(currentClimate);

            Optional<Response> preconditionFailedResponse = evaluateETagPrecondition(request, currentETag);
            if (preconditionFailedResponse.isPresent()) return preconditionFailedResponse.get();

            stockholmClimateRepository.save(climate);
        }

        EntityTag eTag = eTagGenerator.eTagFor(climate);
        return Response.noContent().tag(eTag).build();
    }

    private Optional<Response> evaluateETagPrecondition(Request request, EntityTag currentETag) {
        ResponseBuilder notModifiedResponseBuilder = request.evaluatePreconditions(currentETag);
        return Optional.ofNullable(notModifiedResponseBuilder).map(ResponseBuilder::build);
    }
}
