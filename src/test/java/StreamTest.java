import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import io.vavr.collection.Stream;

public class StreamTest {

    @Test
    public void first() {
        final int SIZE = (int)10E8;
        assertEquals(SIZE, Stream.iterate(1, i->i+1).drop(SIZE - 1).head().intValue());
    }
}
