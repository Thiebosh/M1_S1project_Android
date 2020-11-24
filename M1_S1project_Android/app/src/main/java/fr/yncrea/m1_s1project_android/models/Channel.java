package fr.yncrea.m1_s1project_android.models;

public class Channel {
    private int id;
    private boolean isActive;
    private double currentValue;
    private float minValue;
    private float maxValue;
    private PowerSupply type;
    private Scale scale;

    public boolean isSetActive;
    public boolean isSetCurrentValue;
    public boolean isSetMinValue;
    public boolean isSetMaxValue;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public double getCurrentValue() {
        return currentValue;
    }

    public void setCurrentValue(double currentValue) {
        this.currentValue = currentValue;
        this.isSetCurrentValue = true;
    }

    public float getMinValue() {
        return minValue;
    }

    public void setMinValue(float minValue) {
        this.minValue = minValue;
    }

    public float getMaxValue() {
        return maxValue;
    }

    public void setMaxValue(float maxValue) {
        this.maxValue = maxValue;
    }

    public PowerSupply getType() {
        return type;
    }

    public void setType(PowerSupply type) {
        this.type = type;
    }

    public Scale getScale() {
        return scale;
    }

    public void setScale(Scale scale) {
        this.scale = scale;
    }
}
