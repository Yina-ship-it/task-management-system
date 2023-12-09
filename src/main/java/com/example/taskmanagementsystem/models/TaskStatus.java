package com.example.taskmanagementsystem.models;

/**
 * @author Yina-ship-it
 * @since 09.12.2023
 */
public enum TaskStatus {
    PENDING("в ожидании", 1),
    IN_PROGRESS("в процессе", 2),
    COMPLETED("завершено", 3);

    private final String text;
    private final int value;

    TaskStatus(String text, int value) {
        this.text = text;
        this.value = value;
    }

    public String getText() {
        return text;
    }

    public int getValue() {
        return value;
    }

    public static TaskStatus getByValue(int value) {
        for (TaskStatus status : values()) {
            if (status.getValue() == value) {
                return status;
            }
        }
        throw new IllegalArgumentException("Invalid TaskStatus value: " + value);
    }
}
