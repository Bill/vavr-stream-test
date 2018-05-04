import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import io.vavr.collection.Stream;

public class StreamTest {

    @Test
    public void first() {
        final Stream<Integer> s = Stream.iterate(1, i->i+1);
        final int SIZE = (int)10E7;
        assertEquals(SIZE, s.drop(SIZE-1).head().intValue());
    }
}
