package gomule.d2x;

import gomule.model.VersionController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import randall.d2files.D2TxtFile;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;

import static gomule.item.D2ItemTest.*;
import static gomule.util.TestHelpers.loadItem;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class D2StashWriterTest {

    private static final String EMPTY_STASH_EXPANSION = "443258000069000200C0640700";
    private static final String EMPTY_STASH_ROW = "443258000069000300E0640700";
    private static final String STASH_WITH_POTION = "44325801006900020064829B1D1004A0080588144FB400";
    private static final String STASH_WITH_POTION_AND_CHARM="4432580200690002007BCE1FA01004A0080588144FB40010008000059054D84FD88E840E0B50B00C0068F1EC3F";

    @BeforeEach
    public void setup() {
        D2TxtFile.constructTxtFiles("./d2111");
    }

    @Test
    public void emptyStashRoundTrip() throws Exception {
        runTest(createStash(VersionController.Variant.EXPANSION), decode(EMPTY_STASH_EXPANSION), VersionController.Variant.EXPANSION);
        runTest(createStash(VersionController.Variant.ROW), decode(EMPTY_STASH_ROW), VersionController.Variant.ROW);
    }

    private static D2Stash createStash(VersionController.Variant variant) {
        return new D2Stash(variant, "test.d2x", new ArrayList<>(), false, false, false);
    }

    @Test
    public void potionStashRoundTrip() throws Exception {
        D2Stash stash = createStash(VersionController.Variant.EXPANSION);
        stash.addItem(loadItem(HEALTH_POT));
        runTest(stash, decode(STASH_WITH_POTION), VersionController.Variant.EXPANSION);
    }

    @Test
    public void potionAndCharmStashRoundTrip() throws Exception {
        D2Stash stash = createStash(VersionController.Variant.EXPANSION);
        stash.addItem(loadItem(HEALTH_POT));
        stash.addItem(loadItem(SMALL_CHARM, 4, 2));
        runTest(stash, decode(STASH_WITH_POTION_AND_CHARM), VersionController.Variant.EXPANSION);
    }

    @Test
    public void potionAndCharmStashRoundTrip_oneAtATime() throws Exception {
        D2Stash stash = createStash(VersionController.Variant.EXPANSION);
        stash.addItem(loadItem(HEALTH_POT));
        runTest(stash, decode(STASH_WITH_POTION), VersionController.Variant.EXPANSION);
        stash.addItem(loadItem(SMALL_CHARM, 4, 2));
        runTest(stash, decode(STASH_WITH_POTION_AND_CHARM), VersionController.Variant.EXPANSION);
    }

    @Test
    public void emptyingAStash() throws Exception {
        D2Stash stash = createStash(VersionController.Variant.EXPANSION);
        stash.addItem(loadItem(HEALTH_POT));
        stash.addItem(loadItem(SMALL_CHARM, 4, 2));
        runTest(stash, decode(STASH_WITH_POTION_AND_CHARM), VersionController.Variant.EXPANSION);
        stash.removeAllItems();
        runTest(stash, decode(EMPTY_STASH_EXPANSION), VersionController.Variant.EXPANSION);

    }

    private void runTest(D2Stash stash, byte[] expected, VersionController.Variant variant) throws Exception {
        File tempFile = File.createTempFile("d2SharedStashWriterTest", ".d2x");
        D2StashWriter writer = new D2StashWriter(variant, tempFile);
        writer.write(stash);
        byte[] actual = Files.readAllBytes(tempFile.toPath());
        assertArrayEquals(expected, actual, "\nExpected:\n" + encodeWithSpaces(expected) + "\nActual:\n" + encodeWithSpaces(actual));
        D2Stash readBackStash = new D2StashReader().readStash(variant, tempFile.getAbsolutePath());
        assertEquals(stash.getNrItems(), readBackStash.getNrItems());
        for (int j = 0; j < stash.getNrItems(); j++) {
            assertArrayEquals(
                    stash.getItemList().get(j).get_bytes(),
                    readBackStash.getItemList().get(j).get_bytes());
        }
    }
}
