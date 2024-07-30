package org.erywim.chapter3.protocol;

import com.google.gson.*;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;

/**
 * @author Erywim 2024/7/29
 */
public interface Serializer {

    //反序列化方法
    <T> T deserialize(byte[] data, Class<T> clazz);

    //序列化方法
    <T> byte[] serialize(T obj);

    enum Algorithm implements Serializer {
        Java {
            @Override
            public <T> T deserialize(byte[] data, Class<T> clazz) {
                try {
                    ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(data));
                    return (T) ois.readObject();
                } catch (IOException | ClassNotFoundException e) {
                    throw new RuntimeException("反序列化失败", e);
                }
            }

            @Override
            public <T> byte[] serialize(T obj) {
                ByteArrayOutputStream bos = null;
                try {
                    //bos拿到最终的字节数组
                    bos = new ByteArrayOutputStream();
                    //oos拿到对象转换的字节数组
                    ObjectOutputStream oos = new ObjectOutputStream(bos);
                    //写入对象，然后会进一步被写入到bos中
                    oos.writeObject(obj);
                    return bos.toByteArray();
                } catch (IOException e) {
                    throw new RuntimeException("序列化失败", e);
                }
            }
        },
        Json {
            @Override
            public <T> T deserialize(byte[] data, Class<T> clazz) {
                Gson gson = new GsonBuilder().registerTypeAdapter(Class.class, new ClassCodec()).create();
                String json = new String(data, StandardCharsets.UTF_8);
                return gson.fromJson(json, clazz);
            }

            @Override
            public <T> byte[] serialize(T obj) {
                Gson gson = new GsonBuilder().registerTypeAdapter(Class.class, new ClassCodec()).create();
                String json = gson.toJson(obj);
                return json.getBytes(StandardCharsets.UTF_8);
            }
        }
    }
    class ClassCodec implements JsonSerializer<Class<?>>, JsonDeserializer<Class<?>>{
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
