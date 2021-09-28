package gomule.d2i;

import gomule.item.D2Item;
import gomule.util.D2BitReader;

import java.util.ArrayList;
import java.util.List;

public class D2SharedStashReader {

    public D2SharedStash readStash(byte[] bytes) throws Exception {
        List<List<D2Item>> result = new ArrayList<>();
        D2BitReader bitReader = new D2BitReader(bytes);
        int nextItemBlock;
        while ((nextItemBlock = bitReader.findNextFlag("JM", bitReader.get_byte_pos())) != -1) {
            bitReader.set_byte_pos(nextItemBlock + 2);
            result.add(readSharedStashPane(bitReader));
        }
        result.forEach(it -> it.forEach(item -> System.out.println(item.itemDump(true))));
        return null;
    }

    private List<D2Item> readSharedStashPane(D2BitReader bitReader) throws Exception {
        int numItems = (int) bitReader.read(16);
        List<D2Item> result = new ArrayList<>();
        for (int i = 0; i < numItems; i++) {
            result.add(new D2Item("foo", bitReader, 75));
        }
        return result;
    }

}
