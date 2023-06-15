package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.utils.EventType;
import ru.yandex.practicum.filmorate.utils.Operation;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Event {
    private Integer eventId;
    private long timestamp;
    private int userId;
    private String eventType;
    private String operation;
    private int entityId;

    public Map<String, Object> toMap() {
        Map<String, Object> values = new HashMap<>();
        values.put("event_id", eventId);
        values.put("ts", timestamp);
        values.put("user_id", userId);
        values.put("event_type", eventType);
        values.put("operation", operation);
        values.put("entity_id", entityId);
        return values;
    }

    public Event(int userId, EventType eventType, Operation operation, int entityId) {
        this.timestamp = Instant.now().toEpochMilli();
        this.userId = userId;
        this.eventType = eventType.toString();
        this.operation = operation.toString();
        this.eventId = null;
        this.entityId = entityId;
    }
}
