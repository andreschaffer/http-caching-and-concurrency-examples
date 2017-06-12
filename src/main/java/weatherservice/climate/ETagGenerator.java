package weatherservice.climate;

import org.apache.commons.lang3.builder.HashCodeBuilder;

import javax.ws.rs.core.EntityTag;

public class ETagGenerator {

    public EntityTag eTagFor(Object o) {
        int hashCode = HashCodeBuilder.reflectionHashCode(o);
        return new EntityTag(Integer.toString(hashCode));
    }
}
