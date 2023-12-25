package com.example.taskmanagementsystem.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.io.IOException;

/**
 * @author Yina-ship-it
 * @since 09.12.2023
 */
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@JsonDeserialize(using = TaskStatusDeserializer.class)
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

    @Override
    public String toString() {
        return "{" +
                "text=\"" + text + '\"' +
                ",value=" + value +
                "}";
    }
}
class TaskStatusDeserializer extends JsonDeserializer<TaskStatus> {
    @Override
    public TaskStatus deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        JsonNode node = p.readValueAsTree();
        int value = node.get("value").asInt();
        return TaskStatus.getByValue(value);
    }
}
