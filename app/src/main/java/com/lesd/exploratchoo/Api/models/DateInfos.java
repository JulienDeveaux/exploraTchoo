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
        return LocalDateTime.parse(this.arrival_date_time);
    }

    public LocalDateTime getDepartureDateTime()
    {
        return LocalDateTime.parse(this.departure_date_time);
    }

    public LocalDateTime getBaseArrivalDateTime()
    {
        return LocalDateTime.parse(this.base_arrival_date_time);
    }

    public LocalDateTime getBaseDepartureDateTime()
    {
        return LocalDateTime.parse(this.base_departure_date_time);
    }
}
