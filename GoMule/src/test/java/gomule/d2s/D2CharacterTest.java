package gomule.d2s;

import com.google.common.io.Resources;
import gomule.model.VersionController;
import gomule.model.VersionController.VersionException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import randall.d2files.D2TxtFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static gomule.util.TestHelpers.loadTestResources;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SuppressWarnings("UnstableApiUsage")
public class D2CharacterTest {

    @BeforeAll
    public static void setup() {
        D2TxtFile.constructTxtFiles("./d2111");
    }

    public static class CharacterTestCase {
        final String name;
        final D2Character character;
        final File expectedFile;

        CharacterTestCase(String name, D2Character character, File expectedFile) {
            this.name = name;
            this.character = character;
            this.expectedFile = expectedFile;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    @Test
    public void testWrongVariant() {
        VersionException exception = assertThrows(VersionException.class, () -> new D2Character(VersionController.Variant.EXPANSION, Resources.getResource("charFiles/ROW/bleh.d2s").getFile()));
        assertEquals("Please change the workspace variant before loading this file.\n" +
                "Current Workspace: Expansion\n" +
                "File Needs: Return of the Warlock", exception.getMessage());
    }

    static Stream<CharacterTestCase> charFileProvider() throws Exception {
        Path charFilesPath = Paths.get(loadTestResources("charFiles").toURI());
        try (Stream<Path> pathStream = Files.walk(charFilesPath)) {
            return pathStream
                    .filter(path -> path.toString().endsWith(".d2s"))
                    .map(path -> {
                        try {
                            String fileName = path.getFileName().toString();
                            String charName = fileName.substring(0, fileName.length() - 4);
                            int count = path.getNameCount();
                            D2Character character = new D2Character(VersionController.Variant.valueOf(path.getName(count - 2).toString()), path.toAbsolutePath().toString());
                            File expectedFile = path.resolveSibling(fileName + ".expected").toFile();
                            return new CharacterTestCase(charName, character, expectedFile);
                        } catch (Exception e) {
                            throw new RuntimeException("Error with: " + path.getFileName(), e);
                        }
                    })
                    .collect(Collectors.toList())
                    .stream();
        }
    }

    @ParameterizedTest
    @MethodSource("charFileProvider")
    public void testCharacter(CharacterTestCase testCase) throws Exception {
        String actual = testCase.character.fullDumpStr().replaceAll("\r", "");
        String expected = new String(Files.readAllBytes(testCase.expectedFile.toPath())).replaceAll("\r", "");
        assertEquals(expected, actual, "Character dump for " + testCase.name + " does not match expected output");
    }

    @Test
    @Disabled
    public void regenerateExpectedFiles() throws Exception {
        charFileProvider().forEach(testCase -> {
            try {
                String output = testCase.character.fullDumpStr().replaceAll("\r", "");
                Files.write(testCase.expectedFile.toPath(), output.getBytes());
                System.out.println("Generated: " + testCase.expectedFile.getAbsolutePath());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }
}