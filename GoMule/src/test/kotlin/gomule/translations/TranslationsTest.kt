package gomule.translations

import gomule.files.getResource
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class TranslationsTest {
    @Test
    fun loadTranslations() {
        val actual =
            MapBasedTranslations.loadTranslations(getResource("translationsTestData/translationsTestData.json"))
        val expected = MapBasedTranslations(
            mapOf("ModStr4l" to "Slightly Increased Attack Speed", "ModStr4n" to "Greatly Increased Attack Speed")
        )
        assertEquals(expected, actual)
        assertEquals(null, actual.getTranslationOrNull("missing-key"))
        assertEquals("Greatly Increased Attack Speed", actual.getTranslationOrNull("ModStr4n"))
    }
}