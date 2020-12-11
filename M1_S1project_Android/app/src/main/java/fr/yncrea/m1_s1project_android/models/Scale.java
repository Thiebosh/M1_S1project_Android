package fr.yncrea.m1_s1project_android.models;

import android.util.Log;

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

    public static Scale scaleOf(int powerOfTen) {
        switch (powerOfTen) {
            case 0:
                return Scale._;
            case -3:
                return Scale.m;
            case -6:
                return Scale.u;
            default:
                return null;
        }
    }

    public static ArrayList<Scale> getNamesValues(final Unit unit) {
        ArrayList<Scale> enumNames = new ArrayList<>();

        if (unit.equals(Unit.I)) enumNames.addAll(Arrays.asList(u, m));
        else /*unit.equals(Unit.V) */ enumNames.addAll(Arrays.asList(m, _));

        return enumNames;
    }

    public static ArrayList<String> getNames(final Unit unit) {
        ArrayList<String> enumNames = new ArrayList<>();

        for (Scale tmp : Scale.getNamesValues(unit)) enumNames.add(tmp.name());

        return enumNames;
    }

    public static Scale getMinValue(final Unit unit) {
        Scale min = Scale._;
        for (Scale current: getNamesValues(unit)) if (current.getValue() < min.getValue()) min = current;
        return min;
    }

    public static Scale getMaxValue(final Unit unit) {
        Scale min = Scale.u;
        for (Scale current: getNamesValues(unit)) if (current.getValue() > min.getValue()) min = current;
        return min;
    }

    public static double changeScale(final double value, final Scale initial, final Scale target) {
        int shift = initial.mValue - target.mValue;

        if (shift == 0) return value;

        StringBuilder shifter = new StringBuilder(String.valueOf(value));//fonctionne jusque 0.001 mais casse à 0.0001. fonctionne pour x.0001

        int dotPos = shifter.indexOf(".");
        if (dotPos == -1) {
            dotPos = shifter.length();//pas de point
            shifter.append('.');
        }

        if (shift > 0) {//échelle inférieure
            for (int i = shifter.length(); i <= dotPos + shift; ++i) shifter.append('0');

            shifter.insert(dotPos + shift + 1, '.');
            shifter.deleteCharAt(dotPos);
        }

        else {//échelle supérieure
            dotPos -= shift;

            for (int i = shifter.length(); i <= dotPos + 1; ++i) shifter.insert(0, '0');

            shifter.insert(dotPos + shift, '.');
            shifter.deleteCharAt(dotPos+1);
        }


        double result;
        try {
            result = Double.parseDouble(shifter.toString());
        }
        catch (Exception ignore) {
            return -1;
        }
        return result;
    }
}
