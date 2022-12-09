package com.lesd.exploratchoo.Api;

import static com.lesd.exploratchoo.Api.Sncf.QueryType.ARRIVALS;

import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.Header;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.HttpEntity;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.HttpResponse;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.client.HttpClient;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.client.methods.HttpGet;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.impl.client.HttpClientBuilder;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.message.BasicHeader;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.lesd.exploratchoo.Api.models.ArrDep;
import com.lesd.exploratchoo.Api.models.SNCFResponse;
import com.lesd.exploratchoo.Api.models.VehicleSNCFResponse;

import java.io.IOException;
import java.util.ArrayList;

public class Sncf
{
    public static final String BASE_URL = "https://api.sncf.com/v1/coverage/sncf/";

    private final HttpClient client;
    private final Gson gson;
    private final String api_key;

    public Sncf(String api_key)
    {
        ArrayList<Header> defaultHeaders = new ArrayList<>();

        defaultHeaders.add(new BasicHeader("Authorization", api_key));

        this.client = HttpClientBuilder
                .create()
                .setDefaultHeaders(defaultHeaders)
                .build();

        this.gson = new GsonBuilder().create();

        this.api_key = api_key;
    }

    /**
     * Le Havre station ID: SNCF:87413013 in default
     * @param type The type of query to perform
     * @return The response from the API
     * @throws IOException If the API call fails
     */
    public SNCFResponse getHoraires(QueryType type) throws IOException
    {
        return getHoraires(type, SncfLocations.LE_HAVRE);
    }

    /**
     *
     * @param type The type of query to perform
     * @param location The location to query
     * @return The response from the API
     * @throws IOException If the API call fails
     */
    public SNCFResponse getHoraires(QueryType type, SncfLocations location) throws IOException
    {
        String url = BASE_URL + "stop_areas/stop_area:" + location.getId() + "/";

        switch (type)
        {
            case ARRIVALS:
                url += "arrivals";
                break;
            case DEPARTURES:
                url += "departures";
                break;
        }

        url += "?data_freshness=realtime";

        HttpResponse response = this.client.execute(new HttpGet(url));

        if(response.getStatusLine().getStatusCode() != 200)
            throw new IOException("API call failed with status code " + response.getStatusLine().getStatusCode() + " and reason: \n" + this.readContent(response.getEntity()));

        HttpEntity entity = response.getEntity();
        String content = this.readContent(entity);

        SNCFResponse sncfResponse = this.gson.fromJson(content, SNCFResponse.class);

        sncfResponse.queryType = type;

        return sncfResponse;
    }

    public ArrDep rectifyTime(ArrDep data, QueryType type) {
        try {
            ArrayList<Header> defaultHeaders = new ArrayList<>();

            defaultHeaders.add(new BasicHeader("Authorization", this.api_key));
            HttpClient localClient = HttpClientBuilder
                    .create()
                    .setDefaultHeaders(defaultHeaders)
                    .build();
            String vehicleJourneyId = data.links[1].id;
            String vehicleJourneyUrl = BASE_URL +
                    "vehicle_journeys/" +
                    vehicleJourneyId +
                    "?filter=vehicle_journeys.has_code(headsign," +
                    data.display_informations.headsign +
                    ")";

            HttpResponse vehicleResponse = localClient.execute(new HttpGet(vehicleJourneyUrl));

            HttpEntity vehicleEntity = vehicleResponse.getEntity();
            String vehicleContent = this.readContent(vehicleEntity);

            VehicleSNCFResponse vehicleSncfResponse = this.gson.fromJson(vehicleContent, VehicleSNCFResponse.class);
            String firstArrivalTime = "";
            String lastArrivalTime = "";
            if (type.equals(ARRIVALS)) {
                if (vehicleSncfResponse.vehicle_journeys[0].stop_times[0].stop_point.name.equals("Le Havre")) {
                    firstArrivalTime = vehicleSncfResponse.vehicle_journeys[0].stop_times[0].arrival_time;
                    lastArrivalTime = vehicleSncfResponse.vehicle_journeys[0].stop_times[vehicleSncfResponse.vehicle_journeys[0].stop_times.length - 1].arrival_time;
                } else {
                    firstArrivalTime = vehicleSncfResponse.vehicle_journeys[0].stop_times[vehicleSncfResponse.vehicle_journeys[0].stop_times.length - 1].arrival_time;
                    lastArrivalTime = vehicleSncfResponse.vehicle_journeys[0].stop_times[0].arrival_time;
                }
            } else {
                if (!vehicleSncfResponse.vehicle_journeys[0].stop_times[0].stop_point.name.equals("Le Havre")) {
                    firstArrivalTime = vehicleSncfResponse.vehicle_journeys[0].stop_times[0].arrival_time;
                    lastArrivalTime = vehicleSncfResponse.vehicle_journeys[0].stop_times[vehicleSncfResponse.vehicle_journeys[0].stop_times.length - 1].arrival_time;
                } else {
                    firstArrivalTime = vehicleSncfResponse.vehicle_journeys[0].stop_times[vehicleSncfResponse.vehicle_journeys[0].stop_times.length - 1].arrival_time;
                    lastArrivalTime = vehicleSncfResponse.vehicle_journeys[0].stop_times[0].arrival_time;
                }
            }
            String before_arrival_date_time = data.stop_date_time.arrival_date_time;
            String before_departure_date_time = data.stop_date_time.departure_date_time;
            String now_arrival_date_time = before_arrival_date_time.substring(0, before_arrival_date_time.indexOf("T") + 1) + firstArrivalTime;
            String now_departure_date_time = before_departure_date_time.substring(0, before_departure_date_time.indexOf("T") + 1) + lastArrivalTime;
            data.stop_date_time.arrival_date_time = now_arrival_date_time;
            data.stop_date_time.departure_date_time = now_departure_date_time;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return data;
    }

    private String readContent(HttpEntity entity) throws IOException
    {
        ArrayList<Byte> bytes = new ArrayList<>();
        byte current = -1;

        do
        {
            current = (byte) entity.getContent().read();

            if (current > -1)
                bytes.add(current);
        }
        while (current > -1);

        byte[] bytesArray = new byte[bytes.size()];

        for (int i = 0; i < bytes.size(); i++)
            bytesArray[i] = bytes.get(i);

        return new String(bytesArray);
    }

    public enum QueryType
    {
        DEPARTURES("departures"),
        ARRIVALS("arrivals");

        private final String value;

        QueryType(String value)
        {
            this.value = value;
        }

        public String getValue()
        {
            return value;
        }
    }
}
