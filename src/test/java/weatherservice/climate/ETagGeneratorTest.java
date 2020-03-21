package weatherservice.climate;

import static java.util.Collections.singletonList;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.jupiter.api.Test;

class ETagGeneratorTest {

  private ETagGenerator etagGenerator = new ETagGenerator();

  @Test
  void equalValueObjectsHaveEqualETags() {
    assertThat(etagGenerator.etagFor("a"), equalTo(etagGenerator.etagFor("a")));
    assertThat(etagGenerator.etagFor(1), equalTo(etagGenerator.etagFor(1)));
    assertThat(etagGenerator.etagFor(true), equalTo(etagGenerator.etagFor(true)));
    assertThat(etagGenerator.etagFor(singletonList(1)),
        equalTo(etagGenerator.etagFor(singletonList(1))));
  }

  @Test
  void differentValueObjectsHaveDifferentETags() {
    assertThat(etagGenerator.etagFor("a"), not(equalTo(etagGenerator.etagFor("b"))));
    assertThat(etagGenerator.etagFor(1), not(equalTo(etagGenerator.etagFor(2))));
    assertThat(etagGenerator.etagFor(true), not(equalTo(etagGenerator.etagFor(false))));
    assertThat(etagGenerator.etagFor(singletonList(1)),
        not(equalTo(etagGenerator.etagFor(singletonList(3)))));
  }
}