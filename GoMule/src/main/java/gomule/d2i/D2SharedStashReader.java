package gomule.d2i;

import com.google.common.io.BaseEncoding;
import gomule.d2i.D2SharedStash.D2SharedStashPane;
import gomule.item.D2Item;
import gomule.util.D2BitReader;

import java.util.ArrayList;
import java.util.List;

public class D2SharedStashReader {

    private static final byte[] STASH_HEADER_START = BaseEncoding.base16().decode("55AA55AA");

    public D2SharedStash readStash(String filename) throws Exception {
        return readStash(filename, new D2BitReader(filename));
    }

    public D2SharedStash readStash(String filename, D2BitReader bitReader) throws Exception {
        int[] stashHeaderOffsets = bitReader.findBytes(STASH_HEADER_START);
        if (stashHeaderOffsets.length != 3) throw new RuntimeException("Stash unsupported");
        List<D2SharedStashPane> result = new ArrayList<>();
        for (int stashHeaderOffset : stashHeaderOffsets) {
            bitReader.set_byte_pos(stashHeaderOffset);
            result.add(readSharedStashPane(bitReader, filename));
        }
        return new D2SharedStash(filename, result, bitReader.getFileContent());
    }

    private D2SharedStashPane readSharedStashPane(D2BitReader bitReader, String filename) throws Exception {
        int stashPaneStart = bitReader.get_byte_pos();
        D2SharedStashHeader header = readHeader(bitReader);
        if (header.version != 97) throw new RuntimeException("Incorrect shared stash version: " + header.version);
        bitReader.set_byte_pos(bitReader.findNextFlag("JM", bitReader.get_byte_pos()));
        bitReader.skipBytes(2);
        int numItems = (int) bitReader.read(16);
        List<D2Item> result = new ArrayList<>();
        for (int i = 0; i < numItems; i++) {
            result.add(new D2Item(filename, bitReader, 75));
        }
        int calculatedLength = bitReader.get_byte_pos() - stashPaneStart;
        if (calculatedLength != header.length)
            throw new RuntimeException("Incorrect shared stash length: " + calculatedLength + " expected: " + header.length);
        return D2SharedStashPane.fromItems(result, header.gold);
    }

    private D2SharedStashHeader readHeader(D2BitReader bitReader) {
        bitReader.skipBytes(8);
        long version = bitReader.read(8);
        bitReader.skipBytes(3);
        long gold = bitReader.read(32);
        long length = bitReader.read(32);
        return new D2SharedStashHeader(version, gold, length);
    }

    private static class D2SharedStashHeader {
        private final long version;
        private final long gold;
        private final long length;

        public D2SharedStashHeader(long version, long gold, long length) {
            this.version = version;
            this.gold = gold;
            this.length = length;
        }
    }

}
