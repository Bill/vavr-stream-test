import org.junit.Ignore;
import org.junit.Test;

import io.vavr.collection.Iterator;
import io.vavr.collection.LinearSeq;
import io.vavr.collection.Stream;
import io.vavr.collection.Traversable;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class StreamTest {

    private static final int SIZE = (int)1E8;

    @Test
    public void simplestIteratorScalingTest() {
        assertThat(process(naturalsIterator(), SIZE), is(SIZE+1));
    }

    @Test
    public void arbitrarilyComplexIteratorScalingTest() {
        assertThat(process(roundTripToStringAndBack(evenNaturalsIterator()), SIZE), is(2 * (SIZE +1)));
    }

    @Ignore // because vavr Iterator is NOT immutable
    @Test
    public void iteratorImmutabilityTest() {
        final Iterator<Integer> sixPlus = naturalsIterator().drop(5);
        assertThat(sixPlus.drop(5).head(), is(11));
        assertThat(sixPlus.drop(5).head(), is(11)); // FAILS!!!!
    }

    @Ignore // because vavr Stream does not discard head, so memory consumption increases as you iterate!
    @Test
    public void simplestStreamScalingTest() {
        assertThat(process(naturalsStream(), SIZE), is(SIZE+1));
    }

    @Ignore // (inlining, unsurprisingly, doesn't help)
    @Test
    public void simplestStreamScalingTestInlined() {
        assertThat(Stream.iterate(1, i -> i + 1).drop(SIZE).head(), is(SIZE+1));
    }

    @Ignore // because vavr Stream does not discard head, so memory consumption increases as you iterate!
    @Test
    public void arbitrarilyComplexStreamScalingTest() {
        assertThat(process(roundTripToStringAndBack(evenNaturalsStream()), SIZE), is(2 * (SIZE +1)));
    }

    private static Stream<Integer> naturalsStream() {
        return Stream
                .iterate(1, i->i+1);
    }

    private static Iterator<Integer> naturalsIterator() {
        return Iterator.iterate(1, i-> i + 1);
    }

    private static Iterator<Integer> evenNaturalsIterator() {
        return naturalsIterator()
                .filter(StreamTest::isEven);
    }

    private static Stream<Integer> evenNaturalsStream() {
        return naturalsStream()
                .filter(StreamTest::isEven);
    }

    private static boolean isEven(final int i) {
        return i % 2 == 0;
    }


    private static Iterator<Integer> roundTripToStringAndBack(final Iterator<Integer> integers) {
        return integers
                .map(Object::toString)
                .map(Integer::parseInt);
    }

    private static LinearSeq<Integer> roundTripToStringAndBack(final LinearSeq<Integer> integers) {
        return integers
                .map(Object::toString)
                .map(Integer::parseInt);
    }


    private static int process(final Traversable<Integer> traversable, final int size) {
        return traversable.drop(size).head();
    }

}
