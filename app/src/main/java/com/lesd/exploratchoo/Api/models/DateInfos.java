package com.lesd.exploratchoo.Api.models;

import java.time.LocalDateTime;

public class DateInfos
{
    public String arrival_date_time;
    public String departure_date_time;
    public String base_arrival_date_time;
    public String base_departure_date_time;

    public LocalDateTime getArrivalDateTime()
    {
        String newArrivalDateTime = arrival_date_time.substring(0, 4) + "-" +
                arrival_date_time.substring(4, 6) + "-" +
                arrival_date_time.substring(6, 8) + "T" +
                arrival_date_time.substring(9, 11) + ":" +
                arrival_date_time.substring(11, 13) + ":" +
                arrival_date_time.substring(13, 15);

        return LocalDateTime.parse(newArrivalDateTime);
    }

    public LocalDateTime getDepartureDateTime()
    {
        String newDepartureDateTime = this.departure_date_time.substring(0, 4) + "-" +
                this.departure_date_time.substring(4, 6) + "-" +
                this.departure_date_time.substring(6, 8) + "T" +
                this.departure_date_time.substring(9, 11) + ":" +
                this.departure_date_time.substring(11, 13) + ":" +
                this.departure_date_time.substring(13, 15);

        return LocalDateTime.parse(newDepartureDateTime);
    }

    public LocalDateTime getBaseArrivalDateTime()
    {
        String newBaseArrivalDateTime = this.base_arrival_date_time.substring(0, 4) + "-" +
                this.base_arrival_date_time.substring(4, 6) + "-" +
                this.base_arrival_date_time.substring(6, 8) + "T" +
                this.base_arrival_date_time.substring(9, 11) + ":" +
                this.base_arrival_date_time.substring(11, 13) + ":" +
                this.base_arrival_date_time.substring(13);

        return LocalDateTime.parse(newBaseArrivalDateTime);
    }

    public LocalDateTime getBaseDepartureDateTime()
    {
        String newBaseDepartureDateTime = this.base_departure_date_time.substring(0, 4) + "-" +
                this.base_departure_date_time.substring(4, 6) + "-" +
                this.base_departure_date_time.substring(6, 8) + "T" +
                this.base_departure_date_time.substring(9, 11) + ":" +
                this.base_departure_date_time.substring(11, 13) + ":" +
                this.base_departure_date_time.substring(13);

        return LocalDateTime.parse(newBaseDepartureDateTime);
    }
}
