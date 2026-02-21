package gomule.d2x;

import gomule.item.D2Item;
import gomule.model.VersionController;
import gomule.model.VersionController.Variant;
import gomule.util.D2BitReader;

import java.io.File;
import java.util.ArrayList;

import static gomule.d2x.D2Stash.FIXED_STASH_CHAR_LEVEL;
import static gomule.d2x.D2StashWriter.*;
import static gomule.model.VersionController.Variant.tryParseStashIdentifier;
import static gomule.model.VersionController.Version.D2R3;

public class D2StashReader {
    public D2Stash readStash(Variant variant, String filename) {
        return readStash(variant, filename, new D2BitReader(filename));
    }

    public D2Stash readStash(Variant variant, String filename, D2BitReader bitReader) {
        if (filename == null || !filename.toLowerCase().endsWith(".d2x")) {
            throw new RuntimeException("Incorrect Stash file name");
        }
        File file = new File(filename);
        boolean iSC = file.getName().toLowerCase().startsWith("sc_");
        boolean iHC = file.getName().toLowerCase().startsWith("hc_");
        if (!iSC && !iHC) {
            iSC = true;
            iHC = true;
        }
        return new D2Stash(variant, filename, checkHeaderAndLoadItems(variant, filename, bitReader), iSC, iHC, bitReader.isNewFile());
    }

    private ArrayList<D2Item> checkHeaderAndLoadItems(Variant variant, String filename, D2BitReader bitReader) {
        if (!bitReader.isNewFile()) {
            bitReader.set_byte_pos(0);
            byte[] startingBytes = bitReader.get_bytes(3);
            String lStart = new String(startingBytes);
            if (!"D2X".equals(lStart)) throw new RuntimeException("Incorrect Stash type: " + lStart);
            checkVersionAndVariant(variant, bitReader);
            checkChecksum(bitReader);
            return readItems(filename, bitReader);
        } else {
            return new ArrayList<>();
        }
    }

    private void checkVersionAndVariant(Variant expectedVariant, D2BitReader bitReader) {
        bitReader.set_byte_pos(5);
        long versionNumber = bitReader.read(16);
        if (versionNumber != D2R3.getFileVersionIdentifier())
            throw VersionController.VersionException.forVersion(D2R3, VersionController.Version.tryParseFileVersionIdentifier((int) versionNumber));
        int variantAsInt = (int) bitReader.read(16);
        Variant variantOrNull = tryParseStashIdentifier(variantAsInt);
        if (variantOrNull != expectedVariant)
            throw VersionController.VersionException.forVariant(expectedVariant, variantOrNull);
    }

    private void checkChecksum(D2BitReader bitReader) {
        bitReader.set_byte_pos(CHECKSUM_BYTE_OFFSET_START);
        long originalChecksum = bitReader.read(CHECKSUM_BYTE_LENGTH * 8);
        long calculatedChecksum = calculateChecksum(bitReader);
        if (originalChecksum != calculatedChecksum) {
            throw new RuntimeException("Checksum Incorrect! Expected: " + originalChecksum + " Found: " + calculatedChecksum);
        }
    }

    private ArrayList<D2Item> readItems(String filename, D2BitReader bitReader) {
        bitReader.set_byte_pos(3);
        long numItems = bitReader.read(16);
        bitReader.set_byte_pos(HEADER_BYTE_LENGTH);
        ArrayList<D2Item> items = new ArrayList<>();
        for (int i = 0; i < numItems; i++) {
            D2Item lItem;
            try {
                lItem = new D2Item(filename, bitReader, FIXED_STASH_CHAR_LEVEL);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            items.add(lItem);
        }
        return items;
    }
}
