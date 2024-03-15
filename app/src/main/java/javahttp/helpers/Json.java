package javahttp.helpers;
import com.google.gson.Gson;

public class Json {
    static Gson gson;

    private Json() {
        if (gson == null) {
            gson = new Gson();
        }
    }

    public static Gson getGson() {
        if (gson == null) {
            new Json();
            return gson;
        }
        return gson;
    }

}
