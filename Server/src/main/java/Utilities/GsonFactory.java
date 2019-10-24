package Utilities;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.time.LocalDate;


public class GsonFactory {
    static private GsonBuilder gsonBuilder;
    
    static public void initialize() {
        gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(LocalDate.class, new LocalDateSerializer());
        gsonBuilder.registerTypeAdapter(LocalDate.class, new LocalDateDeserializer());
        gsonBuilder.excludeFieldsWithoutExposeAnnotation();
        gsonBuilder.setPrettyPrinting();        
    }
    
    static public Gson createGsonObject() {
        return gsonBuilder.create();
    }    
}
