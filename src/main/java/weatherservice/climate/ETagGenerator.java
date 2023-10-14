package weatherservice.climate;

import jakarta.ws.rs.core.EntityTag;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class ETagGenerator {

  public EntityTag etagFor(Object o) {
    int hashCode = HashCodeBuilder.reflectionHashCode(o);
    return new EntityTag(Integer.toString(hashCode));
  }
}
