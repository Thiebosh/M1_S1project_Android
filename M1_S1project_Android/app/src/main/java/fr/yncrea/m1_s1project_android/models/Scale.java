package fr.yncrea.m1_s1project_android.models;

public enum Scale {
    u(-6), m(-3), _(0), k(3), M(6);

    private final int mValue;

    Scale(final int value) {
        mValue = value;
    }

    public int getValue() {
        return this.mValue;
    }
}
