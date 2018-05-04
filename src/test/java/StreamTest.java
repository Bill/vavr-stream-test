import org.junit.Ignore;
import org.junit.Test;

import io.vavr.collection.LinearSeq;
import io.vavr.collection.Stream;
import io.vavr.collection.Traversable;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class StreamTest {

    private static final int SIZE = (int)1E1;

    @Test
    public void simplestTest() {
        assertThat(SIZE+1, is(process(naturals(), SIZE)));
    }

    private static int process(final Traversable<Integer> traversable, final int size) {
        return traversable.drop(size).head();
    }

    private static Stream<Integer> naturals() {
        return Stream
                .iterate(1, i->i+1);
    }

    @Ignore
    @Test
    public void arbitrarilyComplexTest() {
        assertThat(2 * (SIZE +1), is(process(roundTripToStringAndBack(evenNaturals()), SIZE)));
    }

    private static Stream<Integer> evenNaturals() {
        return naturals()
                .filter(StreamTest::isEven);
    }

    private static LinearSeq<Integer> roundTripToStringAndBack(final LinearSeq<Integer> integers) {
        return integers
                .map(Object::toString)
                .map(Integer::parseInt);
    }

    private static boolean isEven(final int i) {
        return i % 2 == 0;
    }
}
