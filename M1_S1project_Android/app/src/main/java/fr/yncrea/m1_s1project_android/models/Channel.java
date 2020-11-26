package fr.yncrea.m1_s1project_android.models;

public class Channel {
    private int id;
    private boolean isActive;
    private double currentValue;
    private double minValue;
    private double maxValue;
    private PowerSupply type;
    private Scale scale;

    private boolean isSetActive;
    private boolean isSetCurrentValue;
    private boolean isSetMinValue;
    private boolean isSetMaxValue;
    private boolean isSetType;
    private boolean isSetScale;


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
        this.isActive = active;
        this.isSetActive = true;
    }

    public double getCurrentValue() {
        return currentValue;
    }

    public void setCurrentValue(double currentValue) {
        this.currentValue = currentValue;
        this.isSetCurrentValue = true;
    }

    public double getMinValue() {
        return minValue;
    }

    public void setMinValue(double minValue) {
        this.minValue = minValue;
        this.isSetMinValue = true;
    }

    public double getMaxValue() {
        return maxValue;
    }

    public void setMaxValue(double maxValue) {
        this.maxValue = maxValue;
        this.isSetMaxValue = true;
    }

    public PowerSupply getType() {
        return type;
    }

    public void setType(PowerSupply type) {
        this.type = type;
        this.isSetType = true;
    }

    public Scale getScale() {
        return scale;
    }

    public void setScale(Scale scale) {
        this.scale = scale;
        this.isSetScale = true;
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

    public boolean isSetType() {
        return isSetType;
    }

    public boolean isSetScale() {
        return isSetScale;
    }
}
