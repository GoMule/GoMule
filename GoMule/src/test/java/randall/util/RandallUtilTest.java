package randall.util;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.junit.jupiter.api.Assertions.assertEquals;

class RandallUtilTest {

    private static final String DELIMITER = "-";

    static Stream<Arguments> stringsToMerge() {
        return Stream.of(
                Arguments.of(emptyList(), ""),
                Arguments.of(asList("1", "2"), "1" + DELIMITER + "2"),
                Arguments.of(asList("1", "2", "3"), "1" + DELIMITER + "2" + DELIMITER + "3")
        );
    }

    @ParameterizedTest
    @MethodSource("stringsToMerge")
    void mergeShouldWork(List<String> stringsToMerge, String expected) {
        String actual = RandallUtil.merge(stringsToMerge, DELIMITER);
        assertEquals(expected, actual);
    }
}