package com.project.dadn.enums;

public enum NotificationType {
    PLANT_STAGE_CHANGE("Plant Stage Change"),
    SYSTEM_ALERT("System Alert"),
    PLANT_HEALTH_WARNING("Plant Health Warning"),
    MAINTENANCE_REMINDER("Maintenance Reminder"),
    GROWTH_MILESTONE("Growth Milestone"),
    SENSOR_ALERT("Sensor Alert"),
    PEST_DETECTION("Pest Detection"),
    DISEASE_DETECTION("Disease Detection"),
    WATER_REMINDER("Water Reminder"),
    FERTILIZER_REMINDER("Fertilizer Reminder"),
    HARVEST_TIME("Harvest Time"),
    ENVIRONMENTAL_ALERT("Environmental Alert");

    private final String displayName;

    NotificationType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
