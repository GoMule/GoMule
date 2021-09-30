package gomule.d2i;

import com.google.common.io.BaseEncoding;
import gomule.util.D2BitReader;
import org.junit.jupiter.api.Test;
import randall.d2files.D2TblFile;
import randall.d2files.D2TxtFile;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.*;

public class D2SharedStashReaderTest {

    @Test
    public void simpleStash() throws Exception {
        D2TxtFile.constructTxtFiles("./d2111");
        D2TblFile.readAllFiles("./d2111");
        byte[] simpleStash = BaseEncoding.base16().decode("55AA55AA0000000061000000000000004D00000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000004A4D01001000A2000564D6900855AA55AA0000000061000000000000004D00000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000004A4D01001000A2000564F6472255AA55AA0000000061000000000000004400000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000004A4D0000");
        D2SharedStash stash = new D2SharedStashReader().readStash("somethingSoftCore.d2i", new D2BitReader(simpleStash));
        assertTrue(stash.isSC());
        assertFalse(stash.isHC());
        assertEquals(singletonList("Scroll of Town Portal\n" +
                "Version: Resurrected\n"), getItemDumps(stash.getPane(0)));
        assertEquals(singletonList("Scroll of Identify\n" +
                "Version: Resurrected\n"), getItemDumps(stash.getPane(1)));
        assertEquals(emptyList(), getItemDumps(stash.getPane(2)));
        StringWriter out = new StringWriter();
        stash.fullDump(new PrintWriter(out));
        assertEquals("somethingSoftCore.d2i\n" +
                "\n" +
                "\n" +
                "Scroll of Town Portal\n" +
                "Version: Resurrected\n" +
                "\n" +
                "Scroll of Identify\n" +
                "Version: Resurrected\n" +
                "Finished: somethingSoftCore.d2i\n\n", out.toString().replaceAll("\r", ""));
    }

    private List<String> getItemDumps(D2SharedStash.D2SharedStashPane pane) {
        return pane.getItems().stream().map(it -> it.itemDump(true).replace("\r", "")).collect(Collectors.toList());
    }
}