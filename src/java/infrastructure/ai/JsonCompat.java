package infrastructure.ai;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.util.Map;

/**
 * Tiny JSON helper without external dependencies.
 *
 * Uses the JavaScript engine (Nashorn on older JDKs) to parse JSON into Java Maps/Lists.
 * If the engine is unavailable, callers should fall back to a non-JSON-based provider.
 */
public final class JsonCompat {
    private JsonCompat() {}

    @SuppressWarnings("unchecked")
    public static Map<String, Object> parseObject(String json) throws Exception {
        if (json == null) throw new IllegalArgumentException("json is null");

        ScriptEngine engine = new ScriptEngineManager().getEngineByName("javascript");
        if (engine == null) {
            throw new IllegalStateException("No JavaScript engine available to parse JSON.");
        }

        // Java.asJSONCompatible returns Maps/Lists/Strings/Numbers/Booleans/null
        Object result = engine.eval("Java.asJSONCompatible(" + json + ")");
        if (!(result instanceof Map)) {
            throw new IllegalArgumentException("Expected JSON object.");
        }
        return (Map<String, Object>) result;
    }
}

