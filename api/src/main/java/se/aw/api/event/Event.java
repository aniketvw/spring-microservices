package se.aw.api.event;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.ZonedDateTimeSerializer;
import lombok.Getter;

import java.time.LocalDateTime;

public class Event<K,T> {

    public enum Type{CREATE,DELETE}

    @Getter
    private Event.Type eventType;
    @Getter
    private K key;
    @Getter
    private T data;
    private LocalDateTime eventCreatedAt;


    public Event(){
        this.eventType=null;
        this.key=null;
        this.data=null;
        this.eventCreatedAt=null;
    }

    public Event(Type eventType,K key, T data){
        this.eventType=eventType;
        this.key=key;
        this.data=data;
        this.eventCreatedAt=LocalDateTime.now();
    }

    @JsonSerialize(using = ZonedDateTimeSerializer.class)
    public LocalDateTime getEventCreatedAt() {
        return eventCreatedAt;
    }
}
