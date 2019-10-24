package Utilities;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
import java.time.LocalDate;


public class LocalDateSerializer implements JsonSerializer<LocalDate> {
    
    @Override
    public JsonElement serialize(LocalDate src, Type typeOfSrc, JsonSerializationContext context) {
        String dateString = DateHandler.dateToJsonString(src);
        
        if(dateString != null)
            return new JsonPrimitive(dateString);
        else
            return null;
    }
    
}
