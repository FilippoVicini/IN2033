package com.lancaster.database;

import java.util.Date;

/**
 * Abstract class representing different types of bookings in the Lancaster system.
 * Contains nested classes for various booking types.
 */
public abstract class Bookings {

    /**
     * Represents priority bookings for specific seats and events.
     */
    public static class PriorityBookings {
        private int priorityID;
        private String room;
        private int row;
        private int seat;
        private int eventID;
        private Date date;
        private int friendID;

        /**
         * Constructs a PriorityBooking with the specified details.
         *
         * @param priorityID The unique ID for this priority booking
         * @param room The room where the booking is located
         * @param row The row number of the seat
         * @param seat The seat number
         * @param eventID The ID of the associated event
         * @param date The date of the booking
         * @param friendID The ID of the friend associated with this booking
         */
        public PriorityBookings(int priorityID, String room, int row, int seat, int eventID, Date date, int friendID) {
            this.priorityID = priorityID;
            this.room = room;
            this.row = row;
            this.seat = seat;
            this.eventID = eventID;
            this.date = date;
            this.friendID = friendID;
        }

        /**
         * @return The priority booking ID
         */
        public int getPriorityID() {
            return priorityID;
        }

        /**
         * @return The room name
         */
        public String getRoom() {
            return room;
        }

        /**
         * @return The row number
         */
        public int getRow() {
            return row;
        }

        /**
         * @return The seat number
         */
        public int getSeat() {
            return seat;
        }

        /**
         * @return The event ID
         */
        public int getEventID() {
            return eventID;
        }

        /**
         * @return The booking date
         */
        public Date getDate() {
            return date;
        }

        /**
         * @return The friend ID
         */
        public int getFriendID() {
            return friendID;
        }
    }

    /**
     * Represents group bookings for events with multiple people.
     */
    public static class GroupBookings {
        private int bookingID;
        private int people;
        private Date date;
        private String room;
        private String event;

        /**
         * Constructs a GroupBooking with the specified details.
         *
         * @param bookingID The unique ID for this group booking
         * @param people The number of people in the group
         * @param date The date of the booking
         * @param room The room where the event will take place
         * @param event The name or description of the event
         */
        public GroupBookings(int bookingID, int people, Date date, String room, String event) {
            this.bookingID = bookingID;
            this.people = people;
            this.room = room;
            this.event = event;
            this.date = date;
        }

        /**
         * @return The booking ID
         */
        public int getBookingID() {
            return bookingID;
        }

        /**
         * @return The number of people
         */
        public int getPeople() {
            return people;
        }

        /**
         * @return The room name
         */
        public String getRoom() {
            return room;
        }

        /**
         * @return The event name
         */
        public String getEvent() {
            return event;
        }

        /**
         * @return The booking date
         */
        public Date getDate() {
            return date;
        }
    }
}