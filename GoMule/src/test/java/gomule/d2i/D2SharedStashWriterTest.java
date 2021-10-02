package gomule.d2i;

import com.google.common.io.BaseEncoding;
import gomule.item.D2Item;
import gomule.util.D2BitReader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import randall.d2files.D2TblFile;
import randall.d2files.D2TxtFile;

import java.io.File;
import java.nio.file.Files;

import static gomule.d2i.D2SharedStash.D2SharedStashPane;
import static gomule.item.D2ItemTest.HEALTH_POT;
import static gomule.item.D2ItemTest.SMALL_CHARM;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class D2SharedStashWriterTest {

    private static final String EMPTY_STASH = "55AA55AA0000000061000000000000004400000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000004A4D0000";
    private static final String STASH_WITH_POTION = "55AA55AA0000000061000000000000004E00000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000004A4D01001004A0080588144FB400";
    private static final String STASH_WITH_POTION_AND_CHARM = "55AA55AA0000000061000000000000006400000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000004A4D02001004A0080588144FB40010008000059054D84FD88E840E0B50B00C00B4C8F90F";
    private static final String STASH_WITH_3_POTIONS = "55AA55AA0000000061000000000000006200000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000004A4D03001004A0080580144FB4001004A00805C0144FB4001004A0080500154FB400";

    @BeforeEach
    public void setup() {
        D2TxtFile.constructTxtFiles("./d2111");
        D2TblFile.readAllFiles("./d2111");
    }

    @Test
    public void writeReplacingSingleItem() throws Exception {
        byte[] simpleStash = BaseEncoding.base16().decode(EMPTY_STASH + EMPTY_STASH + EMPTY_STASH);
        D2SharedStash stash = new D2SharedStash("", asList(
                D2SharedStashPane.fromItems(emptyList(), 0),
                D2SharedStashPane.fromItems(singletonList(loadItem(HEALTH_POT)), 0),
                D2SharedStashPane.fromItems(emptyList(), 0)
        ), simpleStash);
        byte[] expected = BaseEncoding.base16().decode(EMPTY_STASH + STASH_WITH_POTION + EMPTY_STASH);
        runTest(simpleStash, stash, expected);
    }

    @Test
    public void writeItemsOfDifferentSizes() throws Exception {
        byte[] simpleStash = BaseEncoding.base16().decode(EMPTY_STASH + EMPTY_STASH + EMPTY_STASH);
        D2SharedStash stash = new D2SharedStash("", asList(
                D2SharedStashPane.fromItems(emptyList(), 0),
                D2SharedStashPane.fromItems(asList(loadItem(HEALTH_POT), loadItem(SMALL_CHARM, 4, 2)), 0),
                D2SharedStashPane.fromItems(emptyList(), 0)
        ), simpleStash);
        byte[] expected = BaseEncoding.base16().decode(EMPTY_STASH + STASH_WITH_POTION_AND_CHARM + EMPTY_STASH);
        runTest(simpleStash, stash, expected);
    }

    @Test
    public void writeReplacingMultipleItems() throws Exception {
        byte[] simpleStash = BaseEncoding.base16().decode(EMPTY_STASH + EMPTY_STASH + EMPTY_STASH);
        D2SharedStash stash = new D2SharedStash("", asList(
                D2SharedStashPane.fromItems(emptyList(), 0),
                D2SharedStashPane.fromItems(asList(loadItem(HEALTH_POT, 0, 2), loadItem(HEALTH_POT, 0, 3), loadItem(HEALTH_POT, 0, 4)), 0),
                D2SharedStashPane.fromItems(emptyList(), 0)
        ), simpleStash);
        byte[] expected = BaseEncoding.base16().decode(EMPTY_STASH + STASH_WITH_3_POTIONS + EMPTY_STASH);
        runTest(simpleStash, stash, expected);
    }

    @Test
    public void writeReplacingAllItems() throws Exception {
        byte[] simpleStash = BaseEncoding.base16().decode(EMPTY_STASH + EMPTY_STASH + EMPTY_STASH);
        D2SharedStash stash = new D2SharedStash("", asList(
                D2SharedStashPane.fromItems(singletonList(loadItem(HEALTH_POT)), 0),
                D2SharedStashPane.fromItems(singletonList(loadItem(HEALTH_POT)), 0),
                D2SharedStashPane.fromItems(singletonList(loadItem(HEALTH_POT)), 0)
        ), simpleStash);
        byte[] expected = BaseEncoding.base16().decode(STASH_WITH_POTION + STASH_WITH_POTION + STASH_WITH_POTION);
        runTest(simpleStash, stash, expected);
    }

    private void runTest(byte[] originalStashBytes, D2SharedStash stash, byte[] expected) throws Exception {
        File tempFile = File.createTempFile("d2SharedStashWriterTest", null);
        D2SharedStashWriter writer = new D2SharedStashWriter(tempFile, originalStashBytes);
        writer.write(stash);
        byte[] actual = Files.readAllBytes(tempFile.toPath());
        assertArrayEquals(expected, actual);
        D2SharedStash readBackStash = new D2SharedStashReader().readStash("foo", new D2BitReader(actual));
        assertEquals(stash.getPanes().size(), readBackStash.getPanes().size());
        for (int i = 0; i < stash.getPanes().size(); i++) {
            D2SharedStashPane pane = stash.getPane(i);
            D2SharedStashPane readBackPane = readBackStash.getPane(i);
            assertEquals(pane.getGold(), readBackPane.getGold());
            assertEquals(pane.getItems().size(), readBackPane.getItems().size());
            for (int j = 0; j < pane.getItems().size(); j++) {
                assertArrayEquals(pane.getItems().get(j).get_bytes(), readBackPane.getItems().get(j).get_bytes());
            }
        }
    }

    private D2Item loadItem(byte[] b) throws Exception {
        return loadItem(b, 2, 2);
    }

    private D2Item loadItem(byte[] b, int col, int row) throws Exception {
        D2Item item = new D2Item("foo", new D2BitReader(b), 75);
        item.set_col((short) col);
        item.set_row((short) row);
        item.set_location((short) 0);
        item.set_body_position((short) 0);
        item.set_panel((short) 5);
        return item;
    }
}