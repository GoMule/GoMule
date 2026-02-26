package gomule.translations;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.google.common.collect.ImmutableMap;
import com.google.common.io.CharStreams;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.Objects;

import static java.nio.charset.StandardCharsets.UTF_8;

public class MapBasedTranslations implements Translations {
    private final Map<String, String> translationData;

    public MapBasedTranslations(Map<String, String> translationData) {
        this.translationData = translationData;
    }

    public static Translations loadTranslations(InputStream inputStream) {
        try (InputStreamReader reader = new InputStreamReader(inputStream, UTF_8)) {
            ImmutableMap.Builder<String, String> mapBuilder = ImmutableMap.builder();
            String content = CharStreams.toString(reader).replace("\uFEFF", "").trim();
            for (JsonValue value : Json.parse(content).asArray()) {
                JsonObject node = value.asObject();
                int id = node.getInt("id", -1);
                if (id == 27893 || id == 27502 || id == 27542 || id == 28085) {
                    continue;
                }
                String key = node.getString("Key", null);
                String valueStr = node.getString("enUS", "");
                if (key != null) {
                    mapBuilder.put(key, valueStr);
                }
            }
            return new MapBasedTranslations(mapBuilder.build());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getTranslationOrNull(String key) {
        return translationData.get(key);
    }

    @Override
    public String toString() {
        return "MapBasedTranslations{" + "translationData=" + translationData + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MapBasedTranslations that = (MapBasedTranslations) o;
        return Objects.equals(translationData, that.translationData);
    }

    @Override
    public int hashCode() {
        return Objects.hash(translationData);
    }
}
