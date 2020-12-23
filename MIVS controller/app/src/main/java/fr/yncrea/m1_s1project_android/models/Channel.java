package fr.yncrea.m1_s1project_android.models;

public class Channel {
    public static final String ATTRIBUTE_ID = "id";
    public static final String ATTRIBUTE_UNIT = "unit";
    public static final String ATTRIBUTE_SCALE = "scale";
    public static final String ATTRIBUTE_IS_ACTIVE = "isActive";
    public static final String ATTRIBUTE_MIN_VALUE = "minValue";//aiguillage volt / ampere selon unit
    public static final String ATTRIBUTE_MAX_VALUE = "maxValue";//aiguillage volt / ampere selon unit
    public static final String ATTRIBUTE_CURRENT_VALUE = "currentValue";

    private int id;
    private Unit unit = Unit.V;
    private Scale scale = Scale._;
    private boolean isActive = false;
    private double currentValue = 0;
    private double minVoltValue = -3.3;
    private double maxVoltValue = 3.3;
    private double minAmpereValue = -5000;
    private double maxAmpereValue = 5000;

    private boolean isSetUnit = false;
    private boolean isSetScale = false;
    private boolean isSetActive = false;
    private boolean isSetMinValue = false;
    private boolean isSetMaxValue = false;
    private boolean isSetCurrentValue = false;


    public int getId() {
        return id;
    }

    public Channel setId(int id) {
        this.id = id;
        return this;
    }

    public Unit getUnit() {
        return unit;
    }

    public Channel setUnit(Unit unit) {
        this.unit = unit;
        this.isSetUnit = true;
        return this;
    }

    public Scale getScale() {
        return scale;
    }

    public Channel setScale(Scale scale) {
        this.scale = scale;
        this.isSetScale = true;
        return this;
    }

    public boolean isActive() {
        return isActive;
    }

    public Channel setActive(boolean active) {
        this.isActive = active;
        this.isSetActive = true;
        return this;
    }

    public double getMinValue() {
        return unit == Unit.V ? minVoltValue : minAmpereValue;
    }

    public Channel setMinValue(double minValue) {
        if (unit == Unit.V) minVoltValue = minValue;
        else minAmpereValue = minValue;

        this.isSetMinValue = true;

        return this;
    }

    public double getMaxValue() {
        return unit == Unit.V ? maxVoltValue : maxAmpereValue;
    }

    public Channel setMaxValue(double maxValue) {
        if (unit == Unit.V) maxVoltValue = maxValue;
        else maxAmpereValue = maxValue;

        this.isSetMaxValue = true;

        return this;
    }

    public double getCurrentValue() {
        return currentValue;
    }

    public Channel setCurrentValue(double currentValue) {
        this.currentValue = currentValue;
        this.isSetCurrentValue = true;
        return this;
    }

    public boolean isSetActive() {
        return isSetActive;
    }

    public boolean isSetCurrentValue() {
        return isSetCurrentValue;
    }

    public boolean isSetMinValue() {
        return isSetMinValue;
    }

    public boolean isSetMaxValue() {
        return isSetMaxValue;
    }

    public boolean isSetUnit() {
        return isSetUnit;
    }

    public boolean isSetScale() {
        return isSetScale;
    }
}
