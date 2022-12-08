package com.lesd.exploratchoo.Api;

import com.google.gson.GsonBuilder;
import com.lesd.exploratchoo.Api.models.SNCFResponse;

import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.core5.http.Header;
import org.apache.hc.core5.http.HttpResponse;
import org.apache.hc.core5.http.message.BasicHeader;

import java.io.IOException;
import java.util.ArrayList;

public class Sncf
{
    public static final String BASE_URL = "https://api.sncf.com/v1/coverage/sncf/";

    private final HttpClient client;

    public Sncf()
    {
        ArrayList<Header> defaultHeaders = new ArrayList<>();

        defaultHeaders.add(new BasicHeader("Authorization", "c18b51c9-a711-41d0-9424-97aab2dded92"));

        this.client = HttpClientBuilder
                .create()
                .setDefaultHeaders(defaultHeaders)
                .build();
    }

    public SNCFResponse getHoraires(Type type) throws IOException
    {
        String url = BASE_URL + "/stop_areas/stop_area:SNCF:87413013/";

        switch (type)
        {
            case ARRIVALS:
                url += "arrivals";
                break;
            case DEPARTURES:
                url += "departures";
                break;
        }

        HttpResponse response = this.client.execute(new HttpGet(url));

        String json = response.toString();

        return new GsonBuilder().create().fromJson(json, SNCFResponse.class);
    }

    public enum Type
    {
        DEPARTURES("departures"),
        ARRIVALS("arrivals");

        private final String value;

        Type(String value)
        {
            this.value = value;
        }

        public String getValue()
        {
            return value;
        }
    }
}
