package com.lesd.exploratchoo;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.lesd.exploratchoo.Api.Sncf;
import com.lesd.exploratchoo.Api.models.ArrDep;

import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.Arrays;

public class RecyclerViewAdapter extends RecyclerView.Adapter<ItemViewHolder>
{
    private ArrayList<ArrDep> listElements;
    private Sncf.QueryType elementsTypes;

    public RecyclerViewAdapter(ArrDep[] baseElements, Sncf.QueryType type)
    {
        this.listElements = new ArrayList<>();
        this.elementsTypes = type;

        this.listElements.addAll(Arrays.asList(baseElements));
    }

    @SuppressLint("NotifyDataSetChanged")
    public void changeList(ArrDep[] newElements)
    {
        this.listElements.clear();
        this.listElements.addAll(Arrays.asList(newElements));

        this.notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_view, parent, false);

        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position)
    {
        ArrDep item = this.listElements.get(position);

        DateTimeFormatter formatter = new DateTimeFormatterBuilder()
                .appendValue(ChronoField.DAY_OF_MONTH, 2)
                .appendLiteral("/")
                .appendValue(ChronoField.MONTH_OF_YEAR, 2)
                .appendLiteral("/")
                .appendValue(ChronoField.YEAR)
                .appendLiteral(" ")
                .appendValue(ChronoField.HOUR_OF_DAY, 2)
                .appendLiteral("h")
                .appendValue(ChronoField.MINUTE_OF_HOUR, 2)
                .toFormatter();

        holder.provenance.setText(this.elementsTypes == Sncf.QueryType.ARRIVALS ? item.display_informations.label : item.display_informations.direction);
        holder.type.setText(item.display_informations.physical_mode);
        holder.Depart.setText(item.stop_date_time.getDepartureDateTime().format(formatter));
        holder.Arrivee.setText(item.stop_date_time.getArrivalDateTime().format(formatter));
    }

    @Override
    public int getItemCount()
    {
        return this.listElements.size();
    }
}
