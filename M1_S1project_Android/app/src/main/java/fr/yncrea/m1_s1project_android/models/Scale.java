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
        Scale max = Scale.u;
        for (Scale current: getNamesValues(unit)) if (current.getValue() > max.getValue()) max = current;
        return max;
    }

    public static double changeScale(final double value, final Scale initial, final Scale target) {
        int shift = initial.mValue - target.mValue;

        if (shift == 0) return value;

        StringBuilder shifter = new StringBuilder(String.valueOf(Math.abs(value)));//fonctionne jusque 0.001 mais casse à 0.0001. fonctionne pour x.0001

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
            for (int i = 0; i < -shift; ++i) shifter.insert(0, '0');

            shifter.insert(dotPos, '.');
            shifter.deleteCharAt(dotPos - shift + 1);
        }

        if (value < 0) shifter.insert(0, '-');

        return Double.parseDouble(shifter.toString());//si erreur, pb de changement de point
    }
}
