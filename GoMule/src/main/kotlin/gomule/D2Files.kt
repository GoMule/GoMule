package gomule

import gomule.translations.Translations
import gomule.translations.TranslationsLoader

class D2Files(val translations: Translations) {
    companion object {
        @JvmStatic
        val instance = D2Files(TranslationsLoader.loadTranslations())
    }
}