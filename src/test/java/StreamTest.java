import io.vavr.control.Option;
import org.junit.Ignore;
import org.junit.Test;

import io.vavr.collection.Iterator;
import io.vavr.collection.LinearSeq;
import io.vavr.collection.Stream;
import io.vavr.collection.Traversable;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class StreamTest {

    private static final int SIZE = (int)1E7;

    @Test
    public void simplestIteratorScalingTest() {
        assertThat(process(naturalsIterator(), SIZE), is(SIZE+1));
    }

    @Test
    public void arbitrarilyComplexIteratorScalingTest() {
        assertThat(process(roundTripToStringAndBack(evenNaturalsIterator()), SIZE), is(2 * (SIZE +1)));
    }

    // vavr Iterator is NOT immutable
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


    @Test
    public void compareIteratorsRecursivelyTest() {
        assertThat(compareRecursively( naturalsIterator().take(4),         naturalsIterator().take(4) ), is(0));
        assertThat(compareRecursively( naturalsIterator().take(4),         naturalsIterator().take(3) ), is(1));
        assertThat(compareRecursively( naturalsIterator().drop(1).take(4), naturalsIterator().take(4) ), is(1));
        assertThat(compareRecursively( naturalsIterator().take(3),         naturalsIterator().take(4) ), is(-1));
        assertThat(compareRecursively( naturalsIterator().take(4),         naturalsIterator().drop(1).take(4) ), is(-1));
    }

    /*
     Stack-consuming comparison of two vavr "sequences" (Traversables)
     */
    public <T extends Comparable<T>> int compareRecursively(final Traversable<T> at, final Traversable<T> bt) {
        final Option<T> ao = at.headOption();
        final Option<T> bo = bt.headOption();
        return ao.map( a ->
                bo.map( b -> {
                    final int comparison = a.compareTo(b);
                    // not compareRecursively(at.tail(),bt.tail()) because at, bt were advanced (mutated)!
                    return comparison == 0 ? compareRecursively(at,bt) : comparison;
                }).getOrElse(1) // at is longer
        ).getOrElse(()->bo.map( b -> -1) // bt is longer
                .getOrElse(0)); // at, bt are equal
    }

    @Test
    public void compareIteratorsLoopilyTest() {
        assertThat(compareLoopily( naturalsIterator().take(4),         naturalsIterator().take(4) ), is(0));
        assertThat(compareLoopily( naturalsIterator().take(4),         naturalsIterator().take(3) ), is(1));
        assertThat(compareLoopily( naturalsIterator().drop(1).take(4), naturalsIterator().take(4) ), is(1));
        assertThat(compareLoopily( naturalsIterator().take(3),         naturalsIterator().take(4) ), is(-1));
        assertThat(compareLoopily( naturalsIterator().take(4),         naturalsIterator().drop(1).take(4) ), is(-1));
    }

    @Test
    public void compareIteratorsLoopilyScalingTest() {
        assertThat(compareLoopily( naturalsIterator().take(SIZE),      naturalsIterator().take(SIZE) ), is(0));
        assertThat(compareLoopily( naturalsIterator().take(SIZE),      naturalsIterator().take(SIZE-1) ), is(1));
        assertThat(compareLoopily( naturalsIterator().take(SIZE-1),    naturalsIterator().take(SIZE) ),   is(-1));
    }

    /*
     Looping (not stack-consuming) comparison of two vavr "sequences" (Traversables)

     Compare this method to the looping Ruby one:

       https://github.com/Bill/enumerator-comparable/blob/master/lib/enumerator_comparable/initializers/enumerator_comparable.rb#L14

     See how we use 2 here as the sentinel value to continue iterating whereas in the Ruby
     code we used nil.

     See how the Option monad takes the place of the Ruby array returned by try_iter(&block)
     */
    public static final int CONTINUE_SENTINEL = 2; // int that is none of: 1, 0, -1
    public <T extends Comparable<T>> int compareLoopily(final Traversable<T> at, final Traversable<T> bt) {
        while(true) {
            final Option<T> ao = at.headOption();
            final Option<T> bo = bt.headOption();

            final int compare = ao.map(a ->
                    bo.map(b -> {
                        final int comparison = a.compareTo(b);
                        // Not we use Option.some(): null means we have more work to do...
                        return comparison == 0 ? CONTINUE_SENTINEL : comparison;
                    }).getOrElse(1) // at is longer
            ).getOrElse(() -> bo.map(b -> -1) // bt is longer
                    .getOrElse(0));// at, bt are equal

            if (compare == CONTINUE_SENTINEL)
                continue;
            return compare;
        }
    }

}
