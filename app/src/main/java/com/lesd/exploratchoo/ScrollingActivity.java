package com.lesd.exploratchoo;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.lesd.exploratchoo.Api.Sncf;
import com.lesd.exploratchoo.Api.SncfLocations;
import com.lesd.exploratchoo.Api.models.ArrDep;
import com.lesd.exploratchoo.Api.models.SNCFResponse;
import com.lesd.exploratchoo.databinding.ActivityScrollingBinding;

public class ScrollingActivity extends AppCompatActivity
{

    private ActivityScrollingBinding binding;
    private Sncf service;

    private RecyclerViewAdapter arrivals;
    private RecyclerViewAdapter departures;

    private TextView textArrivals;
    private TextView textDepartures;

    private SncfLocations currentLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        this.currentLocation = SncfLocations.LE_HAVRE;

        binding = ActivityScrollingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        RecyclerView arrivalsRecyclerView = binding.contentScrolling.arrivalsRecyclerView;
        RecyclerView departuresRecyclerView = binding.contentScrolling.departRecyclerView;

        this.arrivals = new RecyclerViewAdapter(new ArrDep[0]);
        this.departures = new RecyclerViewAdapter(new ArrDep[0]);

        this.textArrivals = binding.contentScrolling.textArrive;
        this.textDepartures = binding.contentScrolling.textDepart;

        assert arrivalsRecyclerView != null;
        arrivalsRecyclerView.setAdapter(this.arrivals);
        assert departuresRecyclerView != null;
        departuresRecyclerView.setAdapter(this.departures);

        Toolbar toolbar = binding.toolbar;
        setSupportActionBar(toolbar);
        CollapsingToolbarLayout toolBarLayout = binding.toolbarLayout;
        toolBarLayout.setTitle(getTitle());

        FloatingActionButton fab = binding.fab;
        fab.setOnClickListener(view -> loadDatas());

        Spinner locations = binding.contentScrolling.locations;

        assert locations != null;

        ArrayAdapter<SncfLocations> adapter = new ArrayAdapter<>(this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, SncfLocations.values());

        locations.setAdapter(adapter);

        locations.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                ScrollingActivity.this.currentLocation = adapter.getItem(i);
                ScrollingActivity.this.loadDatas();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        this.loadDatas();
    }

    private void loadDatas()
    {
        SharedPreferences preferences = this.getSharedPreferences("com.lesd.exploratchoo", MODE_PRIVATE);

        String api_key = preferences.getString("api_key", null);

        service = new Sncf(api_key);

        if(api_key == null || api_key.isEmpty())
        {
            runOnUiThread(() ->
            {
              this.textArrivals.setText(R.string.no_api_key);
              this.textDepartures.setText("");
            });
        }
        else
        {
            new Thread(() ->
            {
               SNCFResponse arrivals = null;
               SNCFResponse departures = null;
               Exception ex = null;

               try
               {
                   arrivals = service.getHoraires(Sncf.QueryType.ARRIVALS, this.currentLocation);
                   departures = service.getHoraires(Sncf.QueryType.DEPARTURES, this.currentLocation);

                    Thread[] threads = new Thread[arrivals.arrivals.length];
                    for (int i = 0; i < arrivals.arrivals.length; i++) {
                        final ArrDep[] data = {arrivals.arrivals[i]};
                        Runnable th = () ->
                        {
                            Sncf localService = new Sncf(api_key);
                            data[0] = localService.rectifyTime(data[0], Sncf.QueryType.ARRIVALS);
                        };
                        Thread run = new Thread(th);
                        run.start();
                        threads[i] = run;
                    }
                    for (Thread thread : threads) {
                        try {
                            thread.join();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                    threads = new Thread[departures.departures.length];
                    for (int i = 0; i < departures.departures.length; i++) {
                        final ArrDep[] data = {departures.departures[i]};
                        Runnable th = () ->
                        {
                            Sncf localService = new Sncf(api_key);
                            data[0] = localService.rectifyTime(data[0], Sncf.QueryType.DEPARTURES);
                        };
                        Thread run = new Thread(th);
                        run.start();
                        threads[i] = run;
                    }
                    for (Thread thread : threads) {
                        try {
                            thread.join();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                    SNCFResponse finalArrivals = arrivals;
                    SNCFResponse finalDepartures = departures;
                    runOnUiThread(() ->
                    {
                        String tmp = "Arrivées de " + this.currentLocation;

                        this.textArrivals.setText(tmp);

                        tmp = "Départs de " + this.currentLocation;

                        this.textDepartures.setText(tmp);

                        this.arrivals.changeList(finalArrivals.arrivals);
                        this.departures.changeList(finalDepartures.departures);
                   });
               }
               catch (Exception e)
               {
                   e.printStackTrace();

                   ex = e;
               }

               if(ex != null)
               {
                   String baseText = "Erreur lors de la récupération des données " + (arrivals != null ? "d'arrivées" : "de départ") + "\n" + ex.getMessage();

                   runOnUiThread(() ->
                                 {
                                     this.textDepartures.setText("");
                                     this.textArrivals.setText(baseText);
                                 });
               }

            }).start();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        this.loadDatas();

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_scrolling, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings)
        {
            Intent intent = new Intent(this, SettingsActivity.class);

            startActivityForResult(intent, Intent.FLAG_ACTIVITY_FORWARD_RESULT);
        }
        return super.onOptionsItemSelected(item);
    }
}