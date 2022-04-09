package gomule.translations

import com.fasterxml.jackson.module.kotlin.jsonMapper
import com.fasterxml.jackson.module.kotlin.kotlinModule
import gomule.files.getResource
import java.io.InputStream

private val mapper = jsonMapper {
    addModule(kotlinModule())
}

interface Translations {
    fun getTranslationOrNull(key: String): String?
    fun getTranslation(key: String): String =
        getTranslationOrNull(key) ?: throw IllegalArgumentException("No translation for $key")
}

class TranslationsLoader {
    companion object {
        @JvmStatic
        fun loadTranslations(): Translations {
            return CompositeTranslations(
                MapBasedTranslations.loadTranslations(getResource("d2Files/D2R_1.0/translations/item-names.json")),
                MapBasedTranslations.loadTranslations(getResource("d2Files/D2R_1.0/translations/item-modifiers.json")),
                MapBasedTranslations.loadTranslations(getResource("d2Files/D2R_1.0/translations/item-nameaffixes.json")),
                MapBasedTranslations.loadTranslations(getResource("d2Files/D2R_1.0/translations/item-runes.json")),
                MapBasedTranslations.loadTranslations(getResource("d2Files/D2R_1.0/translations/mercenaries.json")),
                MapBasedTranslations.loadTranslations(getResource("d2Files/D2R_1.0/translations/monsters.json")),
                MapBasedTranslations.loadTranslations(getResource("d2Files/D2R_1.0/translations/npcs.json")),
                MapBasedTranslations.loadTranslations(getResource("d2Files/D2R_1.0/translations/skills.json")),
                MapBasedTranslations.loadTranslations(getResource("d2Files/D2R_1.0/translations/ui-controller.json")),
            )
        }
    }
}

class CompositeTranslations(private vararg val translations: Translations) : Translations {
    override fun getTranslationOrNull(key: String): String? =
        translations.asSequence().firstNotNullOfOrNull { it.getTranslationOrNull(key) }
}

class MapBasedTranslations(private val translationData: Map<String, String>) : Translations {
    companion object {
        @JvmStatic
        fun loadTranslations(inputStream: InputStream): Translations {
            return MapBasedTranslations(
                mapper.readTree(inputStream).associateBy(
                    { it.get("Key").textValue() },
                    { it.get("enUS").textValue() })
            )
        }
    }

    override fun getTranslationOrNull(key: String) = translationData[key]

    override fun toString(): String {
        return "Translations(translationData=$translationData)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as MapBasedTranslations

        if (translationData != other.translationData) return false

        return true
    }

    override fun hashCode(): Int {
        return translationData.hashCode()
    }
}