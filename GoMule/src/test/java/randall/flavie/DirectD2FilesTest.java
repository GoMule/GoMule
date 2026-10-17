package randall.flavie;

import gomule.item.D2Item;
import gomule.item.D2Prop;
import gomule.item.D2PropCollection;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import randall.d2files.D2TxtFile;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.*;

import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class DirectD2FilesTest {

    private DirectD2Files directD2Files;
    private ArrayList<ItemObject> dataObjects;
    private PrintStream out;

    @BeforeEach
    public void setUp() {
        Flavie flavie = Mockito.mock(Flavie.class);
        flavie.iAllItemsFP = new HashMap<>();
        flavie.iRuneCount = new HashMap<>();
        Mockito.when(flavie.checkFilters(Mockito.any())).thenReturn(true);
        directD2Files = new DirectD2Files(flavie);
        dataObjects = new ArrayList<>();
        out = new PrintStream(new ByteArrayOutputStream());
    }

    @Test
    public void uniqueNonFacetJewelMatchesOnNameAlone() {
        // Regression: unique jewels without Rainbow Facet die/level-up properties
        // (matchStr stays null) used to NPE instead of matching by name.
        ItemObject itemObject = uniqueJewelObject("Defender's Bile", null);
        dataObjects.add(itemObject);

        D2Item item = mockItem("Defender's Bile", true, true, null);

        directD2Files.matchItem(dataObjects, item, out);

        assertEquals(singletonList(item), itemObject.getInstances());
    }

    @Test
    public void uniqueNonFacetJewelMatchesEvenWhenExtraDetectConfigured() {
        // matchStr is null for non-facet jewels, so extraDetect on the dat entry
        // must not be dereferenced; name alone is enough.
        ItemObject itemObject = uniqueJewelObject("Guardian's Thunder", Arrays.asList("Attack", "Die"));
        dataObjects.add(itemObject);

        D2Item item = mockItem("Guardian's Thunder", true, true, null);

        directD2Files.matchItem(dataObjects, item, out);

        assertEquals(singletonList(item), itemObject.getInstances());
    }

    @Test
    public void rainbowFacetMatchesWhenSkillAndTriggerMatch() {
        D2TxtFile.constructTxtFiles("./d2111");
        ItemObject itemObject = uniqueJewelObject("Rainbow Facet", Arrays.asList("Attack", "Die"));
        dataObjects.add(itemObject);

        // prop 197 = die trigger, pVals[1] = skills.txt row 0 ("Attack")
        D2Item item = mockItem("Rainbow Facet", true, true, "0xabc",
                new D2Prop(197, new int[]{0, 0}, 0));

        directD2Files.matchItem(dataObjects, item, out);

        assertEquals(singletonList(item), itemObject.getInstances());
    }

    @Test
    public void rainbowFacetDoesNotMatchWhenSkillDiffers() {
        D2TxtFile.constructTxtFiles("./d2111");
        ItemObject itemObject = uniqueJewelObject("Rainbow Facet", Arrays.asList("Fire Ball", "Die"));
        dataObjects.add(itemObject);

        D2Item item = mockItem("Rainbow Facet", true, true, "0xabc",
                new D2Prop(197, new int[]{0, 0}, 0));

        directD2Files.matchItem(dataObjects, item, out);

        assertEquals(0, itemObject.getInstances().size());
    }

    @Test
    public void rainbowFacetMatchesLevelUpTrigger() {
        D2TxtFile.constructTxtFiles("./d2111");
        ItemObject itemObject = uniqueJewelObject("Rainbow Facet", Arrays.asList("Attack", "Level Up"));
        dataObjects.add(itemObject);

        // prop 199 = level up trigger
        D2Item item = mockItem("Rainbow Facet", true, true, "0xabc",
                new D2Prop(199, new int[]{0, 0}, 0));

        directD2Files.matchItem(dataObjects, item, out);

        assertEquals(singletonList(item), itemObject.getInstances());
    }

    private ItemObject uniqueJewelObject(String name, java.util.List<String> extraDetect) {
        CatObject catObject = new CatObject("Jewelry");
        catObject.setGroup("unique");
        SubCatObject subCatObject = new SubCatObject("Jewels", catObject);
        ItemObject itemObject = new ItemObject(name, "", subCatObject);
        itemObject.setExtraDetect(extraDetect == null ? null : new ArrayList<>(extraDetect));
        return itemObject;
    }

    private D2Item mockItem(String name, boolean unique, boolean jewel, String fingerprint, D2Prop... props) {
        D2Item item = Mockito.mock(D2Item.class);
        Mockito.when(item.getName()).thenReturn(name);
        Mockito.when(item.isUnique()).thenReturn(unique);
        Mockito.when(item.isJewel()).thenReturn(jewel);
        Mockito.when(item.getFingerprint()).thenReturn(fingerprint);
        D2PropCollection propCollection = new D2PropCollection();
        //noinspection unchecked
        propCollection.addAll(Arrays.asList(props));
        Mockito.when(item.getPropCollection()).thenReturn(propCollection);
        return item;
    }
}
