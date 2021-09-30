package gomule.item;

import com.google.common.collect.ImmutableMap;
import com.google.common.io.BaseEncoding;
import gomule.util.D2BitReader;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

public class HuffmanLookupTableTest {

    @Test
    public void test() {
        HuffmanLookupTable huffmanLookupTable = new HuffmanLookupTable(ImmutableMap.of("1111", 'a', "0000", 'b', "01010101", ' '));
        assertEquals(Character.valueOf('a'), huffmanLookupTable.lookupBinarySequence("1111"));
        assertEquals(Character.valueOf('b'), huffmanLookupTable.lookupBinarySequence("0000"));
        assertEquals(Character.valueOf(' '), huffmanLookupTable.lookupBinarySequence("01010101"));
        assertNull(huffmanLookupTable.lookupBinarySequence("something-strange"));
        assertEquals("ab", huffmanLookupTable.readHuffmanEncodedString(new D2BitReader(BaseEncoding.base16().decode("0FAA"))));
    }

}