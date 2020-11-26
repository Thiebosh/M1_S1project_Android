package fr.yncrea.m1_s1project_android.services;

import android.util.JsonReader;
import android.util.Log;
import java.util.Enumeration;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import fr.yncrea.m1_s1project_android.models.Channel;
import fr.yncrea.m1_s1project_android.models.Generator;
import fr.yncrea.m1_s1project_android.models.PowerSupply;
import fr.yncrea.m1_s1project_android.models.Scale;

import static fr.yncrea.m1_s1project_android.models.PowerSupply.I;
import static fr.yncrea.m1_s1project_android.models.PowerSupply.V;

public class ConverterService {

    public static Generator createJsonObject(final String str) {
        return (new Gson()).fromJson(str, Generator.class);
    }

    public static String extractJsonData(final Channel data) {
        JsonObject json = new JsonObject();

        json.addProperty("id", data.getId());
        if (data.isSetActive()) json.addProperty("isActive", data.isActive());
        if (data.isSetCurrentValue()) json.addProperty("currentValue", data.getCurrentValue());
        if (data.isSetMinValue()) json.addProperty("minValue", data.getMinValue());
        if (data.isSetMaxValue()) json.addProperty("maxValue", data.getMaxValue());
        if (data.isSetType()) json.addProperty("type", data.getType().name());
        if (data.isSetScale()) json.addProperty("scale", data.getScale().name());

        return json.toString();
    }

    public static int applyJsonData(final Generator generator, final String json) {
        JsonObject data;
        try {
            data = (new Gson()).fromJson(json, JsonElement.class).getAsJsonObject();
        }
        catch (Exception ignore) {
            Log.d("testy", "invalid Json string");
            return -10;
        }

        if (data.keySet().size() < 2) {//aucune ou une seule clé
            Log.d("testy", "nécessite au moins deux clés : id + quelque chose. a reçu "+data.keySet().size());
            return -10;
        }

        String attribute;
        int channel;

        attribute = "id";
        try {
            channel = Integer.parseInt(String.valueOf(data.get(attribute)));
        }
        catch (Exception ignore) {
            Log.d("testy", "erreur en récup "+attribute);
            return -10;
        }
        if (channel > generator.getChannelList().size()) {
            Log.d("testy", "index hors tableau");
            return -10;
        }


        attribute = "isActive";
        if(data.has(attribute)){
            try {
                boolean tmp = Boolean.parseBoolean(String.valueOf(data.get(attribute)));
                generator.getChannelList().get(channel).setActive(tmp);
            }
            catch (Exception ignore) {
                Log.d("testy", "erreur récup "+attribute);
                return -10;
            }
        }

        attribute = "currentValue";
        if(data.has(attribute)){
            try {
                double tmp = Double.parseDouble(String.valueOf(data.get(attribute)));
                generator.getChannelList().get(channel).setCurrentValue(tmp);
            }
            catch (Exception ignore) {
                Log.d("testy", "erreur de la récup "+attribute);
                return -10;
            }
        }

        attribute = "minValue";
        if(data.has(attribute)){
            try {
                float tmp = Float.parseFloat(String.valueOf(data.get(attribute)));
                generator.getChannelList().get(channel).setMinValue(tmp);
            }
            catch (Exception ignore) {
                Log.d("testy", "erreur récup "+attribute);
                return -10;
            }
        }

        attribute = "maxValue";
        if(data.has(attribute)){
            try {
                float tmp = Float.parseFloat(String.valueOf(data.get(attribute)));
                generator.getChannelList().get(channel).setMaxValue(tmp);
            }
            catch (Exception ignore) {
                Log.d("testy", "erreur récup "+attribute);
                return -10;
            }
        }

        attribute = "type";
        if(data.has(attribute)){
            try {
                PowerSupply tmp = PowerSupply.valueOf(data.get(attribute).getAsString());
                generator.getChannelList().get(channel).setType(tmp);
            }
            catch (Exception ignore) {
                Log.d("testy", "erreur récup "+attribute);
                return -10;
            }
        }

        attribute = "scale";
        if(data.has(attribute)){
            try {
                Scale tmp = Scale.valueOf(data.get(attribute).getAsString());
                generator.getChannelList().get(channel).setScale(tmp);
            }
            catch (Exception ignore) {
                Log.d("testy", "erreur récup "+attribute);
                return -10;
            }
        }

        return channel;
    }
}

