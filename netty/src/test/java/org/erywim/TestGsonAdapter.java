package org.erywim;

import com.google.gson.*;
import org.junit.Test;

import java.lang.reflect.Type;

/**
 * @author Erywim 2024/7/30
 */

public class TestGsonAdapter {
    @Test
    public void test() {
        Gson gson = new GsonBuilder().registerTypeAdapter(Class.class, new ClassCodec()).create();
        System.out.println("gson.toJson(String.class) = " + gson.toJson(String.class));
    }
    static class ClassCodec implements JsonSerializer<Class<?>>, JsonDeserializer<Class<?>> {
        @Override
        public Class<?> deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            try {
                String asString = jsonElement.getAsString();
                return Class.forName(asString);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public JsonElement serialize(Class<?> aClass, Type type, JsonSerializationContext jsonSerializationContext) {
            String name = aClass.getName();
            return new JsonPrimitive(name);
        }
    }
}
