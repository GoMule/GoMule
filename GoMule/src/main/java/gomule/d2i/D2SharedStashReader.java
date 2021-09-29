package gomule.d2i;

import gomule.d2i.D2SharedStash.D2SharedStashPane;
import gomule.item.D2Item;
import gomule.util.D2BitReader;

import java.util.ArrayList;
import java.util.List;

public class D2SharedStashReader {

    public D2SharedStash readStash(String filename) throws Exception {
        return readStash(filename, new D2BitReader(filename));
    }

    public D2SharedStash readStash(String filename, D2BitReader bitReader) throws Exception {
        List<D2SharedStashPane> result = new ArrayList<>();
        int nextItemBlock;
        while ((nextItemBlock = bitReader.findNextFlag("JM", bitReader.get_byte_pos())) != -1) {
            bitReader.set_byte_pos(nextItemBlock + 2);
            result.add(readSharedStashPane(bitReader, filename));
        }
        return new D2SharedStash(filename, result);
    }

    private D2SharedStashPane readSharedStashPane(D2BitReader bitReader, String filename) throws Exception {
        int numItems = (int) bitReader.read(16);
        List<D2Item> result = new ArrayList<>();
        for (int i = 0; i < numItems; i++) {
            result.add(new D2Item(filename, bitReader, 75));
        }
        return D2SharedStashPane.fromItems(result);
    }

}
