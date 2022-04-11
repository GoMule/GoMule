package randall.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

class RandallUtilTest {

    private static final List<String> DELIMITERS = asList(
            "a", "ab", "aB", "AB", ",", ".", "[", "]", "/", "-", " "
    );

    static Stream<Arguments> stringsToMerge() {
        String delimiter = DELIMITERS.get(0);
        return Stream.of(
                Arguments.of(emptyList(), delimiter, ""),
                Arguments.of(asList("1", "2"), delimiter, "1" + delimiter + "2"),
                Arguments.of(asList("1", "2", "3"), delimiter, "1" + delimiter + "2" + delimiter + "3")
        );
    }

    @ParameterizedTest
    @MethodSource("stringsToMerge")
    void mergeShouldWork(List<String> stringsToMerge, String delimiter, String expected) {
        String actual = RandallUtil.merge(stringsToMerge, delimiter);
        assertEquals(expected, actual);
    }

    static Stream<Arguments> stringsToSplit() {
        List<Arguments> result = new ArrayList<>();
        for (String delimiter : DELIMITERS) {
            result.add(Arguments.of("", delimiter, emptyList()));
            result.add(Arguments.of("1" + delimiter + "2", delimiter, asList("1", "2")));
            result.add(Arguments.of("1" + delimiter + "2" + delimiter + "3", delimiter, asList("1", "2", "3")));
            result.add(Arguments.of("test" + delimiter + "text", delimiter, asList("test", "text")));
            result.add(Arguments.of("test" + delimiter + "text" + delimiter + "long", delimiter, asList("test", "text", "long")));
            result.add(Arguments.of("TEST" + delimiter + "TEXT", delimiter, asList("TEST", "TEXT")));
            result.add(Arguments.of("TEST" + delimiter + "TEXT" + delimiter + "LONG", delimiter, asList("TEST", "TEXT", "LONG")));
        }
        return result.stream();
    }

    @ParameterizedTest
    @MethodSource("stringsToSplit")
    void regularSplitShouldWork(String stringToSplit, String delimiter, List<String> expected) {
        List<String> actual = RandallUtil.split(stringToSplit, delimiter, false);
        assertArrayEquals(expected.toArray(), actual.toArray());
    }

    @ParameterizedTest
    @MethodSource("stringsToSplit")
    void splitIgnoringCaseShouldWorkWithUpperCaseDelimiter(String stringToSplit,
                                                           String delimiter, List<String> expected) {
        List<String> actual = RandallUtil.split(stringToSplit, delimiter.toUpperCase(), true);
        assertArrayEquals(expected.toArray(), actual.toArray());
    }

    @ParameterizedTest
    @MethodSource("stringsToSplit")
    void splitIgnoringCaseShouldWorkWithLowerCaseDelimiter(String stringToSplit,
                                                           String delimiter, List<String> expected) {
        List<String> actual = RandallUtil.split(stringToSplit, delimiter.toLowerCase(), true);
        assertArrayEquals(expected.toArray(), actual.toArray());
    }
}