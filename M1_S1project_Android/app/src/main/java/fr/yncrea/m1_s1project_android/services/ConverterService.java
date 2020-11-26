package fr.yncrea.m1_s1project_android.services;

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

    public static Generator stringToObject(final String str) {
        return (new Gson()).fromJson(str, Generator.class);
    }

    public static JsonObject stringToJson(final Generator generator, final String str) {
        Log.d("testyfy", str);
        JsonObject data = (new Gson()).fromJson (str, JsonElement.class).getAsJsonObject();

        if (data.keySet().size() < 2) {//aucune ou une seule clé
            Log.d("testy", "nécessite au moins deux clés : id + quelque chose. a reçu "+data.keySet().size());
            return null;//retourner string unique dans le json?
        }

        String attribute;
        int channel = 0;

        attribute = "id";
        try {
            channel = Integer.parseInt(String.valueOf(data.get(attribute)));

        }
        catch (Exception ignore) {
            Log.d("testy", "erreur en récup "+attribute);
            return null;
        }

        if (channel > generator.getChannelList().size()) {
            Log.d("testy", "index hors tableau");
            return null;
        }


        attribute = "isActive";
        if(data.has(attribute)){
            try {
                boolean tmp = Boolean.parseBoolean(String.valueOf(data.get(attribute)));
                generator.getChannelList().get(channel).setActive(tmp);
            }
            catch (Exception ignore) {
                Log.d("testy", "erreur récup "+attribute);
                return null;
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
                return null;
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
                return null;
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
                return null;
            }
        }

        attribute = "type";
        if(data.has(attribute)){
            try {
                String mode = String.valueOf(data.get(attribute));
                Log.d("testys", mode);
                PowerSupply tmp = mode.equals("\"V\"") ? V : I;

                Log.d("testym", String.valueOf(tmp));
                generator.getChannelList().get(channel).setType(tmp);
            }
            catch (Exception ignore) {
                Log.d("testy", "erreur récup "+attribute);
                Log.d("testy", data.get(attribute).toString());
                return null;
            }
        }

        attribute = "scale";
        if(data.has(attribute)){
            try {
                Scale tmp = Scale.valueOf(String.valueOf(data.get(attribute)));
                generator.getChannelList().get(channel).setScale(tmp);
            }
            catch (Exception ignore) {
                Log.d("testy", "erreur récup "+attribute);
                return null;
            }
        }

        return data;
    }


    public static String objectToString(final Channel data) {
        JsonObject json = new JsonObject();
        json.addProperty("id", data.getId());


        if (data.isSetActive()) {
            json.addProperty("isActive", data.isActive());
        }
        if (data.isSetCurrentValue()) {
            json.addProperty("currentValue", data.getCurrentValue());
        }
        if (data.isSetMinValue()) {
            json.addProperty("minValue", data.getCurrentValue());
        }
        if (data.isSetMaxValue()) {
            json.addProperty("maxValue", data.getCurrentValue());
        }
        if (data.isSetType()) {
            json.addProperty("type", data.getType().name());
            Log.d("testype", data.getType().name());
        }
        if (data.isSetScale()) {
            json.addProperty("scale", data.getCurrentValue());
        }
        Log.d("testyJSON", json.toString());
        return json.toString();

        /*
        JsonArray array = new JsonArray();
        for (int val = 0; val <= 3; ++val) {
            JsonObject item = new JsonObject();
            item.addProperty("type", val);
            array.add(item);
        }
        json.add("array", array);
        */
    }


    /*
    {
        "fragment":"backup/main",
        "backup":"load/save",

        "channel":-1(all)/0/.../7,
        "activation":true/false, -> couple avec channel -1 pour (dés)activer toutes les pistes
        "type": "I/V",
        "scale": -6/.../0/.../6, -> puissance de 10
        "currentVal": 12.5,
        "minVal": -6,
        "maxVal": 20,

        "init":
            [
                {
                    "channel":0,
                    "activation":true/false,
                    "type": "I/V",
                    "scale": -6/.../0/.../6, -> puissance de 10
                    "currentVal": 12.5,
                    "minVal": -6,
                    "maxVal": 20
                },
                ...,
                {
                    "channel":7,
                    "activation":true/false,
                    "type": "I/V",
                    "scale": -6/.../0/.../6, -> puissance de 10
                    "currentVal": 12.5,
                    "minVal": -6,
                    "maxVal": 20
                },
            ]
    }

     */
}

