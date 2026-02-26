package gomule.translations;

import static gomule.files.FileReaderUtils.getResource;

public class TranslationsLoader {

    public static Translations loadTranslations() {
        return new CompositeTranslations(
                MapBasedTranslations.loadTranslations(getResource("d2Files/D2R_1.0/translations/item-names.json")),
                MapBasedTranslations.loadTranslations(getResource("d2Files/D2R_1.0/translations/item-modifiers.json")),
                MapBasedTranslations.loadTranslations(
                        getResource("d2Files/D2R_1.0/translations/item-nameaffixes.json")),
                MapBasedTranslations.loadTranslations(getResource("d2Files/D2R_1.0/translations/item-runes.json")),
                MapBasedTranslations.loadTranslations(getResource("d2Files/D2R_1.0/translations/mercenaries.json")),
                MapBasedTranslations.loadTranslations(getResource("d2Files/D2R_1.0/translations/monsters.json")),
                MapBasedTranslations.loadTranslations(getResource("d2Files/D2R_1.0/translations/npcs.json")),
                MapBasedTranslations.loadTranslations(getResource("d2Files/D2R_1.0/translations/skills.json")),
                MapBasedTranslations.loadTranslations(getResource("d2Files/D2R_1.0/translations/ui-controller.json")),
                MapBasedTranslations.loadTranslations(getResource("d2Files/D2R_1.0/translations/ui.json")),
                MapBasedTranslations.loadTranslations(getResource("d2Files/D2R_1.0/translations/custom-gomule.json")));
    }
}
