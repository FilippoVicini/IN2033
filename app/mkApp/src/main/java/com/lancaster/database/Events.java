package com.lancaster.database;

import java.util.Date;

/**
 * Represents an event in the Lancaster system.
 * Contains details about event types, dates, rooms, and duration.
 */
public class Events {
    private int eventId;
    private String type;
    private Date date;
    private int room;
    private int duration;

    /**
     * Constructs an Event with the specified details.
     *
     * @param eventId The unique identifier for this event
     * @param type The type or category of the event
     * @param date The date and time when the event occurs
     * @param room The room number where the event takes place
     * @param duration The duration of the event in minutes
     */
    public Events(int eventId, String type, Date date, int room, int duration) {
        this.eventId = eventId;
        this.type = type;
        this.date = date;
        this.room = room;
        this.duration = duration;
    }

    /**
     * @return The event ID
     */
    public int getEventId() {
        return eventId;
    }

    /**
     * Sets the event ID.
     * @param eventId The new event ID
     */
    public void setEventId(int eventId) {
        this.eventId = eventId;
    }

    /**
     * @return The event type
     */
    public String getType() {
        return type;
    }

    /**
     * Sets the event type.
     * @param type The new event type
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * @return The event date and time
     */
    public Date getDate() {
        return date;
    }

    /**
     * Sets the event date.
     * @param date The new event date
     */
    public void setDate(Date date) {
        this.date = date;
    }

    /**
     * @return The room number
     */
    public int getRoom() {
        return room;
    }

    /**
     * Sets the room number.
     * @param room The new room number
     */
    public void setRoom(int room) {
        this.room = room;
    }

    /**
     * @return The event duration in minutes
     */
    public int getDuration() {
        return duration;
    }

    /**
     * Sets the event duration.
     * @param duration The new duration in minutes
     */
    public void setDuration(int duration) {
        this.duration = duration;
    }

    /**
     * Returns a string representation of this Events object.
     * @return A string containing all event details
     */
    @Override
    public String toString() {
        return "Events{" +
                "eventId=" + eventId +
                ", type='" + type + '\'' +
                ", date=" + date +
                ", room=" + room +
                ", duration=" + duration +
                '}';
    }
}