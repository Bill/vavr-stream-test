import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import io.vavr.collection.Stream;

public class StreamTest {

    @Test
    public void first() {
        final int SIZE = (int)10E7;
        assertEquals(2 * (SIZE +1), Stream
                .iterate(1, i->i+1)
                .filter(StreamTest::isEven)
                .map(Object::toString)
                .map(Integer::parseInt)
                .drop(SIZE)
                .head()
                .intValue());
    }

    private static boolean isEven(final int i) {
        return i % 2 == 0;
    }
}
