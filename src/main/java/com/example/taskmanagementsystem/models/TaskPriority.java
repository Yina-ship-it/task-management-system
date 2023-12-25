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
@JsonDeserialize(using = TaskPriorityDeserializer.class)
public enum TaskPriority {
    LOW("низкий", 1),
    MEDIUM("средний", 2),
    HIGH("высокий", 3);

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

    @Override
    public String toString() {
        return "{" +
                "text=\"" + text + '\"' +
                ",value=" + value +
                "}";
    }
}
class TaskPriorityDeserializer extends JsonDeserializer<TaskPriority> {
    @Override
    public TaskPriority deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        JsonNode node = p.readValueAsTree();
        int value = node.get("value").asInt();
        return TaskPriority.getByValue(value);
    }
}
