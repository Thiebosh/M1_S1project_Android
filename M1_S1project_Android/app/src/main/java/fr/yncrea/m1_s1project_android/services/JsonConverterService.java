package fr.yncrea.m1_s1project_android.services;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import fr.yncrea.m1_s1project_android.models.Channel;
import fr.yncrea.m1_s1project_android.models.Generator;
import fr.yncrea.m1_s1project_android.models.Unit;
import fr.yncrea.m1_s1project_android.models.Scale;


public class JsonConverterService {

    public static Generator createJsonObject(final String str) {
        try {
            return (new Gson()).fromJson(str, Generator.class);
        }
        catch (Exception ignore) {
            return null;
        }
    }

    public static String extractJsonData(final Channel data) {
        JsonObject json = new JsonObject();

        json.addProperty("id", data.getId());
        if (data.isSetActive()) json.addProperty("isActive", data.isActive());
        if (data.isSetCurrentValue()) json.addProperty("currentValue", data.getCurrentValue());
        if (data.isSetMinValue()) json.addProperty("minValue", data.getMinValue());
        if (data.isSetMaxValue()) json.addProperty("maxValue", data.getMaxValue());
        if (data.isSetUnit()) json.addProperty("type", data.getUnit().name());
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
            //Log.d("testy", "nécessite au moins deux clés : id + quelque chose. a reçu "+data.keySet().size());
            return -10;
        }

        String attribute;
        int channel;

        attribute = "id";
        try {
            channel = data.get(attribute).getAsInt();
        }
        catch (Exception ignore) {
            //Log.d("testy", "erreur en récup "+attribute);
            return -10;
        }
        if(channel == -1){
            boolean switchAll;
            try {
                switchAll = data.get("isActive").getAsBoolean();
            }catch(Exception e){
                return -10;
            }
            for(int i = 0; i < generator.getChannelList().size(); i++) {
                generator.setAllChannelActive(switchAll);
            }
            return -1;
        }
        if (channel > generator.getChannelList().size()) {
            //Log.d("testy", "index hors tableau");
            return -10;
        }


        attribute = "isActive";
        if(data.has(attribute)){
            try {
                boolean tmp = data.get(attribute).getAsBoolean();
                generator.getChannel(channel).setActive(tmp);
            }
            catch (Exception ignore) {
                //Log.d("testy", "erreur récup "+attribute);
                return -10;
            }
        }

        attribute = "currentValue";
        if(data.has(attribute)){
            try {
                double tmp = data.get(attribute).getAsDouble();
                generator.getChannel(channel).setCurrentValue(tmp);
            }
            catch (Exception ignore) {
                //Log.d("testy", "erreur de la récup "+attribute);
                return -10;
            }
        }

        attribute = "minValue";
        if(data.has(attribute)){
            try {
                float tmp = data.get(attribute).getAsFloat();
                generator.getChannel(channel).setMinValue(tmp);
            }
            catch (Exception ignore) {
                //Log.d("testy", "erreur récup "+attribute);
                return -10;
            }
        }

        attribute = "maxValue";
        if(data.has(attribute)){
            try {
                float tmp = data.get(attribute).getAsFloat();
                generator.getChannel(channel).setMaxValue(tmp);
            }
            catch (Exception ignore) {
                //Log.d("testy", "erreur récup "+attribute);
                return -10;
            }
        }

        attribute = "type";
        if(data.has(attribute)){
            try {
                Unit tmp = Unit.valueOf(data.get(attribute).getAsString());
                generator.getChannel(channel).setUnit(tmp);
            }
            catch (Exception ignore) {
                //Log.d("testy", "erreur récup "+attribute);
                return -10;
            }
        }

        attribute = "scale";
        if(data.has(attribute)){
            try {
                Scale tmp = Scale.valueOf(data.get(attribute).getAsString());
                generator.getChannel(channel).setScale(tmp);
            }
            catch (Exception ignore) {
                //Log.d("testy", "erreur récup "+attribute);
                return -10;
            }
        }

        return channel;
    }
}

