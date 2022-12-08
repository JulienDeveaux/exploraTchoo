package com.lesd.exploratchoo.Api;

import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.Header;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.HttpEntity;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.HttpResponse;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.client.HttpClient;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.client.methods.HttpGet;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.impl.client.HttpClientBuilder;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.message.BasicHeader;
import com.google.gson.GsonBuilder;
import com.lesd.exploratchoo.Api.models.SNCFResponse;

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

        HttpEntity entity = response.getEntity();

        byte[] bytes = new byte[(int) entity.getContentLength()];

        int res = entity.getContent().read(bytes, 0, bytes.length);

        String content = new String(bytes);

        return new GsonBuilder().create().fromJson(content, SNCFResponse.class);
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
