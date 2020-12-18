package fr.yncrea.m1_s1project_android.services;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import fr.yncrea.m1_s1project_android.models.Channel;
import fr.yncrea.m1_s1project_android.models.Generator;
import fr.yncrea.m1_s1project_android.models.Unit;
import fr.yncrea.m1_s1project_android.models.Scale;


public class JsonConverterService {
    public static final char OBJECT_SEPARATOR = ':';
    public static final int ERROR_CONVERSION = -10;
    public static final int APPLYING_GLOBAL = -1;

    public static Generator createJsonObject(final String str) {
        try {
            return (new Gson()).fromJson(str, Generator.class);
        }
        catch (Exception ignore) {
            return null;
        }
    }

    public static String createJsonString(final Object instance) {
        return (new Gson()).toJson(instance);//rewrite for sending only not default values
    }

    public static String extractJsonData(final Channel data) {
        JsonObject json = new JsonObject();

        json.addProperty(Channel.ATTRIBUTE_ID, data.getId());
        if (data.isSetActive()) json.addProperty("isActive", (data.isActive()) ? 1 : 0);
        if (data.isSetCurrentValue()) json.addProperty("currentValue", data.getCurrentValue());
        if (data.isSetMinValue()) json.addProperty("minValue", data.getMinValue());
        if (data.isSetMaxValue()) json.addProperty("maxValue", data.getMaxValue());
        if (data.isSetUnit()) json.addProperty("unit", data.getUnit().name());
        if (data.isSetScale()) json.addProperty("scale", data.getScale().name());

        return json.toString();
    }

    public static ArrayList<Integer> extractIntegerArray(final String str, final String key) {
        JSONArray jArray;
        try {
            jArray = (new JSONObject(str)).getJSONArray(key);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }

        ArrayList<Integer> result = new ArrayList<>();
        for (int i = 0; i < jArray.length(); ++i) {
            try {
                result.add(jArray.getInt(i));
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }
        }
        return result;
    }

    public static int applyJsonData(final Generator generator, final String json) {
        JsonObject data;
        try {
            data = (new Gson()).fromJson(json, JsonElement.class).getAsJsonObject();
        }
        catch (Exception ignore) {
            return ERROR_CONVERSION;
        }

        if (data.keySet().size() < 2) {//aucune ou une seule clé
            return ERROR_CONVERSION;
        }

        String attribute;
        int channel;

        attribute = Channel.ATTRIBUTE_ID;
        try {
            channel = data.get(attribute).getAsInt();
        }
        catch (Exception ignore) {
            return ERROR_CONVERSION;
        }
        if(channel == APPLYING_GLOBAL){
            boolean switchAll;
            try {
                switchAll = data.get(Channel.ATTRIBUTE_IS_ACTIVE).getAsBoolean();
            }catch(Exception e){
                return ERROR_CONVERSION;
            }
            for(int i = 0; i < generator.getChannelList().size(); i++) {
                generator.setAllChannelActive(switchAll);
            }
            return APPLYING_GLOBAL;
        }
        if (channel > generator.getChannelList().size()) {
            return ERROR_CONVERSION;
        }


        attribute = Channel.ATTRIBUTE_IS_ACTIVE;
        if(data.has(attribute)){
            try {
                boolean tmp = data.get(attribute).getAsBoolean();
                generator.getChannel(channel).setActive(tmp);
            }
            catch (Exception ignore) {
                return ERROR_CONVERSION;
            }
        }

        attribute = Channel.ATTRIBUTE_UNIT;
        if(data.has(attribute)){
            try {
                Unit tmp = Unit.valueOf(data.get(attribute).getAsString());
                generator.getChannel(channel).setUnit(tmp);
            }
            catch (Exception ignore) {
                return ERROR_CONVERSION;
            }
        }

        attribute = Channel.ATTRIBUTE_SCALE;
        if(data.has(attribute)){
            try {
                Scale tmp = Scale.valueOf(data.get(attribute).getAsString());
                generator.getChannel(channel).setScale(tmp);
            }
            catch (Exception ignore) {
                return ERROR_CONVERSION;
            }
        }

        attribute = Channel.ATTRIBUTE_MIN_VALUE;
        if(data.has(attribute)){
            try {
                double tmp = data.get(attribute).getAsDouble();
                generator.getChannel(channel).setMinValue(tmp);
            }
            catch (Exception ignore) {
                return ERROR_CONVERSION;
            }
        }

        attribute = Channel.ATTRIBUTE_MAX_VALUE;
        if(data.has(attribute)){
            try {
                double tmp = data.get(attribute).getAsDouble();
                generator.getChannel(channel).setMaxValue(tmp);
            }
            catch (Exception ignore) {
                return ERROR_CONVERSION;
            }
        }

        attribute = Channel.ATTRIBUTE_CURRENT_VALUE;
        if(data.has(attribute)){
            try {
                double tmp = data.get(attribute).getAsDouble();
                generator.getChannel(channel).setCurrentValue(tmp);
            }
            catch (Exception ignore) {
                return ERROR_CONVERSION;
            }
        }

        return channel;
    }
}

