package com.lancaster.database;

import java.util.Date;

/**
 * Abstract class representing different aspects of films in the Lancaster system.
 * Contains nested classes for various film-related data structures.
 */
public abstract class Films {

    /**
     * Represents financial data related to films.
     */
    public static class FilmFinancials {

    }

    /**
     * Represents engagement metrics for films.
     */
    public static class FilmEngagement {

    }

    /**
     * Represents screening details for films.
     */
    public static class FilmScreening {

    }

    /**
     * Represents general information about a film screening.
     */
    public static class FilmInformation {
        private int showId;
        private int filmId;
        private String filmTitle;
        private Date showDate;
        private int duration;
        private Date endDate;
        private int ticketPrice;

        /**
         * Constructs a FilmInformation object with the specified details.
         *
         * @param showId The unique identifier for this show
         * @param filmId The unique identifier for this film
         * @param filmTitle The title of the film
         * @param showDate The date and time when the film is shown
         * @param duration The duration of the film in minutes
         * @param endDate The end date and time of the film showing
         * @param ticketPrice The price of tickets for this screening
         */
        public FilmInformation(int showId, int filmId, String filmTitle, Date showDate, int duration, Date endDate, int ticketPrice) {
            this.showId = showId;
            this.filmId = filmId;
            this.filmTitle = filmTitle;
            this.showDate = showDate;
            this.duration = duration;
            this.endDate = endDate;
            this.ticketPrice = ticketPrice;
        }

        /**
         * Returns a string representation of this FilmInformation object.
         * @return A string containing the film screening details
         */
        @Override
        public String toString() {
            return "FilmInformation{" +
                    "showId=" + showId +
                    ", filmId=" + filmId +
                    ", filmTitle='" + filmTitle + '\'' +
                    ", showDate=" + showDate +
                    ", duration=" + duration +
                    ", ticketPrice=" + ticketPrice +
                    '}';
        }

        /**
         * @return The show ID
         */
        public int getShowId() {
            return showId;
        }

        /**
         * @return The film ID
         */
        public int getFilmId() {
            return filmId;
        }

        /**
         * @return The film title
         */
        public String getFilmTitle() {
            return filmTitle;
        }

        /**
         * @return The show date and time
         */
        public Date getShowDate() {
            return showDate;
        }

        /**
         * @return The duration in minutes
         */
        public int getDuration() {
            return duration;
        }

        /**
         * @return The ticket price
         */
        public int getTicketPrice() {
            return ticketPrice;
        }
    }
}