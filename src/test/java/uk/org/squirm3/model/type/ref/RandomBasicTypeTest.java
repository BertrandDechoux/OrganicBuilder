package uk.org.squirm3.model.type.ref;

import org.junit.Test;

import uk.org.squirm3.model.type.def.BasicType;
import uk.org.squirm3.model.type.def.RandomBasicType;
import static org.fest.assertions.Assertions.assertThat;

public class RandomBasicTypeTest {

    @Test
    public void shouldExistOnePerBasicType() {
        assertThat(RandomBasicType.values().length).isEqualTo(
                BasicType.values().length);
    }
}
