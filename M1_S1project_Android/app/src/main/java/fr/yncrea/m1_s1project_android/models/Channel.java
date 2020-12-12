package fr.yncrea.m1_s1project_android.models;

public class Channel {
    private int id;
    private boolean isActive;
    private double currentValue;
    private double minVoltValue = -3.3;
    private double maxVoltValue = 3.3;
    private double minAmpereValue = -5000;
    private double maxAmpereValue = 5000;
    private Unit unit;
    private Scale scale;

    private boolean isSetActive;
    private boolean isSetCurrentValue;
    private boolean isSetMinValue;
    private boolean isSetMaxValue;
    private boolean isSetUnit;
    private boolean isSetScale;


    public int getId() {
        return id;
    }

    public Channel setId(int id) {
        this.id = id;
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

    public double getCurrentValue() {
        return currentValue;
    }

    public Channel setCurrentValue(double currentValue) {
        this.currentValue = currentValue;
        this.isSetCurrentValue = true;
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
