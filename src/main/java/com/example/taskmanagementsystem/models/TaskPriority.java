package com.example.taskmanagementsystem.models;

/**
 * @author Yina-ship-it
 * @since 09.12.2023
 */
public enum TaskPriority {
    HIGH("высокий", 3),
    MEDIUM("средний", 2),
    LOW("низкий", 1);

    private final String text;
    private final int value;

    TaskPriority(String text, int value) {
        this.text = text;
        this.value = value;
    }

    public String getText() {
        return text;
    }

    public int getValue() {
        return value;
    }

    public static TaskPriority getByValue(int value) {
        for (TaskPriority priority : values()) {
            if (priority.getValue() == value) {
                return priority;
            }
        }
        throw new IllegalArgumentException("Invalid TaskPriority value: " + value);
    }
}
