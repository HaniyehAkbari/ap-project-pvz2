package com.pvz2.models.network;

import com.google.gson.*;

import java.lang.reflect.Type;

public class PolymorphicTypeAdapter<T> implements JsonSerializer<T>, JsonDeserializer<T> {
    private static final String TYPE_FIELD = "@type";
    private static final String DATA_FIELD = "data";

    @Override
    public JsonElement serialize(T src, Type typeOfSrc, JsonSerializationContext context) {
        Class<?> actualClass = src.getClass();
        JsonObject wrapper = new JsonObject();
        wrapper.addProperty(TYPE_FIELD, actualClass.getName());
        wrapper.add(DATA_FIELD, context.serialize(src, actualClass));
        return wrapper;
    }

    @Override
    public T deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject wrapper = json.getAsJsonObject();
        String className = wrapper.get(TYPE_FIELD).getAsString();
        try {
            Class<?> actualClass = Class.forName(className);
            return context.deserialize(wrapper.get(DATA_FIELD), actualClass);
        } catch (ClassNotFoundException e) {
            throw new JsonParseException("Unknown subclass while deserializing: " + className, e);
        }
    }
}
