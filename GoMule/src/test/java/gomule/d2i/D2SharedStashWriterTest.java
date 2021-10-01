package gomule.d2i;

import com.google.common.io.BaseEncoding;
import gomule.item.D2Item;
import gomule.util.D2BitReader;
import org.junit.jupiter.api.Test;
import randall.d2files.D2TblFile;
import randall.d2files.D2TxtFile;

import java.io.File;
import java.nio.file.Files;

import static gomule.d2i.D2SharedStash.D2SharedStashPane;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.*;

public class D2SharedStashWriterTest {

    @Test
    public void writeReplacingAll() throws Exception {
        D2TxtFile.constructTxtFiles("./d2111");
        D2TblFile.readAllFiles("./d2111");
        byte[] simpleStash = BaseEncoding.base16().decode("55AA55AA0000000061000000000000004D00000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000004A4D01001000A2000564D6900855AA55AA0000000061000000000000004D00000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000004A4D01001000A2000564F6472255AA55AA0000000061000000000000004400000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000004A4D0000");
        D2Item healthPot = loadItem(new byte[]{16, 4, -96, 8, 21, 0, 0, 79, -76, 0});
        File tempFile = File.createTempFile("d2SharedStashWriterTest", null);
        D2SharedStashWriter writer = new D2SharedStashWriter(tempFile, simpleStash);
        writer.write(new D2SharedStash("myfile.foo", asList(
                D2SharedStashPane.fromItems(emptyList(), 0),
                D2SharedStashPane.fromItems(singletonList(healthPot), 0),
                D2SharedStashPane.fromItems(emptyList(), 0)
        ), simpleStash));//TODO there's some kind of a checksum in the stash header preceeding each stash. Don't understand it.
        byte[] expected = BaseEncoding.base16().decode("55AA55AA0000000061000000000000004D00000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000004A4D00004A4D01001004A0080588144FB4004A4D0000");
        assertArrayEquals(expected, Files.readAllBytes(tempFile.toPath()));
    }

    private D2Item loadItem(byte[] b) throws Exception {
        D2Item item = new D2Item("foo", new D2BitReader(b), 75);
        item.set_col((short) 2);
        item.set_row((short) 2);
        item.set_location((short) 0);
        item.set_body_position((short) 0);
        item.set_panel((short) 5);
        return item;
    }

}