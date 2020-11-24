package fr.yncrea.m1_s1project_android.services;

public class ConverterService {

    //String fragment
    //Enum type (channel, values

    public static Object stringToData(final String str) {
        return str;
    }

    public static String dataToString(final Object obj) {
        return obj.toString();
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

