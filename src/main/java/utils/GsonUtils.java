package utils;

import com.google.gson.Gson;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class GsonUtils {
    private final static Gson gson = new Gson();

    public static <T> T convertJsonToClass(final String msg, final Class<T> clazz) {
        try {
            return gson.fromJson(msg, clazz);
        } catch (Exception e) {

            throw new RuntimeException("Error converting JSON to object: " + e.getMessage(), e);
        }
    }

    public static String convertClassToJson(final Object object) {
        return gson.toJson(object);
    }
}
