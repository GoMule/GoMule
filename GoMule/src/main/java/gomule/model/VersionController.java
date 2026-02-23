package gomule.model;

import java.util.Arrays;
import java.util.Optional;
import java.util.function.Function;

import static gomule.model.VersionController.Variant.SharedStashConfig.*;

public class VersionController {

    public enum Version {
        D2LOD(false, 96, "LoD 1.07+"),
        D2LOD_110(false, 97, "LoD 1.10+ / Resurrected: 1+"),
        D2R2_4(false, 98, "Resurrected: 2.4+"),
        D2R2_5(false, 99, "Resurrected: 2.5+"),
        D2R3(true, 105, "Resurrected: 3+");

        private final boolean enabled;
        private final int fileVersionIdentifier;
        private final String humanName;

        Version(boolean enabled, int fileVersionIdentifier, String humanName) {
            this.enabled = enabled;
            this.fileVersionIdentifier = fileVersionIdentifier;
            this.humanName = humanName;
        }

        public int getFileVersionIdentifier() {
            return fileVersionIdentifier;
        }

        public String getHumanName() {
            return humanName;
        }

        public boolean isEnabled() {
            return enabled;
        }

        public static Version fromHumanName(String humanName) {
            return getFirst(Version.values(), it -> it.humanName, humanName).orElseThrow(() -> new IllegalArgumentException("Unknown version: " + humanName));
        }

        public static Version tryParseFileVersionIdentifier(int fileVersionIdentifier) {
            return firstOrNull(Version.values(), it -> it.fileVersionIdentifier, fileVersionIdentifier);
        }
    }

    public enum Variant {

        CLASSIC(false, 1, 1, NONE, "Classic"),
        EXPANSION(true, 2, 2, SIMPLE, "Expansion"),
        ROW(true, 3, 3, MODERN, "Return of the Warlock");

        private final boolean enabled;
        private final int stashIdentifier;
        private final int fileVersionIdentifier;
        private final SharedStashConfig sharedStashConfig;
        private final String humanName;

        Variant(boolean enabled, int stashIdentifier, int fileVersionIdentifier, SharedStashConfig sharedStashConfig, String humanName) {
            this.enabled = enabled;
            this.stashIdentifier = stashIdentifier;
            this.fileVersionIdentifier = fileVersionIdentifier;
            this.sharedStashConfig = sharedStashConfig;
            this.humanName = humanName;
        }

        public int getStashIdentifier() {
            return stashIdentifier;
        }

        public String getHumanName() {
            return humanName;
        }

        public int getFileVersionIdentifier() {
            return fileVersionIdentifier;
        }

        public SharedStashConfig getSharedStashConfig() {
            return sharedStashConfig;
        }

        public boolean isEnabled() {
            return enabled;
        }

        public static Variant tryParseStashIdentifier(int stashIdentifier) {
            return firstOrNull(Variant.values(), it -> it.stashIdentifier, stashIdentifier);
        }

        public static Variant tryParseSharedStashPaneCount(int sharedStashPaneCount) {
            return firstOrNull(Variant.values(), it -> it.getSharedStashConfig().totalStashPaneCount, sharedStashPaneCount);
        }

        public static Variant tryParseFileVersionIdentifier(int fileVersionIdentifier) {
            return firstOrNull(Variant.values(), it -> it.fileVersionIdentifier, fileVersionIdentifier);
        }

        public static Variant fromHumanName(String humanName) {
            return getFirst(Variant.values(), it -> it.humanName, humanName).orElseThrow(() -> new IllegalArgumentException("Unknown variant: " + humanName));
        }

        public enum SharedStashConfig {
            NONE(0, 0),
            SIMPLE(3, 3),
            MODERN(5, 7);

            private final int itemStashPaneCount;
            private final int totalStashPaneCount;

            SharedStashConfig(int itemStashPaneCount, int totalStashPaneCount) {
                this.itemStashPaneCount = itemStashPaneCount;
                this.totalStashPaneCount = totalStashPaneCount;
            }

            public int getItemStashPaneCount() {
                return itemStashPaneCount;
            }

            public int getTotalStashPaneCount() {
                return totalStashPaneCount;
            }
        }
    }

    private static <T, U> T firstOrNull(T[] values, Function<T, U> identifierExtractor, U identifier) {
        return getFirst(values, identifierExtractor, identifier).orElse(null);
    }

    private static <T, U> Optional<T> getFirst(T[] values, Function<T, U> identifierExtractor, U identifier) {
        return Arrays.stream(values).filter(it -> identifierExtractor.apply(it).equals(identifier)).findFirst();
    }

    public static class VersionException extends RuntimeException {

        private final String message;

        public static VersionException forVariant(Variant expected, Variant actual) {
            String expectedName = (expected != null) ? expected.humanName : "Unknown";
            String actualName = (actual != null) ? actual.humanName : "Unknown";
            return new VersionException("Please change the workspace variant before loading this file.\nCurrent Workspace: " + expectedName + "\nFile Needs: " + actualName);
        }

        public static VersionException forVersion(Version expected, Version actual) {
            String expectedName = (expected != null) ? expected.humanName : "Unknown";
            String actualName = (actual != null) ? actual.humanName : "Unknown";
            return new VersionException("Please change the workspace version before loading this file.\nCurrent Workspace: " + expectedName + "\nFile Needs: " + actualName);
        }

        private VersionException(String message) {
            super(message);
            this.message = message;
        }

        @Override
        public String getMessage() {
            return message;
        }
    }
}
