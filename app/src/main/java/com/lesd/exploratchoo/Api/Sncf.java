package com.lesd.exploratchoo.Api;

import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.Header;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.HttpEntity;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.HttpResponse;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.client.HttpClient;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.client.methods.HttpGet;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.impl.client.HttpClientBuilder;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.message.BasicHeader;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.lesd.exploratchoo.Api.models.SNCFResponse;

import java.io.IOException;
import java.util.ArrayList;

public class Sncf
{
    public static final String BASE_URL = "https://api.sncf.com/v1/coverage/sncf/";

    private final HttpClient client;
    private final Gson gson;

    public Sncf()
    {
        ArrayList<Header> defaultHeaders = new ArrayList<>();

        defaultHeaders.add(new BasicHeader("Authorization", "c18b51c9-a711-41d0-9424-97aab2dded92"));

        this.client = HttpClientBuilder
                .create()
                .setDefaultHeaders(defaultHeaders)
                .build();

        this.gson = new GsonBuilder().create();
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
        String url = BASE_URL + "stop_areas/stop_area:" + location + "/";

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
