package weatherservice.climate;

import org.junit.Test;

import static java.util.Collections.singletonList;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;

public class ETagGeneratorTest {

    private ETagGenerator eTagGenerator = new ETagGenerator();

    @Test
    public void equalValueObjectsHaveEqualETags() throws Exception {
        assertThat(eTagGenerator.eTagFor("a"), equalTo(eTagGenerator.eTagFor("a")));
        assertThat(eTagGenerator.eTagFor(1), equalTo(eTagGenerator.eTagFor(1)));
        assertThat(eTagGenerator.eTagFor(true), equalTo(eTagGenerator.eTagFor(true)));
        assertThat(eTagGenerator.eTagFor(singletonList(1)), equalTo(eTagGenerator.eTagFor(singletonList(1))));
    }

    @Test
    public void differentValueObjectsHaveDifferentETags() throws Exception {
        assertThat(eTagGenerator.eTagFor("a"), not(equalTo(eTagGenerator.eTagFor("b"))));
        assertThat(eTagGenerator.eTagFor(1), not(equalTo(eTagGenerator.eTagFor(2))));
        assertThat(eTagGenerator.eTagFor(true), not(equalTo(eTagGenerator.eTagFor(false))));
        assertThat(eTagGenerator.eTagFor(singletonList(1)), not(equalTo(eTagGenerator.eTagFor(singletonList(3)))));
    }
}