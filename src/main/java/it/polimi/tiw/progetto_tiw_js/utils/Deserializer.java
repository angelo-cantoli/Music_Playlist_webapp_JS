package it.polimi.tiw.progetto_tiw_js.utils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;

public class Deserializer {


    public static ArrayList<Integer> fromJsonToArrayList(String jsonString){
        Gson gson = new Gson();
        Type listType = new TypeToken<ArrayList<Integer>>() {}.getType();
        return gson.fromJson(jsonString, listType);
    }
}
