package gomule.util;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class D2BitReaderTest {

    @Test
    public void getNextByteBoundaryInBits() {
        D2BitReader d2BitReader = new D2BitReader(new byte[]{1, 2, 3});
        assertEquals(0, d2BitReader.getNextByteBoundaryInBits());
        d2BitReader.read(1);
        assertEquals(8, d2BitReader.getNextByteBoundaryInBits());
        d2BitReader.read(1);
        assertEquals(8, d2BitReader.getNextByteBoundaryInBits());
        d2BitReader.read(6);
        assertEquals(8, d2BitReader.getNextByteBoundaryInBits());
        d2BitReader.read(1);
        assertEquals(16, d2BitReader.getNextByteBoundaryInBits());
    }
}