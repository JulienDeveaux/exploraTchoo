package com.lesd.exploratchoo;

import android.os.Bundle;

import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

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
    Sncf service;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        service = new Sncf();

        binding = ActivityScrollingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Toolbar toolbar = binding.toolbar;
        setSupportActionBar(toolbar);
        CollapsingToolbarLayout toolBarLayout = binding.toolbarLayout;
        toolBarLayout.setTitle(getTitle());

        TextView textView = binding.contentScrolling.textView;
        refreshList(textView);

        FloatingActionButton fab = binding.fab;
        fab.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                refreshList(textView);
            }
        });
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

    public void refreshList(TextView textView) {
        StringBuilder Arrivals = new StringBuilder();
        StringBuilder Departures = new StringBuilder();
        new Thread(() ->
        {
            try {
                SNCFResponse response = service.getHoraires(Sncf.Type.ARRIVALS);

                Arrivals.append("Provenance|");
                Arrivals.append("Type|");
                Arrivals.append("Départ|");
                Arrivals.append("Arrivé<br>");
                for (ArrDep arrDep : response.arrivals) {
                    Arrivals.append(arrDep.display_informations.label).append("|");
                    Arrivals.append(arrDep.display_informations.physical_mode).append("|");
                    Arrivals.append(
                            arrDep.stop_date_time.getDepartureDateTime()
                                    .format(new DateTimeFormatterBuilder()
                                            .appendValue(ChronoField.YEAR)
                                            .appendLiteral(", ")
                                            .appendValue(ChronoField.MONTH_OF_YEAR)
                                            .appendLiteral(", ")
                                            .appendValue(ChronoField.DAY_OF_MONTH)
                                            .toFormatter())).append("|");
                    Arrivals.append(arrDep.stop_date_time.getArrivalDateTime()).append("<br>");
                }
            }
            catch (Exception e)
            {
                Arrivals.append("Error while fetching Arrivals : ").append(e.getMessage());

                e.printStackTrace();
            }

            try {
                SNCFResponse response = service.getHoraires(Sncf.Type.DEPARTURES);

                Departures.append("Destination|");
                Departures.append("Type|");
                Departures.append("Départ|");
                Departures.append("Arrivé<br>");
                for (ArrDep arrDep : response.departures) {
                    Departures.append(arrDep.display_informations.direction).append("|");
                    Departures.append(arrDep.display_informations.physical_mode).append("|");
                    Departures.append(
                            arrDep.stop_date_time.getDepartureDateTime()
                            .format(new DateTimeFormatterBuilder()
                                    .appendValue(ChronoField.YEAR)
                                    .appendLiteral(", ")
                                    .appendValue(ChronoField.MONTH_OF_YEAR)
                                    .appendLiteral(", ")
                                    .appendValue(ChronoField.DAY_OF_MONTH)
                                    .toFormatter())).append("|");
                    Departures.append(arrDep.stop_date_time.getArrivalDateTime()).append("<br>");
                }
            }
            catch (Exception e)
            {
                Departures.append("Error while fetching Departures : ").append(e.getMessage());

                e.printStackTrace();
            }
            String finalResult = "<h1>Arrivées du Havre : </h1>\n" + Arrivals + "\n<h1>Départs du Havre : </h1>\n" + Departures;
            runOnUiThread(() -> textView.setText(Html.fromHtml(finalResult, 0)));
        }).start();
    }
}