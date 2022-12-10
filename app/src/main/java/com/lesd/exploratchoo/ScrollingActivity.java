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
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

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

    private SwipeRefreshLayout swipeLayout;

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

        swipeLayout = binding.contentScrolling.swipe;

        RecyclerView arrivalsRecyclerView = binding.contentScrolling.arrivalsRecyclerView;
        RecyclerView departuresRecyclerView = binding.contentScrolling.departRecyclerView;

        this.arrivals   = new RecyclerViewAdapter(new ArrDep[0], Sncf.QueryType.ARRIVALS);
        this.departures = new RecyclerViewAdapter(new ArrDep[0], Sncf.QueryType.DEPARTURES);

        this.textArrivals   = binding.contentScrolling.textArrive;
        this.textDepartures = binding.contentScrolling.textDepart;

        arrivalsRecyclerView  .setAdapter(this.arrivals);
        departuresRecyclerView.setAdapter(this.departures);

        Toolbar toolbar = binding.toolbar;
        setSupportActionBar(toolbar);
        CollapsingToolbarLayout toolBarLayout = binding.toolbarLayout;
        toolBarLayout.setTitle(getTitle());

        FloatingActionButton fab = binding.fab;
        fab.setOnClickListener(view -> loadDatas());

        Spinner locations = binding.contentScrolling.locations;

        ArrayAdapter<SncfLocations> adapter = new ArrayAdapter<>(this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, SncfLocations.values());
        locations.setAdapter(adapter);

        locations.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l)
            {
                ScrollingActivity.this.currentLocation = adapter.getItem(i);
                ScrollingActivity.this.loadDatas();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });

        swipeLayout.setOnRefreshListener(this::loadDatas);
    }

    private void loadDatas()
    {
        this.swipeLayout.setRefreshing(true);

        SharedPreferences preferences = this.getSharedPreferences("com.lesd.exploratchoo", MODE_PRIVATE);

        String api_key = preferences.getString("api_key", null);

        if(service == null)
            service = new Sncf(api_key);

        service.majApiKey(api_key);

        if(api_key == null || api_key.isEmpty())
        {
            runOnUiThread(() ->
            {
                this.textArrivals.setText(R.string.no_api_key);
                this.textDepartures.setText("");
                this.swipeLayout.setRefreshing(false);
            });
        }
        else
        {
            new Thread(() ->
            {
               ArrDep[] arrivals = null;
               ArrDep[] departures;
               Exception ex = null;

               try
               {
                   arrivals = service.getHoraires(Sncf.QueryType.ARRIVALS, this.currentLocation).arrivals;
                   departures = service.getHoraires(Sncf.QueryType.DEPARTURES, this.currentLocation).departures;

                    Thread[] threads = new Thread[arrivals.length + departures.length];

                    for (int i = 0; i < threads.length; i++)
                    {
                        boolean isArrivals = i < arrivals.length;
                        final ArrDep data = isArrivals ? arrivals[i] : departures[i-arrivals.length];

                        Thread run = new Thread(() -> service.rectifyTime(data, isArrivals ? Sncf.QueryType.ARRIVALS : Sncf.QueryType.DEPARTURES));
                        run.start();

                        threads[i] = run;
                    }

                    for (Thread thread : threads)
                    {
                        try
                        {
                            thread.join();
                        }
                        catch (InterruptedException e)
                        {
                            e.printStackTrace();
                        }
                    }

                    ArrDep[] finalArrivals = arrivals;
                    ArrDep[] finalDepartures = departures;

                    runOnUiThread(() ->
                    {
                        String tmp = "Arrivées de " + this.currentLocation;

                        this.textArrivals.setText(tmp);

                        tmp = "Départs de " + this.currentLocation;

                        this.textDepartures.setText(tmp);

                        this.arrivals.changeList(finalArrivals);
                        this.departures.changeList(finalDepartures);
                        this.swipeLayout.setRefreshing(false);
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
                        this.swipeLayout.setRefreshing(false);
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