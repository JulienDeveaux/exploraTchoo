package com.lesd.exploratchoo.Api.models;

import com.lesd.exploratchoo.Api.Sncf;

public class SNCFResponse
{
    public ArrDep[] arrivals;
    public ArrDep[] departures;
    public Sncf.QueryType queryType;
    public VehicleJourneys[] vehicle_journeys;
}
