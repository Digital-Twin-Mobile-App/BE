package com.project.dadn.enums;

public enum WateringFrequency {
    DAILY("Daily Day"),
    EVERY_2_DAYS("2 Days"),
    WEEKLY("Daily Weekly"),;

    private final String displayName;

    WateringFrequency(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}

