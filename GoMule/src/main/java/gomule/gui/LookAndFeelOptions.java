package gomule.gui;

import javax.swing.*;
import java.util.function.Supplier;

public enum LookAndFeelOptions {

    CLASSIC("Classic GoMule", () -> {
        if (System.getProperty("os.name").toLowerCase().contains("mac")) {
            return "apple.laf.AquaLookAndFeel";
        } else {
            return UIManager.getCrossPlatformLookAndFeelClassName();
        }
    }),
    NEW_LIGHT("New Light GoMule", () -> "com.formdev.flatlaf.FlatIntelliJLaf"),
    DARK("Dark GoMule", () -> "com.formdev.flatlaf.FlatDarculaLaf"),
    ;

    public static final String PROPERTY_NAME = "lookAndFeel";
    private final String nameString;
    private final Supplier<String> lookAndFeelName;

    LookAndFeelOptions(String nameString, Supplier<String> lookAndFeelName) {
        this.nameString = nameString;
        this.lookAndFeelName = lookAndFeelName;
    }

    public String getNameString() {
        return nameString;
    }

    public String getLookAndFeelName() {
        return lookAndFeelName.get();
    }
}
