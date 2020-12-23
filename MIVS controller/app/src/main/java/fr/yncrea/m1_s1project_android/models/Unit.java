package fr.yncrea.m1_s1project_android.models;

import java.util.ArrayList;

//vf : Intensité (tension), Voltage (courant)
public enum Unit {
    I, V;

    public static ArrayList<String> getNames() {
        ArrayList<String> enumNames = new ArrayList<>();
        for (Unit value : Unit.values()) enumNames.add(value.name());
        return enumNames;
    }
}
