package uk.org.squirm3.engine.generator;

import junit.framework.Assert;

import org.fest.assertions.Assertions;
import org.junit.Test;

import uk.org.squirm3.model.type.def.RandomBuilderType;

public class AtomRandomizerTest {

    @Test
    public void shouldReturnRandomConfiguredTypeIntIdentifier() {
        Assert.assertEquals(0, new AtomRandomizer().getIntegerIdentifier(
                RandomBuilderType.RANDOM, "a"));
        Assert.assertEquals(1, new AtomRandomizer().getIntegerIdentifier(
                RandomBuilderType.RANDOM, "b"));
        Assert.assertEquals(2, new AtomRandomizer().getIntegerIdentifier(
                RandomBuilderType.RANDOM, "c"));
        Assert.assertEquals(3, new AtomRandomizer().getIntegerIdentifier(
                RandomBuilderType.RANDOM, "d"));
        Assert.assertEquals(4, new AtomRandomizer().getIntegerIdentifier(
                RandomBuilderType.RANDOM, "e"));
        Assert.assertEquals(5, new AtomRandomizer().getIntegerIdentifier(
                RandomBuilderType.RANDOM, "f"));
    }

    @Test
    public void shouldReturnRandomConfiguredTypeIntIdentifierInRange() {
        Assertions.assertThat(new AtomRandomizer().getIntegerIdentifier(
                RandomBuilderType.RANDOM, "def")).isGreaterThanOrEqualTo(3);
        Assertions.assertThat(new AtomRandomizer().getIntegerIdentifier(
                RandomBuilderType.RANDOM, "abc")).isLessThan(3);
    }

}
