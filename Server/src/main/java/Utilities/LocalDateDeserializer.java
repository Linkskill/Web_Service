package Utilities;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;
import java.time.LocalDate;


public class LocalDateDeserializer implements JsonDeserializer<LocalDate> {

    @Override
    public LocalDate deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
        
        LocalDate parsedDate = DateHandler.jsonStringToDate(json.getAsString());

        if(parsedDate != null)
            return parsedDate;
        else
            throw new JsonParseException(json.getAsString() + " not in expected format (" + "yyyy-M-d" + ")");
    }
    
}
