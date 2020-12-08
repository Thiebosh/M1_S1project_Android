package fr.yncrea.m1_s1project_android.models;

import java.util.ArrayList;
import java.util.Arrays;

public enum Scale {
    u(-6), m(-3), _(0);

    private final int mValue;

    Scale(final int value) {
        mValue = value;
    }

    public int getValue() {
        return this.mValue;
    }

    public static ArrayList<Scale> getNamesValues(Unit unit) {
        ArrayList<Scale> enumNames = new ArrayList<>();

        if (unit.equals(Unit.I)) enumNames.addAll(Arrays.asList(u, m));
        else /*unit.equals(Unit.V) */ enumNames.addAll(Arrays.asList(m, _));

        return enumNames;
    }

    public static ArrayList<String> getNames(Unit unit) {
        ArrayList<String> enumNames = new ArrayList<>();

        for (Scale tmp : Scale.getNamesValues(unit)) enumNames.add(tmp.name());

        return enumNames;
    }
}
