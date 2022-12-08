package com.lesd.exploratchoo;

import android.os.Bundle;

import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Html;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.lesd.exploratchoo.Api.Sncf;
import com.lesd.exploratchoo.Api.models.ArrDep;
import com.lesd.exploratchoo.Api.models.SNCFResponse;
import com.lesd.exploratchoo.databinding.ActivityScrollingBinding;

import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.Arrays;
import java.util.Formatter;
import java.util.stream.Collectors;

public class ScrollingActivity extends AppCompatActivity
{

    private ActivityScrollingBinding binding;
    private Sncf service;

    private RecyclerViewAdapter arrivals;
    private RecyclerViewAdapter departures;

    private TextView textArrivals;
    private TextView textDepartures;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        service = new Sncf();

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

        refreshList();

        FloatingActionButton fab = binding.fab;
        fab.setOnClickListener(view -> refreshList());
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
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void refreshList()
    {
        new Thread(() ->
        {
            SNCFResponse arrivals = null;
            SNCFResponse departures = null;
            Exception ex = null;

            try
            {
                arrivals = service.getHoraires(Sncf.QueryType.ARRIVALS);
                departures = service.getHoraires(Sncf.QueryType.DEPARTURES);

                SNCFResponse finalArrivals = arrivals;
                SNCFResponse finalDepartures = departures;
                runOnUiThread(() ->
                {
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

                this.textDepartures.setText("");
                this.textArrivals.setText(baseText);
            }

        }).start();
    }
}