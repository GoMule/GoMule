package collections;

import java.util.Arrays;
import java.util.List;

public class CollectionHelpers {

    @SafeVarargs
    public static <T> List<T> listOf( T... items ) {
        return Arrays.asList( items );
    }

}
