package gomule.d2i;

import gomule.d2i.D2SharedStash.D2SharedStashPane;
import gomule.item.D2Item;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

public class D2SharedStashTest {

    @Test
    public void testConstruction() {
        List<D2Item> items = Arrays.asList(
                mockItem(0, 0, 5, 5),
                mockItem(9, 9, 1, 1)
        );
        D2Item[][] expectedGrid = new D2Item[10][10];
        expectedGrid[0][0] = items.get(0);
        expectedGrid[1][0] = items.get(0);
        expectedGrid[2][0] = items.get(0);
        expectedGrid[3][0] = items.get(0);
        expectedGrid[4][0] = items.get(0);
        expectedGrid[0][1] = items.get(0);
        expectedGrid[1][1] = items.get(0);
        expectedGrid[2][1] = items.get(0);
        expectedGrid[3][1] = items.get(0);
        expectedGrid[4][1] = items.get(0);
        expectedGrid[0][2] = items.get(0);
        expectedGrid[1][2] = items.get(0);
        expectedGrid[2][2] = items.get(0);
        expectedGrid[3][2] = items.get(0);
        expectedGrid[4][2] = items.get(0);
        expectedGrid[0][3] = items.get(0);
        expectedGrid[1][3] = items.get(0);
        expectedGrid[2][3] = items.get(0);
        expectedGrid[3][3] = items.get(0);
        expectedGrid[4][3] = items.get(0);
        expectedGrid[0][4] = items.get(0);
        expectedGrid[1][4] = items.get(0);
        expectedGrid[2][4] = items.get(0);
        expectedGrid[3][4] = items.get(0);
        expectedGrid[4][4] = items.get(0);
        expectedGrid[9][9] = items.get(1);
        D2SharedStashPane expected = new D2SharedStashPane(items, expectedGrid);
        assertEquals(expected, D2SharedStashPane.fromItems(items));
    }

    @Test(expected = RuntimeException.class)
    public void testOverlappingItems() {
        List<D2Item> items = Arrays.asList(
                mockItem(0, 0, 5, 5),
                mockItem(1, 1, 5, 5)
        );
        D2SharedStashPane.fromItems(items);
    }

    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public void testOutOfBoundsItems() {
        List<D2Item> items = Collections.singletonList(
                mockItem(0, 0, 50, 50)
        );
        D2SharedStashPane.fromItems(items);
    }

    @Test
    public void getItemCovering() {
        List<D2Item> items = Arrays.asList(
                mockItem(0, 0, 5, 5),
                mockItem(7, 7, 2, 2));
        D2SharedStashPane d2SharedStashPane = D2SharedStashPane.fromItems(items);
        assertEquals(items.get(0), d2SharedStashPane.getItemCovering(0, 0));
        assertEquals(items.get(0), d2SharedStashPane.getItemCovering(4, 4));
        assertNull(d2SharedStashPane.getItemCovering(5, 4));
        assertEquals(items.get(1), d2SharedStashPane.getItemCovering(7, 8));
        assertNull(d2SharedStashPane.getItemCovering(6, 6));
    }

    @Test
    public void canDropItem() {
        List<D2Item> items = Arrays.asList(
                mockItem(0, 0, 5, 5),
                mockItem(7, 7, 2, 2));
        D2SharedStashPane d2SharedStashPane = D2SharedStashPane.fromItems(items);
        assertFalse(d2SharedStashPane.canDropItem(0, 0, items.get(1)));
        assertTrue(d2SharedStashPane.canDropItem(5, 0, items.get(1)));
        assertFalse(d2SharedStashPane.canDropItem(4, 0, items.get(1)));
        assertFalse(d2SharedStashPane.canDropItem(6, 7, items.get(1)));
        assertFalse(d2SharedStashPane.canDropItem(9, 9, items.get(1)));
        assertFalse(d2SharedStashPane.canDropItem(20, 20, items.get(1)));
    }

    private D2Item mockItem(int col, int row, int width, int height) {
        D2Item mock = Mockito.mock(D2Item.class, Mockito.CALLS_REAL_METHODS);
        Mockito.when(mock.get_row()).thenReturn((short) row);
        Mockito.when(mock.get_col()).thenReturn((short) col);
        Mockito.when(mock.get_width()).thenReturn((short) width);
        Mockito.when(mock.get_height()).thenReturn((short) height);
        return mock;
    }
}