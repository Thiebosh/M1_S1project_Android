package fr.yncrea.m1_s1project_android.services;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.Objects;

import fr.yncrea.m1_s1project_android.models.Channel;

public class ConverterService {

    //String fragment
    //Enum type (channel, values)

    public static Channel stringToData(final String str) {
        return (new Gson()).fromJson(str, Channel.class);
    }

    public static String dataToString(final Channel data) {
        JsonObject json = new JsonObject();
        json.addProperty("channel", data.getId());

        if (data.getType() != null) {
            json.addProperty("type", data.getType().toString());
        }
        if (data.isSetCurrentValue) {
            json.addProperty("type", data.getCurrentValue());
        }
        if (data.isSetActive) {
            json.addProperty("type", data.isActive());
        }



        /*
        JsonArray array = new JsonArray();
        for (int val = 0; val <= 3; ++val) {
            JsonObject item = new JsonObject();
            item.addProperty("type", val);
            array.add(item);
        }
        json.add("array", array);
        */

        return json.toString();
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

